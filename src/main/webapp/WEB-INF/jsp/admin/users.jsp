<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>用户管理</h4>
        <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addUserModal">新增用户</button>
    </div>

    <c:if test="${not empty error && empty openModal}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
    <c:if test="${not empty message}">
        <div class="alert alert-success">${message}</div>
    </c:if>


    <div class="mb-3">
        <a href="${pageContext.request.contextPath}/admin/users.action" class="btn btn-outline-secondary btn-sm">全部</a>
        <a href="${pageContext.request.contextPath}/admin/users.action?role=director" class="btn btn-outline-warning btn-sm">专业负责人</a>
        <a href="${pageContext.request.contextPath}/admin/users.action?role=teacher" class="btn btn-outline-primary btn-sm">教师</a>
        <a href="${pageContext.request.contextPath}/admin/users.action?role=student" class="btn btn-outline-success btn-sm">学生</a>
        <a href="${pageContext.request.contextPath}/admin/users.action?role=admin" class="btn btn-outline-danger btn-sm">管理员</a>
    </div>


    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>ID</th>
                <th>用户名</th>
                <th>姓名</th>
                <th>角色</th>
                <th>专业</th>
                <th>学号/班级</th>
                <th>电话</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="user" items="${users}">
                <tr data-user-id="${user.id}">
                    <td>${user.id}</td>
                    <td>${user.username}</td>
                    <td>${user.name}</td>
                    <td>
                        <c:choose>
                            <c:when test="${user.role == 'admin'}"><span class="badge bg-danger">管理员</span></c:when>
                            <c:when test="${user.role == 'director'}"><span class="badge bg-warning">专业负责人</span></c:when>
                            <c:when test="${user.role == 'teacher'}"><span class="badge bg-primary">教师</span></c:when>
                            <c:when test="${user.role == 'student'}"><span class="badge bg-success">学生</span></c:when>
                        </c:choose>
                    </td>
                    <td>${user.major}</td>
                    <td>
                        <c:if test="${not empty user.studentNo}">${user.studentNo}</c:if>
                        <c:if test="${not empty user.className}"> / ${user.className}</c:if>
                    </td>
                    <td>${user.phone}</td>
                    <td>
                        <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#editUserModal${user.id}">编辑</button>
                        <a class="btn btn-secondary btn-sm"
                           href="${pageContext.request.contextPath}/admin/users.action?opttype=resetPassword&id=${user.id}"
                           onclick="return confirm('确定将该用户密码重置为123456？')">重置密码</a>
                        <button type="button" class="btn btn-danger btn-sm ajax-delete-user" data-id="${user.id}">删除</button>
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
                                <input type="hidden" name="opttype" value="edit">
                                <input type="hidden" name="id" value="${user.id}">
                                <input type="hidden" name="roleFilter" value="${roleFilter}">
                                <div class="modal-body">
                                    <c:if test="${errorUserId == user.id && not empty error}">
                                        <div class="alert alert-danger">${error}</div>
                                    </c:if>
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
                                            <option value="director" <c:if test="${user.role == 'director'}">selected</c:if>>专业负责人</option>
                                            <option value="admin" <c:if test="${user.role == 'admin'}">selected</c:if>>管理员</option>
                                        </select>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">学院</label>
                                        <input type="text" name="college" class="form-control" value="${user.college}">
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">专业</label>
                                        <input type="text" name="major" class="form-control" value="${user.major}">
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">学号（学生）</label>
                                        <input type="text" name="studentNo" class="form-control" value="${user.studentNo}">
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">班级（学生）</label>
                                        <input type="text" name="className" class="form-control" value="${user.className}">
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">邮箱</label>
                                        <input type="email" name="email" class="form-control" value="${user.email}">
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">电话</label>
                                        <input type="tel" name="phone" class="form-control" value="${user.phone}" pattern="1[0-9]{10}" maxlength="11">
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


    <div class="modal fade" id="addUserModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">新增用户</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/admin/users.action" method="post">
                    <input type="hidden" name="opttype" value="add">
                    <input type="hidden" name="roleFilter" value="${roleFilter}">
                    <div class="modal-body">
                        <c:if test="${openModal == 'addUserModal' && not empty error}">
                            <div class="alert alert-danger">${error}</div>
                        </c:if>
                        <div class="mb-3">
                            <label class="form-label">用户名</label>
                            <input type="text" name="username" class="form-control" id="addUsername" value="${formUsername}" required>
                            <div class="form-text username-check-message"></div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">密码</label>
                            <input type="password" name="password" class="form-control" value="${formPassword}" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">姓名</label>
                            <input type="text" name="name" class="form-control" value="${formName}" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">角色</label>
                            <select name="role" class="form-select">
                                <option value="student" <c:if test="${formRole == 'student'}">selected</c:if>>学生</option>
                                <option value="teacher" <c:if test="${formRole == 'teacher'}">selected</c:if>>教师</option>
                                <option value="director" <c:if test="${formRole == 'director'}">selected</c:if>>专业负责人</option>
                                <option value="admin" <c:if test="${formRole == 'admin'}">selected</c:if>>管理员</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">学院</label>
                            <input type="text" name="college" class="form-control" value="${formCollege}">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">专业（学生/专业负责人必填）</label>
                            <input type="text" name="major" class="form-control" value="${formMajor}">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">学号（学生）</label>
                            <input type="text" name="studentNo" class="form-control" value="${formStudentNo}">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">班级（学生）</label>
                            <input type="text" name="className" class="form-control" value="${formClassName}">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">邮箱</label>
                            <input type="email" name="email" class="form-control" value="${formEmail}">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">电话</label>
                            <input type="tel" name="phone" class="form-control" value="${formPhone}" pattern="1[0-9]{10}" maxlength="11">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                        <button type="submit" class="btn btn-primary add-user-submit">确定添加</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <c:if test="${not empty openModal}">
        <script>
            window.onload = function () {
                var button = document.querySelector('[data-bs-target="#${openModal}"]');
                if (button) {
                    button.click();
                }
            };
        </script>
    </c:if>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
