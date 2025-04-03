package web_anime.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import web_anime.entity.Anime;
import web_anime.entity.CategoryAnime;
import web_anime.repository.AnimeRepository;
import web_anime.repository.CategoryAnimeRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class AnimeServiceImpl implements AnimeService {

    @Autowired
    private AnimeRepository animeRepo;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private CategoryAnimeRepository categoryAnimeRepository;

    @Override
    public List<Anime> getAll() {
        return animeRepo.findAll();
    }

    @Transactional
    public void deleteAnime(Integer animeId) {
        if (!animeRepo.existsById(animeId)) {
            throw new RuntimeException("Anime không tồn tại!");
        }
        animeRepo.deleteAnimeById(animeId);
    }

    public Anime addAnime(String animeName, String title, String description, String hashtag, String link, Integer categoryId, MultipartFile image, MultipartFile video) throws IOException {
        // Kiểm tra nếu không có file upload
        String imageUrl = null, videoUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = cloudinaryService.uploadFile(image);
        }
        if (video != null && !video.isEmpty()) {
            videoUrl = cloudinaryService.uploadFile(video);
        }

        // Tìm danh mục theo ID
        Optional<CategoryAnime> categoryOpt = categoryAnimeRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            throw new RuntimeException("Category không tồn tại!");
        }

        // Tạo đối tượng Anime mới
        Anime anime = new Anime();
        anime.setAnimeName(animeName);
        anime.setTitle(title);
        anime.setDescription(description);
        anime.setHashtag(hashtag);
        anime.setLink(link);
        anime.setViewCount(0);
        anime.setRating(0.0);
        anime.setImageUrl(imageUrl);
        anime.setVideoUrl(videoUrl);
        anime.setCategoryAnime(categoryOpt.get());

        // Lưu vào database
        return animeRepo.save(anime);
    }
}
