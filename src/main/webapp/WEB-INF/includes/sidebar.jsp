<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 侧边栏 -->
<div class="sidebar">
    <div class="brand">毕业设计管理系统</div>

    <a href="${pageContext.request.contextPath}/dashboard.action">首页</a>
    <a href="${pageContext.request.contextPath}/profile.action">个人资料</a>

    <!-- 管理员菜单 -->
    <c:if test="${sessionScope.loginUser.role == 'admin'}">
        <a href="${pageContext.request.contextPath}/admin/users.action">用户管理</a>
        <a href="${pageContext.request.contextPath}/admin/import.action">数据导入</a>
        <a href="${pageContext.request.contextPath}/admin/topics.action">课题审核</a>
        <a href="${pageContext.request.contextPath}/admin/selections.action">在线选题管理</a>
        <a href="${pageContext.request.contextPath}/admin/groups.action">教师分组</a>
        <a href="${pageContext.request.contextPath}/admin/documents.action">文档管理</a>
        <a href="${pageContext.request.contextPath}/admin/templates.action">文件模板管理</a>
        <a href="${pageContext.request.contextPath}/admin/announcements.action">公告管理</a>
        <a href="${pageContext.request.contextPath}/admin/settings.action">系统开关</a>
        <a href="${pageContext.request.contextPath}/admin/archive.action">后期归档</a>
    </c:if>

    <!-- 教师菜单 -->
    <c:if test="${sessionScope.loginUser.role == 'teacher'}">
        <a href="${pageContext.request.contextPath}/teacher/topics.action">课题管理</a>
        <a href="${pageContext.request.contextPath}/teacher/tasks.action">任务管理</a>
        <a href="${pageContext.request.contextPath}/teacher/resources.action">资料管理</a>
        <a href="${pageContext.request.contextPath}/teacher/selections.action">选题审批</a>
        <a href="${pageContext.request.contextPath}/teacher/documents.action">文档审核</a>
        <a href="${pageContext.request.contextPath}/teacher/evaluations.action">自评互评</a>
        <a href="${pageContext.request.contextPath}/teacher/questions.action">答疑管理</a>
        <a href="${pageContext.request.contextPath}/teacher/defense.action">答辩评分</a>
    </c:if>

    <!-- 学生菜单 -->
    <c:if test="${sessionScope.loginUser.role == 'student'}">
        <a href="${pageContext.request.contextPath}/student/topics.action">浏览课题</a>
        <a href="${pageContext.request.contextPath}/student/selections.action">我的选题</a>
        <a href="${pageContext.request.contextPath}/student/resources.action">任务资料</a>
        <a href="${pageContext.request.contextPath}/student/documents.action">文档提交</a>
        <a href="${pageContext.request.contextPath}/student/questions.action">我的提问</a>
        <a href="${pageContext.request.contextPath}/student/scores.action">我的成绩</a>
    </c:if>

    <!-- 退出登录 -->
    <div class="logout">
        <a href="${pageContext.request.contextPath}/login.action">退出登录</a>
    </div>
</div>
