<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<!-- 主内容区域 -->
<div class="main-content">
    <div class="page-header">
        <h4>仪表盘</h4>
        <span>欢迎, ${sessionScope.loginUser.name}</span>
    </div>

    <!-- 管理员仪表盘 -->
    <c:if test="${sessionScope.loginUser.role == 'admin'}">
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">教师人数</h5>
                        <p class="card-text fs-3 text-primary">${teacherCount}</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">学生人数</h5>
                        <p class="card-text fs-3 text-success">${studentCount}</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">课题数量</h5>
                        <p class="card-text fs-3 text-info">${topicCount}</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">已选题学生</h5>
                        <p class="card-text fs-3 text-warning">${selectedCount}</p>
                    </div>
                </div>
            </div>
        </div>
    </c:if>

    <!-- 教师仪表盘 -->
    <c:if test="${sessionScope.loginUser.role == 'teacher'}">
        <div class="row mb-4">
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">我的课题</h5>
                        <p class="card-text fs-3 text-primary">${topicCount}</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">待审批选题</h5>
                        <p class="card-text fs-3 text-warning">${pendingSelections}</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">待审核文档</h5>
                        <p class="card-text fs-3 text-danger">${pendingDocuments}</p>
                    </div>
                </div>
            </div>
        </div>
    </c:if>

    <!-- 学生仪表盘 -->
    <c:if test="${sessionScope.loginUser.role == 'student'}">
        <div class="row mb-4">
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">我的选题</h5>
                        <c:choose>
                            <c:when test="${mySelection != null}">
                                <p class="card-text text-success">${mySelection.topicTitle}</p>
                            </c:when>
                            <c:otherwise>
                                <p class="card-text text-muted">暂未选题</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">已提交文档</h5>
                        <p class="card-text fs-3 text-info">${docCount}</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title">系统公告</h5>
                        <p class="card-text fs-3 text-secondary">${announcementCount}</p>
                    </div>
                </div>
            </div>
        </div>
    </c:if>

    <!-- 公告列表 -->
    <div class="card">
        <div class="card-header">系统公告</div>
        <div class="card-body">
            <c:forEach var="ann" items="${announcements}">
                <div class="mb-3 pb-2 border-bottom">
                    <strong>
                        <c:if test="${ann.isTop == 1}">
                            <span class="badge bg-danger">置顶</span>
                        </c:if>
                        ${ann.title}
                    </strong>
                    <small class="text-muted float-end">${ann.createdAt}</small>
                    <p class="mb-0 mt-1 text-muted">${ann.content}</p>
                </div>
            </c:forEach>
            <c:if test="${empty announcements}">
                <p class="text-muted">暂无公告</p>
            </c:if>
        </div>
    </div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>