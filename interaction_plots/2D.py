import os
import re
import base64
import shutil
import numpy as np
from io import BytesIO
from datetime import datetime

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import matplotlib.font_manager as fm
from matplotlib.lines import Line2D

from rdkit import Chem
from rdkit.Chem import AllChem
from rdkit import RDLogger
from rdkit.Chem import GetPeriodicTable

RDLogger.DisableLog('rdApp.*')

# ========== Chinese font setup (still needed for matplotlib internal) ==========
def setup_chinese_font():
    candidates = [
        r"C:\Windows\Fonts\msyh.ttc",
        r"C:\Windows\Fonts\simhei.ttf",
        r"C:\Windows\Fonts\simsun.ttc",
    ]
    for path in candidates:
        if os.path.exists(path):
            fm.fontManager.addfont(path)
            font_name = fm.FontProperties(fname=path).get_name()
            matplotlib.rcParams['font.sans-serif'] = [font_name] + matplotlib.rcParams['font.sans-serif']
            matplotlib.rcParams['axes.unicode_minus'] = False
            print(f"✅ Loaded Chinese font: {font_name}")
            return True
    print("⚠️ No Chinese font found, text may display as boxes")
    return False

setup_chinese_font()
# ===================================

# ========== Paths (modify as needed) ==========
PDB_DIR = r"D:\1博士期间所有的工作（开始建立是在2024年11月7日）\20260602sci论文重要\汇总数据库的全部的数据\Orhers\huting\Structure6YEJ\6YEJ_LIG"
PROTEIN_DIR = r"D:\1博士期间所有的工作（开始建立是在2024年11月7日）\20260602sci论文重要\汇总数据库的全部的数据\Orhers\huting\Structure6YEJ\6YEJ_Protein"
OUTPUT_DIR = r"D:\1博士期间所有的工作（开始建立是在2024年11月7日）\20260602sci论文重要\汇总数据库的全部的数据\Orhers\huting\lig_plot_6YEJ"
FAIL_LOG_FILE = os.path.join(OUTPUT_DIR, "fail_log.txt")
PROGRESS_EVERY = 100
CLEAN_OUTPUT_BEFORE_RUN = True
# =============================================

HBOND_CUTOFF = 3.5
HYDROPHOBIC_CUTOFF = 4.5
MAX_HYDROPHOBIC_PER_RESIDUE = 3

STANDARD_AMINO_ACIDS = {
    'ALA','ARG','ASN','ASP','CYS','GLN','GLU','GLY','HIS','ILE',
    'LEU','LYS','MET','PHE','PRO','SER','THR','TRP','TYR','VAL',
    'MSE','SEC','PYL','HSD','HSE','HSP','CYX','HID','HIE','HIP',
}
HYDROPHOBIC_RESNAMES = {'ALA','VAL','LEU','ILE','PRO','PHE','MET','TRP','CYS'}
BACKBONE_ATOMS = {'N', 'CA', 'C', 'O', 'OXT'}

RESIDUE_COLOR = {}
for r in ['ALA','VAL','LEU','ILE','MET','PHE','TRP','PRO','GLY']:
    RESIDUE_COLOR[r] = "#8FCB9B"
for r in ['SER','THR','ASN','GLN','CYS','TYR']:
    RESIDUE_COLOR[r] = "#8FB6D9"
for r in ['ASP','GLU']:
    RESIDUE_COLOR[r] = "#E58F8F"
for r in ['LYS','ARG','HIS']:
    RESIDUE_COLOR[r] = "#E5C98F"
DEFAULT_RES_COLOR = "#CCCCCC"

# Interaction styles – English labels
INTERACTION_STYLE = {
    "hbond":       dict(color="#1B7A1B", ls=(0, (4, 2)), label="H-bond (≤3.5 Å)"),
    "hydrophobic": dict(color="#CC5500", ls="-",          label="Hydrophobic (≤4.5 Å)"),
}

ATOM_COLOR = {
    "N": "#1155CC", "O": "#CC0000", "S": "#B8860B", "P": "#CC7A00",
    "F": "#00994D", "CL": "#00994D", "BR": "#7A3300", "I": "#5C0099",
}

AUTODOCK_ELEMENT_MAP = {
    'A': 'C',
    'NA': 'N',
    'NS': 'N',
    'OA': 'O',
    'OS': 'O',
    'SA': 'S',
    'HD': 'H',
    'HS': 'H',
    'W': 'O',
}

_PT = GetPeriodicTable()

def is_valid_element(sym):
    try:
        _PT.GetAtomicNumber(sym)
        return True
    except Exception:
        return False

def normalize_element(raw_field, atom_name=""):
    field = (raw_field or "").strip().upper()
    if field in AUTODOCK_ELEMENT_MAP:
        return AUTODOCK_ELEMENT_MAP[field]
    if field and is_valid_element(field.capitalize()):
        return field.capitalize()
    guessed = guess_element_simple(atom_name)
    guessed_upper = guessed.upper()
    if guessed_upper in AUTODOCK_ELEMENT_MAP:
        return AUTODOCK_ELEMENT_MAP[guessed_upper]
    if is_valid_element(guessed):
        return guessed
    return "C"

def guess_element_simple(atom_name):
    name = re.sub(r'^[0-9]+', '', atom_name.strip().upper())
    two_letter = ['CL','BR','NA','MG','CA','ZN','FE','MN','CU','NI','CO',
                  'SE','SI','AL','LI','AS']
    for e in two_letter:
        if name.startswith(e):
            return e.capitalize()
    return name[0] if name else 'C'

def parse_pdb_atoms(path, exclude_water=True):
    atoms = []
    with open(path, 'r', encoding='utf-8', errors='ignore') as f:
        for line in f:
            if not line.startswith(('ATOM', 'HETATM')):
                continue
            atom_name = line[12:16].strip()
            resname = line[17:20].strip()
            chain = line[21].strip() or 'A'
            if exclude_water and resname in ('HOH', 'WAT', 'H2O'):
                continue
            try:
                resnum = int(line[22:26])
            except ValueError:
                resnum = 0
            try:
                x = float(line[30:38]); y = float(line[38:46]); z = float(line[46:54])
            except ValueError:
                parts = line.split()
                x, y, z = float(parts[-6]), float(parts[-5]), float(parts[-4])
            elem_field = line[76:78].strip() if len(line) >= 78 else ''
            element = normalize_element(elem_field, atom_name)
            atoms.append({
                'name': atom_name, 'resname': resname, 'chain': chain,
                'resnum': resnum, 'x': x, 'y': y, 'z': z,
                'element': element,
                'is_backbone': atom_name in BACKBONE_ATOMS,
            })
    return atoms

def load_protein_once(protein_file):
    atoms = parse_pdb_atoms(protein_file, exclude_water=True)
    atoms = [a for a in atoms if a['resname'] in STANDARD_AMINO_ACIDS]
    coords = np.array([[a['x'], a['y'], a['z']] for a in atoms])
    print(f"Protein parsed: {len(atoms)} standard residue atoms")
    return coords, atoms

def sanitize_pdb_block_for_rdkit(path):
    fixed_lines = []
    with open(path, 'r', encoding='utf-8', errors='ignore') as f:
        for line in f:
            if line.startswith(('ATOM', 'HETATM')):
                line = line.rstrip('\n')
                if len(line) < 80:
                    line = line.ljust(80)
                atom_name = line[12:16].strip()
                elem_field = line[76:78].strip()
                real_elem = normalize_element(elem_field, atom_name)
                new_elem_field = real_elem.rjust(2)
                line = line[:76] + new_elem_field + line[78:]
                fixed_lines.append(line)
            else:
                fixed_lines.append(line.rstrip('\n'))
    return "\n".join(fixed_lines) + "\n"

def detect_interactions(protein_coords, protein_atoms, ligand_atoms):
    interactions = []
    hydrophobic_pool = {}
    for li, la in enumerate(ligand_atoms):
        lig_xyz = np.array([la['x'], la['y'], la['z']])
        elem = la['element'].upper()
        dists = np.linalg.norm(protein_coords - lig_xyz, axis=1)
        if elem in ('N', 'O'):
            idxs = np.where(dists <= HBOND_CUTOFF)[0]
            for pi in idxs:
                pa = protein_atoms[pi]
                if pa['element'].upper() not in ('N', 'O'):
                    continue
                interactions.append({
                    "restype": pa['resname'], "resnr": pa['resnum'], "reschain": pa['chain'],
                    "type": "hbond",
                    "distance": round(float(dists[pi]), 2),
                    "lig_xyz": lig_xyz,
                })
        elif elem == 'C':
            idxs = np.where(dists <= HYDROPHOBIC_CUTOFF)[0]
            for pi in idxs:
                pa = protein_atoms[pi]
                if pa['element'].upper() != 'C':
                    continue
                if pa['is_backbone']:
                    continue
                if pa['resname'] not in HYDROPHOBIC_RESNAMES:
                    continue
                res_key = f"{pa['resname']}{pa['resnum']}{pa['chain']}"
                hydrophobic_pool.setdefault(res_key, []).append({
                    "restype": pa['resname'], "resnr": pa['resnum'], "reschain": pa['chain'],
                    "type": "hydrophobic",
                    "distance": round(float(dists[pi]), 2),
                    "lig_xyz": lig_xyz,
                })
    for res_key, items in hydrophobic_pool.items():
        items_sorted = sorted(items, key=lambda x: x['distance'])
        interactions.extend(items_sorted[:MAX_HYDROPHOBIC_PER_RESIDUE])
    return interactions

def load_ligand_rdkit(ligand_file):
    sanitized_text = sanitize_pdb_block_for_rdkit(ligand_file)
    mol = Chem.MolFromPDBBlock(sanitized_text, removeHs=False, sanitize=False)
    if mol is None or mol.GetNumAtoms() == 0:
        raise RuntimeError("RDKit cannot parse the ligand file (even after element correction)")
    try:
        Chem.SanitizeMol(mol, sanitizeOps=Chem.SANITIZE_ALL ^ Chem.SANITIZE_KEKULIZE)
    except Exception:
        pass
    return mol

def match_atom_by_3d(conf, target_xyz):
    best_idx, best_dist = -1, 1e9
    for i in range(conf.GetNumAtoms()):
        p = conf.GetAtomPosition(i)
        d = (p.x - target_xyz[0])**2 + (p.y - target_xyz[1])**2 + (p.z - target_xyz[2])**2
        if d < best_dist:
            best_dist, best_idx = d, i
    return best_idx

def compute_2d_layout(mol):
    mol2d = Chem.Mol(mol)
    AllChem.Compute2DCoords(mol2d)
    conf = mol2d.GetConformer()
    pos = {i: (conf.GetAtomPosition(i).x, conf.GetAtomPosition(i).y)
           for i in range(mol2d.GetNumAtoms())}
    bonds = [(b.GetBeginAtomIdx(), b.GetEndAtomIdx(), b.GetBondTypeAsDouble())
             for b in mol2d.GetBonds()]
    symbols = {i: mol2d.GetAtomWithIdx(i).GetSymbol() for i in range(mol2d.GetNumAtoms())}
    return pos, bonds, symbols

def draw_ligplot(pos, bonds, symbols, interactions, title, out_buf):
    fig, ax = plt.subplots(figsize=(8, 6.5), dpi=100)
    ax.set_aspect("equal")
    ax.axis("off")

    xs = [p[0] for p in pos.values()]
    ys = [p[1] for p in pos.values()]
    cx, cy = float(np.mean(xs)), float(np.mean(ys))
    extent = max(max(xs) - min(xs), max(ys) - min(ys), 1.0)

    for i, j, order in bonds:
        x1, y1 = pos[i]; x2, y2 = pos[j]
        if order >= 2.0:
            dx, dy = x2 - x1, y2 - y1
            length = (dx**2 + dy**2) ** 0.5 or 1e-6
            perp = (-dy / length * 0.06, dx / length * 0.06)
            ax.plot([x1+perp[0], x2+perp[0]], [y1+perp[1], y2+perp[1]], color="black", lw=1.4, zorder=1)
            ax.plot([x1-perp[0], x2-perp[0]], [y1-perp[1], y2-perp[1]], color="black", lw=1.4, zorder=1)
        else:
            ax.plot([x1, x2], [y1, y2], color="black", lw=1.4, zorder=1)

    for i, (x, y) in pos.items():
        sym = symbols[i]
        if sym != "C":
            color = ATOM_COLOR.get(sym.upper(), "#444444")
            ax.text(x, y, sym, fontsize=11, fontweight="bold", color=color,
                     ha="center", va="center", zorder=3,
                     bbox=dict(boxstyle="round,pad=0.08", fc="white", ec="none"))

    grouped = {}
    for it in interactions:
        idx = it.get("atom_idx")
        if idx is None or idx not in pos:
            continue
        res_key = f"{it['restype']}{it['resnr']}{it['reschain']}"
        grouped.setdefault(res_key, {"restype": it["restype"], "items": []})
        grouped[res_key]["items"].append(it)

    if not grouped:
        plt.close(fig)
        raise RuntimeError("No interactions detected, skipping plot")

    residue_angle = {}
    for res_key, info in grouped.items():
        angles = [np.arctan2(pos[it["atom_idx"]][1]-cy, pos[it["atom_idx"]][0]-cx) for it in info["items"]]
        residue_angle[res_key] = np.mean(angles)

    sorted_keys = sorted(residue_angle.keys(), key=lambda k: residue_angle[k])
    n_res = len(sorted_keys)
    if n_res > 0:
        min_gap = 2 * np.pi / max(n_res, 1) * 0.9
        for idx2 in range(1, n_res):
            prev = residue_angle[sorted_keys[idx2-1]]
            cur = residue_angle[sorted_keys[idx2]]
            if cur - prev < min_gap:
                residue_angle[sorted_keys[idx2]] = prev + min_gap

    radius = extent * 1.55 + 1.0
    used_types = set()

    for res_key, info in grouped.items():
        angle = residue_angle[res_key]
        bx, by = cx + radius*np.cos(angle), cy + radius*np.sin(angle)
        restype = info["restype"]
        text_color = RESIDUE_COLOR.get(restype.upper(), DEFAULT_RES_COLOR)

        # ---------- Draw residue label as plain text (no box) ----------
        ax.text(bx, by, res_key, fontsize=10, fontweight="bold", ha="center", va="center",
                color=text_color, zorder=4)

        for it in info["items"]:
            style = INTERACTION_STYLE.get(it["type"], INTERACTION_STYLE["hydrophobic"])
            used_types.add(it["type"])
            ax_, ay_ = pos[it["atom_idx"]]
            ax.plot([bx, ax_], [by, ay_], color=style["color"], linestyle=style["ls"],
                     linewidth=1.3, zorder=2, alpha=0.85)
            if it.get("distance"):
                mx, my = (bx+ax_)/2, (by+ay_)/2
                ax.text(mx, my, f"{it['distance']}", fontsize=7, color=style["color"],
                         ha="center", va="center", zorder=6,
                         bbox=dict(boxstyle="round,pad=0.05", fc="white", ec="none", alpha=0.8))

    legend_handles = [Line2D([0],[0], color=INTERACTION_STYLE[k]["color"], linestyle=INTERACTION_STYLE[k]["ls"],
                              lw=1.6, label=INTERACTION_STYLE[k]["label"]) for k in used_types]
    if legend_handles:
        ax.legend(handles=legend_handles, loc="lower center", bbox_to_anchor=(0.5, -0.06),
                  ncol=len(legend_handles), frameon=False, fontsize=9)

    ax.set_title(title, fontsize=13, fontweight="bold", pad=14)
    margin = radius + 1.5
    ax.set_xlim(cx-margin, cx+margin)
    ax.set_ylim(cy-margin, cy+margin)

    plt.tight_layout()
    plt.savefig(out_buf, format="png", dpi=100, bbox_inches="tight")
    plt.close(fig)

# --------------------------- HTML packaging (all English) ---------------------------
HTML_TEMPLATE = """<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><title>{title}</title>
<style>
body {{ font-family:"Microsoft YaHei",Arial,sans-serif; background:#f5f5f5; margin:0; padding:20px; }}
.container {{ max-width:1100px; margin:0 auto; background:#fff; border-radius:10px;
              box-shadow:0 2px 10px rgba(0,0,0,0.1); padding:24px; }}
h1 {{ font-size:20px; border-bottom:2px solid #eee; padding-bottom:10px; }}
img {{ width:100%; height:auto; border:1px solid #ddd; border-radius:6px; margin-top:10px; }}
table {{ width:100%; border-collapse:collapse; margin-top:20px; font-size:13px; }}
th, td {{ border:1px solid #e0e0e0; padding:6px 10px; text-align:center; }}
th {{ background:#f0f4f8; }}
tr:nth-child(even) {{ background:#fafafa; }}
</style></head><body>
<div class="container">
  <h1>{title} — Protein-Ligand Interaction Diagram (H-bonds & Hydrophobic Contacts)</h1>
  <img src="data:image/png;base64,{img_b64}">
  <h2 style="font-size:15px;margin-top:24px;">Interaction Details</h2>
  <table><tr><th>Residue</th><th>Interaction Type</th><th>Distance (Å)</th></tr>{table_rows}</table>
</div></body></html>"""

TYPE_EN = {"hbond": "H-bond", "hydrophobic": "Hydrophobic"}

def build_html(title, png_bytes, interactions):
    img_b64 = base64.b64encode(png_bytes).decode("utf-8")
    rows = []
    for it in sorted(interactions, key=lambda x: (x["restype"], x["resnr"])):
        res_label = f"{it['restype']}{it['resnr']}{it['reschain']}"
        rows.append(f"<tr><td>{res_label}</td><td>{TYPE_EN.get(it['type'], it['type'])}</td>"
                     f"<td>{it.get('distance','-')}</td></tr>")
    table_rows = "\n".join(rows) if rows else "<tr><td colspan='3'>No interactions detected</td></tr>"
    return HTML_TEMPLATE.format(title=title, img_b64=img_b64, table_rows=table_rows)

def process_one_ligand(protein_coords, protein_atoms, ligand_file, output_path):
    lig_name = os.path.splitext(os.path.basename(ligand_file))[0]
    ligand_atoms = parse_pdb_atoms(ligand_file, exclude_water=True)
    if not ligand_atoms:
        raise RuntimeError("No valid atoms in ligand file")
    interactions = detect_interactions(protein_coords, protein_atoms, ligand_atoms)
    if not interactions:
        raise RuntimeError("No hydrogen bonds or hydrophobic contacts detected")
    mol3d = load_ligand_rdkit(ligand_file)
    conf3d = mol3d.GetConformer()
    for it in interactions:
        it["atom_idx"] = match_atom_by_3d(conf3d, it["lig_xyz"])
    pos, bonds, symbols = compute_2d_layout(mol3d)
    buf = BytesIO()
    draw_ligplot(pos, bonds, symbols, interactions, lig_name, buf)
    html = build_html(lig_name, buf.getvalue(), interactions)
    with open(output_path, "w", encoding="utf-8") as f:
        f.write(html)

def find_pdb_files(directory):
    if not os.path.exists(directory):
        return []
    return [os.path.join(directory, f) for f in os.listdir(directory) if f.lower().endswith(".pdb")]

def main():
    start_time = datetime.now()
    print("=" * 70)
    print("Scheme C (revised v2): Chinese font + residue box overlap fixed")
    print("=" * 70)

    if CLEAN_OUTPUT_BEFORE_RUN and os.path.exists(OUTPUT_DIR):
        print(f"Cleaning output directory: {OUTPUT_DIR}")
        shutil.rmtree(OUTPUT_DIR, ignore_errors=True)

    os.makedirs(OUTPUT_DIR, exist_ok=True)

    ligand_files = find_pdb_files(PDB_DIR)
    protein_files = find_pdb_files(PROTEIN_DIR)
    print(f"Found {len(ligand_files)} ligand files, {len(protein_files)} protein files")

    if not ligand_files or not protein_files:
        print("❌ No files found, check paths!")
        return

    protein_coords, protein_atoms = load_protein_once(protein_files[0])

    print("\nTesting with first ligand...")
    test_lig = sorted(ligand_files)[0]
    test_out = os.path.join(OUTPUT_DIR, "__test__.html")
    try:
        process_one_ligand(protein_coords, protein_atoms, test_lig, test_out)
        print("✅ Test successful. Starting batch processing.\n")
        os.remove(test_out)
    except Exception as e:
        print(f"❌ Test failed: {e}")
        return

    total = len(ligand_files)
    success, fail, skipped = 0, 0, 0
    fail_log = open(FAIL_LOG_FILE, "w", encoding="utf-8")

    print(f"Total ligands to process: {total}\n")
    print("=" * 70)

    for idx, lig_file in enumerate(sorted(ligand_files), 1):
        lig_name = os.path.splitext(os.path.basename(lig_file))[0]
        output_path = os.path.join(OUTPUT_DIR, f"{lig_name}.html")

        if os.path.exists(output_path):
            skipped += 1
            continue

        try:
            process_one_ligand(protein_coords, protein_atoms, lig_file, output_path)
            success += 1
        except Exception as e:
            fail += 1
            fail_log.write(f"{lig_name}\t{str(e)[:300]}\n")
            fail_log.flush()

        if idx % PROGRESS_EVERY == 0 or idx == total:
            elapsed = (datetime.now() - start_time).total_seconds()
            rate = idx / elapsed if elapsed > 0 else 0
            eta = (total - idx) / rate if rate > 0 else 0
            print(f"[{idx}/{total}] Success:{success} Fail:{fail} Skipped:{skipped} "
                  f"| Speed:{rate:.1f}/s | ETA:{eta/60:.1f} min")

    fail_log.close()
    elapsed_total = (datetime.now() - start_time).total_seconds()
    print(f"\nFinished! Time:{elapsed_total/60:.1f} min | Success:{success} Fail:{fail} Skipped:{skipped}")
    print(f"Fail log: {FAIL_LOG_FILE}")
    print(f"Output: {OUTPUT_DIR}")

if __name__ == "__main__":
    main()
