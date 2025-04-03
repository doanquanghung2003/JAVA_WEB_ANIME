package web_anime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web_anime.service.CloudinaryService;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/cloudinary")
public class CloudinaryController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public CompletableFuture<ResponseEntity<String>> uploadFileAsync(
            @RequestParam("file") MultipartFile file
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = cloudinaryService.uploadFile(file);
                return ResponseEntity.ok("Upload thành công: " + url);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Lỗi upload: " + e.getMessage());
            }
        });
    }

//    public String uploadFile(MultipartFile file) throws IOException {
//        if (file.getSize() > 100 * 1024 * 1024) {
//            throw new IOException("File quá lớn (>100MB)");
//        }
//
//        String contentType = file.getContentType();
//        if (!Arrays.asList("image/jpeg", "video/mp4").contains(contentType)) {
//            throw new IOException("Định dạng không hỗ trợ");
//        }
//
//    }

}
