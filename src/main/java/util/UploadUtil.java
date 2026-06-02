package util;

import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import java.io.File;

public class UploadUtil {
    public static class UploadedFile {
        private String filePath;
        private String fileName;

        public UploadedFile(String filePath, String fileName) {
            this.filePath = filePath;
            this.fileName = fileName;
        }

        public String getFilePath() { return filePath; }
        public String getFileName() { return fileName; }
    }

    public static UploadedFile save(ServletContext context, Part part, String folder, int ownerId) throws Exception {
        String fileName = getSubmittedFileName(part);
        if (fileName == null || fileName.length() == 0 || part.getSize() <= 0) {
            return null;
        }

        String uploadDir = context.getRealPath("/uploads/" + folder);
        if (uploadDir == null) {
            uploadDir = System.getProperty("java.io.tmpdir") + File.separator + "graduation-design-uploads" + File.separator + folder;
        }
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String safeName = System.currentTimeMillis() + "_" + ownerId + "_" + sanitizeFileName(fileName);
        File target = new File(dir, safeName);
        part.write(target.getAbsolutePath());
        return new UploadedFile(target.getAbsolutePath(), fileName);
    }

    public static String getSubmittedFileName(Part part) {
        if (part == null) return null;
        String header = part.getHeader("content-disposition");
        if (header == null) return null;
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

    private static String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
