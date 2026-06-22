package controller;

import bean.User;
import bean.Topic;
import bean.TopicSelection;
import dao.TopicDao;
import dao.TopicSelectionDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 教师-选题审批控制器
 */
public class TeacherSelectionController extends HttpServlet {

    private TopicSelectionDao selectionDao = new TopicSelectionDao();
    private TopicDao topicDao = new TopicDao();
    private static final String LIST_PAGE = "/teacher/selections.action";
    private static final String JSP_PAGE = "/WEB-INF/jsp/teacher/selections.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            showSelectionList(request, user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        request.getRequestDispatcher(JSP_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        Integer id = ParamUtil.getInt(request, "id");
        if (id == null) {
            redirectToList(request, response);
            return;
        }

        String action = request.getParameter("opttype");

        try {
            if ("approve".equals(action)) {
                approveSelection(id, user);
            }

            if ("reject".equals(action)) {
                rejectSelection(id, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        redirectToList(request, response);
    }

    private void showSelectionList(HttpServletRequest request, User user) throws Exception {
        request.setAttribute("selections", selectionDao.findByTeacher(user.getId()));
    }

    private void approveSelection(int selectionId, User teacher) throws Exception {
        TopicSelection selection = selectionDao.findById(selectionId);
        if (selection == null) {
            return;
        }

        Topic topic = topicDao.findById(selection.getTopicId());
        if (!canApprove(topic, teacher)) {
            return;
        }

        int rows = selectionDao.updateStatus(selectionId, "approved", teacher.getId());
        if (rows > 0) {
            topicDao.incrementSelected(topic.getId());
            topicDao.closeIfFull(topic.getId());
        }
    }

    private boolean canApprove(Topic topic, User teacher) {
        return topic != null
                && topic.getTeacherId() == teacher.getId()
                && topic.getSelectedCount() < topic.getMaxStudents();
    }

    private void rejectSelection(int selectionId, User teacher) throws Exception {
        selectionDao.updateStatus(selectionId, "rejected", teacher.getId());
    }

    private void redirectToList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + LIST_PAGE);
    }
}
