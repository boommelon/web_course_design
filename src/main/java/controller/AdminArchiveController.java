package controller;

import dao.DefenseScoreDao;
import dao.DocumentDao;
import dao.EvaluationDao;
import dao.SystemSettingDao;
import dao.TopicDao;
import dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminArchiveController extends HttpServlet {
    private UserDao userDao = new UserDao();
    private TopicDao topicDao = new TopicDao();
    private DocumentDao documentDao = new DocumentDao();
    private DefenseScoreDao defenseScoreDao = new DefenseScoreDao();
    private EvaluationDao evaluationDao = new EvaluationDao();
    private SystemSettingDao settingDao = new SystemSettingDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("studentCount", userDao.countByRole("student"));
            request.setAttribute("teacherCount", userDao.countByRole("teacher"));
            request.setAttribute("topicCount", topicDao.count());
            request.setAttribute("documentCount", documentDao.count());
            request.setAttribute("reviewedDocumentCount", documentDao.countByStatus("reviewed"));
            request.setAttribute("defenseScoreCount", defenseScoreDao.count());
            request.setAttribute("evaluationCount", evaluationDao.count());
            request.setAttribute("projectClosed", settingDao.isOpen("project_closed"));
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
            settingDao.update("project_closed", closed);
            if (closed) {
                settingDao.update("topic_publish_open", false);
                settingDao.update("student_selection_open", false);
                settingDao.update("document_upload_open", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/admin/archive.action");
    }
}
