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
        Map<String, Object> params = new HashMap<>();

        // Auto optimize định dạng
        params.put("resource_type", "auto");

        // Tối ưu chất lượng ảnh
        params.put("quality", "auto:best");

        // Tối ưu định dạng video
        params.put("video_codec", "auto");

        // Chế độ upload nhanh
        params.put("async", true);

        // Giảm kích thước file
        params.put("transformation", new Transformation().width(1920).height(1080).crop("limit"));

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return (String) uploadResult.get("secure_url");
    }
}
