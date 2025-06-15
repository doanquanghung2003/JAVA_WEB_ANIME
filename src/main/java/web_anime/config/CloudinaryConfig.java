package web_anime.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        System.out.println("Khởi tạo Cloudinary với cloud name: " + cloudName);
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true,
                "cdn_subdomain", true
        ));
    }

    public Map<String, Object> uploadMedia(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File không được để trống");
        }

        System.out.println("Bắt đầu upload media: " + file.getOriginalFilename());
        Cloudinary cloudinary = cloudinary();
        
        try {
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            System.out.println("Upload media thành công: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("Lỗi khi upload media: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Lỗi upload media: " + e.getMessage());
        }
    }
}
