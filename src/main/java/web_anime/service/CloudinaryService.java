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
        if (file.getSize() > 100 * 1024 * 1024) { // 100MB
            throw new IOException("File quá lớn (>100MB)");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("resource_type", "auto");
        params.put("quality", "auto:best");
        params.put("video_codec", "auto");
        params.put("async", false);
        
        // Thêm các tham số để xử lý file lớn
        params.put("chunk_size", 6000000); // 6MB chunks
        params.put("timeout", 60000); // 60 seconds timeout

        int maxRetries = 3;
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < maxRetries) {
            try {
                System.out.println("Thử upload lần " + (retryCount + 1));
                Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
                String secureUrl = (String) uploadResult.get("secure_url");
                
                if (secureUrl == null) {
                    System.out.println("Upload result: " + uploadResult);
                    throw new IOException("Không nhận được URL từ Cloudinary");
                }
                
                System.out.println("Upload thành công. URL: " + secureUrl);
                return secureUrl;
            } catch (Exception e) {
                lastException = e;
                System.out.println("Lỗi khi upload file (lần " + (retryCount + 1) + "): " + e.getMessage());
                retryCount++;
                
                if (retryCount < maxRetries) {
                    try {
                        // Đợi 5 giây trước khi thử lại
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Upload bị gián đoạn", ie);
                    }
                }
            }
        }

        throw new IOException("Không thể upload file sau " + maxRetries + " lần thử. Lỗi cuối: " + lastException.getMessage());
    }
}
