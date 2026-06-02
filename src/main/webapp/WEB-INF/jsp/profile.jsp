<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>个人资料</h4>
    </div>

    <c:if test="${message != null}">
        <div class="alert alert-success">${message}</div>
    </c:if>
    <c:if test="${error != null}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <div class="row">
        <div class="col-md-6">
            <div class="card mb-4">
                <div class="card-header">基本信息</div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/profile.action" method="post">
                        <input type="hidden" name="action" value="profile">
                        <div class="mb-3">
                            <label class="form-label">用户名</label>
                            <input type="text" class="form-control" value="${sessionScope.loginUser.username}" disabled>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">姓名</label>
                            <input type="text" name="name" class="form-control" value="${sessionScope.loginUser.name}" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">邮箱</label>
                            <input type="email" name="email" class="form-control" value="${sessionScope.loginUser.email}">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">电话</label>
                            <input type="text" name="phone" class="form-control" value="${sessionScope.loginUser.phone}">
                        </div>
                        <button type="submit" class="btn btn-primary">保存资料</button>
                    </form>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="card mb-4">
                <div class="card-header">修改密码</div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/profile.action" method="post">
                        <input type="hidden" name="action" value="password">
                        <div class="mb-3">
                            <label class="form-label">原密码</label>
                            <input type="password" name="oldPassword" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">新密码</label>
                            <input type="password" name="newPassword" class="form-control" minlength="6" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">确认新密码</label>
                            <input type="password" name="confirmPassword" class="form-control" minlength="6" required>
                        </div>
                        <button type="submit" class="btn btn-primary">修改密码</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
