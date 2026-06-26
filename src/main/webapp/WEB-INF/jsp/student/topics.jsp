<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>浏览题目</h4>
        <span>${sessionScope.loginUser.major} · 当前第 ${round} 轮</span>
    </div>

    <c:if test="${not empty sessionScope.flash}">
        <div class="alert alert-info">${sessionScope.flash}</div>
        <c:remove var="flash" scope="session"/>
    </c:if>

    <c:if test="${assigned}">
        <div class="alert alert-success">
            你已被最终分配题目：<strong>${myAssignment.topicTitle}</strong>（指导教师：${myAssignment.teacherName}）。
            可前往“我的选题”查看详情。
        </div>
    </c:if>
    <c:if test="${!selectionOpen}">
        <div class="alert alert-warning">当前未开放选题。</div>
    </c:if>

    <p class="text-muted">下表为本专业已审核通过且尚未被分配的题目。每个题目本轮最多被 3 名学生填报为志愿。</p>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th style="width:60px;">编号</th>
                <th>题目名称</th>
                <th>指导教师</th>
                <th>题目描述</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="topic" items="${topics}">
                <tr>
                    <td class="text-center">${topic.id}</td>
                    <td>${topic.title}</td>
                    <td>${topic.teacherName}</td>
                    <td>${topic.description}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty topics}">
                <tr><td colspan="4" class="text-center text-muted">暂无可选题目</td></tr>
            </c:if>
        </tbody>
    </table>

    <c:if test="${canSelect && !empty topics}">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">填报本轮志愿（至少 1 个，最多 3 个，不能重复）</h5>
                <form action="${pageContext.request.contextPath}/student/selections.action" method="post"
                      class="selection-form">
                    <c:forEach var="rank" begin="1" end="3">
                        <div class="mb-2 row">
                            <label class="col-sm-2 col-form-label">第 ${rank} 志愿</label>
                            <div class="col-sm-6">
                                <select name="choice${rank}" class="form-select">
                                    <option value="">-- 不选 --</option>
                                    <c:forEach var="topic" items="${topics}">
                                        <option value="${topic.id}">${topic.title}（${topic.teacherName}）</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </c:forEach>
                    <button type="submit" class="btn btn-primary mt-2">提交志愿</button>
                </form>
            </div>
        </div>
    </c:if>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
