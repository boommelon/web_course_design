<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>数据导入</h4>
    </div>

    <div class="card mb-4">
        <div class="card-header">导入有资格参加毕业设计的学生和教师信息</div>
        <div class="card-body">
            <c:if test="${successCount != null}">
                <div class="alert alert-success">成功导入或更新 ${successCount} 条用户信息。</div>
            </c:if>
            <c:if test="${not empty errors}">
                <div class="alert alert-danger">
                    <c:forEach var="error" items="${errors}">
                        <div>${error}</div>
                    </c:forEach>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/import.action" method="post" enctype="multipart/form-data">
                <div class="mb-3">
                    <label class="form-label">CSV文件</label>
                    <input type="file" name="file" class="form-control" accept=".csv,text/csv" required>
                </div>
                <button type="submit" class="btn btn-primary">开始导入</button>
            </form>
        </div>
    </div>

    <div class="card">
        <div class="card-header">CSV格式</div>
        <div class="card-body">
            <p class="mb-2">第一行可以写表头，后续每行一名用户。密码留空时默认使用 123456。</p>
            <pre class="bg-light p-3 mb-0">username,password,name,role,email,phone
student04,123456,陈同学,student,chen@example.com,13800000004
teacher03,123456,王老师,teacher,wang@example.com,13800000005</pre>
        </div>
    </div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
