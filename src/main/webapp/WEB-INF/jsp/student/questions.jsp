<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>我的提问</h4>
    </div>

    <c:choose>
        <c:when test="${selection == null}">
            <div class="alert alert-warning">选题通过后才能向指导教师提问。</div>
        </c:when>
        <c:otherwise>
            <div class="card mb-4">
                <div class="card-header">提交问题</div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/student/questions.action" method="post">
                        <input type="hidden" name="teacherId" value="${topic.teacherId}">
                        <p>当前课题：<strong>${selection.topicTitle}</strong>，指导教师：<strong>${topic.teacherName}</strong></p>
                        <div class="mb-3">
                            <label class="form-label">问题内容</label>
                            <textarea name="question" class="form-control" rows="4" required></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary">提交问题</button>
                    </form>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>课题</th>
                <th>指导教师</th>
                <th>问题</th>
                <th>状态</th>
                <th>教师答复</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="q" items="${questions}">
                <tr>
                    <td>${q.topicTitle}</td>
                    <td>${q.teacherName}</td>
                    <td>${q.question}</td>
                    <td>
                        <c:choose>
                            <c:when test="${q.status == 'pending'}"><span class="badge bg-warning">待答复</span></c:when>
                            <c:otherwise><span class="badge bg-success">已答复</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td>${q.answer != null ? q.answer : '-'}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
