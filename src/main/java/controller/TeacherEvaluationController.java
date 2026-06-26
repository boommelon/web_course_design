package controller;

import bean.User;
import dao.EvaluationDao;
import dao.SystemSettingDao;
import util.ParamUtil;
import util.Stage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 教师成绩评定：导师自评 + 被指定为评阅教师后的评阅评分。
 */
public class TeacherEvaluationController extends HttpServlet {

    private EvaluationDao evaluationDao = new EvaluationDao();
    private SystemSettingDao settingDao = new SystemSettingDao();
    private static final String PAGE = "/teacher/evaluations.action";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            request.setAttribute("advisorEvaluations", evaluationDao.findAdvisorByTeacher(user.getId()));
            request.setAttribute("reviewerEvaluations", evaluationDao.findReviewerByTeacher(user.getId()));
            request.setAttribute("defenseEvaluations", evaluationDao.findDefenseByTeacher(user.getId()));
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
        String action = ParamUtil.getString(request, "action");
        try {
            if (!settingDao.isOpen(Stage.GRADE_OPEN)) {
                request.getSession().setAttribute("flash", "当前未开放成绩评定。");
                response.sendRedirect(request.getContextPath() + PAGE);
                return;
            }
            if (studentId == null || score == null) {
                request.getSession().setAttribute("flash", "请填写完整的成绩信息。");
                response.sendRedirect(request.getContextPath() + PAGE);
                return;
            }
            if (score.intValue() < 0 || score.intValue() > 100) {
                request.getSession().setAttribute("flash", "成绩必须在 0-100 之间。");
                response.sendRedirect(request.getContextPath() + PAGE);
                return;
            }

            boolean ok = false;
            if ("reviewer".equals(action)) {
                ok = evaluationDao.saveReviewerScore(studentId.intValue(), user.getId(),
                        score.intValue(), request.getParameter("comment"));
                request.getSession().setAttribute("flash", ok ? "评阅评分已保存。" : "无权为该学生录入评阅评分。");
            } else if ("defense".equals(action)) {
                ok = evaluationDao.saveDefenseTeacherScore(studentId.intValue(), user.getId(),
                        score.intValue(), request.getParameter("comment"));
                request.getSession().setAttribute("flash", ok ? "答辩评分已保存。" : "无权为该学生录入答辩评分。");
            } else {
                ok = evaluationDao.saveAdvisorScore(studentId.intValue(), user.getId(),
                        score.intValue(), request.getParameter("comment"));
                request.getSession().setAttribute("flash", ok ? "导师自评已保存。" : "无权为该学生录入导师自评。");
            }
        } catch (IllegalArgumentException e) {
            request.getSession().setAttribute("flash", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + PAGE);
    }
}
