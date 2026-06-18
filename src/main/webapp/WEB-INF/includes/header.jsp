<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>毕业设计管理系统</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static/css/academic.css" rel="stylesheet">
    <script>var contextPath='${pageContext.request.contextPath}';</script>
    <style>
        body { margin: 0; display: flex; min-height: 100vh; }
        .sidebar {
            width: 220px;
            min-height: 100vh;
            padding-top: 15px;
            position: fixed;
            left: 0;
            top: 0;
        }
        .sidebar .brand {
            font-size: 16px;
            font-weight: bold;
            padding: 10px 20px 20px 20px;
            margin-bottom: 10px;
        }
        .sidebar a {
            display: block;
            padding: 10px 20px;
            text-decoration: none;
            font-size: 14px;
        }
        .sidebar .logout {
            margin-top: 20px;
            padding-top: 10px;
        }
        .main-content {
            margin-left: 220px;
            padding: 20px 30px;
            flex: 1;
        }
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 10px;
        }
    </style>
</head>
<body>
