package web_anime.service;

import web_anime.entity.Comment;
import web_anime.entity.Anime;
import web_anime.entity.Account;
import java.util.List;

public interface CommentService {
    Comment addComment(String content, Anime anime, Account account);
    List<Comment> getCommentsByAnime(Anime anime);
} 