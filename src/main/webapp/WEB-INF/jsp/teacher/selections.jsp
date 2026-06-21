<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>选题审批</h4>
    </div>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>学生姓名</th>
                <th>申请课题</th>
                <th>申请理由</th>
                <th>申请时间</th>
                <th>状态</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="sel" items="${selections}">
                <tr>
                    <td>${sel.studentName}</td>
                    <td>${sel.topicTitle}</td>
                    <td>${sel.reason}</td>
                    <td>${sel.createdAt}</td>
                    <td>
                        <c:choose>
                            <c:when test="${sel.status == 'pending'}"><span class="badge bg-warning">待审批</span></c:when>
                            <c:when test="${sel.status == 'approved'}"><span class="badge bg-success">已通过</span></c:when>
                            <c:when test="${sel.status == 'rejected'}"><span class="badge bg-danger">已驳回</span></c:when>
                        </c:choose>
                    </td>
                    <td>
                        <c:if test="${sel.status == 'pending'}">
                            <form action="${pageContext.request.contextPath}/teacher/selections.action" method="post" style="display:inline;">
                                <input type="hidden" name="id" value="${sel.id}">
                                <input type="hidden" name="topicId" value="${sel.topicId}">
                                <input type="hidden" name="opttype" value="approve">
                                <button type="submit" class="btn btn-success btn-sm">批准</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/teacher/selections.action" method="post" style="display:inline;">
                                <input type="hidden" name="id" value="${sel.id}">
                                <input type="hidden" name="topicId" value="${sel.topicId}">
                                <input type="hidden" name="opttype" value="reject">
                                <button type="submit" class="btn btn-danger btn-sm">驳回</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty selections}">
        <p class="text-muted text-center">暂无选题申请</p>
    </c:if>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
