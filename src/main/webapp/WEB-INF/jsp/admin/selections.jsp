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
                <input type="hidden" name="opttype" value="settings">
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
        <div class="card-header">第一轮选题对应关系确认</div>
        <div class="card-body">
            <p class="text-muted mb-0">
                第一轮选题面向所有尚未确定题目的学生开放。学生可以一次勾选多个课题并确认本轮选题，
                系统会按提交顺序确认第一个仍有名额的课题；课题满额后不再出现在学生可选列表中。
            </p>
        </div>
    </div>

    <div class="card mb-4">
        <div class="card-header">学生选题情况总览</div>
        <div class="card-body">
            <table class="table table-sm table-bordered">
                <thead class="table-light">
                    <tr>
                        <th>学生</th>
                        <th>用户名</th>
                        <th>最终课题</th>
                        <th>选题记录</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="student" items="${students}">
                        <c:set var="approvedTitle" value="未确定" />
                        <c:forEach var="sel" items="${selections}">
                            <c:if test="${sel.studentId == student.id && sel.status == 'approved'}">
                                <c:set var="approvedTitle" value="${sel.topicTitle}" />
                            </c:if>
                        </c:forEach>
                        <tr>
                            <td>${student.name}</td>
                            <td>${student.username}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${approvedTitle == '未确定'}">
                                        <span class="badge bg-warning">未确定</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-success">${approvedTitle}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:set var="hasRecord" value="false" />
                                <ul class="mb-0 ps-3">
                                    <c:forEach var="sel" items="${selections}">
                                        <c:if test="${sel.studentId == student.id}">
                                            <c:set var="hasRecord" value="true" />
                                            <li>
                                                第${sel.roundNo}轮：${sel.topicTitle}
                                                <c:choose>
                                                    <c:when test="${sel.status == 'pending'}">（待确认）</c:when>
                                                    <c:when test="${sel.status == 'approved'}">（已确定）</c:when>
                                                    <c:when test="${sel.status == 'rejected'}">（未中选）</c:when>
                                                </c:choose>
                                            </li>
                                        </c:if>
                                    </c:forEach>
                                </ul>
                                <c:if test="${!hasRecord}">
                                    <span class="text-muted">暂无记录</span>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div class="card mb-4">
        <div class="card-header">第二轮选题名单确认</div>
        <div class="card-body">
            <div class="row">
                <div class="col-md-6">
                    <h6>将要进行第二轮选题的学生名单</h6>
                    <table class="table table-sm table-bordered">
                        <thead class="table-light">
                            <tr>
                                <th>姓名</th>
                                <th>用户名</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="student" items="${unselectedStudents}">
                                <tr>
                                    <td>${student.name}</td>
                                    <td>${student.username}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <c:if test="${empty unselectedStudents}">
                        <p class="text-muted mb-0">暂无需要进入第二轮的学生。</p>
                    </c:if>
                </div>
                <div class="col-md-6">
                    <h6>第二轮可选题目名单</h6>
                    <table class="table table-sm table-bordered">
                        <thead class="table-light">
                            <tr>
                                <th>课题</th>
                                <th>指导教师</th>
                                <th>名额</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="topic" items="${availableTopics}">
                                <tr>
                                    <td>${topic.title}</td>
                                    <td>${topic.teacherName}</td>
                                    <td>${topic.selectedCount}/${topic.maxStudents}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <c:if test="${empty availableTopics}">
                        <p class="text-muted mb-0">暂无第二轮可选题目。</p>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <div class="card mb-4">
        <div class="card-header">最终未选题名单与未被选择题目名单</div>
        <div class="card-body">
            <p class="text-muted">关闭第二轮选题后，以下学生和课题用于最终人工分配。</p>
            <div class="row">
                <div class="col-md-6">
                    <h6>最终还未选题的学生名单</h6>
                    <ul class="list-group">
                        <c:forEach var="student" items="${unselectedStudents}">
                            <li class="list-group-item">${student.name}（${student.username}）</li>
                        </c:forEach>
                    </ul>
                    <c:if test="${empty unselectedStudents}">
                        <p class="text-muted mb-0">所有学生均已确定选题。</p>
                    </c:if>
                </div>
                <div class="col-md-6">
                    <h6>最终未被选择的题目名单</h6>
                    <ul class="list-group">
                        <c:forEach var="topic" items="${availableTopics}">
                            <li class="list-group-item">${topic.title} - ${topic.teacherName}（${topic.selectedCount}/${topic.maxStudents}）</li>
                        </c:forEach>
                    </ul>
                    <c:if test="${empty availableTopics}">
                        <p class="text-muted mb-0">暂无可分配课题。</p>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <div class="card mb-4">
        <div class="card-header">最终分配未选题学生</div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/selections.action" method="post" class="row g-3">
                <input type="hidden" name="opttype" value="forceAssign">
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

    <h5>选题记录与人工确认</h5>
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
                            <c:when test="${sel.status == 'rejected'}"><span class="badge bg-danger">未中选</span></c:when>
                        </c:choose>
                    </td>
                    <td>
                        <c:if test="${sel.status == 'pending'}">
                            <form action="${pageContext.request.contextPath}/admin/selections.action" method="post" style="display:inline;">
                                <input type="hidden" name="opttype" value="approve">
                                <input type="hidden" name="id" value="${sel.id}">
                                <input type="hidden" name="topicId" value="${sel.topicId}">
                                <button type="submit" class="btn btn-success btn-sm">确认对应</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/admin/selections.action" method="post" style="display:inline;">
                                <input type="hidden" name="opttype" value="reject">
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
