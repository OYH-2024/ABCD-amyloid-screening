<%--
  Created by IntelliJ IDEA.
  User: 15855
  Date: 2022/12/3
  Time: 17:13
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add File - Molecular Descriptor System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<div style="max-width:800px;margin:40px auto;padding:0 20px;">
    <div style="background:#fff;border-radius:8px;box-shadow:0 4px 15px rgba(0,0,0,0.1);overflow:hidden;">
        <div style="background:linear-gradient(135deg,#2a5298,#1e3c72);color:#fff;padding:2rem;text-align:center;">
            <h1><i class="fas fa-file-upload"></i> Add smiles file</h1>
        </div>
        <div style="padding:40px;">
            <!-- 关键：action 必须是 AddMoleculeFileUpload，对应 MoleculeAddServlet.java 的 @WebServlet 映射 -->
            <form action="AddMoleculeFileUpload" method="post" enctype="multipart/form-data">
                <div style="margin-bottom:25px;">
                    <label for="modelFile" style="display:block;font-weight:600;color:#2a5298;margin-bottom:10px;">smi file</label>
                    <input type="file" id="modelFile" name="filename1" class="form-control" accept=".smi">
                </div>
                <input type="submit" value="Submit" class="submit-btn" style="width:100%;padding:15px;background:linear-gradient(135deg,#00bcd4,#0097a7);color:#fff;border:none;border-radius:8px;font-size:1.1rem;cursor:pointer;">
                <p style="text-align:center;margin-top:20px;">After the upload is completed, please return to the previous page to calculate the molecular descriptor...</p>
            </form>
        </div>
    </div>
    <div style="text-align:center;margin-top:30px;">
        <a href="molcule.html">← Return to the calculation descriptor interface</a>
    </div>
</div>
</body>
</html>