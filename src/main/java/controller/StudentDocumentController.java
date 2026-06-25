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
            return;
        }

        // 没有最终分配题目的学生不能上传资料
        FinalAssignment assignment = assignmentDao.findByStudent(user.getId());
        if (assignment == null) {
            return;
        }

        Part filePart = request.getPart("file");
        String fileName = getSubmittedFileName(filePart);
        String filePath = saveUploadFile(filePart, fileName, user.getId());

        Document document = buildDocument(request, user, assignment, fileName, filePath);
        documentDao.insert(document);
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
