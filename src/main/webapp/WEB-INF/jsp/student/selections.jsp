<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>我的选题</h4>
        <span>当前第${selectionRound}轮选题</span>
    </div>

    
    <c:if test="${!studentSelectionOpen}">
        <div class="alert alert-warning">当前选题系统已关闭。</div>
    </c:if>
    <c:if test="${hasApproved}">
        <div class="alert alert-success">您已确定课题，后续流程将以已通过记录为准。</div>
    </c:if>
    <c:if test="${!hasApproved && hasSubmittedCurrentRound}">
        <div class="alert alert-info">您已确认本轮选题，本轮不能再次修改。</div>
    </c:if>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>课题名称</th>
                <th>轮次</th>
                <th>申请理由</th>
                <th>申请时间</th>
                <th>状态</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="sel" items="${selections}">
                <tr>
                    <td>${sel.topicTitle}</td>
                    <td>第${sel.roundNo}轮</td>
                    <td>${sel.reason}</td>
                    <td>${sel.createdAt}</td>
                    <td>
                        <c:choose>
                            <c:when test="${sel.status == 'pending'}"><span class="badge bg-warning">待确认</span></c:when>
                            <c:when test="${sel.status == 'approved'}"><span class="badge bg-success">已通过</span></c:when>
                            <c:when test="${sel.status == 'rejected'}"><span class="badge bg-danger">未中选</span></c:when>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty selections}">
        <p class="text-muted text-center">您还没有确认过选题，请前往"浏览课题"页面选择课题。</p>
    </c:if>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
