<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>答疑管理</h4>
    </div>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>学生</th>
                <th>课题</th>
                <th>问题</th>
                <th>状态</th>
                <th>答复</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="q" items="${questions}">
                <tr>
                    <td>${q.studentName}</td>
                    <td>${q.topicTitle}</td>
                    <td>${q.question}</td>
                    <td>
                        <c:choose>
                            <c:when test="${q.status == 'pending'}"><span class="badge bg-warning">待答复</span></c:when>
                            <c:otherwise><span class="badge bg-success">已答复</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td>${q.answer != null ? q.answer : '-'}</td>
                    <td>
                        <c:if test="${q.status == 'pending'}">
                            <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#answerModal${q.id}">答复</button>
                        </c:if>
                    </td>
                </tr>

                <div class="modal fade" id="answerModal${q.id}" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <form action="${pageContext.request.contextPath}/teacher/questions.action" method="post">
                                <input type="hidden" name="id" value="${q.id}">
                                <div class="modal-header">
                                    <h5 class="modal-title">答复学生问题</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <div class="modal-body">
                                    <p>${q.question}</p>
                                    <textarea name="answer" class="form-control" rows="4" required></textarea>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    <button type="submit" class="btn btn-primary">提交答复</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty questions}">
        <p class="text-muted text-center">暂无学生提问。</p>
    </c:if>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
