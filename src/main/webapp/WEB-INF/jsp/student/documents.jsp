<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>文档提交</h4>
    </div>

    <!-- 未选题通过时的提示 -->
    <c:if test="${selection == null}">
        <div class="alert alert-warning">您的选题尚未通过审批，暂时无法提交文档。</div>
    </c:if>

    <c:if test="${selection != null && !documentUploadOpen}">
        <div class="alert alert-warning">当前文档上传系统已关闭，暂时无法提交新文档。</div>
    </c:if>

    <!-- 选题通过后显示提交表单 -->
    <c:if test="${selection != null && documentUploadOpen}">
        <div class="card mb-4">
            <div class="card-header">提交新文档</div>
            <div class="card-body">
                <p>当前课题：<strong>${selection.topicTitle}</strong></p>
                <form action="${pageContext.request.contextPath}/student/documents.action" method="post" enctype="multipart/form-data">
                    <div class="mb-3">
                        <label class="form-label">文档类型</label>
                        <select name="type" class="form-select" required>
                            <option value="proposal">开题报告</option>
                            <option value="midterm">中期检查</option>
                            <option value="final">毕业论文</option>
                            <option value="source">源代码</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">上传文件</label>
                        <input type="file" name="file" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">内容摘要</label>
                        <textarea name="content" class="form-control" rows="5" placeholder="请输入文档内容摘要" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">提交文档</button>
                </form>
            </div>
        </div>
    </c:if>

    <!-- 已提交文档列表 -->
    <h5>已提交文档</h5>
    <table class="table table-bordered">
        <thead class="table-light">
            <tr>
                <th>文档类型</th>
                <th>提交时间</th>
                <th>状态</th>
                <th>附件</th>
                <th>评分</th>
                <th>反馈</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="doc" items="${documents}">
                <tr>
                    <td>
                        <c:choose>
                            <c:when test="${doc.type == 'proposal'}">开题报告</c:when>
                            <c:when test="${doc.type == 'midterm'}">中期检查</c:when>
                            <c:when test="${doc.type == 'final'}">毕业论文</c:when>
                            <c:when test="${doc.type == 'source'}">源代码</c:when>
                        </c:choose>
                    </td>
                    <td>${doc.createdAt}</td>
                    <td>
                        <c:choose>
                            <c:when test="${doc.status == 'submitted'}"><span class="badge bg-warning">待审核</span></c:when>
                            <c:when test="${doc.status == 'reviewed'}"><span class="badge bg-success">已审核</span></c:when>
                            <c:when test="${doc.status == 'rejected'}"><span class="badge bg-danger">已退回</span></c:when>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${doc.fileName != null}">
                                <a href="${pageContext.request.contextPath}/documents/download.action?id=${doc.id}" class="btn btn-outline-primary btn-sm">${doc.fileName}</a>
                            </c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </td>
                    <td>${doc.score != null ? doc.score : '-'}</td>
                    <td>${doc.feedback != null ? doc.feedback : '-'}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty documents}">
        <p class="text-muted text-center">暂无已提交文档</p>
    </c:if>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
