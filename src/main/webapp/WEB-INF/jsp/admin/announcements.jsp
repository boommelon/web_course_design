<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>公告管理</h4>
        <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addModal">发布公告</button>
    </div>

    
    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>ID</th>
                <th>标题</th>
                <th>置顶</th>
                <th>发布时间</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="ann" items="${announcements}">
                <tr data-announcement-id="${ann.id}">
                    <td>${ann.id}</td>
                    <td>${ann.title}</td>
                    <td>
                        <c:if test="${ann.isTop == 1}"><span class="badge bg-danger">置顶</span></c:if>
                        <c:if test="${ann.isTop == 0}">否</c:if>
                    </td>
                    <td>${ann.createdAt}</td>
                    <td>
                        <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#editModal${ann.id}">编辑</button>
                        <button type="button" class="btn btn-danger btn-sm ajax-delete-announcement" data-id="${ann.id}">删除</button>
                    </td>
                </tr>

                <div class="modal fade" id="editModal${ann.id}" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">编辑公告</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <form action="${pageContext.request.contextPath}/admin/announcements.action" method="post">
                                <input type="hidden" name="opttype" value="edit">
                                <input type="hidden" name="id" value="${ann.id}">
                                <div class="modal-body">
                                    <div class="mb-3">
                                        <label class="form-label">标题</label>
                                        <input type="text" name="title" class="form-control" value="${ann.title}" required>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">内容</label>
                                        <textarea name="content" class="form-control" rows="4" required>${ann.content}</textarea>
                                    </div>
                                    <div class="form-check">
                                        <input type="checkbox" name="isTop" class="form-check-input" id="isTopEdit${ann.id}"
                                               <c:if test="${ann.isTop == 1}">checked</c:if>>
                                        <label class="form-check-label" for="isTopEdit${ann.id}">设为置顶</label>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    <button type="submit" class="btn btn-primary">保存修改</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </tbody>
    </table>

    
    <div class="modal fade" id="addModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">发布公告</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/admin/announcements.action" method="post">
                    <input type="hidden" name="opttype" value="add">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">标题</label>
                            <input type="text" name="title" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">内容</label>
                            <textarea name="content" class="form-control" rows="4" required></textarea>
                        </div>
                        <div class="form-check">
                            <input type="checkbox" name="isTop" class="form-check-input" id="isTopCheck">
                            <label class="form-check-label" for="isTopCheck">设为置顶</label>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                        <button type="submit" class="btn btn-primary">发布</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
