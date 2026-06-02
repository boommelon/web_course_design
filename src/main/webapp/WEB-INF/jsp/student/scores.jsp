<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>我的成绩</h4>
    </div>

    <div class="card mb-4">
        <div class="card-header">答辩成绩</div>
        <div class="card-body">
            <c:choose>
                <c:when test="${defenseScore != null}">
                    <p class="mb-1">课题：<strong>${defenseScore.topicTitle}</strong></p>
                    <p class="mb-1">答辩成绩：<strong>${defenseScore.score}</strong> 分</p>
                    <p class="mb-0">答辩意见：${defenseScore.comment != null ? defenseScore.comment : '暂无意见'}</p>
                </c:when>
                <c:otherwise>
                    <p class="text-muted mb-0">暂无答辩成绩。</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <h5>阶段文档成绩</h5>
    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>文档类型</th>
                <th>课题名称</th>
                <th>提交时间</th>
                <th>附件</th>
                <th>状态</th>
                <th>评分</th>
                <th>教师反馈</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="doc" items="${documents}">
                <tr>
                    <td>
                        <c:choose>
                            <c:when test="${doc.type == 'proposal'}">开题报告</c:when>
                            <c:when test="${doc.type == 'midterm'}">中期检查</c:when>
                            <c:when test="${doc.type == 'final'}">终稿</c:when>
                        </c:choose>
                    </td>
                    <td>${doc.topicTitle}</td>
                    <td>${doc.createdAt}</td>
                    <td>
                        <c:choose>
                            <c:when test="${doc.fileName != null}">
                                <a href="${pageContext.request.contextPath}/documents/download.action?id=${doc.id}" class="btn btn-outline-primary btn-sm">${doc.fileName}</a>
                            </c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${doc.status == 'submitted'}"><span class="badge bg-warning">待审核</span></c:when>
                            <c:when test="${doc.status == 'reviewed'}"><span class="badge bg-success">已审核</span></c:when>
                            <c:when test="${doc.status == 'rejected'}"><span class="badge bg-danger">已退回</span></c:when>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${doc.score != null}">
                                <span class="fw-bold">${doc.score}</span>分
                            </c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </td>
                    <td>${doc.feedback != null ? doc.feedback : '暂无反馈'}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty documents}">
        <p class="text-muted text-center">暂无成绩记录</p>
    </c:if>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
