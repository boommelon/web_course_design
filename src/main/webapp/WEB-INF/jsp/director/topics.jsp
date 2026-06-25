<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>题目审核</h4>
        <span>${sessionScope.loginUser.major}</span>
    </div>

    <c:if test="${!reviewOpen}">
        <div class="alert alert-warning">当前未开放审题阶段，下方仅供查看。</div>
    </c:if>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>题目名称</th>
                <th>指导教师</th>
                <th>描述</th>
                <th>状态</th>
                <th>审核意见</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="topic" items="${topics}">
                <tr>
                    <td>${topic.title}</td>
                    <td>${topic.teacherName}</td>
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
                    <td>${topic.reviewComment}</td>
                    <td>
                        <c:if test="${reviewOpen && topic.status != 'assigned'}">
                            <button class="btn btn-success btn-sm" data-bs-toggle="modal" data-bs-target="#approveModal${topic.id}">通过</button>
                            <button class="btn btn-danger btn-sm" data-bs-toggle="modal" data-bs-target="#rejectModal${topic.id}">退回</button>
                        </c:if>
                    </td>
                </tr>

                <c:if test="${reviewOpen && topic.status != 'assigned'}">
                    <div class="modal fade" id="approveModal${topic.id}" tabindex="-1">
                        <div class="modal-dialog"><div class="modal-content">
                            <form action="${pageContext.request.contextPath}/director/topics.action" method="post">
                                <input type="hidden" name="id" value="${topic.id}">
                                <input type="hidden" name="opttype" value="approve">
                                <div class="modal-header"><h5 class="modal-title">通过题目</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
                                <div class="modal-body">
                                    <p>确认通过：<strong>${topic.title}</strong></p>
                                    <label class="form-label">审核意见</label>
                                    <textarea name="comment" class="form-control" rows="2">题目审核通过</textarea>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    <button type="submit" class="btn btn-success">确认通过</button>
                                </div>
                            </form>
                        </div></div>
                    </div>

                    <div class="modal fade" id="rejectModal${topic.id}" tabindex="-1">
                        <div class="modal-dialog"><div class="modal-content">
                            <form action="${pageContext.request.contextPath}/director/topics.action" method="post">
                                <input type="hidden" name="id" value="${topic.id}">
                                <input type="hidden" name="opttype" value="reject">
                                <div class="modal-header"><h5 class="modal-title">退回题目</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
                                <div class="modal-body">
                                    <p>确认退回：<strong>${topic.title}</strong></p>
                                    <label class="form-label">退回原因</label>
                                    <textarea name="comment" class="form-control" rows="2" required></textarea>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    <button type="submit" class="btn btn-danger">确认退回</button>
                                </div>
                            </form>
                        </div></div>
                    </div>
                </c:if>
            </c:forEach>
            <c:if test="${empty topics}">
                <tr><td colspan="6" class="text-center text-muted">本专业暂无题目</td></tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
