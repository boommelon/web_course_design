<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>课题审核</h4>
    </div>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>课题名称</th>
                <th>指导教师</th>
                <th>描述</th>
                <th>人数</th>
                <th>审核状态</th>
                <th>开放状态</th>
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
                    <td>${topic.selectedCount}/${topic.maxStudents}</td>
                    <td>
                        <c:choose>
                            <c:when test="${topic.reviewStatus == 'pending'}"><span class="badge bg-warning">待审核</span></c:when>
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
                        <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#editTopicModal${topic.id}">编辑</button>
                        <c:if test="${topic.reviewStatus == 'pending'}">
                            <button class="btn btn-success btn-sm" data-bs-toggle="modal" data-bs-target="#approveModal${topic.id}">通过</button>
                            <button class="btn btn-danger btn-sm" data-bs-toggle="modal" data-bs-target="#rejectModal${topic.id}">驳回</button>
                        </c:if>
                    </td>
                </tr>

                <div class="modal fade" id="editTopicModal${topic.id}" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <form action="${pageContext.request.contextPath}/admin/topics.action" method="post">
                                <input type="hidden" name="id" value="${topic.id}">
                                <input type="hidden" name="action" value="edit">
                                <div class="modal-header">
                                    <h5 class="modal-title">调整课题</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="mb-3">
                                        <label class="form-label">课题名称</label>
                                        <input type="text" name="title" class="form-control" value="${topic.title}" required>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">描述</label>
                                        <textarea name="description" class="form-control" rows="3">${topic.description}</textarea>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">最大人数</label>
                                        <input type="number" name="maxStudents" class="form-control" value="${topic.maxStudents}" min="1" required>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">开放状态</label>
                                        <select name="status" class="form-select">
                                            <option value="open" <c:if test="${topic.status == 'open'}">selected</c:if>>开放</option>
                                            <option value="closed" <c:if test="${topic.status == 'closed'}">selected</c:if>>关闭</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    <button type="submit" class="btn btn-primary">保存调整</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <div class="modal fade" id="approveModal${topic.id}" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <form action="${pageContext.request.contextPath}/admin/topics.action" method="post">
                                <input type="hidden" name="id" value="${topic.id}">
                                <input type="hidden" name="action" value="approve">
                                <div class="modal-header">
                                    <h5 class="modal-title">通过课题</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <div class="modal-body">
                                    <p>确认通过课题：<strong>${topic.title}</strong></p>
                                    <label class="form-label">审核意见</label>
                                    <textarea name="comment" class="form-control" rows="3">课题审核通过</textarea>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    <button type="submit" class="btn btn-success">确认通过</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <div class="modal fade" id="rejectModal${topic.id}" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <form action="${pageContext.request.contextPath}/admin/topics.action" method="post">
                                <input type="hidden" name="id" value="${topic.id}">
                                <input type="hidden" name="action" value="reject">
                                <div class="modal-header">
                                    <h5 class="modal-title">驳回课题</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <div class="modal-body">
                                    <p>确认驳回课题：<strong>${topic.title}</strong></p>
                                    <label class="form-label">驳回原因</label>
                                    <textarea name="comment" class="form-control" rows="3" required></textarea>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    <button type="submit" class="btn btn-danger">确认驳回</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty topics}">
        <p class="text-muted text-center">暂无课题</p>
    </c:if>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
