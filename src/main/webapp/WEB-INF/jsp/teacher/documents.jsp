<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>文档审核</h4>
    </div>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>学生</th>
                <th>课题</th>
                <th>文档类型</th>
                <th>内容摘要</th>
                <th>附件</th>
                <th>状态</th>
                <th>评分</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="doc" items="${documents}">
                <tr>
                    <td>${doc.studentName}</td>
                    <td>${doc.topicTitle}</td>
                    <td>
                        <c:choose>
                            <c:when test="${doc.type == 'proposal'}">开题报告</c:when>
                            <c:when test="${doc.type == 'midterm'}">中期检查</c:when>
                            <c:when test="${doc.type == 'final'}">终稿</c:when>
                        </c:choose>
                    </td>
                    <td>${doc.content}</td>
                    <td>
                        <c:choose>
                            <c:when test="${doc.fileName != null}">
                                <a href="${pageContext.request.contextPath}/documents/download.action?id=${doc.id}" class="btn btn-outline-primary btn-sm">${doc.fileName}</a>
                            </c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${doc.status == 'submitted'}"><span class="badge bg-warning">待审核</span></c:when>
                            <c:when test="${doc.status == 'reviewed'}"><span class="badge bg-success">已审核</span></c:when>
                            <c:when test="${doc.status == 'rejected'}"><span class="badge bg-danger">已退回</span></c:when>
                        </c:choose>
                    </td>
                    <td>${doc.score != null ? doc.score : '-'}</td>
                    <td>
                        <c:if test="${doc.status == 'submitted'}">
                            <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#reviewModal${doc.id}">审核</button>
                        </c:if>
                    </td>
                </tr>

                <!-- 审核弹窗 -->
                <div class="modal fade" id="reviewModal${doc.id}" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">审核文档 - ${doc.studentName}</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <form action="${pageContext.request.contextPath}/teacher/documents.action" method="post">
                                <input type="hidden" name="id" value="${doc.id}">
                                <div class="modal-body">
                                    <div class="mb-3">
                                        <label class="form-label">评分（0-100）</label>
                                        <input type="number" name="score" class="form-control" min="0" max="100" required>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">反馈意见</label>
                                        <textarea name="feedback" class="form-control" rows="3"></textarea>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">审核结果</label>
                                        <select name="status" class="form-select">
                                            <option value="reviewed">通过</option>
                                            <option value="rejected">退回修改</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    <button type="submit" class="btn btn-primary">提交审核</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty documents}">
        <p class="text-muted text-center">暂无待审核文档</p>
    </c:if>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
