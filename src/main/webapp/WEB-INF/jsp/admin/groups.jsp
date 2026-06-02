<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>教师分组</h4>
    </div>

    <p class="text-muted">系统按已确定选题自动形成指导教师分组，用于后续文档审核、答疑和答辩评分。</p>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>学生</th>
                <th>课题</th>
                <th>选题轮次</th>
                <th>状态</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="sel" items="${selections}">
                <c:if test="${sel.status == 'approved'}">
                    <tr>
                        <td>${sel.studentName}</td>
                        <td>${sel.topicTitle}</td>
                        <td>第${sel.roundNo}轮</td>
                        <td><span class="badge bg-success">已分组</span></td>
                    </tr>
                </c:if>
            </c:forEach>
        </tbody>
    </table>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
