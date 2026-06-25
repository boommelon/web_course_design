<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>最终分配</h4>
        <span>全校（只读）</span>
    </div>

    <p class="text-muted">系统按最终分配结果形成指导关系，用于后续文档审核、答疑和成绩评定。</p>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>学生</th>
                <th>学号</th>
                <th>专业</th>
                <th>题目</th>
                <th>指导教师</th>
                <th>来源</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="fa" items="${assignments}">
                <tr>
                    <td>${fa.studentName}</td>
                    <td>${fa.studentNo}</td>
                    <td>${fa.major}</td>
                    <td>${fa.topicTitle}</td>
                    <td>${fa.teacherName}</td>
                    <td>
                        <c:choose>
                            <c:when test="${fa.source == 'round1'}">第一轮</c:when>
                            <c:when test="${fa.source == 'round2'}">第二轮</c:when>
                            <c:otherwise>强制分配</c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty assignments}">
                <tr><td colspan="6" class="text-center text-muted">暂无最终分配</td></tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
