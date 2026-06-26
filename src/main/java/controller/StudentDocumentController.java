package controller;

import bean.Document;
import bean.FinalAssignment;
import bean.User;
import dao.DocumentDao;
import dao.FinalAssignmentDao;
import dao.SystemSettingDao;
import util.Stage;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

 




@MultipartConfig(maxFileSize = 20 * 1024 * 1024, maxRequestSize = 25 * 1024 * 1024)
public class StudentDocumentController extends HttpServlet {

    private DocumentDao documentDao = new DocumentDao();
    private FinalAssignmentDao assignmentDao = new FinalAssignmentDao();
    private SystemSettingDao settingDao = new SystemSettingDao();
    private static final String LIST_PAGE = "/student/documents.action";
    private static final String JSP_PAGE = "/WEB-INF/jsp/student/documents.jsp";
    private static final String UPLOAD_FOLDER = "/uploads";
    private static final String[] DOCUMENT_TYPES = {"proposal", "midterm", "final", "source"};
    private static final String[] ALLOWED_EXTENSIONS = {
            ".doc", ".docx", ".pdf", ".txt", ".zip", ".rar", ".7z", ".java", ".sql"
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            showDocumentPage(request, user);
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
            submitDocument(request, user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        redirectToList(request, response);
    }

    private void showDocumentPage(HttpServletRequest request, User user) throws Exception {
        request.setAttribute("documents", documentDao.findByStudent(user.getId()));
        request.setAttribute("assignment", assignmentDao.findByStudent(user.getId()));
        request.setAttribute("documentUploadOpen", settingDao.isOpen(Stage.DOCUMENT_UPLOAD_OPEN));
    }

    private void submitDocument(HttpServletRequest request, User user) throws Exception {
        if (!settingDao.isOpen(Stage.DOCUMENT_UPLOAD_OPEN)) {
            request.getSession().setAttribute("flash", "当前资料上传系统已关闭");
            return;
        }

        // 没有最终分配题目的学生不能上传资料
        FinalAssignment assignment = assignmentDao.findByStudent(user.getId());
        if (assignment == null) {
            request.getSession().setAttribute("flash", "你尚未被分配最终题目，无法提交资料");
            return;
        }

        Part filePart = request.getPart("file");
        String fileName = getSubmittedFileName(filePart);
        String type = request.getParameter("type");
        String validationError = validateDocumentRequest(user.getId(), type, filePart, fileName);
        if (validationError != null) {
            request.getSession().setAttribute("flash", validationError);
            return;
        }
        String filePath = saveUploadFile(filePart, fileName, user.getId());

        Document document = buildDocument(request, user, assignment, fileName, filePath);
        documentDao.insert(document);
        request.getSession().setAttribute("flash", "文档提交成功");
    }

    private Document buildDocument(HttpServletRequest request, User user, FinalAssignment assignment,
                                   String fileName, String filePath) {
        Document document = new Document();
        document.setStudentId(user.getId());
        document.setTopicId(assignment.getTopicId());
        document.setType(request.getParameter("type"));
        document.setContent(request.getParameter("content"));
        document.setFileName(fileName);
        document.setFilePath(filePath);
        return document;
    }

    private String validateDocumentRequest(int studentId, String type, Part filePart, String fileName) throws Exception {
        if (!isValidDocumentType(type)) {
            return "文档类型不正确";
        }
        if (filePart == null || fileName == null || fileName.length() == 0 || filePart.getSize() == 0) {
            return "请选择要上传的文件";
        }
        if (!isAllowedFileName(fileName)) {
            return "文件类型不允许，请上传 doc、docx、pdf、txt、zip、rar、7z、java 或 sql 文件";
        }
        String stageError = validateStageOrder(studentId, type);
        if (stageError != null) {
            return stageError;
        }
        return null;
    }

    private boolean isValidDocumentType(String type) {
        if (type == null) {
            return false;
        }
        for (String candidate : DOCUMENT_TYPES) {
            if (candidate.equals(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllowedFileName(String fileName) {
        String lower = fileName.toLowerCase();
        for (String extension : ALLOWED_EXTENSIONS) {
            if (lower.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private String validateStageOrder(int studentId, String type) throws Exception {
        if ("midterm".equals(type) && !hasReviewedDocument(studentId, "proposal")) {
            return "开题报告审核通过后才能提交中期检查";
        }
        if ("final".equals(type) && !hasReviewedDocument(studentId, "midterm")) {
            return "中期检查审核通过后才能提交毕业论文";
        }
        if ("source".equals(type) && !hasReviewedDocument(studentId, "final")) {
            return "毕业论文审核通过后才能提交源代码";
        }
        return null;
    }

    private boolean hasReviewedDocument(int studentId, String type) throws Exception {
        Document document = documentDao.findLatestByStudentAndType(studentId, type);
        return document != null && "reviewed".equals(document.getStatus());
    }

    private String saveUploadFile(Part filePart, String fileName, int userId) throws IOException {
        if (filePart == null || fileName == null || fileName.length() == 0 || filePart.getSize() == 0) {
            return null;
        }

        File dir = new File(getUploadDir());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String safeName = System.currentTimeMillis() + "_" + userId + "_" + sanitizeFileName(fileName);
        File target = new File(dir, safeName);
        filePart.write(target.getAbsolutePath());
        return target.getAbsolutePath();
    }

    private String getUploadDir() {
        String uploadDir = getServletContext().getRealPath(UPLOAD_FOLDER);
        if (uploadDir == null) {
            uploadDir = System.getProperty("java.io.tmpdir") + File.separator + "graduation-design-uploads";
        }
        return uploadDir;
    }

    private String getSubmittedFileName(Part part) {
        if (part == null) {
            return null;
        }
        String header = part.getHeader("content-disposition");
        if (header == null) {
            return null;
        }
        String[] segments = header.split(";");
        for (String segment : segments) {
            String item = segment.trim();
            if (item.startsWith("filename=")) {
                String fileName = item.substring(item.indexOf('=') + 1).trim().replace("\"", "");
                return new File(fileName).getName();
            }
        }
        return null;
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private void redirectToList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + LIST_PAGE);
    }
}
