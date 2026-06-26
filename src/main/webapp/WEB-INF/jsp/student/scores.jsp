<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="main-content">
    <div class="page-header">
        <h4>我的成绩</h4>
    </div>

    <div class="card mb-4">
        <div class="card-header">最终题目</div>
        <div class="card-body">
            <c:choose>
                <c:when test="${assignment != null}">
                    <p class="mb-1">题目：<strong>${assignment.topicTitle}</strong></p>
                    <p class="mb-0">指导教师：${assignment.teacherName}</p>
                </c:when>
                <c:otherwise>
                    <p class="text-muted mb-0">尚未分配到最终题目。</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="card mb-4">
        <div class="card-header">毕业设计成绩</div>
        <div class="card-body">
            <table class="table table-bordered mb-0">
                <thead class="table-light">
                    <tr>
                        <th>成绩项</th>
                        <th>权重</th>
                        <th>成绩</th>
                        <th>评语</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>导师自评</td>
                        <td>40%</td>
                        <td>${evaluation != null && evaluation.advisorScore != null ? evaluation.advisorScore : '待评分'}</td>
                        <td>${evaluation != null && evaluation.advisorComment != null ? evaluation.advisorComment : '待评分'}</td>
                    </tr>
                    <tr>
                        <td>评阅评分</td>
                        <td>20%</td>
                        <td>${evaluation != null && evaluation.reviewerScore != null ? evaluation.reviewerScore : '待评分'}</td>
                        <td>${evaluation != null && evaluation.reviewerComment != null ? evaluation.reviewerComment : '待评分'}</td>
                    </tr>
                    <tr>
                        <td>答辩成绩</td>
                        <td>40%</td>
                        <td>${evaluation != null && evaluation.defenseScore != null ? evaluation.defenseScore : '待评分'}</td>
                        <td>${evaluation != null && evaluation.defenseComment != null ? evaluation.defenseComment : '待评分'}</td>
                    </tr>
                    <tr>
                        <td><strong>最终成绩</strong></td>
                        <td>100%</td>
                        <td colspan="2">
                            <c:choose>
                                <c:when test="${evaluation != null && evaluation.finalScore != null}">
                                    <strong>${evaluation.finalScore}</strong>
                                </c:when>
                                <c:otherwise>待完成</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <h5>阶段文档成绩</h5>
    <table class="table table-bordered table-hover">
        <thead class="table-light">
            <tr>
                <th>文档类型</th>
                <th>课题名称</th>
                <th>提交时间</th>
                <th>附件</th>
                <th>状态</th>
                <th>评分</th>
                <th>教师反馈</th>
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
                    <td>${doc.topicTitle}</td>
                    <td>${doc.createdAt}</td>
                    <td>
                        <c:choose>
                            <c:when test="${doc.fileName != null}">
                                <a href="${pageContext.request.contextPath}/documents/download.action?id=${doc.id}"
                                   class="btn btn-outline-primary btn-sm">${doc.fileName}</a>
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
                    <td>
                        <c:choose>
                            <c:when test="${doc.score != null}">
                                <span class="fw-bold">${doc.score}</span> 分
                            </c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </td>
                    <td>${doc.feedback != null ? doc.feedback : '暂无反馈'}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty documents}">
                <tr><td colspan="7" class="text-center text-muted">暂无文档成绩记录</td></tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>
