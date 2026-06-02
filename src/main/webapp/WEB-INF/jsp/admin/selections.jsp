<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>在线选题管理</h4>
    </div>

    <div class="card mb-4">
        <div class="card-header">选题系统控制</div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/selections.action" method="post" class="row g-3 align-items-end">
                <input type="hidden" name="action" value="settings">
                <div class="col-md-3">
                    <label class="form-label">当前轮次</label>
                    <select name="selection_round" class="form-select">
                        <option value="1" <c:if test="${settings.selection_round == '1'}">selected</c:if>>第一轮选题</option>
                        <option value="2" <c:if test="${settings.selection_round == '2'}">selected</c:if>>第二轮选题</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <div class="form-check form-switch">
                        <input class="form-check-input" type="checkbox" name="student_selection_open" id="selectionOpen"
                               <c:if test="${settings.student_selection_open == 'true'}">checked</c:if>>
                        <label class="form-check-label" for="selectionOpen">开启选题系统</label>
                    </div>
                </div>
                <div class="col-md-3">
                    <button type="submit" class="btn btn-primary">保存控制状态</button>
                </div>
            </form>
        </div>
    </div>

    <div class="card mb-4">
        <div class="card-header">最终分配未选题学生</div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/selections.action" method="post" class="row g-3">
                <input type="hidden" name="action" value="forceAssign">
                <div class="col-md-4">
                    <label class="form-label">未确定选题学生</label>
                    <select name="studentId" class="form-select" required>
                        <c:forEach var="student" items="${unselectedStudents}">
                            <option value="${student.id}">${student.name}（${student.username}）</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-5">
                    <label class="form-label">可分配课题</label>
                    <select name="topicId" class="form-select" required>
                        <c:forEach var="topic" items="${availableTopics}">
                            <option value="${topic.id}">${topic.title} - ${topic.teacherName}（${topic.selectedCount}/${topic.maxStudents}）</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3 d-flex align-items-end">
                    <button type="submit" class="btn btn-success">确认分配</button>
                </div>
            </form>
            <c:if test="${empty unselectedStudents}">
                <p class="text-muted mt-3 mb-0">所有学生均已确定选题。</p>
            </c:if>
        </div>
    </div>

    <h5>选题申请与确认结果</h5>
    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>轮次</th>
                <th>学生</th>
                <th>课题</th>
                <th>申请理由</th>
                <th>状态</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="sel" items="${selections}">
                <tr>
                    <td>第${sel.roundNo}轮</td>
                    <td>${sel.studentName}</td>
                    <td>${sel.topicTitle}</td>
                    <td>${sel.reason}</td>
                    <td>
                        <c:choose>
                            <c:when test="${sel.status == 'pending'}"><span class="badge bg-warning">待确认</span></c:when>
                            <c:when test="${sel.status == 'approved'}"><span class="badge bg-success">已确定</span></c:when>
                            <c:when test="${sel.status == 'rejected'}"><span class="badge bg-danger">已驳回</span></c:when>
                        </c:choose>
                    </td>
                    <td>
                        <c:if test="${sel.status == 'pending'}">
                            <form action="${pageContext.request.contextPath}/admin/selections.action" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="approve">
                                <input type="hidden" name="id" value="${sel.id}">
                                <input type="hidden" name="topicId" value="${sel.topicId}">
                                <button type="submit" class="btn btn-success btn-sm">确认对应</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/admin/selections.action" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="reject">
                                <input type="hidden" name="id" value="${sel.id}">
                                <button type="submit" class="btn btn-danger btn-sm">驳回</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty selections}">
        <p class="text-muted text-center">暂无选题申请。</p>
    </c:if>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
