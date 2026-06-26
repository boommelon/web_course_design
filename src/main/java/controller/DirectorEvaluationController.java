package controller;

import bean.User;
import dao.EvaluationDao;
import dao.SystemSettingDao;
import dao.UserDao;
import util.ParamUtil;
import util.Stage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 专业负责人成绩管理：分配评阅教师、录入答辩成绩、查看最终成绩。
 */
public class DirectorEvaluationController extends HttpServlet {

    private EvaluationDao evaluationDao = new EvaluationDao();
    private UserDao userDao = new UserDao();
    private SystemSettingDao settingDao = new SystemSettingDao();
    private static final String PAGE = "/director/evaluations.action";
    private static final String JSP_PAGE = "/WEB-INF/jsp/director/evaluations.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            request.setAttribute("gradeOpen", settingDao.isOpen(Stage.GRADE_OPEN));
            request.setAttribute("teachers",
                    userDao.findByRoleAndMajor(Stage.ROLE_TEACHER, user.getCollege(), user.getMajor()));
            request.setAttribute("evaluations",
                    evaluationDao.findByMajor(user.getCollege(), user.getMajor()));
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
        try {
            if (!settingDao.isOpen(Stage.GRADE_OPEN)) {
                request.getSession().setAttribute("flash", "当前未开放成绩评定。");
                response.sendRedirect(request.getContextPath() + PAGE);
                return;
            }

            String action = ParamUtil.getString(request, "action");
            Integer studentId = ParamUtil.getInt(request, "studentId");
            if (studentId == null) {
                request.getSession().setAttribute("flash", "请选择学生。");
                response.sendRedirect(request.getContextPath() + PAGE);
                return;
            }

            if ("assignReviewer".equals(action)) {
                Integer reviewerTeacherId = ParamUtil.getInt(request, "reviewerTeacherId");
                if (reviewerTeacherId == null) {
                    request.getSession().setAttribute("flash", "请选择评阅教师。");
                } else {
                    boolean ok = evaluationDao.assignReviewer(studentId.intValue(), reviewerTeacherId.intValue(),
                            user.getId(), user.getCollege(), user.getMajor());
                    request.getSession().setAttribute("flash", ok ? "评阅教师已分配。" : "评阅教师分配失败：请确认学生、教师属于本专业且评阅教师不是指导教师。");
                }
            } else if ("assignDefense".equals(action)) {
                Integer teacherId1 = ParamUtil.getInt(request, "defenseTeacherId1");
                Integer teacherId2 = ParamUtil.getInt(request, "defenseTeacherId2");
                Integer teacherId3 = ParamUtil.getInt(request, "defenseTeacherId3");
                if (teacherId1 == null || teacherId2 == null || teacherId3 == null) {
                    request.getSession().setAttribute("flash", "请选择 3 名答辩教师。");
                } else {
                    boolean ok = evaluationDao.assignDefenseTeachers(studentId.intValue(),
                            teacherId1.intValue(), teacherId2.intValue(), teacherId3.intValue(),
                            user.getId(), user.getCollege(), user.getMajor());
                    request.getSession().setAttribute("flash", ok ? "答辩教师已分配。" : "答辩教师分配失败：请确认 3 名教师不重复、属于本专业且不是指导教师。");
                }
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
