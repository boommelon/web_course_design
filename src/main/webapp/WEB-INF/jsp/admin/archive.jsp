<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>答辩结束与后期归档</h4>
    </div>

    <div class="row mb-4">
        <div class="col-md-3 mb-3">
            <div class="card">
                <div class="card-body">
                    <h6 class="text-muted">学生人数</h6>
                    <h3>${studentCount}</h3>
                </div>
            </div>
        </div>
        <div class="col-md-3 mb-3">
            <div class="card">
                <div class="card-body">
                    <h6 class="text-muted">教师人数</h6>
                    <h3>${teacherCount}</h3>
                </div>
            </div>
        </div>
        <div class="col-md-3 mb-3">
            <div class="card">
                <div class="card-body">
                    <h6 class="text-muted">课题数量</h6>
                    <h3>${topicCount}</h3>
                </div>
            </div>
        </div>
        <div class="col-md-3 mb-3">
            <div class="card">
                <div class="card-body">
                    <h6 class="text-muted">最终分配数</h6>
                    <h3>${assignmentCount}</h3>
                </div>
            </div>
        </div>
    </div>

    <div class="card mb-4">
        <div class="card-header">归档检查</div>
        <div class="card-body">
            <table class="table table-bordered mb-0">
                <tr>
                    <th>学生提交材料总数</th>
                    <td>${documentCount}</td>
                </tr>
                <tr>
                    <th>已审核材料数</th>
                    <td>${reviewedDocumentCount}</td>
                </tr>
                <tr>
                    <th>成绩评定记录数</th>
                    <td>${evaluationCount}</td>
                </tr>
                <tr>
                    <th>当前流程状态</th>
                    <td>
                        <c:choose>
                            <c:when test="${projectClosed}">
                                <span class="badge bg-success">答辩结束，已进入后期归档</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-warning">流程进行中</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <div class="card">
        <div class="card-header">结束流程控制</div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/archive.action" method="post">
                <div class="form-check form-switch mb-3">
                    <input class="form-check-input" type="checkbox" name="project_closed" id="projectClosed"
                           <c:if test="${projectClosed}">checked</c:if>>
                    <label class="form-check-label" for="projectClosed">确认答辩结束并完成后期归档</label>
                </div>
                <button type="submit" class="btn btn-primary">保存归档状态</button>
            </form>
        </div>
    </div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
