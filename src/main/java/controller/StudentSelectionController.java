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
import java.util.ArrayList;
import java.util.List;

/**
 * 学生选题：提交本轮 1-3 个志愿，查看本轮申请与最终结果。
 * 业务规则由 SelectionDao 在事务内强校验（至少1最多3、不重复、本专业、每题每轮意向≤3）。
 */
public class StudentSelectionController extends HttpServlet {

    private SelectionDao selectionDao = new SelectionDao();
    private FinalAssignmentDao assignmentDao = new FinalAssignmentDao();
    private TopicDao topicDao = new TopicDao();
    private SystemSettingDao settingDao = new SystemSettingDao();
    private static final String LIST_PAGE = "/student/selections.action";
    private static final String JSP_PAGE = "/WEB-INF/jsp/student/selections.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            int round = settingDao.getInt(Stage.CURRENT_ROUND, 1);
            boolean assigned = assignmentDao.isStudentAssigned(user.getId());

            request.setAttribute("round", round);
            request.setAttribute("selectionOpen", settingDao.isOpen(Stage.SELECTION_OPEN));
            request.setAttribute("assigned", assigned);
            request.setAttribute("myAssignment", assignmentDao.findByStudent(user.getId()));
            request.setAttribute("myApplication",
                    selectionDao.findApplicationWithChoices(user.getId(), round));
            request.setAttribute("topics",
                    topicDao.findSelectableByMajor(user.getCollege(), user.getMajor()));
            request.setAttribute("canSelect", settingDao.isOpen(Stage.SELECTION_OPEN) && !assigned);
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
            if (!settingDao.isOpen(Stage.SELECTION_OPEN)) {
                request.getSession().setAttribute("flash", "当前未开放选题");
            } else if (assignmentDao.isStudentAssigned(user.getId())) {
                request.getSession().setAttribute("flash", "你已被最终分配题目，无法再次选题");
            } else {
                int round = settingDao.getInt(Stage.CURRENT_ROUND, 1);
                List<Integer> topicIds = getRankedTopicIds(request);
                SelectionDao.SubmitResult result = selectionDao.submitChoices(user.getId(), topicIds, round);
                request.getSession().setAttribute("flash", result.message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + LIST_PAGE);
    }

    /**
     * 按志愿顺序读取题目：优先读 choice1/choice2/choice3（顺序明确），
     * 否则回退读 topicIds 多选（顺序即数组顺序）。
     */
    private List<Integer> getRankedTopicIds(HttpServletRequest request) {
        List<Integer> ids = new ArrayList<Integer>();
        boolean hasRanked = false;
        for (int rank = 1; rank <= 3; rank++) {
            Integer id = parsePositive(request.getParameter("choice" + rank));
            if (id != null) {
                ids.add(id);
                hasRanked = true;
            }
        }
        if (hasRanked) {
            return ids;
        }
        String[] values = request.getParameterValues("topicIds");
        if (values != null) {
            for (String value : values) {
                Integer id = parsePositive(value);
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        return ids;
    }

    private Integer parsePositive(String value) {
        if (value == null) {
            return null;
        }
        try {
            int id = Integer.parseInt(value.trim());
            return id > 0 ? Integer.valueOf(id) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
