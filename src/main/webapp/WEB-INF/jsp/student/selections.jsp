<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>我的选题</h4>
        <span>${sessionScope.loginUser.major} · 当前第 ${round} 轮</span>
    </div>

    <c:if test="${not empty sessionScope.flash}">
        <div class="alert alert-info">${sessionScope.flash}</div>
        <c:remove var="flash" scope="session"/>
    </c:if>

    <c:choose>
        <c:when test="${assigned}">
            <div class="alert alert-success">
                <h5 class="mb-2">最终分配结果</h5>
                <p class="mb-1">题目：<strong>${myAssignment.topicTitle}</strong></p>
                <p class="mb-1">指导教师：${myAssignment.teacherName}</p>
                <p class="mb-0">分配来源：
                    <c:choose>
                        <c:when test="${myAssignment.source == 'round1'}">第一轮</c:when>
                        <c:when test="${myAssignment.source == 'round2'}">第二轮</c:when>
                        <c:otherwise>强制分配</c:otherwise>
                    </c:choose>
                    <c:if test="${myAssignment.choiceRank != null}">（第 ${myAssignment.choiceRank} 志愿）</c:if>
                </p>
            </div>
        </c:when>
        <c:otherwise>
            <c:if test="${!selectionOpen}">
                <div class="alert alert-warning">当前未开放选题。</div>
            </c:if>
        </c:otherwise>
    </c:choose>

    <h5>本轮（第 ${round} 轮）志愿</h5>
    <c:choose>
        <c:when test="${myApplication != null}">
            <p class="text-muted">提交时间：${myApplication.submitTime}</p>
            <table class="table table-bordered table-hover">
                <thead class="table-light">
                    <tr>
                        <th style="width:90px;">志愿顺序</th>
                        <th>题目名称</th>
                        <th>指导教师</th>
                        <th>状态</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="choice" items="${myApplication.choices}">
                        <tr>
                            <td class="text-center">第 ${choice.choiceRank} 志愿</td>
                            <td>${choice.topicTitle}</td>
                            <td>${choice.teacherName}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${choice.status == 'pending'}"><span class="badge bg-warning">待确认</span></c:when>
                                    <c:when test="${choice.status == 'selected'}"><span class="badge bg-success">已中选</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary">未中选</span></c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <p class="text-muted">本轮还没有提交志愿，请前往“浏览题目”填报 1-3 个志愿。</p>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
