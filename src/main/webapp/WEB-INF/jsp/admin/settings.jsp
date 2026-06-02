<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>系统开关</h4>
    </div>

    <div class="card">
        <div class="card-header">毕业设计流程控制</div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/settings.action" method="post">
                <div class="form-check form-switch mb-3">
                    <input class="form-check-input" type="checkbox" name="topic_publish_open" id="topicPublishOpen"
                           <c:if test="${settings.topic_publish_open == 'true'}">checked</c:if>>
                    <label class="form-check-label" for="topicPublishOpen">教师出题开放</label>
                </div>
                <div class="form-check form-switch mb-3">
                    <input class="form-check-input" type="checkbox" name="student_selection_open" id="studentSelectionOpen"
                           <c:if test="${settings.student_selection_open == 'true'}">checked</c:if>>
                    <label class="form-check-label" for="studentSelectionOpen">学生选题开放</label>
                </div>
                <div class="form-check form-switch mb-3">
                    <input class="form-check-input" type="checkbox" name="document_upload_open" id="documentUploadOpen"
                           <c:if test="${settings.document_upload_open == 'true'}">checked</c:if>>
                    <label class="form-check-label" for="documentUploadOpen">学生文档上传开放</label>
                </div>
                <button type="submit" class="btn btn-primary">保存设置</button>
            </form>
        </div>
    </div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
