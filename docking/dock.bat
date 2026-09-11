@echo off
setlocal enabledelayedexpansion

rem ==== This variable must match the name of the current folder; simply amend this line in the respective dock.bat file for each target====
set PROTEIN_ID=1AML
set RESULTS_FILE=DockingResults.txt
set TEMP_RESULTS_FILE=DockingResults.tmp.txt

rem Each time a new docking run is initiated: first delete the old ‘final’ results file and any temporary files that may remain.
rem This ensures that, whilst checking the completion status during the current docking run, DockingResults.txt does not exist,
rem and will not be mistakenly identified by the web page as ‘completed’ (to prevent old results from obscuring the new docking run currently in progress).
if exist "%RESULTS_FILE%" del "%RESULTS_FILE%"
if exist "%TEMP_RESULTS_FILE%" del "%TEMP_RESULTS_FILE%"

> "%TEMP_RESULTS_FILE%" echo Docking results summary for %PROTEIN_ID%

for %%f in (*.pdbqt) do (
	rem ---- Skip the receptor itself (e.g. 1AML.pdbqt) to avoid meaningless results such as ‘the protein docking with itself’ ----
	if /I not "%%f"=="%PROTEIN_ID%.pdbqt" (
		echo Processing ligand %%f
		if not exist "%%~nf" mkdir "%%~nf"
		vina1AML --config conf.txt --ligand %%f --out "%%~nf"/"%%~nf".pdb --log "%%~nf"/"%%~nf".txt

		rem ---- Extract the affinity values for mode 1 (lowest energy/best result) from the log file ----
		set "BEST_AFFINITY="
		set "FOUND_HEADER=0"
		for /f "usebackq delims=" %%L in ("%%~nf\%%~nf.txt") do (
			if "!FOUND_HEADER!"=="1" (
				if not defined BEST_AFFINITY (
					for /f "tokens=1,2" %%A in ("%%L") do set "BEST_AFFINITY=%%B"
				)
			)
			echo.%%L | findstr /b /c:"-----" >nul && set "FOUND_HEADER=1"
		)

		if defined BEST_AFFINITY (
			echo The docking energy between %%~nf and %PROTEIN_ID% is !BEST_AFFINITY! kcal/mol>>"%TEMP_RESULTS_FILE%"
		) else (
			echo The docking energy between %%~nf and %PROTEIN_ID% could not be determined>>"%TEMP_RESULTS_FILE%"
		)
	)
)

rem ---- Once all ligands have been processed, convert the temporary file into the official DockingResults.txt ----
rem Only once this step has been completed will the completion status query on the web page return ‘true’
ren "%TEMP_RESULTS_FILE%" "%RESULTS_FILE%"

echo Done. Results written to %RESULTS_FILE%