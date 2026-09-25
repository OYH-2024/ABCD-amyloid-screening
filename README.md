# ABCD: Amyloid Binding Compounds Database — Source Code

This repository contains the source code, database dump, and prediction
models supporting the manuscript *"[paper title here — leave as "manuscript
under review" while the submission is pending]"* submitted to
*Journal of Cheminformatics*.

## Repository structure

- `docking/` — Molecular docking pipeline (AutoDock Vina).
  - `dock.bat` — Main docking execution script.
  - `configs/` — 16 docking box parameter files (`conf.txt`), one per
    target structure.
  - `receptors/` — Receptor protein structures (`.pdbqt`) used as docking
    targets.
- `descriptors/` — Molecular descriptor calculation.
  - `testrd.py` — Calculates 208 RDKit molecular descriptors from an
    input `.smi` file.
- `interaction_plots/` — Protein-ligand interaction visualization.
  - `2D.py` — Detects hydrogen-bond and hydrophobic contacts between a
    docked ligand and its target protein, and renders an interaction
    diagram (packaged as HTML with an embedded PNG).
- `prediction/` — Trained binding-prediction models, one per target
  protein/disease.
  - `AD.py` — Alzheimer's disease amyloid-beta model (12 target
    structures: 1z0q, 6rhy, 6ti7, 1aml, 1ba4, 1iyt, 2beg, 2m9r, 2nao,
    5oqv, 6oc9, 6shs).
  - `Huntingtin.py` — Huntingtin model (1 target structure: 6yej).
  - `TAU.py` — Tau model (1 target structure: 7sp1).
  - `TUHe.py` — Alpha-synuclein model (2 target structures: 1xq8, 6a6b).
  - Each script trains a 5-fold cross-validated neural network on first
    run (selecting the best fold by validation AUC) and reuses the saved
    checkpoint on subsequent runs.
  - `training_data/` — Training CSVs used to train each disease-specific
    model (each script re-runs training automatically if no checkpoint
    is present). Note: the Tau training CSV
    (`7sp1全部分子描述符9743训练集.csv`) is GBK-encoded rather than UTF-8.
- `database/dump/` — MySQL database dump.
  - `dbinfor.sql` — Docking ranking information for screened molecules.
  - `dbtest.sql` — Binding-energy and related data for screened
    molecules.
- `webapp/source/` — Source code for the ABCD web platform (Java,
  Thymeleaf templates). Includes `src/`, third-party libraries (`lib/`,
  `lib_thymeleaf/`), and the essential contents of `WEB-INF/`
  (`web.xml`, `templates/`) plus the core interface pages. Large
  auto-generated per-molecule pages (already viewable on the live
  database site) are not duplicated here.
- `example_data/` — Example input files.
  - `example.pdbqt`, `example.smi` — Sample docking/descriptor inputs.

## Requirements

See `requirements.txt`. Install with:

```bash
pip install -r requirements.txt
```

## Notes on file paths

Scripts in this repository contain absolute file paths corresponding to
the original deployment server's directory structure (e.g.
`E:\DataBase\WebSrever\Model\...`). To run a script locally, update the
path variables near the top of the file to match your own environment.

## Live database

The ABCD database and its online tools (docking, descriptor
calculation, binding prediction) are publicly accessible at:
http://39.99.245.16:8080/ABCDDB/ABCDDB.html

## Citation

If you use this code or database, please cite:
[full citation to be added upon acceptance; manuscript currently under review]
