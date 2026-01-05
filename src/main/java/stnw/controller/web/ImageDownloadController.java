package stnw.controller.web;

import stnw.utils.Constant;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.InvalidPathException;


@WebServlet(urlPatterns = {"/uploads/*"})
public class ImageDownloadController extends HttpServlet {

    /**
	 * */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getPathInfo(); // Lấy tên file từ URL (ví dụ /abc.png)
        
        if (fileName == null || fileName.isEmpty() || "/".equals(fileName)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "File name is required");
            return;
        }
        
        // Cắt bỏ dấu / ở đầu
        fileName = fileName.substring(1);

        File file;
        try {
            // Lấy đường dẫn thực của thư mục uploads từ webapp
            String uploadBasePath = getServletContext().getRealPath("/" + Constant.UPLOAD_DIRECTORY);
            if (uploadBasePath == null) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Upload directory not found");
                return;
            }
            
            // Tạo đường dẫn file an toàn
            File uploadBaseDir = new File(uploadBasePath);
            File targetFile = new File(uploadBaseDir, fileName);
            String safePath = targetFile.getCanonicalPath();
            String baseCanonicalPath = uploadBaseDir.getCanonicalPath();
            
            // Ngăn chặn tấn công "Path Traversal" (../)
            if (!safePath.startsWith(baseCanonicalPath)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found (Invalid Path)");
                return;
            }
            
            file = new File(safePath);
            
        } catch (InvalidPathException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file name");
            return;
        }
        
        if (!file.exists() || file.isDirectory()) {
            // Trả về ảnh "không tìm thấy" thay vì lỗi 404
            String placeholder = getServletContext().getRealPath("/assets/placeholder.png");
            if (placeholder != null) {
                file = new File(placeholder);
            } else {
                 resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
                 return;
            }
        }
        
        // Set content type
        String mimeType = getServletContext().getMimeType(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        resp.setContentType(mimeType);
        
        // Set content length
        resp.setContentLength((int) file.length());
        
        // Thêm header để cache ảnh
        resp.setHeader("Cache-Control", "public, max-age=2592000"); // Cache trong 30 ngày
        
        // Write file to response
        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = resp.getOutputStream()) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
