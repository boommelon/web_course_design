<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>浏览课题</h4>
    </div>

    <c:if test="${!studentSelectionOpen}">
        <div class="alert alert-warning">当前选题系统已关闭，暂时不能提交新的选题申请。</div>
    </c:if>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>课题名称</th>
                <th>指导教师</th>
                <th>课题描述</th>
                <th>可选人数</th>
                <th>已选人数</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="topic" items="${topics}">
                <tr>
                    <td>${topic.title}</td>
                    <td>${topic.teacherName}</td>
                    <td>${topic.description}</td>
                    <td>${topic.maxStudents}</td>
                    <td>${topic.selectedCount}</td>
                    <td>
                        <c:choose>
                            <c:when test="${studentSelectionOpen}">
                                <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#applyModal${topic.id}">申请选题</button>
                            </c:when>
                            <c:otherwise>
                                <button class="btn btn-secondary btn-sm" disabled>选题关闭</button>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>

                <!-- 申请弹窗 -->
                <div class="modal fade" id="applyModal${topic.id}" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">申请选题 - ${topic.title}</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <form action="${pageContext.request.contextPath}/student/selections.action" method="post">
                                <input type="hidden" name="topicId" value="${topic.id}">
                                <div class="modal-body">
                                    <div class="mb-3">
                                        <label class="form-label">申请理由</label>
                                        <textarea name="reason" class="form-control" rows="3" placeholder="请简要说明选择该课题的理由" required></textarea>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    <button type="submit" class="btn btn-primary">提交申请</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty topics}">
        <p class="text-muted text-center">暂无开放课题</p>
    </c:if>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
