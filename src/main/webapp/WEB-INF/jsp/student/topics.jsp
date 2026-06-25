<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>浏览课题</h4>
        <span>当前第${selectionRound}轮选题</span>
    </div>

    <c:if test="${!studentSelectionOpen}">
        <div class="alert alert-warning">当前选题系统已关闭。</div>
    </c:if>
    <c:if test="${hasApproved}">
        <div class="alert alert-success">您已确定课题，不能继续参加后续选题。</div>
    </c:if>
    <c:if test="${!hasApproved && hasSubmittedCurrentRound}">
        <div class="alert alert-info">您已确认本轮选题，请等待管理员进入下一轮或最终分配。</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/student/selections.action" method="post"
          onsubmit="return this.querySelectorAll('input[name=topicIds]:checked').length > 0 && confirm('确认提交本轮选题？提交后本轮不能再次修改。');">
        <table class="table table-bordered table-hover">
            <thead class="table-light">
                <tr>
                    <th style="width: 70px;">选择</th>
                    <th>课题名称</th>
                    <th>指导教师</th>
                    <th>课题描述</th>
                    <th>剩余名额</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="topic" items="${topics}">
                    <tr>
                        <td class="text-center">
                            <input class="form-check-input" type="checkbox" name="topicIds" value="${topic.id}"
                                   <c:if test="${!canSubmitSelection}">disabled</c:if>>
                        </td>
                        <td>${topic.title}</td>
                        <td>${topic.teacherName}</td>
                        <td>${topic.description}</td>
                        <td>${topic.maxStudents - topic.selectedCount}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <c:if test="${canSubmitSelection && !empty topics}">
            <div class="mb-3">
                <label class="form-label">选题说明</label>
                <textarea name="reason" class="form-control" rows="3" placeholder="请简要说明选择这些课题的理由" required></textarea>
            </div>
            <button type="submit" class="btn btn-primary">确认本轮选题</button>
        </c:if>
    </form>

    <c:if test="${empty topics}">
        <p class="text-muted text-center">暂无可选课题</p>
    </c:if>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
