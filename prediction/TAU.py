# -*- coding: utf-8 -*-
# TAU.py -- Streamlined single-purpose prediction script for the Tau (7SP1) model.
# Same design as AD.py: train once (5-fold CV, pick best fold) if no checkpoint
# exists yet, otherwise just load the checkpoint and predict.
#
# NOTE: the training CSV for this model is GBK-encoded (not UTF-8) -- read
# accordingly, see TRAIN_CSV_ENCODING below.

import os
import numpy as np
import pandas as pd
import torch
from torch.utils.data import DataLoader, TensorDataset
from sklearn import preprocessing
from sklearn.model_selection import StratifiedKFold
from sklearn.metrics import roc_auc_score, accuracy_score, f1_score
import warnings

warnings.filterwarnings("ignore")
os.environ["KMP_DUPLICATE_LIB_OK"] = "TRUE"

# ============================================================
# 1. Paths and hyperparameters
# ============================================================
TRAIN_CSV_PATH = r"E:\DataBase\WebSrever\Model\CSV\7sp1全部分子描述符9743训练集.csv"
TRAIN_CSV_ENCODING = "gbk"  # this file is GBK-encoded, unlike the other training CSVs

PT_DIR = r"E:\DataBase\WebSrever\Model\pt"
PT_PATH = os.path.join(PT_DIR, "TAU_best_model.pt")

INPUT_CSV_PATH = r"E:\DataBase\WebSrever\Model\input\TAUModelSource.csv"

OUTPUT_DIR = r"E:\DataBase\WebSrever\Model\output"
OUTPUT_CSV_PATH = os.path.join(OUTPUT_DIR, "TAU_result.csv")

# The binding-energy column(s) this model uses (lowercase for case-insensitive matching)
BINDING_ENERGY_COLS = ['7sp1']

ALL_KNOWN_PROTEIN_IDS = ['1z0q', '6rhy', '6ti7', '1aml', '1ba4', '1iyt',
                          '2beg', '2m9r', '2nao', '5oqv', '6oc9', '6shs',
                          '7sp1', '1xq8', '6a6b', '6yej']

HIDDEN1, HIDDEN2 = 560, 330
N_SPLITS = 5
RANDOM_STATE = 1
EPOCHS = 20
BATCH_SIZE = 16
LR = 1e-4
WEIGHT_DECAY = 1e-5
SELECTION_METRIC = "auc"

torch.manual_seed(RANDOM_STATE)
np.random.seed(RANDOM_STATE)


# ============================================================
# 2. Model definition
# ============================================================
class Net(torch.nn.Module):
    def __init__(self, input_size, output_size, hidden_layer1_neurons, hidden_layer2_neurons):
        super().__init__()
        self.net = torch.nn.Sequential(
            torch.nn.Linear(input_size, hidden_layer1_neurons),
            torch.nn.ReLU(),
            torch.nn.Linear(hidden_layer1_neurons, hidden_layer2_neurons),
            torch.nn.ReLU(),
            torch.nn.Linear(hidden_layer2_neurons, output_size)
        )

    def forward(self, x):
        return self.net(x)


def evaluate(model, data_loader):
    model.eval()
    all_labels, all_preds, all_probs = [], [], []
    with torch.no_grad():
        for inputs, labels_batch in data_loader:
            outputs = model(inputs)
            probs = torch.softmax(outputs, dim=1)[:, 1]
            _, preds = outputs.max(dim=1)
            all_labels.extend(labels_batch.tolist())
            all_preds.extend(preds.tolist())
            all_probs.extend(probs.tolist())

    acc = accuracy_score(all_labels, all_preds)
    f1 = f1_score(all_labels, all_preds, zero_division=0)
    try:
        auc_val = roc_auc_score(all_labels, all_probs)
    except ValueError:
        auc_val = float('nan')
    return {"accuracy": acc, "auc": auc_val, "f1": f1,
            "labels": all_labels, "preds": all_preds, "probs": all_probs}


# ============================================================
# 3. Training (only runs when no checkpoint exists yet)
# ============================================================
def load_training_data(csv_path):
    df = pd.read_csv(csv_path, encoding=TRAIN_CSV_ENCODING)
    df = df.loc[:, ~df.columns.str.contains('^Unnamed')]
    df = df.drop(columns=[c for c in ['序列', '_Name'] if c in df.columns])

    df["mark"] = pd.to_numeric(df["mark"], errors='coerce')
    df = df.dropna()

    labels = df["mark"].astype(int).to_numpy()
    feature_df = df.drop(columns=["mark"])
    feature_columns = feature_df.columns.tolist()
    X = feature_df.to_numpy().astype(float)

    print(f"[Training data] feature shape={X.shape}, labels={labels.size}, "
          f"positive ratio={labels.mean():.2%}")
    return X, labels, feature_columns


def train_one_fold(fold_idx, train_idx, valid_idx, X, y, feature_columns):
    scaler = preprocessing.StandardScaler().fit(X[train_idx])
    X_train_scaled = scaler.transform(X[train_idx])
    X_valid_scaled = scaler.transform(X[valid_idx])

    train_inputs_t = torch.from_numpy(X_train_scaled).to(torch.float32)
    valid_inputs_t = torch.from_numpy(X_valid_scaled).to(torch.float32)
    train_labels_t = torch.from_numpy(y[train_idx]).to(torch.long)
    valid_labels_t = torch.from_numpy(y[valid_idx]).to(torch.long)

    train_loader = DataLoader(TensorDataset(train_inputs_t, train_labels_t),
                               batch_size=BATCH_SIZE, shuffle=True)
    valid_loader = DataLoader(TensorDataset(valid_inputs_t, valid_labels_t),
                               batch_size=BATCH_SIZE * 2)

    net = Net(X.shape[1], 2, HIDDEN1, HIDDEN2)
    optimizer = torch.optim.Adam(net.parameters(), lr=LR, weight_decay=WEIGHT_DECAY)
    loss_fn = torch.nn.CrossEntropyLoss()

    best_metric_value = -np.inf
    best_state = None
    best_val_metrics = None

    print(f"---- Fold {fold_idx}/{N_SPLITS} training "
          f"(train={len(train_idx)}, valid={len(valid_idx)}) ----")

    for i in range(EPOCHS):
        net.train()
        for inputs, labels_batch in train_loader:
            optimizer.zero_grad()
            outputs = net(inputs)
            loss = loss_fn(outputs, labels_batch)
            loss.backward()
            optimizer.step()

        valid_metrics = evaluate(net, valid_loader)
        current_value = valid_metrics[SELECTION_METRIC]

        if not np.isnan(current_value) and current_value > best_metric_value:
            best_metric_value = current_value
            best_val_metrics = valid_metrics
            best_state = {
                'fold': fold_idx, 'epoch': i + 1,
                'state_dict': net.state_dict(),
                'valid_accuracy': valid_metrics["accuracy"],
                'valid_auc': valid_metrics["auc"],
                'valid_f1': valid_metrics["f1"],
                'scaler_mean': scaler.mean_, 'scaler_scale': scaler.scale_,
                'hidden_layer1_neurons': HIDDEN1,
                'hidden_layer2_neurons': HIDDEN2,
                'feature_columns': feature_columns,
            }

    print(f"Fold {fold_idx} best: Acc={best_val_metrics['accuracy']*100:.2f}%, "
          f"AUC={best_val_metrics['auc']:.4f}, F1={best_val_metrics['f1']:.4f}")

    return best_state


def train_and_select_best_fold():
    X, y, feature_columns = load_training_data(TRAIN_CSV_PATH)

    skf = StratifiedKFold(n_splits=N_SPLITS, shuffle=True, random_state=RANDOM_STATE)
    fold_states = []
    for fold_idx, (train_idx, valid_idx) in enumerate(skf.split(X, y), start=1):
        fold_states.append(train_one_fold(fold_idx, train_idx, valid_idx, X, y, feature_columns))

    best_state = max(fold_states, key=lambda s: s['valid_auc'])
    print(f"\nSelected fold {best_state['fold']} as the best model "
          f"(validation AUC={best_state['valid_auc']:.4f})")

    os.makedirs(PT_DIR, exist_ok=True)
    torch.save(best_state, PT_PATH)
    print(f"Checkpoint saved to: {PT_PATH}")
    return best_state


# ============================================================
# 4. Prediction on the web-uploaded TAUModelSource.csv
# ============================================================
def drop_unused_protein_columns(df):
    lower_cols = {c.lower(): c for c in df.columns}
    unused_present = [
        lower_cols[pid] for pid in ALL_KNOWN_PROTEIN_IDS
        if pid not in BINDING_ENERGY_COLS and pid in lower_cols
    ]
    if unused_present:
        print(f"Excluding {len(unused_present)} binding-energy column(s) not used "
              f"by the TAU model: {unused_present}")
        df = df.drop(columns=unused_present)
    return df


def align_columns_case_insensitive(df, feature_columns):
    lower_to_actual = {c.lower(): c for c in df.columns}
    missing = []
    ordered_actual_cols = []
    for col in feature_columns:
        actual = lower_to_actual.get(col.lower())
        if actual is None:
            missing.append(col)
        else:
            ordered_actual_cols.append(actual)

    if missing:
        raise ValueError(
            "The uploaded CSV is missing columns required by the trained model: "
            + ", ".join(missing)
        )

    return df[ordered_actual_cols]


def predict():
    if not os.path.exists(PT_PATH):
        print("No checkpoint found -- training a new model first.")
        ckpt = train_and_select_best_fold()
    else:
        print(f"Loading existing checkpoint: {PT_PATH}")
        ckpt = torch.load(PT_PATH, weights_only=False)

    feature_columns = ckpt['feature_columns']

    # The production input file (uploaded via the web form) is UTF-8, unlike
    # the training CSV which is GBK.
    df = pd.read_csv(INPUT_CSV_PATH, encoding='utf-8')
    df = df.loc[:, ~df.columns.str.contains('^Unnamed')]

    if '_Name' not in df.columns:
        raise ValueError("The input CSV does not contain a '_Name' column.")

    df = drop_unused_protein_columns(df)
    feat_df = align_columns_case_insensitive(df, feature_columns).apply(pd.to_numeric, errors="coerce")

    valid_mask = feat_df.notna().all(axis=1)
    n_dropped = (~valid_mask).sum()
    if n_dropped > 0:
        print(f"Warning: {n_dropped}/{len(df)} rows had missing/invalid values and were skipped.")

    feat_df = feat_df.loc[valid_mask].reset_index(drop=True)
    names = df.loc[valid_mask, '_Name'].reset_index(drop=True)

    scaler = preprocessing.StandardScaler()
    scaler.mean_ = ckpt['scaler_mean']
    scaler.scale_ = ckpt['scaler_scale']
    scaler.var_ = ckpt['scaler_scale'] ** 2

    X_scaled = scaler.transform(feat_df.to_numpy().astype(float))
    X_t = torch.from_numpy(X_scaled).to(torch.float32)

    net = Net(X_t.shape[1], 2, ckpt['hidden_layer1_neurons'], ckpt['hidden_layer2_neurons'])
    net.load_state_dict(ckpt['state_dict'])
    net.eval()

    with torch.no_grad():
        probs = torch.softmax(net(X_t), dim=1)[:, 1].numpy()

    result_df = pd.DataFrame({
        "_Name": names,
        "Probability": probs,
    })

    os.makedirs(OUTPUT_DIR, exist_ok=True)
    if os.path.exists(OUTPUT_CSV_PATH):
        os.remove(OUTPUT_CSV_PATH)
    result_df.to_csv(OUTPUT_CSV_PATH, index=False, encoding='utf-8')
    print(f"Prediction results saved to: {OUTPUT_CSV_PATH}")
    print(f"Predicted {len(result_df)} molecule(s).")


if __name__ == "__main__":
    predict()
