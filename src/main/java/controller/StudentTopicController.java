package controller;

import bean.User;
import dao.FinalAssignmentDao;
import dao.SelectionDao;
import dao.SystemSettingDao;
import dao.TopicDao;
import util.Stage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 学生查看本专业可选题目（approved 且未被最终分配）。
 * 已被最终分配的学生只展示结果，不再选题。
 */
public class StudentTopicController extends HttpServlet {

    private TopicDao topicDao = new TopicDao();
    private SelectionDao selectionDao = new SelectionDao();
    private FinalAssignmentDao assignmentDao = new FinalAssignmentDao();
    private SystemSettingDao settingDao = new SystemSettingDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            int round = settingDao.getInt(Stage.CURRENT_ROUND, 1);
            boolean assigned = assignmentDao.isStudentAssigned(user.getId());

            request.setAttribute("topics",
                    topicDao.findSelectableByMajor(user.getCollege(), user.getMajor()));
            request.setAttribute("selectionOpen", settingDao.isOpen(Stage.SELECTION_OPEN));
            request.setAttribute("round", round);
            request.setAttribute("assigned", assigned);
            request.setAttribute("myAssignment", assignmentDao.findByStudent(user.getId()));
            request.setAttribute("myApplication",
                    selectionDao.findApplicationWithChoices(user.getId(), round));
            request.setAttribute("canSelect", settingDao.isOpen(Stage.SELECTION_OPEN) && !assigned);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/student/topics.jsp").forward(request, response);
    }
}
