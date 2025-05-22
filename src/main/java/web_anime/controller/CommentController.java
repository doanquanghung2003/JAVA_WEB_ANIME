package web_anime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web_anime.entity.Account;
import web_anime.entity.Anime;
import web_anime.entity.Comment;
import web_anime.repository.AccountRepository;
import web_anime.repository.AnimeRepository;
import web_anime.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private AnimeRepository animeRepo;

    @PostMapping("/{animeId}")
    public ResponseEntity<?> postComment(@PathVariable("animeId") Integer animeId,
                                         @RequestBody Map<String, String> body) {
        try {
            // Lấy thông tin người dùng và anime
            String accountIdStr = body.get("accountId");
            String comment = body.get("comment");

            if (accountIdStr == null || comment == null || comment.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Thiếu dữ liệu comment hoặc accountId");
            }

            int accountId = Integer.parseInt(accountIdStr);

            Optional<Account> accOpt = accountRepo.findById(accountId);
            Optional<Anime> animeOpt = animeRepo.findById(animeId);

            if (accOpt.isEmpty() || animeOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy anime hoặc tài khoản");
            }

            // Tạo và lưu comment
            Comment newComment = new Comment();
            newComment.setAccount(accOpt.get());
            newComment.setAnime(animeOpt.get());
            newComment.setComment(comment);
            newComment.setCreateAt(LocalDateTime.now());

            commentRepo.save(newComment);

            // Trả kết quả phản hồi JSON
            return ResponseEntity.ok(Map.of(
                    "comment", newComment.getComment(),
                    "account", accOpt.get().getUsername()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Có lỗi xảy ra khi lưu bình luận.");
        }
    }
}
