<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>阶段开关</h4>
        <span>按流程顺序逐阶段开放</span>
    </div>

    <div class="card">
        <div class="card-header">毕业设计流程控制</div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/settings.action" method="post">
                <div class="form-check form-switch mb-3">
                    <input class="form-check-input" type="checkbox" name="topic_submit_open" id="s1"
                           <c:if test="${settings.topic_submit_open == 'true'}">checked</c:if>>
                    <label class="form-check-label" for="s1">① 教师出题开放</label>
                </div>
                <div class="form-check form-switch mb-3">
                    <input class="form-check-input" type="checkbox" name="topic_review_open" id="s2"
                           <c:if test="${settings.topic_review_open == 'true'}">checked</c:if>>
                    <label class="form-check-label" for="s2">② 专业负责人审题开放</label>
                </div>
                <div class="form-check form-switch mb-3">
                    <input class="form-check-input" type="checkbox" name="selection_open" id="s3"
                           <c:if test="${settings.selection_open == 'true'}">checked</c:if>>
                    <label class="form-check-label" for="s3">③ 学生选题开放</label>
                </div>
                <div class="mb-3 row">
                    <label class="col-sm-3 col-form-label">　　当前选题轮次</label>
                    <div class="col-sm-4">
                        <select name="current_round" class="form-select">
                            <option value="1" <c:if test="${settings.current_round == '1'}">selected</c:if>>第一轮</option>
                            <option value="2" <c:if test="${settings.current_round == '2'}">selected</c:if>>第二轮</option>
                        </select>
                    </div>
                </div>
                <div class="form-check form-switch mb-3">
                    <input class="form-check-input" type="checkbox" name="confirm_open" id="s4"
                           <c:if test="${settings.confirm_open == 'true'}">checked</c:if>>
                    <label class="form-check-label" for="s4">④ 专业负责人选题确认开放</label>
                </div>
                <div class="form-check form-switch mb-3">
                    <input class="form-check-input" type="checkbox" name="manual_assign_open" id="s5"
                           <c:if test="${settings.manual_assign_open == 'true'}">checked</c:if>>
                    <label class="form-check-label" for="s5">⑤ 强制分配开放</label>
                </div>
                <div class="form-check form-switch mb-3">
                    <input class="form-check-input" type="checkbox" name="document_upload_open" id="s6"
                           <c:if test="${settings.document_upload_open == 'true'}">checked</c:if>>
                    <label class="form-check-label" for="s6">⑥ 资料上传开放</label>
                </div>
                <div class="form-check form-switch mb-3">
                    <input class="form-check-input" type="checkbox" name="grade_open" id="s7"
                           <c:if test="${settings.grade_open == 'true'}">checked</c:if>>
                    <label class="form-check-label" for="s7">⑦ 成绩评定开放</label>
                </div>
                <div class="form-check form-switch mb-3">
                    <input class="form-check-input" type="checkbox" name="project_closed" id="s8"
                           <c:if test="${settings.project_closed == 'true'}">checked</c:if>>
                    <label class="form-check-label" for="s8">⑧ 项目归档（关闭全部阶段）</label>
                </div>
                <button type="submit" class="btn btn-primary">保存设置</button>
            </form>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
