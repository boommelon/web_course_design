<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>任务资料</h4>
    </div>

    <h5>阶段任务</h5>
    <table class="table table-bordered table-hover mb-4">
        <thead class="table-light">
            <tr>
                <th>任务</th>
                <th>发布教师</th>
                <th>内容</th>
                <th>截止日期</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="task" items="${tasks}">
                <tr>
                    <td>${task.title}</td>
                    <td>${task.teacherName}</td>
                    <td>${task.content}</td>
                    <td>${task.deadline}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <h5>文件模板</h5>
    <table class="table table-bordered table-hover mb-4">
        <thead class="table-light">
            <tr>
                <th>模板名称</th>
                <th>文件名</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="tpl" items="${templates}">
                <tr>
                    <td>${tpl.title}</td>
                    <td>${tpl.fileName}</td>
                    <td><a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/resources/download.action?type=template&id=${tpl.id}">下载</a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <h5>教师参考资料</h5>
    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>资料名称</th>
                <th>上传教师</th>
                <th>文件名</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="res" items="${resources}">
                <tr>
                    <td>${res.title}</td>
                    <td>${res.teacherName}</td>
                    <td>${res.fileName}</td>
                    <td><a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/resources/download.action?type=resource&id=${res.id}">下载</a></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
