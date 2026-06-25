<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>我的题目</h4>
        <c:choose>
            <c:when test="${topicSubmitOpen}">
                <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addTopicModal">出题</button>
            </c:when>
            <c:otherwise>
                <button class="btn btn-secondary btn-sm" disabled>出题阶段未开放</button>
            </c:otherwise>
        </c:choose>
    </div>

    <p class="text-muted">所属专业：${sessionScope.loginUser.major}。题目经专业负责人审核通过后将锁定，不能再修改。</p>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>题目名称</th>
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
                        <c:choose>
                            <c:when test="${(topic.status == 'pending' || topic.status == 'rejected') && topicSubmitOpen}">
                                <button class="btn btn-outline-primary btn-sm" data-bs-toggle="modal"
                                        data-bs-target="#editTopicModal${topic.id}">修改</button>
                                <a href="${pageContext.request.contextPath}/teacher/topics.action?opttype=delete&id=${topic.id}"
                                   class="btn btn-danger btn-sm"
                                   onclick="return confirm('确定删除该题目？')">删除</a>
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted">已锁定</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>

                <c:if test="${(topic.status == 'pending' || topic.status == 'rejected') && topicSubmitOpen}">
                    <div class="modal fade" id="editTopicModal${topic.id}" tabindex="-1">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">修改题目</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <form action="${pageContext.request.contextPath}/teacher/topics.action" method="post">
                                    <input type="hidden" name="opttype" value="edit">
                                    <input type="hidden" name="id" value="${topic.id}">
                                    <div class="modal-body">
                                        <div class="mb-3">
                                            <label class="form-label">题目名称</label>
                                            <input type="text" name="title" class="form-control" value="${topic.title}" required>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">题目描述</label>
                                            <textarea name="description" class="form-control" rows="3">${topic.description}</textarea>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                        <button type="submit" class="btn btn-primary">保存并重新提交</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:if>
            </c:forEach>
            <c:if test="${empty topics}">
                <tr><td colspan="5" class="text-center text-muted">暂无题目</td></tr>
            </c:if>
        </tbody>
    </table>

    <div class="modal fade" id="addTopicModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">出题</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/teacher/topics.action" method="post">
                    <input type="hidden" name="opttype" value="add">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">题目名称</label>
                            <input type="text" name="title" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">题目描述</label>
                            <textarea name="description" class="form-control" rows="3"></textarea>
                        </div>
                        <p class="text-muted small">题目将归属你所在专业：${sessionScope.loginUser.major}</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                        <button type="submit" class="btn btn-primary">提交</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
