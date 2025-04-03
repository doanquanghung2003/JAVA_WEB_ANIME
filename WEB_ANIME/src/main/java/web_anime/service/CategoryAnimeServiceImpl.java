package web_anime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web_anime.entity.CategoryAnime;
import web_anime.repository.CategoryAnimeRepository;

import java.util.List;

@Service
public class CategoryAnimeServiceImpl implements CategoryAnimeService{

    @Autowired
    private CategoryAnimeRepository categoryAnimeRepo;


    @Override
    public List<CategoryAnime> getAll() {
        List<CategoryAnime> categoryAnimes = categoryAnimeRepo.findAll();
        return categoryAnimes;
    }
}
