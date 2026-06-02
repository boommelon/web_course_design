<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>文件模板管理</h4>
    </div>

    <div class="card mb-4">
        <div class="card-header">上传模板</div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/templates.action" method="post" enctype="multipart/form-data" class="row g-3">
                <div class="col-md-4">
                    <label class="form-label">模板名称</label>
                    <input type="text" name="title" class="form-control" required>
                </div>
                <div class="col-md-5">
                    <label class="form-label">模板文件</label>
                    <input type="file" name="file" class="form-control" required>
                </div>
                <div class="col-md-3 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary">上传</button>
                </div>
            </form>
        </div>
    </div>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>模板名称</th>
                <th>文件名</th>
                <th>上传时间</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="tpl" items="${templates}">
                <tr>
                    <td>${tpl.title}</td>
                    <td>${tpl.fileName}</td>
                    <td>${tpl.createdAt}</td>
                    <td>
                        <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/resources/download.action?type=template&id=${tpl.id}">下载</a>
                        <a class="btn btn-danger btn-sm" href="${pageContext.request.contextPath}/admin/templates.action?action=delete&id=${tpl.id}" onclick="return confirm('确定删除该模板？')">删除</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty templates}">
        <p class="text-muted text-center">暂无模板文件。</p>
    </c:if>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
