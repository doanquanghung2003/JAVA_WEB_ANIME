package web_anime.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import web_anime.entity.FileUpload;
import web_anime.repository.FileUploadRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private FileUploadRepository fileUploadRepository;

//    public String uploadFile(MultipartFile file) throws IOException {
//        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
//                ObjectUtils.asMap("resource_type", "auto"));
//        String fileUrl = uploadResult.get("secure_url").toString();
//
//        FileUpload fileUpload = new FileUpload();
//        fileUpload.setFileName(file.getOriginalFilename());
//        fileUpload.setFileType(file.getContentType());
//        fileUpload.setFileUrl(fileUrl);
//        fileUploadRepository.save(fileUpload);
//
//        return fileUrl;
//    }
    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File không được để trống");
        }

        System.out.println("Bắt đầu upload file: " + file.getOriginalFilename());
        System.out.println("Kích thước file: " + file.getSize() + " bytes");
        System.out.println("Loại file: " + file.getContentType());

        // Kiểm tra kích thước file
        if (file.getSize() > 500 * 1024 * 1024) { // 500MB
            throw new IOException("File quá lớn (>500MB)");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("resource_type", "auto");
        params.put("quality", "auto:best");
        params.put("video_codec", "auto");
        params.put("async", false);
        
        // Thêm các tham số để xử lý file lớn
        params.put("chunk_size", 20000000); // 20MB chunks
        params.put("timeout", 300000); // 5 phút timeout
        params.put("invalidate", true);
        params.put("use_filename", true);
        params.put("unique_filename", true);
        params.put("overwrite", true);

        int maxRetries = 5;
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < maxRetries) {
            try {
                Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), params);
                return (String) result.get("secure_url");
            } catch (Exception e) {
                lastException = e;
                retryCount++;
                System.out.println("Lỗi upload lần " + retryCount + ": " + e.getMessage());
                
                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(5000); // Đợi 5 giây trước khi thử lại
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Upload bị gián đoạn", ie);
                    }
                }
            }
        }

        throw new IOException("Không thể upload file sau " + maxRetries + " lần thử. Lỗi cuối: " + 
            (lastException != null ? lastException.getMessage() : "Không xác định"));
    }
}
