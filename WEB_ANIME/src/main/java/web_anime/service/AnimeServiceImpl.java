package web_anime.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web_anime.entity.Anime;
import web_anime.repository.AnimeRepository;

import java.util.List;

@Service
public class AnimeServiceImpl implements AnimeService{

    @Autowired
    private AnimeRepository animeRepo;

    @Override
    public List<Anime> getAll() {
        List<Anime> animes = animeRepo.findAll();
        return animes;
    }

    @Transactional
    public void deleteAnime(Integer animeId) {
        if (!animeRepo.existsById(animeId)) {
            throw new RuntimeException("Anime không tồn tại!");
        }
        animeRepo.deleteAnimeById(animeId);
    }
}
