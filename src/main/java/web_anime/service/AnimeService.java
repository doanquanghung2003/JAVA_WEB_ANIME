package web_anime.service;

import web_anime.entity.Anime;

import java.util.List;

public interface AnimeService {

    List<Anime> getAll();

    Anime findById(Integer id);

}
