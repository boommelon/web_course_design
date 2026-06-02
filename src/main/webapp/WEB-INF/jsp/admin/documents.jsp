<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>文档管理</h4>
    </div>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>学生</th>
                <th>课题</th>
                <th>阶段</th>
                <th>附件</th>
                <th>状态</th>
                <th>评分</th>
                <th>审核教师</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="doc" items="${documents}">
                <tr>
                    <td>${doc.studentName}</td>
                    <td>${doc.topicTitle}</td>
                    <td>
                        <c:choose>
                            <c:when test="${doc.type == 'proposal'}">开题报告</c:when>
                            <c:when test="${doc.type == 'midterm'}">中期报告</c:when>
                            <c:when test="${doc.type == 'final'}">毕业论文</c:when>
                        </c:choose>
                    </td>
                    <td>
                        <c:if test="${doc.fileName != null}">
                            <a href="${pageContext.request.contextPath}/documents/download.action?id=${doc.id}" class="btn btn-outline-primary btn-sm">${doc.fileName}</a>
                        </c:if>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${doc.status == 'submitted'}"><span class="badge bg-warning">待审核</span></c:when>
                            <c:when test="${doc.status == 'reviewed'}"><span class="badge bg-success">已审核</span></c:when>
                            <c:when test="${doc.status == 'rejected'}"><span class="badge bg-danger">已退回</span></c:when>
                        </c:choose>
                    </td>
                    <td>${doc.score != null ? doc.score : '-'}</td>
                    <td>${doc.reviewerName != null ? doc.reviewerName : '-'}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty documents}">
        <p class="text-muted text-center">暂无学生文档。</p>
    </c:if>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
