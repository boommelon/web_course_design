<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>题目总览</h4>
        <span>全校题目（只读）</span>
    </div>

    <p class="text-muted">题目审核由各专业负责人完成，管理员不直接审题。</p>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>题目名称</th>
                <th>指导教师</th>
                <th>学院</th>
                <th>专业</th>
                <th>描述</th>
                <th>状态</th>
                <th>审核人</th>
                <th>审核意见</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="topic" items="${topics}">
                <tr>
                    <td>${topic.title}</td>
                    <td>${topic.teacherName}</td>
                    <td>${topic.college}</td>
                    <td>${topic.major}</td>
                    <td>${topic.description}</td>
                    <td>
                        <c:choose>
                            <c:when test="${topic.status == 'pending'}"><span class="badge bg-warning">待审核</span></c:when>
                            <c:when test="${topic.status == 'approved'}"><span class="badge bg-success">已通过</span></c:when>
                            <c:when test="${topic.status == 'rejected'}"><span class="badge bg-danger">已退回</span></c:when>
                            <c:when test="${topic.status == 'assigned'}"><span class="badge bg-primary">已分配</span></c:when>
                            <c:otherwise><span class="badge bg-secondary">${topic.status}</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td>${topic.reviewerName}</td>
                    <td>${topic.reviewComment}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty topics}">
                <tr><td colspan="8" class="text-center text-muted">暂无题目</td></tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
