<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>毕业设计管理系统 - 登录</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static/css/academic.css?v=20260621c" rel="stylesheet">
    <style>
        body {
            padding-top: 80px;
        }
        .login-box {
            width: 360px;
            padding: 22px;
            margin: 0 auto;
        }
        .login-box h3 {
            text-align: center;
            margin-bottom: 20px;
        }
    </style>
</head>
<body class="login-page">
    <div class="login-box">
        <h3>毕业设计管理系统</h3>

        
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/login.action" method="post">
            <div class="mb-3">
                <label class="form-label">用户名</label>
                <input type="text" name="username" class="form-control" placeholder="请输入用户名" required>
            </div>
            <div class="mb-3">
                <label class="form-label">密码</label>
                <input type="password" name="password" class="form-control" placeholder="请输入密码" required>
            </div>
            <div style="text-align:center;">
                <button type="submit" class="btn btn-primary">登 录</button>
            </div>
            <div class="text-center text-muted small mt-3">
                忘记密码请联系管理员重置，默认重置密码为 123456
            </div>
        </form>
    </div>
</body>
</html>
