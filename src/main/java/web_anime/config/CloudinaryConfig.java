package web_anime.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dy05v2yjr",
                "api_key", "269885491646361",
                "api_secret", "hT3eQT6eUhgV0EwYW20un6HqAuw",
                "secure", true,
                "cdn_subdomain", true
        ));
    }

    public Map<String, Object> uploadMedia(MultipartFile file) throws IOException {
        Cloudinary cloudinary = cloudinary();
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
    }
}
