<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>毕业设计管理系统</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { margin: 0; display: flex; min-height: 100vh; }
        /* 侧边栏样式 */
        .sidebar {
            width: 220px;
            background-color: #343a40;
            min-height: 100vh;
            padding-top: 15px;
            position: fixed;
            left: 0;
            top: 0;
        }
        .sidebar .brand {
            color: #ffffff;
            font-size: 16px;
            font-weight: bold;
            padding: 10px 20px 20px 20px;
            border-bottom: 1px solid #495057;
            margin-bottom: 10px;
        }
        .sidebar a {
            color: #adb5bd;
            display: block;
            padding: 10px 20px;
            text-decoration: none;
            font-size: 14px;
        }
        .sidebar a:hover {
            color: #ffffff;
            background-color: #495057;
        }
        .sidebar a.active {
            color: #ffffff;
            background-color: #0d6efd;
        }
        .sidebar .logout {
            border-top: 1px solid #495057;
            margin-top: 20px;
            padding-top: 10px;
        }
        /* 主内容区域 */
        .main-content {
            margin-left: 220px;
            padding: 20px 30px;
            flex: 1;
            background-color: #f8f9fa;
        }
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 1px solid #dee2e6;
        }
    </style>
</head>
<body>
