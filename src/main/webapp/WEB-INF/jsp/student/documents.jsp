<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>文档提交</h4>
    </div>

    <c:if test="${not empty sessionScope.flash}">
        <div class="alert alert-info">${sessionScope.flash}</div>
        <c:remove var="flash" scope="session"/>
    </c:if>
    
    <c:if test="${assignment == null}">
        <div class="alert alert-warning">你尚未被分配最终题目，暂时无法提交资料。</div>
    </c:if>

    <c:if test="${assignment != null && !documentUploadOpen}">
        <div class="alert alert-warning">当前资料上传系统已关闭，暂时无法提交新资料。</div>
    </c:if>


    <c:if test="${assignment != null && documentUploadOpen}">
        <div class="card mb-4">
            <div class="card-header">提交新文档</div>
            <div class="card-body">
                <p>当前题目：<strong>${assignment.topicTitle}</strong></p>
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
                        <input type="file" name="file" class="form-control"
                               accept=".doc,.docx,.pdf,.txt,.zip,.rar,.7z,.java,.sql" required>
                        <div class="form-text">支持 doc、docx、pdf、txt、zip、rar、7z、java、sql。中期、论文、源代码需按阶段审核通过后提交。</div>
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
