<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>课题管理</h4>
        <c:choose>
            <c:when test="${topicPublishOpen}">
                <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addTopicModal">发布课题</button>
            </c:when>
            <c:otherwise>
                <button class="btn btn-secondary btn-sm" disabled>出题系统已关闭</button>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- 课题列表 -->
    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>课题名称</th>
                <th>描述</th>
                <th>最大人数</th>
                <th>已选人数</th>
                <th>审核状态</th>
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
                    <td>${topic.maxStudents}</td>
                    <td>${topic.selectedCount}</td>
                    <td>
                        <c:choose>
                            <c:when test="${topic.reviewStatus == 'pending'}"><span class="badge bg-warning">待管理员审核</span></c:when>
                            <c:when test="${topic.reviewStatus == 'approved'}"><span class="badge bg-success">已通过</span></c:when>
                            <c:when test="${topic.reviewStatus == 'rejected'}"><span class="badge bg-danger">已驳回</span></c:when>
                        </c:choose>
                    </td>
                    <td>
                        <c:if test="${topic.status == 'open'}"><span class="badge bg-success">开放</span></c:if>
                        <c:if test="${topic.status == 'closed'}"><span class="badge bg-secondary">关闭</span></c:if>
                    </td>
                    <td>${topic.reviewComment}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/teacher/topics.action?action=delete&id=${topic.id}"
                           class="btn btn-danger btn-sm"
                           onclick="return confirm('确定删除该课题？')">删除</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <!-- 发布课题弹窗 -->
    <div class="modal fade" id="addTopicModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">发布新课题</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/teacher/topics.action" method="post">
                    <input type="hidden" name="action" value="add">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">课题名称</label>
                            <input type="text" name="title" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">课题描述</label>
                            <textarea name="description" class="form-control" rows="3"></textarea>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">最大可选人数</label>
                            <input type="number" name="maxStudents" class="form-control" value="1" min="1" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                        <button type="submit" class="btn btn-primary">发布</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
