package controller;

import dao.DocumentDao;
import dao.EvaluationDao;
import dao.FinalAssignmentDao;
import dao.SystemSettingDao;
import dao.TopicDao;
import dao.UserDao;
import util.Stage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员归档总览。项目归档时一并关闭全部阶段开关。
 */
public class AdminArchiveController extends HttpServlet {
    private UserDao userDao = new UserDao();
    private TopicDao topicDao = new TopicDao();
    private DocumentDao documentDao = new DocumentDao();
    private FinalAssignmentDao assignmentDao = new FinalAssignmentDao();
    private EvaluationDao evaluationDao = new EvaluationDao();
    private SystemSettingDao settingDao = new SystemSettingDao();

    private static final String[] STAGE_KEYS = {
            Stage.TOPIC_SUBMIT_OPEN, Stage.TOPIC_REVIEW_OPEN, Stage.SELECTION_OPEN,
            Stage.CONFIRM_OPEN, Stage.MANUAL_ASSIGN_OPEN, Stage.DOCUMENT_UPLOAD_OPEN,
            Stage.GRADE_OPEN
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("studentCount", userDao.countByRole("student"));
            request.setAttribute("teacherCount", userDao.countByRole("teacher"));
            request.setAttribute("topicCount", topicDao.count());
            request.setAttribute("assignmentCount", assignmentDao.count());
            request.setAttribute("documentCount", documentDao.count());
            request.setAttribute("reviewedDocumentCount", documentDao.countByStatus("reviewed"));
            request.setAttribute("evaluationCount", evaluationDao.count());
            request.setAttribute("projectClosed", settingDao.isOpen(Stage.PROJECT_CLOSED));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/admin/archive.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            boolean closed = request.getParameter("project_closed") != null;
            settingDao.update(Stage.PROJECT_CLOSED, closed);
            if (closed) {
                for (String key : STAGE_KEYS) {
                    settingDao.update(key, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/admin/archive.action");
    }
}
