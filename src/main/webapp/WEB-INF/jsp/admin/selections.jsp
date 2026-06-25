<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>选题结果</h4>
        <span>全校最终分配（只读）</span>
    </div>

    <p class="text-muted">选题确认与强制分配由各专业负责人完成。下表为全校最终分配结果。</p>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>学生</th>
                <th>学号</th>
                <th>专业</th>
                <th>题目</th>
                <th>指导教师</th>
                <th>分配来源</th>
                <th>确认时间</th>
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
                            <c:when test="${fa.source == 'round1'}">第一轮<c:if test="${fa.choiceRank != null}">（志愿${fa.choiceRank}）</c:if></c:when>
                            <c:when test="${fa.source == 'round2'}">第二轮<c:if test="${fa.choiceRank != null}">（志愿${fa.choiceRank}）</c:if></c:when>
                            <c:otherwise>强制分配</c:otherwise>
                        </c:choose>
                    </td>
                    <td>${fa.confirmTime}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty assignments}">
                <tr><td colspan="7" class="text-center text-muted">暂无最终分配结果</td></tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
