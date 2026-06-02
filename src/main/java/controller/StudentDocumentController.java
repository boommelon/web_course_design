package controller;

import bean.Document;
import bean.TopicSelection;
import bean.User;
import dao.DocumentDao;
import dao.SystemSettingDao;
import dao.TopicSelectionDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * 学生-文档提交控制器
 * GET: 查看已提交的文档列表
 * POST: 提交新文档（开题报告/中期检查/终稿）
 */
@MultipartConfig(maxFileSize = 20 * 1024 * 1024, maxRequestSize = 25 * 1024 * 1024)
public class StudentDocumentController extends HttpServlet {

    private DocumentDao documentDao = new DocumentDao();
    private TopicSelectionDao selectionDao = new TopicSelectionDao();
    private SystemSettingDao settingDao = new SystemSettingDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            request.setAttribute("documents", documentDao.findByStudent(user.getId()));
            request.setAttribute("selection", selectionDao.findApprovedByStudent(user.getId()));
            request.setAttribute("documentUploadOpen", settingDao.isOpen("document_upload_open"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/student/documents.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            if (!settingDao.isOpen("document_upload_open")) {
                response.sendRedirect(request.getContextPath() + "/student/documents.action");
                return;
            }

            // 必须选题通过才能提交文档
            TopicSelection selection = selectionDao.findApprovedByStudent(user.getId());
            if (selection != null) {
                Part filePart = request.getPart("file");
                String fileName = getSubmittedFileName(filePart);
                String filePath = null;

                if (fileName != null && fileName.length() > 0 && filePart.getSize() > 0) {
                    String uploadDir = getServletContext().getRealPath("/uploads");
                    if (uploadDir == null) {
                        uploadDir = System.getProperty("java.io.tmpdir") + File.separator + "graduation-design-uploads";
                    }
                    File dir = new File(uploadDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    String safeName = System.currentTimeMillis() + "_" + user.getId() + "_" + sanitizeFileName(fileName);
                    File target = new File(dir, safeName);
                    filePart.write(target.getAbsolutePath());
                    filePath = target.getAbsolutePath();
                }

                Document doc = new Document();
                doc.setStudentId(user.getId());
                doc.setTopicId(selection.getTopicId());
                doc.setType(request.getParameter("type"));
                doc.setContent(request.getParameter("content"));
                doc.setFileName(fileName);
                doc.setFilePath(filePath);
                documentDao.insert(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/student/documents.action");
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
}
