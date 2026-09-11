<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Model File</title>
    <style>
        :root {
            --primary-color: #2a5298;
            --primary-dark: #1e3c72;
            --accent-color: #00bcd4;
            --secondary-color: #ecf0f1;
            --text-dark: #333;
            --text-light: #666;
            --white: #fff;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 40px 20px;
        }

        .page-header {
            text-align: center;
            color: var(--white);
            margin-bottom: 30px;
        }

        .page-header h1 {
            font-size: 28px;
            margin-bottom: 10px;
        }

        .page-header p {
            opacity: 0.9;
        }

        .disease-container {
            background-color: var(--white);
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 800px;
            padding: 30px;
            margin-bottom: 30px;
        }

        .disease-title {
            font-size: 20px;
            font-weight: 600;
            color: var(--primary-color);
            margin-bottom: 20px;
            display: flex;
            align-items: center;
        }

        .disease-title i {
            margin-right: 8px;
            color: var(--accent-color);
        }

        .form-control {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 16px;
            transition: border-color 0.3s;
        }

        .form-control:focus {
            border-color: var(--accent-color);
            outline: none;
        }

        .protein-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 15px;
            margin-bottom: 25px;
        }

        .protein-item label {
            display: block;
            margin-bottom: 5px;
            font-weight: 500;
        }

        .file-upload {
            position: relative;
            display: block;
            border: 2px dashed #ddd;
            border-radius: 8px;
            padding: 25px 20px;
            text-align: center;
            transition: all 0.3s;
            cursor: pointer;
            background-color: #f9f9f9;
            margin-bottom: 20px;
        }

        .file-upload:hover {
            border-color: var(--accent-color);
        }

        .file-upload.dragover {
            background-color: rgba(0, 188, 212, 0.1);
            border-color: var(--accent-color);
        }

        .file-upload i {
            font-size: 40px;
            color: var(--primary-color);
            margin-bottom: 10px;
            display: block;
        }

        .file-upload input[type="file"] {
            position: absolute;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            opacity: 0;
            cursor: pointer;
        }

        .file-info {
            margin-top: 5px;
            font-size: 13px;
            color: var(--text-light);
        }

        .file-name-display {
            margin-top: 10px;
            font-weight: bold;
            color: var(--primary-color);
            display: none;
        }

        .button {
            background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
            color: var(--white);
            border: none;
            border-radius: 4px;
            padding: 12px 24px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            width: 100%;
            transition: all 0.3s;
        }

        .button:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(42, 82, 152, 0.3);
        }

        .back-link {
            display: flex;
            align-items: center;
            justify-content: center;
            color: var(--white);
            text-decoration: none;
            font-weight: 500;
            font-size: 18px;
            margin-top: 10px;
        }

        .back-link:hover {
            color: var(--accent-color);
            text-decoration: underline;
        }

        .back-link i {
            margin-right: 5px;
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>

<div class="page-header">
    <h1>Add the model file</h1>
    <p>Each model below is uploaded independently -- pick the one you need, fill in its target protein values, choose its CSV file, and submit.</p>
</div>

<!-- ================= AD (Abeta) ================= -->
<div class="disease-container">
    <h3 class="disease-title"><i class="fas fa-dna"></i>A&beta; protein (AD model)</h3>
    <form action="AddModelFileUpload" method="post" enctype="multipart/form-data">
        <input type="hidden" name="diseaseType" value="AD">

        <div class="protein-grid">
            <div class="protein-item"><label for="ad_1Z0Q">1Z0Q value:</label>
                <input type="text" id="ad_1Z0Q" name="1Z0Q" class="form-control" placeholder="e.g. -5.48"></div>
            <div class="protein-item"><label for="ad_6RHY">6RHY value:</label>
                <input type="text" id="ad_6RHY" name="6RHY" class="form-control" placeholder="e.g. -5.48"></div>
            <div class="protein-item"><label for="ad_6TI7">6TI7 value:</label>
                <input type="text" id="ad_6TI7" name="6TI7" class="form-control" placeholder="e.g. -5.48"></div>
            <div class="protein-item"><label for="ad_1AML">1AML value:</label>
                <input type="text" id="ad_1AML" name="1AML" class="form-control" placeholder="e.g. -5.48"></div>
            <div class="protein-item"><label for="ad_1BA4">1BA4 value:</label>
                <input type="text" id="ad_1BA4" name="1BA4" class="form-control" placeholder="e.g. -5.48"></div>
            <div class="protein-item"><label for="ad_1IYT">1IYT value:</label>
                <input type="text" id="ad_1IYT" name="1IYT" class="form-control" placeholder="e.g. -5.48"></div>
            <div class="protein-item"><label for="ad_2BEG">2BEG value:</label>
                <input type="text" id="ad_2BEG" name="2BEG" class="form-control" placeholder="e.g. -5.48"></div>
            <div class="protein-item"><label for="ad_2M9R">2M9R value:</label>
                <input type="text" id="ad_2M9R" name="2M9R" class="form-control" placeholder="e.g. -5.48"></div>
            <div class="protein-item"><label for="ad_2NAO">2NAO value:</label>
                <input type="text" id="ad_2NAO" name="2NAO" class="form-control" placeholder="e.g. -5.48"></div>
            <div class="protein-item"><label for="ad_5OQV">5OQV value:</label>
                <input type="text" id="ad_5OQV" name="5OQV" class="form-control" placeholder="e.g. -5.48"></div>
            <div class="protein-item"><label for="ad_6OC9">6OC9 value:</label>
                <input type="text" id="ad_6OC9" name="6OC9" class="form-control" placeholder="e.g. -5.48"></div>
            <div class="protein-item"><label for="ad_6SHS">6SHS value:</label>
                <input type="text" id="ad_6SHS" name="6SHS" class="form-control" placeholder="e.g. -5.48"></div>
        </div>

        <label class="file-upload">
            <i class="fas fa-cloud-upload-alt"></i>
            <h3>Drop the AD CSV file here or click to select</h3>
            <p class="file-info">Format: CSV</p>
            <input type="file" name="csvFile" accept=".csv" onchange="showFileName(this, 'ad_fileName')">
            <div id="ad_fileName" class="file-name-display"></div>
        </label>

        <button type="submit" class="button"><i class="fas fa-upload"></i> Upload AD data</button>
    </form>
</div>

<!-- ================= TUHe ================= -->
<div class="disease-container">
    <h3 class="disease-title"><i class="fas fa-dna"></i>&alpha;-Synuclein protein (TUHe model)</h3>
    <form action="AddModelFileUpload" method="post" enctype="multipart/form-data">
        <input type="hidden" name="diseaseType" value="TUHe">

        <div class="protein-grid">
            <div class="protein-item"><label for="tuhe_1XQ8">1XQ8 value:</label>
                <input type="text" id="tuhe_1XQ8" name="1XQ8" class="form-control" placeholder="e.g. -5.48"></div>
            <div class="protein-item"><label for="tuhe_6A6B">6A6B value:</label>
                <input type="text" id="tuhe_6A6B" name="6A6B" class="form-control" placeholder="e.g. -5.48"></div>
        </div>

        <label class="file-upload">
            <i class="fas fa-cloud-upload-alt"></i>
            <h3>Drop the TUHe CSV file here or click to select</h3>
            <p class="file-info">Format: CSV</p>
            <input type="file" name="csvFile" accept=".csv" onchange="showFileName(this, 'tuhe_fileName')">
            <div id="tuhe_fileName" class="file-name-display"></div>
        </label>

        <button type="submit" class="button"><i class="fas fa-upload"></i> Upload TUHe data</button>
    </form>
</div>

<!-- ================= TAU ================= -->
<div class="disease-container">
    <h3 class="disease-title"><i class="fas fa-dna"></i>Tau protein (TAU model)</h3>
    <form action="AddModelFileUpload" method="post" enctype="multipart/form-data">
        <input type="hidden" name="diseaseType" value="TAU">

        <div class="protein-grid">
            <div class="protein-item"><label for="tau_7SP1">7SP1 value:</label>
                <input type="text" id="tau_7SP1" name="7SP1" class="form-control" placeholder="e.g. -5.48"></div>
        </div>

        <label class="file-upload">
            <i class="fas fa-cloud-upload-alt"></i>
            <h3>Drop the TAU CSV file here or click to select</h3>
            <p class="file-info">Format: CSV</p>
            <input type="file" name="csvFile" accept=".csv" onchange="showFileName(this, 'tau_fileName')">
            <div id="tau_fileName" class="file-name-display"></div>
        </label>

        <button type="submit" class="button"><i class="fas fa-upload"></i> Upload TAU data</button>
    </form>
</div>

<!-- ================= Huntingtin ================= -->
<div class="disease-container">
    <h3 class="disease-title"><i class="fas fa-dna"></i>Huntingtin protein</h3>
    <form action="AddModelFileUpload" method="post" enctype="multipart/form-data">
        <input type="hidden" name="diseaseType" value="Huntingtin">

        <div class="protein-grid">
            <div class="protein-item"><label for="hun_6YEJ">6YEJ value:</label>
                <input type="text" id="hun_6YEJ" name="6YEJ" class="form-control" placeholder="e.g. -5.48"></div>
        </div>

        <label class="file-upload">
            <i class="fas fa-cloud-upload-alt"></i>
            <h3>Drop the Huntingtin CSV file here or click to select</h3>
            <p class="file-info">Format: CSV</p>
            <input type="file" name="csvFile" accept=".csv" onchange="showFileName(this, 'hun_fileName')">
            <div id="hun_fileName" class="file-name-display"></div>
        </label>

        <button type="submit" class="button"><i class="fas fa-upload"></i> Upload Huntingtin data</button>
    </form>
</div>

<a href="model.html" class="back-link">
    <i class="fas fa-arrow-left"></i> Back to the model prediction interface
</a>

<script>
    function showFileName(input, displayId) {
        const display = document.getElementById(displayId);
        if (input.files && input.files[0]) {
            display.textContent = "Selected: " + input.files[0].name;
            display.style.display = 'block';
        } else {
            display.style.display = 'none';
        }
    }
</script>
</body>
</html>
