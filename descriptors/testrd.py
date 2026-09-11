import os
import glob
from rdkit import Chem
from rdkit.Chem import Descriptors
from rdkit.ML.Descriptors import MoleculeDescriptors
import pandas as pd

# ============ Path configuration (dedicated molecular descriptor calculation directories) ============
CODE_DIR = r'E:\DataBase\WebSrever\MoleculeDescription\code'
INPUT_DIR = r'E:\DataBase\WebSrever\MoleculeDescription\input'
OUTPUT_DIR = r'E:\DataBase\WebSrever\MoleculeDescription\output'

# Reference CSV file (provides the list of 208 descriptor columns), stored alongside the script in CODE_DIR
reference_file = os.path.join(CODE_DIR, 'AD_reference_36.csv')

# Output result file, fixed name RES.csv
output_file = os.path.join(OUTPUT_DIR, 'RES.csv')
# ========================================================================================================

# Make sure the output directory exists
os.makedirs(OUTPUT_DIR, exist_ok=True)

# First read the reference CSV to get the 208 descriptor column names
ref_df = pd.read_csv(reference_file)
column_names = ref_df.columns.tolist()
# Exclude the first two columns and the last twelve columns, keep the middle 208 as the descriptor framework
target_descriptors = column_names[2:-12]
print(f"Using {len(target_descriptors)} molecular descriptors as the framework")

# Dynamically look up the .smi file in the input directory instead of hardcoding a filename
# (the upload servlet clears out old .smi files first, so normally there is exactly one file here)
smi_candidates = glob.glob(os.path.join(INPUT_DIR, '*.smi'))
if not smi_candidates:
    raise FileNotFoundError(f"No .smi file found in {INPUT_DIR}. Please upload a file before calculating.")

# If more than one somehow exists, use the most recently modified one
smi_file = max(smi_candidates, key=os.path.getmtime)
print(f"Using SMI file: {smi_file}")

# Lists to store molecule objects and SMILES strings
d4 = []
smiles_list = []

# Read the SMILES file.
# Relaxed format: each non-empty line just needs to start with a SMILES string.
# - "SMILES NAME"  -> SMILES is used for the molecule, NAME is used as _Name
# - "SMILES"       -> SMILES is used for both the molecule AND the _Name column
# Multiple molecules, one per line, are both supported.
with open(smi_file, 'r') as file:
    for index, line in enumerate(file):
        line = line.strip()
        if not line:
            # Skip blank lines silently
            continue

        line_elements = line.split()
        smiles = line_elements[0]
        # If a second column (name) is provided, use it; otherwise fall back to the SMILES itself
        mol_name = line_elements[1] if len(line_elements) >= 2 else smiles

        print(f"Processing SMILES: {smiles}")
        mol = Chem.MolFromSmiles(smiles)
        if mol is not None:
            mol.SetProp('_Name', mol_name)
            d4.append(mol)
            smiles_list.append(smiles)
        else:
            print(f"Warning: Unable to create molecule from SMILES in line {index + 1}: '{smiles}'. Skipping...")

if not d4:
    raise ValueError("No valid molecules were parsed. Please check the contents of the SMI file.")

# Build the descriptor calculator using only our 208 target descriptors
available_descriptors = [x[0] for x in Descriptors._descList]
valid_descriptors = [desc for desc in target_descriptors if desc in available_descriptors]
if len(valid_descriptors) != len(target_descriptors):
    print(f"Warning: {len(target_descriptors) - len(valid_descriptors)} descriptors are not available in the current RDKit version")

descriptor_calculator = MoleculeDescriptors.MolecularDescriptorCalculator(valid_descriptors)
descriptors4 = [descriptor_calculator.CalcDescriptors(mol) for mol in d4]

# Convert the descriptor list into a DataFrame
list4 = pd.DataFrame(descriptors4, columns=valid_descriptors)

# Molecule name column (SMILES itself when no separate name was given)
y_name = '_Name'
y = pd.DataFrame([mol.GetProp(y_name) for mol in d4])
y.columns = [y_name]

# Order column is just the row sequence number: 1, 2, 3, ...
order_df = pd.DataFrame(list(range(1, len(smiles_list) + 1)), columns=['Order'])

# Merge molecule name, sequence number and descriptors
dt4 = pd.concat([y, order_df, list4], axis=1)
dt4.index = dt4.index + 1

# Remove any existing RES.csv before writing the new one
if os.path.exists(output_file):
    print(f"Removing existing file: {output_file}")
    os.remove(output_file)

dt4.to_csv(output_file, encoding='utf-8', sep=',', index=False)
print(f"File saved successfully: {output_file}")
print(f"Generated {len(dt4.columns) - 2} descriptors in total (plus the _Name and Order columns)")