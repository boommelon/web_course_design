<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>答辩评分</h4>
    </div>

    <div class="card mb-4">
        <div class="card-header">录入答辩成绩</div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/teacher/defense.action" method="post" class="row g-3">
                <div class="col-md-4">
                    <label class="form-label">学生课题</label>
                    <select name="studentId" class="form-select" required onchange="this.form.topicId.value=this.options[this.selectedIndex].getAttribute('data-topic')">
                        <c:forEach var="sel" items="${students}">
                            <option value="${sel.studentId}" data-topic="${sel.topicId}">${sel.studentName} - ${sel.topicTitle}</option>
                        </c:forEach>
                    </select>
                    <input type="hidden" name="topicId" value="${empty students ? '' : students[0].topicId}">
                </div>
                <div class="col-md-2">
                    <label class="form-label">成绩</label>
                    <input type="number" name="score" class="form-control" min="0" max="100" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label">答辩意见</label>
                    <input type="text" name="comment" class="form-control">
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary">保存</button>
                </div>
            </form>
        </div>
    </div>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>学生</th>
                <th>课题</th>
                <th>成绩</th>
                <th>意见</th>
                <th>录入时间</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="score" items="${scores}">
                <tr>
                    <td>${score.studentName}</td>
                    <td>${score.topicTitle}</td>
                    <td>${score.score}</td>
                    <td>${score.comment}</td>
                    <td>${score.createdAt}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
