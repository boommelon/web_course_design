<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>强制分配</h4>
        <span>${sessionScope.loginUser.major}</span>
    </div>

    <c:if test="${not empty sessionScope.flash}">
        <div class="alert alert-info">${sessionScope.flash}</div>
        <c:remove var="flash" scope="session"/>
    </c:if>
    <c:if test="${!manualOpen}">
        <div class="alert alert-warning">当前未开放强制分配阶段。</div>
    </c:if>

    <p class="text-muted">第二轮后仍未匹配时，由专业负责人手工指定剩余学生与剩余题目。</p>

    <c:if test="${manualOpen}">
        <div class="card mb-4">
            <div class="card-header">手工指定</div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/director/assign.action" method="post" class="row g-3"
                      onsubmit="return confirm('确认强制分配？');">
                    <div class="col-md-5">
                        <label class="form-label">未分配学生</label>
                        <select name="studentId" class="form-select" required>
                            <option value="">-- 选择学生 --</option>
                            <c:forEach var="stu" items="${unassignedStudents}">
                                <option value="${stu.id}">${stu.name}（${stu.studentNo} / ${stu.className}）</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-5">
                        <label class="form-label">可分配题目</label>
                        <select name="topicId" class="form-select" required>
                            <option value="">-- 选择题目 --</option>
                            <c:forEach var="topic" items="${availableTopics}">
                                <option value="${topic.id}">${topic.title}（${topic.teacherName}）</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <button type="submit" class="btn btn-success">确认分配</button>
                    </div>
                </form>
                <c:if test="${empty unassignedStudents}">
                    <p class="text-muted mt-3 mb-0">本专业学生均已分配题目。</p>
                </c:if>
            </div>
        </div>
    </c:if>

    <div class="row">
        <div class="col-md-6">
            <h6>未分配学生（${fn:length(unassignedStudents)}）</h6>
            <ul class="list-group">
                <c:forEach var="stu" items="${unassignedStudents}">
                    <li class="list-group-item">${stu.name}（${stu.studentNo} / ${stu.className}）</li>
                </c:forEach>
            </ul>
            <c:if test="${empty unassignedStudents}">
                <p class="text-muted">无</p>
            </c:if>
        </div>
        <div class="col-md-6">
            <h6>剩余可分配题目</h6>
            <ul class="list-group">
                <c:forEach var="topic" items="${availableTopics}">
                    <li class="list-group-item">${topic.title} - ${topic.teacherName}</li>
                </c:forEach>
            </ul>
            <c:if test="${empty availableTopics}">
                <p class="text-muted">无</p>
            </c:if>
        </div>
    </div>

    <h5 class="mt-4">本专业已确认的最终分配</h5>
    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr><th>学生</th><th>学号</th><th>题目</th><th>指导教师</th><th>来源</th></tr>
        </thead>
        <tbody>
            <c:forEach var="fa" items="${assignments}">
                <tr>
                    <td>${fa.studentName}</td>
                    <td>${fa.studentNo}</td>
                    <td>${fa.topicTitle}</td>
                    <td>${fa.teacherName}</td>
                    <td>
                        <c:choose>
                            <c:when test="${fa.source == 'round1'}">第一轮</c:when>
                            <c:when test="${fa.source == 'round2'}">第二轮</c:when>
                            <c:otherwise>强制分配</c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty assignments}">
                <tr><td colspan="5" class="text-center text-muted">暂无</td></tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
