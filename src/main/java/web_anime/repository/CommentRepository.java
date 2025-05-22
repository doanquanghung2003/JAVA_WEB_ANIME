package web_anime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web_anime.entity.Comment;
import web_anime.entity.Anime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByAnimeOrderByCreateAtDesc(Anime anime);
} 