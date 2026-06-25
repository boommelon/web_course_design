<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>成绩评定</h4>
    </div>

    <c:if test="${not empty sessionScope.flash}">
        <div class="alert alert-info">${sessionScope.flash}</div>
        <c:remove var="flash" scope="session"/>
    </c:if>
    <c:if test="${!gradeOpen}">
        <div class="alert alert-warning">当前未开放成绩评定。</div>
    </c:if>

    <div class="card mb-4">
        <div class="card-header">指导学生（最终分配到我的题目下）</div>
        <div class="card-body">
            <table class="table table-bordered table-hover mb-0">
                <thead class="table-light">
                    <tr>
                        <th>学生</th>
                        <th>学号</th>
                        <th>题目</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="student" items="${students}">
                        <tr>
                            <td>${student.studentName}</td>
                            <td>${student.studentNo}</td>
                            <td>${student.topicTitle}</td>
                            <td>
                                <c:if test="${gradeOpen}">
                                    <button class="btn btn-primary btn-sm" data-bs-toggle="modal"
                                            data-bs-target="#evaluationModal${student.studentId}">评分评语</button>
                                </c:if>
                            </td>
                        </tr>

                        <c:if test="${gradeOpen}">
                            <div class="modal fade" id="evaluationModal${student.studentId}" tabindex="-1">
                                <div class="modal-dialog modal-lg">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title">评分评语 - ${student.studentName}</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <form action="${pageContext.request.contextPath}/teacher/evaluations.action" method="post">
                                            <input type="hidden" name="studentId" value="${student.studentId}">
                                            <div class="modal-body">
                                                <div class="mb-3">
                                                    <label class="form-label">最终成绩（0-100）</label>
                                                    <input type="number" name="score" class="form-control" min="0" max="100" required>
                                                </div>
                                                <div class="mb-3">
                                                    <label class="form-label">评语</label>
                                                    <textarea name="comment" class="form-control" rows="4" required></textarea>
                                                </div>
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                                <button type="submit" class="btn btn-primary">保存</button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                    <c:if test="${empty students}">
                        <tr><td colspan="4" class="text-center text-muted">暂无最终分配到你题目下的学生。</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <h5>已评成绩</h5>
    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>学生</th>
                <th>学号</th>
                <th>题目</th>
                <th>成绩</th>
                <th>评语</th>
                <th>时间</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="evaluation" items="${evaluations}">
                <tr>
                    <td>${evaluation.studentName}</td>
                    <td>${evaluation.studentNo}</td>
                    <td>${evaluation.topicTitle}</td>
                    <td>${evaluation.score != null ? evaluation.score : '-'}</td>
                    <td>${evaluation.comment}</td>
                    <td>${evaluation.createdAt}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty evaluations}">
                <tr><td colspan="6" class="text-center text-muted">暂无评分记录。</td></tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
