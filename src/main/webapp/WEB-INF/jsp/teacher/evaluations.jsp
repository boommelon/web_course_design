<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>导师自评 / 评阅评分</h4>
    </div>

    <c:if test="${not empty sessionScope.flash}">
        <div class="alert alert-info">${sessionScope.flash}</div>
        <c:remove var="flash" scope="session"/>
    </c:if>
    <c:if test="${!gradeOpen}">
        <div class="alert alert-warning">当前未开放成绩评定，页面只读。</div>
    </c:if>

    <div class="card mb-4">
        <div class="card-header">导师自评（40%）</div>
        <div class="card-body">
            <table class="table table-bordered table-hover mb-0">
                <thead class="table-light">
                    <tr>
                        <th>学生</th>
                        <th>学号</th>
                        <th>题目</th>
                        <th>导师分</th>
                        <th>评语</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="evaluation" items="${advisorEvaluations}">
                        <tr>
                            <td>${evaluation.studentName}</td>
                            <td>${evaluation.studentNo}</td>
                            <td>${evaluation.topicTitle}</td>
                            <td>${evaluation.advisorScore != null ? evaluation.advisorScore : '待评分'}</td>
                            <td>${evaluation.advisorComment != null ? evaluation.advisorComment : '-'}</td>
                            <td>
                                <c:if test="${gradeOpen}">
                                    <button class="btn btn-primary btn-sm" data-bs-toggle="modal"
                                            data-bs-target="#advisorModal${evaluation.studentId}">录入导师自评</button>
                                </c:if>
                            </td>
                        </tr>

                        <c:if test="${gradeOpen}">
                            <div class="modal fade" id="advisorModal${evaluation.studentId}" tabindex="-1">
                                <div class="modal-dialog modal-lg">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title">导师自评 - ${evaluation.studentName}</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <form action="${pageContext.request.contextPath}/teacher/evaluations.action" method="post">
                                            <input type="hidden" name="action" value="advisor">
                                            <input type="hidden" name="studentId" value="${evaluation.studentId}">
                                            <div class="modal-body">
                                                <div class="mb-3">
                                                    <label class="form-label">导师自评分（0-100）</label>
                                                    <input type="number" name="score" class="form-control" min="0" max="100"
                                                           value="${evaluation.advisorScore}" required>
                                                </div>
                                                <div class="mb-3">
                                                    <label class="form-label">导师评语</label>
                                                    <textarea name="comment" class="form-control" rows="4" required>${evaluation.advisorComment}</textarea>
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
                    <c:if test="${empty advisorEvaluations}">
                        <tr><td colspan="6" class="text-center text-muted">暂无最终分配到你题目下的学生。</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <div class="card mb-4">
        <div class="card-header">评阅评分（20%）</div>
        <div class="card-body">
            <table class="table table-bordered table-hover mb-0">
                <thead class="table-light">
                    <tr>
                        <th>学生</th>
                        <th>学号</th>
                        <th>题目</th>
                        <th>指导教师</th>
                        <th>评阅分</th>
                        <th>评语</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="evaluation" items="${reviewerEvaluations}">
                        <tr>
                            <td>${evaluation.studentName}</td>
                            <td>${evaluation.studentNo}</td>
                            <td>${evaluation.topicTitle}</td>
                            <td>${evaluation.teacherName}</td>
                            <td>${evaluation.reviewerScore != null ? evaluation.reviewerScore : '待评分'}</td>
                            <td>${evaluation.reviewerComment != null ? evaluation.reviewerComment : '-'}</td>
                            <td>
                                <c:if test="${gradeOpen}">
                                    <button class="btn btn-primary btn-sm" data-bs-toggle="modal"
                                            data-bs-target="#reviewerModal${evaluation.studentId}">录入评阅评分</button>
                                </c:if>
                            </td>
                        </tr>

                        <c:if test="${gradeOpen}">
                            <div class="modal fade" id="reviewerModal${evaluation.studentId}" tabindex="-1">
                                <div class="modal-dialog modal-lg">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title">评阅评分 - ${evaluation.studentName}</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <form action="${pageContext.request.contextPath}/teacher/evaluations.action" method="post">
                                            <input type="hidden" name="action" value="reviewer">
                                            <input type="hidden" name="studentId" value="${evaluation.studentId}">
                                            <div class="modal-body">
                                                <div class="mb-3">
                                                    <label class="form-label">评阅分（0-100）</label>
                                                    <input type="number" name="score" class="form-control" min="0" max="100"
                                                           value="${evaluation.reviewerScore}" required>
                                                </div>
                                                <div class="mb-3">
                                                    <label class="form-label">评阅评语</label>
                                                    <textarea name="comment" class="form-control" rows="4" required>${evaluation.reviewerComment}</textarea>
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
                    <c:if test="${empty reviewerEvaluations}">
                        <tr><td colspan="7" class="text-center text-muted">暂无分配给你的评阅任务。</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <div class="card">
        <div class="card-header">答辩评分（3 名答辩教师取平均，40%）</div>
        <div class="card-body">
            <table class="table table-bordered table-hover mb-0">
                <thead class="table-light">
                    <tr>
                        <th>学生</th>
                        <th>学号</th>
                        <th>题目</th>
                        <th>指导教师</th>
                        <th>我的答辩分</th>
                        <th>评语</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="evaluation" items="${defenseEvaluations}">
                        <c:set var="myDefenseScore" value="${null}" />
                        <c:forEach var="score" items="${evaluation.defenseScores}">
                            <c:if test="${score.teacherId == sessionScope.loginUser.id}">
                                <c:set var="myDefenseScore" value="${score}" />
                            </c:if>
                        </c:forEach>
                        <tr>
                            <td>${evaluation.studentName}</td>
                            <td>${evaluation.studentNo}</td>
                            <td>${evaluation.topicTitle}</td>
                            <td>${evaluation.teacherName}</td>
                            <td>${myDefenseScore.score != null ? myDefenseScore.score : '待评分'}</td>
                            <td>${myDefenseScore.comment != null ? myDefenseScore.comment : '-'}</td>
                            <td>
                                <c:if test="${gradeOpen}">
                                    <button class="btn btn-primary btn-sm" data-bs-toggle="modal"
                                            data-bs-target="#defenseModal${evaluation.studentId}">录入答辩评分</button>
                                </c:if>
                            </td>
                        </tr>

                        <c:if test="${gradeOpen}">
                            <div class="modal fade" id="defenseModal${evaluation.studentId}" tabindex="-1">
                                <div class="modal-dialog modal-lg">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title">答辩评分 - ${evaluation.studentName}</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <form action="${pageContext.request.contextPath}/teacher/evaluations.action" method="post">
                                            <input type="hidden" name="action" value="defense">
                                            <input type="hidden" name="studentId" value="${evaluation.studentId}">
                                            <div class="modal-body">
                                                <div class="mb-3">
                                                    <label class="form-label">答辩分（0-100）</label>
                                                    <input type="number" name="score" class="form-control" min="0" max="100"
                                                           value="${myDefenseScore.score}" required>
                                                </div>
                                                <div class="mb-3">
                                                    <label class="form-label">答辩评语</label>
                                                    <textarea name="comment" class="form-control" rows="4" required>${myDefenseScore.comment}</textarea>
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
                    <c:if test="${empty defenseEvaluations}">
                        <tr><td colspan="7" class="text-center text-muted">暂无分配给你的答辩任务。</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
