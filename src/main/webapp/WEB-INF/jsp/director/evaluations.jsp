<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>评阅与答辩</h4>
        <span>${sessionScope.loginUser.major}</span>
    </div>

    <c:if test="${not empty sessionScope.flash}">
        <div class="alert alert-info">${sessionScope.flash}</div>
        <c:remove var="flash" scope="session"/>
    </c:if>
    <c:if test="${!gradeOpen}">
        <div class="alert alert-warning">当前未开放成绩评定，页面只读。</div>
    </c:if>

    <div class="card">
        <div class="card-header">本专业成绩评定</div>
        <div class="card-body">
            <table class="table table-bordered table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>学生</th>
                        <th>题目</th>
                        <th>指导教师</th>
                        <th>评阅教师</th>
                        <th>答辩教师</th>
                        <th>导师分</th>
                        <th>评阅分</th>
                        <th>答辩分</th>
                        <th>最终成绩</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="evaluation" items="${evaluations}">
                        <tr>
                            <td>${evaluation.studentName}<br><span class="text-muted">${evaluation.studentNo}</span></td>
                            <td>${evaluation.topicTitle}</td>
                            <td>${evaluation.teacherName}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${gradeOpen}">
                                        <form action="${pageContext.request.contextPath}/director/evaluations.action" method="post" class="d-flex gap-2">
                                            <input type="hidden" name="action" value="assignReviewer">
                                            <input type="hidden" name="studentId" value="${evaluation.studentId}">
                                            <select name="reviewerTeacherId" class="form-select form-select-sm" required>
                                                <option value="">选择评阅教师</option>
                                                <c:forEach var="teacher" items="${teachers}">
                                                    <c:if test="${teacher.id != evaluation.teacherId}">
                                                        <option value="${teacher.id}" ${evaluation.reviewerTeacherId == teacher.id ? 'selected' : ''}>
                                                            ${teacher.name}
                                                        </option>
                                                    </c:if>
                                                </c:forEach>
                                            </select>
                                            <button type="submit" class="btn btn-outline-primary btn-sm">保存</button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        ${evaluation.reviewerTeacherName != null ? evaluation.reviewerTeacherName : '未分配'}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${gradeOpen}">
                                        <form action="${pageContext.request.contextPath}/director/evaluations.action" method="post">
                                            <input type="hidden" name="action" value="assignDefense">
                                            <input type="hidden" name="studentId" value="${evaluation.studentId}">
                                            <div class="d-flex gap-2">
                                                <select name="defenseTeacherId1" class="form-select form-select-sm" required>
                                                    <option value="">答辩1</option>
                                                    <c:forEach var="teacher" items="${teachers}">
                                                        <c:if test="${teacher.id != evaluation.teacherId}">
                                                            <option value="${teacher.id}" ${fn:length(evaluation.defenseScores) > 0 && evaluation.defenseScores[0].teacherId == teacher.id ? 'selected' : ''}>${teacher.name}</option>
                                                        </c:if>
                                                    </c:forEach>
                                                </select>
                                                <select name="defenseTeacherId2" class="form-select form-select-sm" required>
                                                    <option value="">答辩2</option>
                                                    <c:forEach var="teacher" items="${teachers}">
                                                        <c:if test="${teacher.id != evaluation.teacherId}">
                                                            <option value="${teacher.id}" ${fn:length(evaluation.defenseScores) > 1 && evaluation.defenseScores[1].teacherId == teacher.id ? 'selected' : ''}>${teacher.name}</option>
                                                        </c:if>
                                                    </c:forEach>
                                                </select>
                                                <select name="defenseTeacherId3" class="form-select form-select-sm" required>
                                                    <option value="">答辩3</option>
                                                    <c:forEach var="teacher" items="${teachers}">
                                                        <c:if test="${teacher.id != evaluation.teacherId}">
                                                            <option value="${teacher.id}" ${fn:length(evaluation.defenseScores) > 2 && evaluation.defenseScores[2].teacherId == teacher.id ? 'selected' : ''}>${teacher.name}</option>
                                                        </c:if>
                                                    </c:forEach>
                                                </select>
                                                <button type="submit" class="btn btn-outline-primary btn-sm">保存</button>
                                            </div>
                                            <div class="small text-muted mt-1">
                                                <c:forEach var="score" items="${evaluation.defenseScores}" varStatus="st">
                                                    ${st.index + 1}. ${score.teacherName}：${score.score != null ? score.score : '待评分'}<c:if test="${!st.last}">；</c:if>
                                                </c:forEach>
                                                <c:if test="${empty evaluation.defenseScores}">未分配</c:if>
                                            </div>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="score" items="${evaluation.defenseScores}" varStatus="st">
                                            ${st.index + 1}. ${score.teacherName}：${score.score != null ? score.score : '待评分'}<br>
                                        </c:forEach>
                                        <c:if test="${empty evaluation.defenseScores}">未分配</c:if>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${evaluation.advisorScore != null ? evaluation.advisorScore : '待评分'}</td>
                            <td>${evaluation.reviewerScore != null ? evaluation.reviewerScore : '待评分'}</td>
                            <td>${evaluation.defenseScore != null ? evaluation.defenseScore : '待评分'}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${evaluation.finalScore != null}">
                                        <strong>${evaluation.finalScore}</strong>
                                    </c:when>
                                    <c:otherwise>待完成</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty evaluations}">
                        <tr><td colspan="9" class="text-center text-muted">本专业暂无最终分配学生。</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
