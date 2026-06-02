<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>自评互评与综合成绩</h4>
    </div>

    <div class="card mb-4">
        <div class="card-header">待评价学生</div>
        <div class="card-body">
            <table class="table table-bordered table-hover mb-0">
                <thead class="table-light">
                    <tr>
                        <th>学生</th>
                        <th>课题</th>
                        <th>选题轮次</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="student" items="${students}">
                        <tr>
                            <td>${student.studentName}</td>
                            <td>${student.topicTitle}</td>
                            <td>第${student.roundNo}轮</td>
                            <td>
                                <button class="btn btn-primary btn-sm" data-bs-toggle="modal"
                                        data-bs-target="#evaluationModal${student.id}">填写评价</button>
                            </td>
                        </tr>

                        <div class="modal fade" id="evaluationModal${student.id}" tabindex="-1">
                            <div class="modal-dialog modal-lg">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title">填写评价 - ${student.studentName}</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                    </div>
                                    <form action="${pageContext.request.contextPath}/teacher/evaluations.action" method="post">
                                        <input type="hidden" name="studentId" value="${student.studentId}">
                                        <input type="hidden" name="topicId" value="${student.topicId}">
                                        <div class="modal-body">
                                            <div class="mb-3">
                                                <label class="form-label">教师自评意见</label>
                                                <textarea name="selfComment" class="form-control" rows="4" required></textarea>
                                            </div>
                                            <div class="mb-3">
                                                <label class="form-label">互评意见</label>
                                                <textarea name="peerComment" class="form-control" rows="4" required></textarea>
                                            </div>
                                            <div class="mb-3">
                                                <label class="form-label">综合成绩（0-100）</label>
                                                <input type="number" name="score" class="form-control" min="0" max="100" required>
                                            </div>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                            <button type="submit" class="btn btn-primary">保存评价</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </tbody>
            </table>

            <c:if test="${empty students}">
                <p class="text-muted text-center mb-0">暂无已确定指导关系的学生。</p>
            </c:if>
        </div>
    </div>

    <h5>已填写评价</h5>
    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>学生</th>
                <th>课题</th>
                <th>自评意见</th>
                <th>互评意见</th>
                <th>综合成绩</th>
                <th>填写时间</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="evaluation" items="${evaluations}">
                <tr>
                    <td>${evaluation.studentName}</td>
                    <td>${evaluation.topicTitle}</td>
                    <td>${evaluation.selfComment}</td>
                    <td>${evaluation.peerComment}</td>
                    <td>${evaluation.score != null ? evaluation.score : '-'}</td>
                    <td>${evaluation.createdAt}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty evaluations}">
        <p class="text-muted text-center">暂无评价记录。</p>
    </c:if>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
