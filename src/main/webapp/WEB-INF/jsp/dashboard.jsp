<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>首页</h4>
        <span>欢迎，${sessionScope.loginUser.name}</span>
    </div>

    <c:if test="${sessionScope.loginUser.role == 'admin'}">
        <h5>系统统计</h5>
        <table class="table table-bordered">
            <thead class="table-light">
                <tr>
                    <th>专业负责人</th>
                    <th>教师人数</th>
                    <th>学生人数</th>
                    <th>题目数量</th>
                    <th>已最终分配</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>${directorCount}</td>
                    <td>${teacherCount}</td>
                    <td>${studentCount}</td>
                    <td>${topicCount}</td>
                    <td>${assignedCount}</td>
                </tr>
            </tbody>
        </table>
    </c:if>

    <c:if test="${sessionScope.loginUser.role == 'director'}">
        <h5>本专业概览（${major} · 第 ${round} 轮）</h5>
        <table class="table table-bordered">
            <thead class="table-light">
                <tr>
                    <th>待审题目</th>
                    <th>本专业学生</th>
                    <th>本轮已提交志愿</th>
                    <th>已最终分配</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>${pendingTopicCount}</td>
                    <td>${studentCount}</td>
                    <td>${submittedCount}</td>
                    <td>${assignedCount}</td>
                </tr>
            </tbody>
        </table>
    </c:if>

    <c:if test="${sessionScope.loginUser.role == 'teacher'}">
        <h5>我的工作</h5>
        <table class="table table-bordered">
            <thead class="table-light">
                <tr>
                    <th>我的题目</th>
                    <th>指导学生</th>
                    <th>待审核文档</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>${topicCount}</td>
                    <td>${myStudentCount}</td>
                    <td>${pendingDocuments}</td>
                </tr>
            </tbody>
        </table>
    </c:if>

    <c:if test="${sessionScope.loginUser.role == 'student'}">
        <h5>我的信息</h5>
        <table class="table table-bordered">
            <thead class="table-light">
                <tr>
                    <th>我的题目</th>
                    <th>已提交文档</th>
                    <th>系统公告</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>
                        <c:choose>
                            <c:when test="${myAssignment != null}">${myAssignment.topicTitle}</c:when>
                            <c:otherwise>暂未分配</c:otherwise>
                        </c:choose>
                    </td>
                    <td>${docCount}</td>
                    <td>${announcementCount}</td>
                </tr>
            </tbody>
        </table>
    </c:if>

    <h5>系统公告</h5>
    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th width="25%">标题</th>
                <th>内容</th>
                <th width="18%">发布时间</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="ann" items="${announcements}">
                <tr>
                    <td>
                        <c:if test="${ann.isTop == 1}">[置顶]</c:if>
                        ${ann.title}
                    </td>
                    <td>${ann.content}</td>
                    <td>${ann.createdAt}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty announcements}">
                <tr>
                    <td colspan="3">暂无公告</td>
                </tr>
            </c:if>
        </tbody>
    </table>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
