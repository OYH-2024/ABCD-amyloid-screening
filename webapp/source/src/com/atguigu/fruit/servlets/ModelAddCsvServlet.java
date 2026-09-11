package com.atguigu.fruit.servlets;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@WebServlet("/AddModelFileUpload")
public class ModelAddCsvServlet extends HttpServlet {

    // Directory where the uploaded (and protein-column-enriched) CSV is stored,
    // ready to be read by the prediction scripts in the code directory.
    private static final String UPLOAD_DIR = "E:/DataBase/WebSrever/Model/input/";

    // ------------------------------------------------------------------
    // Registry of disease models. Each is now submitted through its own
    // dedicated <form> on ModelAdd.jsp, tagged with a hidden "diseaseType"
    // field -- so the servlet no longer has to guess which disease a
    // submission belongs to, it just looks it up here. Add new entries as
    // more disease models come online.
    // ------------------------------------------------------------------
    private static final Map<String, String[]> DISEASE_REQUIRED_PROTEINS = new LinkedHashMap<>();
    private static final Map<String, String> DISEASE_OUTPUT_FILENAMES = new LinkedHashMap<>();
    static {
        DISEASE_REQUIRED_PROTEINS.put("AD", new String[]{
                "1Z0Q", "6RHY", "6TI7", "1AML", "1BA4", "1IYT",
                "2BEG", "2M9R", "2NAO", "5OQV", "6OC9", "6SHS"
        });
        DISEASE_OUTPUT_FILENAMES.put("AD", "ADModelSource.csv");

        DISEASE_REQUIRED_PROTEINS.put("TUHe", new String[]{"1XQ8", "6A6B"});
        DISEASE_OUTPUT_FILENAMES.put("TUHe", "TUHeModelSource.csv");

        DISEASE_REQUIRED_PROTEINS.put("TAU", new String[]{"7SP1"});
        DISEASE_OUTPUT_FILENAMES.put("TAU", "TAUModelSource.csv");

        DISEASE_REQUIRED_PROTEINS.put("Huntingtin", new String[]{"6YEJ"});
        DISEASE_OUTPUT_FILENAMES.put("Huntingtin", "HuntingtinModelSource.csv");
    }

    // Expected CSV header, taken from the molecular descriptor calculation output template (RES.csv).
    private static final String EXPECTED_HEADER_RAW =
            "_Name,Order,MaxEStateIndex,MinEStateIndex,MaxAbsEStateIndex,MinAbsEStateIndex,qed,MolWt,HeavyAtomMolWt,ExactMolWt,NumValenceElectrons,NumRadicalElectrons,MaxPartialCharge,MinPartialCharge,MaxAbsPartialCharge,MinAbsPartialCharge,FpDensityMorgan1,FpDensityMorgan2,FpDensityMorgan3,BCUT2D_MWHI,BCUT2D_MWLOW,BCUT2D_CHGHI,BCUT2D_CHGLO,BCUT2D_LOGPHI,BCUT2D_LOGPLOW,BCUT2D_MRHI,BCUT2D_MRLOW,BalabanJ,BertzCT,Chi0,Chi0n,Chi0v,Chi1,Chi1n,Chi1v,Chi2n,Chi2v,Chi3n,Chi3v,Chi4n,Chi4v,HallKierAlpha,Ipc,Kappa1,Kappa2,Kappa3,LabuteASA,PEOE_VSA1,PEOE_VSA10,PEOE_VSA11,PEOE_VSA12,PEOE_VSA13,PEOE_VSA14,PEOE_VSA2,PEOE_VSA3,PEOE_VSA4,PEOE_VSA5,PEOE_VSA6,PEOE_VSA7,PEOE_VSA8,PEOE_VSA9,SMR_VSA1,SMR_VSA10,SMR_VSA2,SMR_VSA3,SMR_VSA4,SMR_VSA5,SMR_VSA6,SMR_VSA7,SMR_VSA8,SMR_VSA9,SlogP_VSA1,SlogP_VSA10,SlogP_VSA11,SlogP_VSA12,SlogP_VSA2,SlogP_VSA3,SlogP_VSA4,SlogP_VSA5,SlogP_VSA6,SlogP_VSA7,SlogP_VSA8,SlogP_VSA9,TPSA,EState_VSA1,EState_VSA10,EState_VSA11,EState_VSA2,EState_VSA3,EState_VSA4,EState_VSA5,EState_VSA6,EState_VSA7,EState_VSA8,EState_VSA9,VSA_EState1,VSA_EState10,VSA_EState2,VSA_EState3,VSA_EState4,VSA_EState5,VSA_EState6,VSA_EState7,VSA_EState8,VSA_EState9,FractionCSP3,HeavyAtomCount,NHOHCount,NOCount,NumAliphaticCarbocycles,NumAliphaticHeterocycles,NumAliphaticRings,NumAromaticCarbocycles,NumAromaticHeterocycles,NumAromaticRings,NumHAcceptors,NumHDonors,NumHeteroatoms,NumRotatableBonds,NumSaturatedCarbocycles,NumSaturatedHeterocycles,NumSaturatedRings,RingCount,MolLogP,MolMR,fr_Al_COO,fr_Al_OH,fr_Al_OH_noTert,fr_ArN,fr_Ar_COO,fr_Ar_N,fr_Ar_NH,fr_Ar_OH,fr_COO,fr_COO2,fr_C_O,fr_C_O_noCOO,fr_C_S,fr_HOCCN,fr_Imine,fr_NH0,fr_NH1,fr_NH2,fr_N_O,fr_Ndealkylation1,fr_Ndealkylation2,fr_Nhpyrrole,fr_SH,fr_aldehyde,fr_alkyl_carbamate,fr_alkyl_halide,fr_allylic_oxid,fr_amide,fr_amidine,fr_aniline,fr_aryl_methyl,fr_azide,fr_azo,fr_barbitur,fr_benzene,fr_benzodiazepine,fr_bicyclic,fr_diazo,fr_dihydropyridine,fr_epoxide,fr_ester,fr_ether,fr_furan,fr_guanido,fr_halogen,fr_hdrzine,fr_hdrzone,fr_imidazole,fr_imide,fr_isocyan,fr_isothiocyan,fr_ketone,fr_ketone_Topliss,fr_lactam,fr_lactone,fr_methoxy,fr_morpholine,fr_nitrile,fr_nitro,fr_nitro_arom,fr_nitro_arom_nonortho,fr_nitroso,fr_oxazole,fr_oxime,fr_para_hydroxylation,fr_phenol,fr_phenol_noOrthoHbond,fr_phos_acid,fr_phos_ester,fr_piperdine,fr_piperzine,fr_priamide,fr_prisulfonamd,fr_pyridine,fr_quatN,fr_sulfide,fr_sulfonamd,fr_sulfone,fr_term_acetylene,fr_tetrazole,fr_thiazole,fr_thiocyan,fr_thiophene,fr_unbrch_alkane,fr_urea";

    private static final List<String> EXPECTED_COLUMNS = Arrays.asList(EXPECTED_HEADER_RAW.split(","));

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        Map<String, String> proteinValues = new HashMap<>();
        String diseaseType = null;
        String uploadedFileName = null;
        String savedFilePath = null;
        boolean uploadSuccess = false;
        String errorMessage = null;
        List<String> headerMismatchDetails = null;
        List<String> missingProteinIds = null;

        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            FileItemFactory fileItemFactory = new DiskFileItemFactory();
            ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);
            List<FileItem> list = fileUpload.parseRequest(req);

            // Pass 1: read the diseaseType hidden field and whichever protein values were submitted
            for (FileItem item : list) {
                if (item.isFormField()) {
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString("UTF-8");

                    if ("diseaseType".equals(fieldName)) {
                        diseaseType = fieldValue == null ? null : fieldValue.trim();
                    } else if (fieldValue != null && !fieldValue.trim().isEmpty()) {
                        proteinValues.put(fieldName, fieldValue.trim());
                    }
                }
            }

            if (diseaseType == null || !DISEASE_REQUIRED_PROTEINS.containsKey(diseaseType)) {
                errorMessage = "Unknown or missing disease type: " + diseaseType;
            } else {
                String[] requiredIds = DISEASE_REQUIRED_PROTEINS.get(diseaseType);

                // Check that every protein required by THIS disease has a value.
                // Since each disease now has its own dedicated form, there is no
                // "extra protein" case to worry about anymore -- only missing.
                missingProteinIds = new ArrayList<>();
                for (String proteinId : requiredIds) {
                    if (!proteinValues.containsKey(proteinId)) {
                        missingProteinIds.add(proteinId);
                    }
                }

                if (missingProteinIds.isEmpty()) {
                    // Pass 2: handle the uploaded file, validating its header against the template first
                    for (FileItem item : list) {
                        if (!item.isFormField() && item.getName() != null && !item.getName().isEmpty()) {
                            uploadedFileName = item.getName();

                            List<String> actualHeader = readHeaderColumns(item);
                            headerMismatchDetails = compareHeaders(EXPECTED_COLUMNS, actualHeader);

                            if (!headerMismatchDetails.isEmpty()) {
                                break;
                            }

                            String outputFileName = DISEASE_OUTPUT_FILENAMES.get(diseaseType);
                            File tempFile = new File(uploadDir, outputFileName);
                            item.write(tempFile);

                            File processedFile = new File(uploadDir, "processed_" + UUID.randomUUID() + ".csv");
                            addProteinColumns(tempFile, processedFile, proteinValues, requiredIds);
                            tempFile.delete();
                            processedFile.renameTo(tempFile);

                            savedFilePath = tempFile.getAbsolutePath();
                            uploadSuccess = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            e.printStackTrace();
        }

        if (missingProteinIds != null && !missingProteinIds.isEmpty()) {
            outputMissingProteinsPage(out, diseaseType, missingProteinIds);
        } else if (headerMismatchDetails != null && !headerMismatchDetails.isEmpty()) {
            outputHeaderMismatchPage(out, uploadedFileName, headerMismatchDetails);
        } else {
            outputResponsePage(out, uploadSuccess, uploadedFileName, savedFilePath, errorMessage, diseaseType);
        }
    }

    private List<String> readHeaderColumns(FileItem item) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(item.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return Collections.emptyList();
            }
            if (headerLine.startsWith("\uFEFF")) {
                headerLine = headerLine.substring(1);
            }
            String[] parts = headerLine.split(",", -1);
            List<String> columns = new ArrayList<>();
            for (String p : parts) {
                columns.add(p.trim());
            }
            return columns;
        }
    }

    private List<String> compareHeaders(List<String> expected, List<String> actual) {
        List<String> problems = new ArrayList<>();

        if (actual.isEmpty()) {
            problems.add("The uploaded file appears to have no header row, or it could not be read.");
            return problems;
        }

        List<String> missing = new ArrayList<>(expected);
        missing.removeAll(actual);

        List<String> extra = new ArrayList<>(actual);
        extra.removeAll(expected);

        if (!missing.isEmpty()) {
            problems.add("Missing " + missing.size() + " required column(s): " + String.join(", ", missing));
        }
        if (!extra.isEmpty()) {
            problems.add("Found " + extra.size() + " unexpected column(s) not in the template: " + String.join(", ", extra));
        }

        if (missing.isEmpty() && extra.isEmpty() && !expected.equals(actual)) {
            problems.add("All expected columns are present, but the column order does not match the template.");
        }

        return problems;
    }

    private void addProteinColumns(File inputFile, File outputFile, Map<String, String> proteinValues, String[] requiredIds) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {

            String line = reader.readLine();
            if (line != null) {
                StringBuilder header = new StringBuilder(line);
                for (String proteinId : requiredIds) {
                    header.append(",").append(proteinId);
                }
                writer.write(header.toString());
                writer.newLine();

                while ((line = reader.readLine()) != null) {
                    StringBuilder dataLine = new StringBuilder(line);
                    for (String proteinId : requiredIds) {
                        dataLine.append(",").append(proteinValues.get(proteinId));
                    }
                    writer.write(dataLine.toString());
                    writer.newLine();
                }
            }
        }
    }

    private void outputMissingProteinsPage(PrintWriter out, String diseaseType, List<String> missingProteinIds) {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Missing Binding Energy Data</title>");
        out.println(commonStyle());
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<div class='icon error'>&#10007;</div>");
        out.println("<h2>Missing binding energy data for " + diseaseType + "</h2>");
        out.println("<div class='file-details'>");
        out.println("<p><strong>Missing " + missingProteinIds.size() + " protein(s):</strong></p>");
        out.println("<ul style='margin:0;padding-left:20px;'>");
        for (String proteinId : missingProteinIds) {
            out.println("<li>" + proteinId + "</li>");
        }
        out.println("</ul>");
        out.println("</div>");
        out.println("<a href='javascript:history.back()' class='button'>Go back and fill in the missing values</a>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    private void outputHeaderMismatchPage(PrintWriter out, String fileName, List<String> problems) {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>CSV Format Mismatch</title>");
        out.println(commonStyle());
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<div class='icon error'>&#10007;</div>");
        out.println("<h2>The CSV columns don't match the required template</h2>");
        out.println("<p>File: " + fileName + "</p>");
        out.println("<div class='file-details'>");
        out.println("<ul style='margin:0;padding-left:20px;'>");
        for (String problem : problems) {
            out.println("<li style='margin-bottom:10px;'>" + problem + "</li>");
        }
        out.println("</ul>");
        out.println("</div>");
        out.println("<p>Please fix the column names (and order) to match the molecular descriptor template, then upload again.</p>");
        out.println("<a href='javascript:history.back()' class='button'>Go back and re-upload</a>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    private void outputResponsePage(PrintWriter out, boolean success, String fileName, String filePath, String errorMessage, String diseaseType) {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>" + (success ? "Upload Successful" : "Upload Failed") + "</title>");
        out.println(commonStyle());
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");

        if (success) {
            out.println("<div class='icon success'>&#10003;</div>");
            out.println("<h2>The file was submitted successfully</h2>");
            out.println("<div class='file-details'>");
            out.println("<p><strong>Model:</strong> " + diseaseType + "</p>");
            out.println("<p><strong>File name:</strong> " + fileName + "</p>");
            out.println("<p><strong>Saved path:</strong> " + filePath + "</p>");
            out.println("<p><strong>Encoding:</strong> UTF-8</p>");
            out.println("</div>");
            out.println("<p>The file has been validated against the template, uploaded, and processed successfully. Please return to the model prediction interface to continue.</p>");
        } else {
            out.println("<div class='icon error'>&#10007;</div>");
            out.println("<h2>Upload Failed</h2>");
            out.println("<p>" + (errorMessage != null ? errorMessage : "An unknown error occurred during upload") + "</p>");
        }

        out.println("<a href='model.html' class='button'>Back to the model prediction interface</a>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    private String commonStyle() {
        return "<style>"
                + "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); margin: 0; padding: 0; min-height: 100vh; display: flex; justify-content: center; align-items: center; }"
                + ".container { background-color: white; border-radius: 10px; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1); width: 85%; max-width: 650px; padding: 30px; text-align: center; margin: 30px 0; }"
                + ".icon { font-size: 70px; margin-bottom: 15px; }"
                + ".success { color: #28a745; }"
                + ".error { color: #dc3545; }"
                + "h2 { color: #333; margin-bottom: 15px; }"
                + "p { color: #666; margin-bottom: 10px; word-break: break-word; }"
                + ".file-details { background-color: #f9f9f9; border-radius: 5px; padding: 15px; margin: 20px 0; text-align: left; }"
                + ".button { background: linear-gradient(135deg, #2a5298, #1e3c72); color: white; border: none; padding: 12px 24px; text-decoration: none; display: inline-block; font-size: 16px; margin: 15px 0 0; cursor: pointer; border-radius: 4px; }"
                + "</style>";
    }
}