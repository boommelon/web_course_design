<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>资料管理</h4>
    </div>

    <div class="card mb-4">
        <div class="card-header">上传参考资料</div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/teacher/resources.action" method="post" enctype="multipart/form-data" class="row g-3">
                <div class="col-md-4">
                    <label class="form-label">资料名称</label>
                    <input type="text" name="title" class="form-control" required>
                </div>
                <div class="col-md-5">
                    <label class="form-label">资料文件</label>
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
                <th>资料名称</th>
                <th>文件名</th>
                <th>上传时间</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="res" items="${resources}">
                <tr>
                    <td>${res.title}</td>
                    <td>${res.fileName}</td>
                    <td>${res.createdAt}</td>
                    <td>
                        <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/resources/download.action?type=resource&id=${res.id}">下载</a>
                        <a class="btn btn-danger btn-sm" href="${pageContext.request.contextPath}/teacher/resources.action?opttype=delete&id=${res.id}" onclick="return confirm('\u786e\u5b9a\u5220\u9664\u8be5\u8d44\u6599\uff1f')">删除</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
