package controller;

import bean.FinalAssignment;
import bean.User;
import dao.EvaluationDao;
import dao.FinalAssignmentDao;
import dao.SystemSettingDao;
import util.ParamUtil;
import util.Stage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 教师评分评语（简化版：成绩 + 评语）。
 * 只能给最终分配到自己题目下的学生评分。
 */
public class TeacherEvaluationController extends HttpServlet {

    private FinalAssignmentDao assignmentDao = new FinalAssignmentDao();
    private EvaluationDao evaluationDao = new EvaluationDao();
    private SystemSettingDao settingDao = new SystemSettingDao();
    private static final String PAGE = "/teacher/evaluations.action";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            request.setAttribute("students", assignmentDao.findByTeacher(user.getId()));
            request.setAttribute("evaluations", evaluationDao.findByTeacher(user.getId()));
            request.setAttribute("gradeOpen", settingDao.isOpen(Stage.GRADE_OPEN));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/teacher/evaluations.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        Integer studentId = ParamUtil.getInt(request, "studentId");
        Integer score = ParamUtil.getInt(request, "score");
        try {
            if (settingDao.isOpen(Stage.GRADE_OPEN) && studentId != null && score != null) {
                FinalAssignment assignment = assignmentDao.findByStudent(studentId);
                // 校验：该学生确实最终分配在本教师的题目下
                if (assignment != null && assignment.getTeacherId() == user.getId()) {
                    evaluationDao.save(studentId, assignment.getTopicId(), user.getId(),
                            score.intValue(), request.getParameter("comment"));
                    request.getSession().setAttribute("flash", "成绩已保存");
                } else {
                    request.getSession().setAttribute("flash", "无权为该学生评分");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + PAGE);
    }
}
