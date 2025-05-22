package web_anime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web_anime.entity.Comment;
import web_anime.entity.Anime;
import web_anime.entity.Account;
import web_anime.repository.CommentRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Comment addComment(String content, Anime anime, Account account) {
        Comment comment = new Comment();
        comment.setComment(content);
        comment.setAnime(anime);
        comment.setAccount(account);
        comment.setCreateAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByAnime(Anime anime) {
        return commentRepository.findByAnimeOrderByCreateAtDesc(anime);
    }
} 