<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>选题确认（第 ${round} 轮）</h4>
        <span>${sessionScope.loginUser.major}</span>
    </div>

    <c:if test="${not empty sessionScope.flash}">
        <div class="alert alert-info">${sessionScope.flash}</div>
        <c:remove var="flash" scope="session"/>
    </c:if>
    <c:if test="${!confirmOpen}">
        <div class="alert alert-warning">当前未开放选题确认，下方仅供查看。</div>
    </c:if>

    <p class="text-muted">下表按题目聚合显示本轮志愿候选。确认时系统保证一人一题、一题一人；
        已被分配的学生或题目无法再次确认。</p>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>题目</th>
                <th>指导教师</th>
                <th>志愿顺序</th>
                <th>学生</th>
                <th>学号</th>
                <th>班级</th>
                <th>状态</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="c" items="${choices}">
                <tr>
                    <td>${c.topicTitle}</td>
                    <td>${c.teacherName}</td>
                    <td class="text-center">第 ${c.choiceRank} 志愿</td>
                    <td>${c.studentName}</td>
                    <td>${c.studentNo}</td>
                    <td>${c.className}</td>
                    <td>
                        <c:choose>
                            <c:when test="${c.status == 'selected'}"><span class="badge bg-success">已中选</span></c:when>
                            <c:when test="${c.status == 'not_selected'}"><span class="badge bg-secondary">未中选</span></c:when>
                            <c:otherwise><span class="badge bg-warning">待确认</span></c:otherwise>
                        </c:choose>
                        <c:if test="${c.studentAssigned}"><span class="badge bg-info">学生已分配</span></c:if>
                        <c:if test="${c.topicAssigned}"><span class="badge bg-dark">题目已分配</span></c:if>
                    </td>
                    <td>
                        <c:if test="${confirmOpen && !c.studentAssigned && !c.topicAssigned}">
                            <form action="${pageContext.request.contextPath}/director/confirm.action" method="post" style="display:inline;"
                                  onsubmit="return confirm('确认把该学生分配给该题目？');">
                                <input type="hidden" name="opttype" value="confirm">
                                <input type="hidden" name="studentId" value="${c.studentId}">
                                <input type="hidden" name="topicId" value="${c.topicId}">
                                <input type="hidden" name="choiceRank" value="${c.choiceRank}">
                                <button type="submit" class="btn btn-success btn-sm">确认分配</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty choices}">
                <tr><td colspan="8" class="text-center text-muted">本轮暂无志愿记录</td></tr>
            </c:if>
        </tbody>
    </table>

    <h5>本专业已确认的最终分配</h5>
    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>学生</th>
                <th>学号</th>
                <th>题目</th>
                <th>指导教师</th>
                <th>来源</th>
                <th>操作</th>
            </tr>
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
                    <td>
                        <c:if test="${confirmOpen}">
                            <form action="${pageContext.request.contextPath}/director/confirm.action" method="post" style="display:inline;"
                                  onsubmit="return confirm('确认撤销该分配？');">
                                <input type="hidden" name="opttype" value="revoke">
                                <input type="hidden" name="studentId" value="${fa.studentId}">
                                <button type="submit" class="btn btn-outline-danger btn-sm">撤销</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty assignments}">
                <tr><td colspan="6" class="text-center text-muted">暂无已确认分配</td></tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
