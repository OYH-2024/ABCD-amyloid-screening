<%--
  Created by IntelliJ IDEA.
  User: 15855
  Date: 2022/12/3
  Time: 17:13
  To change this template use File | Settings | File Templates.
--%>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload a PDBQT file - Online Molecular Docking System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --primary-color: #2a5298;
            --secondary-color: #1e3c72;
            --accent-color: #00bcd4;
            --light-bg: #f8f9fa;
            --dark-text: #333;
            --light-text: #fff;
            --border-radius: 8px;
            --box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            --transition: all 0.3s ease;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            margin: 0;
            padding: 20px 0;
        }

        /* 导航菜单栏 */
        .navbar-custom {
            background: linear-gradient(135deg, var(--secondary-color) 0%, var(--primary-color) 100%);
            box-shadow: var(--box-shadow);
            padding: 0.8rem 1rem;
            margin-bottom: 30px;
        }

        .navbar-brand {
            font-weight: 700;
            font-size: 1.5rem;
            color: var(--light-text);
            display: flex;
            align-items: center;
        }

        .navbar-brand i {
            margin-right: 10px;
        }

        .main-container {
            max-width: 800px;
            margin: 0 auto;
            padding: 0 20px;
        }

        .upload-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: var(--border-radius);
            box-shadow: var(--box-shadow);
            overflow: hidden;
            margin-bottom: 20px;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }

        .card-header {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            color: var(--light-text);
            padding: 2rem;
            text-align: center;
        }

        .card-header h1 {
            margin: 0;
            font-size: 2.2rem;
            font-weight: 700;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 15px;
        }

        .card-body {
            padding: 40px;
        }

        form {
            background: #fff;
            border-radius: var(--border-radius);
            padding: 40px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
            border: 1px solid #e9ecef;
        }

        .file-group {
            margin-bottom: 25px;
            position: relative;
        }

        .file-label {
            display: block;
            font-size: 1.1rem;
            font-weight: 600;
            color: var(--primary-color);
            margin-bottom: 10px;
            padding-left: 5px;
        }

        .file-input-wrapper {
            position: relative;
            display: block;
        }

        input[type="file"] {
            width: 100%;
            padding: 15px;
            border: 2px dashed #ddd;
            border-radius: var(--border-radius);
            background: var(--light-bg);
            font-size: 1rem;
            color: var(--dark-text);
            cursor: pointer;
            transition: var(--transition);
        }

        input[type="file"]:hover {
            border-color: var(--accent-color);
            background: rgba(0, 188, 212, 0.05);
            transform: translateY(-2px);
        }

        input[type="file"]:focus {
            outline: none;
            border-color: var(--accent-color);
            box-shadow: 0 0 0 3px rgba(0, 188, 212, 0.1);
        }

        input[type="submit"] {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            color: var(--light-text);
            border: none;
            padding: 15px 50px;
            border-radius: var(--border-radius);
            font-size: 1.2rem;
            font-weight: 600;
            cursor: pointer;
            transition: var(--transition);
            box-shadow: 0 4px 15px rgba(42, 82, 152, 0.3);
            margin-top: 20px;
            width: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
        }

        input[type="submit"]:hover {
            transform: translateY(-3px);
            box-shadow: 0 6px 20px rgba(42, 82, 152, 0.4);
        }

        input[type="submit"]:active {
            transform: translateY(-1px);
        }

        input[type="submit"]::before {
            content: '\f093';
            font-family: 'Font Awesome 6 Free';
            font-weight: 900;
            margin-right: 8px;
        }

        h1 {
            color: var(--primary-color);
            font-size: 1.3rem;
            font-weight: 600;
            text-align: center;
            margin: 30px 0 15px 0;
            padding: 20px;
            background: rgba(42, 82, 152, 0.05);
            border-radius: var(--border-radius);
            border-left: 4px solid var(--accent-color);
        }

        .info-section {
            background: rgba(0, 188, 212, 0.1);
            border-radius: var(--border-radius);
            padding: 25px;
            margin-bottom: 30px;
            border-left: 4px solid var(--accent-color);
        }

        .info-title {
            color: var(--primary-color);
            font-size: 1.2rem;
            font-weight: 600;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .info-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .info-list li {
            padding: 8px 0;
            display: flex;
            align-items: center;
            color: #555;
        }

        .info-list li::before {
            content: "✓";
            color: var(--accent-color);
            font-weight: bold;
            margin-right: 10px;
        }

        .back-section {
            text-align: center;
            margin-top: 30px;
        }

        .back-button {
            background: rgba(255, 255, 255, 0.9);
            border: 2px solid var(--accent-color);
            color: var(--accent-color);
            padding: 12px 30px;
            border-radius: var(--border-radius);
            text-decoration: none;
            font-weight: 600;
            transition: var(--transition);
            display: inline-flex;
            align-items: center;
            gap: 10px;
            font-size: 1rem;
        }

        .back-button:hover {
            background: var(--accent-color);
            color: var(--light-text);
            transform: translateY(-2px);
            text-decoration: none;
        }

        /* 文件图标样式 */
        .file-group::before {
            content: '\f15c';
            font-family: 'Font Awesome 6 Free';
            font-weight: 900;
            color: var(--accent-color);
            position: absolute;
            top: 45px;
            right: 15px;
            font-size: 1.2rem;
            pointer-events: none;
            z-index: 1;
        }

        /* 响应式设计 */
        @media (max-width: 768px) {
            .main-container {
                padding: 10px;
            }

            .card-body {
                padding: 20px;
            }

            form {
                padding: 25px;
            }

            .card-header h1 {
                font-size: 1.8rem;
                flex-direction: column;
                gap: 10px;
            }

            input[type="submit"] {
                padding: 15px 30px;
            }
        }

        /* 动画效果 */
        .upload-card {
            animation: fadeInUp 0.6s ease-out;
        }

        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .file-group {
            animation: slideInLeft 0.5s ease-out;
        }

        .file-group:nth-child(2) { animation-delay: 0.1s; }
        .file-group:nth-child(3) { animation-delay: 0.2s; }
        .file-group:nth-child(4) { animation-delay: 0.3s; }

        @keyframes slideInLeft {
            from {
                opacity: 0;
                transform: translateX(-20px);
            }
            to {
                opacity: 1;
                transform: translateX(0);
            }
        }
    </style>
</head>
<body>
<!-- 导航菜单 -->
<nav class="navbar navbar-expand-lg navbar-dark navbar-custom">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">
            <i class="fas fa-flask"></i>
            Inhibitory molecular system
        </a>
    </div>
</nav>

<div class="main-container">
    <!-- 主卡片 -->
    <div class="upload-card">
        <!-- 卡片头部 -->
        <div class="card-header">
            <h1>
                <i class="fas fa-cloud-upload-alt"></i>
                Add the PDBQT file
            </h1>
        </div>

        <!-- 卡片主体 -->
        <div class="card-body">
            <!-- 使用说明 -->
            <div class="info-section">
                <div class="info-title">
                    <i class="fas fa-info-circle"></i>
                    Instructions for Use
                </div>
                <ul class="info-list">
                    <li>Please select 1 to 3 ligand molecule files in PDBQT format</li>
                    <li>Make sure the file format is correct to achieve the best docking result</li>
                    <li>After uploading, please return to the main page to perform the molecular docking operation</li>
                </ul>
            </div>

            <!-- 保持原始表单结构 -->
            <form action="fileUpload" method="post" enctype="multipart/form-data">
                <div class="file-group">
                    <label class="file-label">PDBQT file 1</label>
                    <div class="file-input-wrapper">
                        <input type="file" name="filename1">
                    </div>
                </div>

                <div class="file-group">
                    <label class="file-label">PDBQT file 2</label>
                    <div class="file-input-wrapper">
                        <input type="file" name="filename2">
                    </div>
                </div>

                <div class="file-group">
                    <label class="file-label">PDBQT file 3</label>
                    <div class="file-input-wrapper">
                        <input type="file" name="filename3">
                    </div>
                </div>

                <input type="submit" value="Submit">

                <h1>Please select one to three files to upload.</h1>
                <h1>After uploading, please return to the previous page for molecular docking...</h1>
            </form>
        </div>
    </div>

    <!-- 返回按钮 -->
    <div class="back-section">
        <a href="cmd.html" class="back-button">
            <i class="fas fa-arrow-left"></i>
            Return to the main interface
        </a>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // 添加文件选择反馈效果
    document.querySelectorAll('input[type="file"]').forEach(input => {
        input.addEventListener('change', function() {
            if (this.files.length > 0) {
                this.style.borderColor = 'var(--accent-color)';
                this.style.backgroundColor = 'rgba(0, 188, 212, 0.1)';
            } else {
                this.style.borderColor = '#ddd';
                this.style.backgroundColor = 'var(--light-bg)';
            }
        });
    });

    // 提交按钮动画效果
    document.querySelector('input[type="submit"]').addEventListener('click', function() {
        this.style.transform = 'scale(0.98)';
        setTimeout(() => {
            this.style.transform = '';
        }, 150);
    });

    // 拖拽上传功能
    document.querySelectorAll('input[type="file"]').forEach(input => {
        input.addEventListener('dragover', function(e) {
            e.preventDefault();
            this.style.backgroundColor = 'rgba(0, 188, 212, 0.15)';
        });

        input.addEventListener('dragleave', function(e) {
            e.preventDefault();
            this.style.backgroundColor = '';
        });

        input.addEventListener('drop', function(e) {
            e.preventDefault();
            this.style.backgroundColor = '';

            if (e.dataTransfer.files.length > 0) {
                this.files = e.dataTransfer.files;
                // 触发change事件
                this.dispatchEvent(new Event('change'));
            }
        });
    });
</script>
</body>
</html>

