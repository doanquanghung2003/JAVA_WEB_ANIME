package web_anime.controller;

import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web_anime.config.CloudinaryConfig;
import web_anime.entity.Account;
import web_anime.entity.Anime;
import web_anime.entity.CategoryAnime;
import web_anime.entity.Episode;
import web_anime.repository.AccountRepository;
import web_anime.repository.AnimeRepository;
import web_anime.repository.CategoryAnimeRepository;
import web_anime.repository.EpisodeRepository;
import web_anime.service.CloudinaryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/anime")
public class AnimeAdminController {

    @Autowired
    private AnimeRepository animeRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private CategoryAnimeRepository categoryAnimeRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private CloudinaryConfig cloudinaryConfig;

    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            return cloudinaryConfig.uploadMedia(file);
        } catch (IOException e) {
            e.printStackTrace();
            return Map.of("error", "Error uploading file: " + e.getMessage());
        }
    }

    @GetMapping
    public String getAll(@RequestParam(defaultValue = "0") int page, Model model) {

        int pageSize = 3;
        Page<Anime> animePage = animeRepo.findAll(PageRequest.of(page, pageSize));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        model.addAttribute("animeList", animePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", animePage.getTotalPages());
        model.addAttribute("page", "/Admin/anime-list");

        Optional<Account> optionalAccount = accountRepo.findByUsername(username);
        model.addAttribute("loggedInAccount", optionalAccount.orElse(null));
        return "Admin/admin-index";
    }

    @GetMapping("/add")
    public String showFormForAdd(Model model) {
        List<CategoryAnime> categoryAnimes = categoryAnimeRepository.findAll();
        model.addAttribute("categoryAnimes", categoryAnimes);
        return "/Admin/admin-anime-add";
    }

    @PostMapping("/save")
    public String save(@RequestParam String animeName,
                       @RequestParam String title,
                       @RequestParam String description,
                       @RequestParam String hashtag,
                       @RequestParam MultipartFile image,
                       @RequestParam MultipartFile trailer,
                       @RequestParam String link,
                       @RequestParam Integer category_anime_id,
                       @RequestParam Integer episodeCount,
                       @RequestParam MultipartFile[] episodes) throws IOException {

        String imageUrl = cloudinaryService.uploadFile(image);
        String trailerUrl = cloudinaryService.uploadFile(trailer);

        Anime anime = new Anime();
        anime.setAnimeName(animeName);
        anime.setTitle(title);
        anime.setDescription(description);
        anime.setHashtag(hashtag);
        anime.setImageUrl(imageUrl);
        anime.setTrailerUrl(trailerUrl);
        anime.setLink(link);

        Optional<CategoryAnime> categoryAnime = categoryAnimeRepository.findById(category_anime_id);
        categoryAnime.ifPresent(anime::setCategoryAnime);

        animeRepo.save(anime);

        for (int i = 0; i < episodeCount; i++) {
            if (i < episodes.length && !episodes[i].isEmpty()) {
                String episodeVideoUrl = cloudinaryService.uploadFile(episodes[i]);
                Episode episode = new Episode();
                episode.setEpisodeNumber(i + 1);
                episode.setVideoUrl(episodeVideoUrl);
                episode.setAnime(anime);
                episodeRepository.save(episode);
            }
        }

        return "redirect:/admin/anime";
    }

    @GetMapping("/edit/{id}")
    public String editAnime(@PathVariable Integer id, Model model) {
        Optional<Anime> optionalAnime = animeRepo.findById(id);
        if (optionalAnime.isEmpty()) {
            throw new IllegalArgumentException("Invalid anime Id: " + id);
        }
        Anime anime = optionalAnime.get();
        List<CategoryAnime> categoryAnimes = categoryAnimeRepository.findAll();
        List<Episode> episodes = episodeRepository.findByAnimeId(id);
        model.addAttribute("anime", anime);
        model.addAttribute("episodes", anime.getEpisodes());
        model.addAttribute("episodes", episodes);
        model.addAttribute("categoryAnimes", categoryAnimes);
        return "/Admin/admin-anime-edit";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id,
                         @RequestParam String animeName,
                         @RequestParam String title,
                         @RequestParam String description,
                         @RequestParam String hashtag,
                         @RequestParam MultipartFile image,
                         @RequestParam MultipartFile trailer,
                         @RequestParam String link,
                         @RequestParam(value = "episodeIds", required = false) List<Integer> episodeIds,
                         @RequestParam(value = "episodeNumbers", required = false) List<Integer> episodeNumbers,
                         @RequestParam(value = "episodes", required = false) MultipartFile[] episodes,
                         @RequestParam Integer category_anime_id) throws IOException {
        Optional<Anime> optionalAnime = animeRepo.findById(id);
        if (optionalAnime.isEmpty()) {
            throw new IllegalArgumentException("Invalid anime Id: " + id);
        }
        Anime anime = optionalAnime.get();
        anime.setAnimeName(animeName);
        anime.setTitle(title);
        anime.setDescription(description);
        anime.setHashtag(hashtag);
        anime.setLink(link);

        if (!image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(image);
            anime.setImageUrl(imageUrl);
        }
        if (!trailer.isEmpty()) {
            String trailerUrl = cloudinaryService.uploadFile(trailer);
            anime.setTrailerUrl(trailerUrl);
        }

        Optional<CategoryAnime> categoryAnime = categoryAnimeRepository.findById(category_anime_id);
        categoryAnime.ifPresent(anime::setCategoryAnime);

        List<Episode> updatedEpisodes = new ArrayList<>();
        if (episodeIds != null) {
            for (int i = 0; i < episodeIds.size(); i++) {
                Integer episodeId = episodeIds.get(i);
                Integer episodeNumber = episodeNumbers.get(i);
                String videoUrl = "";

                if (i < episodes.length && !episodes[i].isEmpty()) {
                    videoUrl = cloudinaryService.uploadFile(episodes[i]);
                }

                Episode episode = episodeRepository.findById(episodeId).orElse(new Episode());
                episode.setEpisodeNumber(episodeNumber);
                episode.setVideoUrl(videoUrl);
                episode.setAnime(anime);

                updatedEpisodes.add(episode);
            }
        }

        animeRepo.save(anime);
        episodeRepository.saveAll(updatedEpisodes);
        return "redirect:/admin/anime";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
//        if (!animeRepo.existsById(id)) {
//            System.out.println("Anime not found with id = " + id);
//        }
//        animeRepo.deleteById(id);
//        return "redirect:/admin/anime";
        Anime anime = animeRepo.findById(id).orElse(null);
        if (anime != null) {
            anime.setCategoryAnime(null);
            animeRepo.save(anime);
        }

        animeRepo.deleteById(id);
        return "redirect:/admin/anime";
    }

}
