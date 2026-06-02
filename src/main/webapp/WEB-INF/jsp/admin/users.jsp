<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>用户管理</h4>
        <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addUserModal">新增用户</button>
    </div>

    <!-- 角色筛选 -->
    <div class="mb-3">
        <a href="${pageContext.request.contextPath}/admin/users.action" class="btn btn-outline-secondary btn-sm">全部</a>
        <a href="${pageContext.request.contextPath}/admin/users.action?role=teacher" class="btn btn-outline-primary btn-sm">教师</a>
        <a href="${pageContext.request.contextPath}/admin/users.action?role=student" class="btn btn-outline-success btn-sm">学生</a>
        <a href="${pageContext.request.contextPath}/admin/users.action?role=admin" class="btn btn-outline-danger btn-sm">管理员</a>
    </div>

    <!-- 用户列表表格 -->
    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>ID</th>
                <th>用户名</th>
                <th>姓名</th>
                <th>角色</th>
                <th>邮箱</th>
                <th>电话</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="user" items="${users}">
                <tr>
                    <td>${user.id}</td>
                    <td>${user.username}</td>
                    <td>${user.name}</td>
                    <td>
                        <c:choose>
                            <c:when test="${user.role == 'admin'}"><span class="badge bg-danger">管理员</span></c:when>
                            <c:when test="${user.role == 'teacher'}"><span class="badge bg-primary">教师</span></c:when>
                            <c:when test="${user.role == 'student'}"><span class="badge bg-success">学生</span></c:when>
                        </c:choose>
                    </td>
                    <td>${user.email}</td>
                    <td>${user.phone}</td>
                    <td>
                        <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#editUserModal${user.id}">编辑</button>
                        <a href="${pageContext.request.contextPath}/admin/users.action?action=delete&id=${user.id}"
                           class="btn btn-danger btn-sm"
                           onclick="return confirm('确定要删除该用户吗？')">删除</a>
                    </td>
                </tr>

                <div class="modal fade" id="editUserModal${user.id}" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">编辑用户</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <form action="${pageContext.request.contextPath}/admin/users.action" method="post">
                                <input type="hidden" name="action" value="edit">
                                <input type="hidden" name="id" value="${user.id}">
                                <div class="modal-body">
                                    <div class="mb-3">
                                        <label class="form-label">用户名</label>
                                        <input type="text" class="form-control" value="${user.username}" disabled>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">姓名</label>
                                        <input type="text" name="name" class="form-control" value="${user.name}" required>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">角色</label>
                                        <select name="role" class="form-select">
                                            <option value="student" <c:if test="${user.role == 'student'}">selected</c:if>>学生</option>
                                            <option value="teacher" <c:if test="${user.role == 'teacher'}">selected</c:if>>教师</option>
                                            <option value="admin" <c:if test="${user.role == 'admin'}">selected</c:if>>管理员</option>
                                        </select>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">邮箱</label>
                                        <input type="email" name="email" class="form-control" value="${user.email}">
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">电话</label>
                                        <input type="text" name="phone" class="form-control" value="${user.phone}">
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

    <!-- 新增用户弹窗 -->
    <div class="modal fade" id="addUserModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">新增用户</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/admin/users.action" method="post">
                    <input type="hidden" name="action" value="add">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">用户名</label>
                            <input type="text" name="username" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">密码</label>
                            <input type="password" name="password" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">姓名</label>
                            <input type="text" name="name" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">角色</label>
                            <select name="role" class="form-select">
                                <option value="student">学生</option>
                                <option value="teacher">教师</option>
                                <option value="admin">管理员</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">邮箱</label>
                            <input type="email" name="email" class="form-control">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">电话</label>
                            <input type="text" name="phone" class="form-control">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                        <button type="submit" class="btn btn-primary">确定添加</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
