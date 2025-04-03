package web_anime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import web_anime.dto.AnimeShowDTO;
import web_anime.entity.Anime;
import web_anime.entity.CategoryAnime;
import web_anime.repository.AnimeRepository;
import web_anime.repository.CategoryAnimeRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AnimeAdminController {

    @Autowired
    private AnimeRepository animeRepo;
    @Autowired
    private CategoryAnimeRepository categoryAnimeRepository;

    public AnimeAdminController(AnimeRepository animeRepo) {
        this.animeRepo = animeRepo;
    }

    @GetMapping("/admin/anime")
    public String getAll(@RequestParam(required = false) String keyword, Model model, Pageable pageable) {

        Page<Anime> pageAnime;
        if (keyword != null && !keyword.isEmpty()) {
            pageAnime = animeRepo.findByAnimeNameContaining(keyword, pageable);
        } else {
            pageAnime = animeRepo.findAll(pageable);
        }

        List<Anime> animes = pageAnime.toList();
        List<AnimeShowDTO> animeShowDTOS = new ArrayList<>();
        animes.forEach(obj -> {
            AnimeShowDTO animeShowDTO = new AnimeShowDTO();
            animeShowDTO.setId(obj.getId());
            animeShowDTO.setAnimeName(obj.getAnimeName());
            animeShowDTO.setTitle(obj.getTitle());
            animeShowDTO.setDescription(obj.getDescription());
            animeShowDTO.setHashtag(obj.getHashtag());
            animeShowDTO.setImage(obj.getImage());
            animeShowDTO.setTrailer(obj.getTrailer());
            animeShowDTO.setLink(obj.getLink());

            if (obj.getCategoryAnime() != null) {
                animeShowDTO.setCategory_name(obj.getCategoryAnime().getCategoryName());
            }
            animeShowDTOS.add(animeShowDTO);
        });

        model.addAttribute("animes", animeShowDTOS);
        model.addAttribute("totalPage", pageAnime.getTotalPages());
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("page", "/Admin/anime-list");
        model.addAttribute("keyword", keyword);

        return "/Admin/admin-index";
    }

    @GetMapping("/admin/anime/add")
    public String showFormForAdd(Model model) {
        List<CategoryAnime> categoryAnimes = categoryAnimeRepository.findAll();
        model.addAttribute("categoryAnimes", categoryAnimes);
        model.addAttribute("page", "admin-anime-add");

        return "/Admin/admin-anime-add";
    }

    @PostMapping("/admin/anime/save")
    public String save(@RequestParam String animeName,
                       @RequestParam String title,
                       @RequestParam String description,
                       @RequestParam String hashtag,
                       @RequestParam MultipartFile image,
                       @RequestParam MultipartFile trailer,
                       @RequestParam String link,
                       @RequestParam Integer category_anime_id) {
        String uploadDir = "D:\\DEMO\\src\\main\\resources\\static\\images\\anime";
        String imageName = StringUtils.cleanPath(image.getOriginalFilename());
        String uploadTrailerDir = "D:\\DEMO\\src\\main\\resources\\static\\videos";
        String trailerName = StringUtils.cleanPath(trailer.getOriginalFilename());

        try {
            Path uploadPathImage = Paths.get(uploadDir);
            if (!Files.exists(uploadPathImage)) {
                Files.createDirectories(uploadPathImage);
            }
            Files.copy(image.getInputStream(), uploadPathImage.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);

            Path uploadPathTrailer = Paths.get(uploadTrailerDir);
            if (!Files.exists(uploadPathTrailer)) {
                Files.createDirectories(uploadPathTrailer);
            }
            Files.copy(trailer.getInputStream(), uploadPathTrailer.resolve(trailerName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("\n=====> Loi:" + e.getMessage());
        }

        Anime theAnime = new Anime();
        theAnime.setAnimeName(animeName);
        theAnime.setTitle(title);
        theAnime.setDescription(description);
        theAnime.setHashtag(hashtag);
        theAnime.setImage(imageName);
        theAnime.setTrailer(trailerName);
        theAnime.setLink(link);

        CategoryAnime categoryAnimes = categoryAnimeRepository.findById(category_anime_id).orElse(null);
        theAnime.setCategoryAnime(categoryAnimes);

        animeRepo.save(theAnime);

        return "redirect:/admin/anime";
    }


    @GetMapping("/admin/anime/edit/{id}")
    public String editAnime(@PathVariable Integer id, Model model) {
        Optional<Anime> optionalAnime = animeRepo.findById(id);
        if (optionalAnime.isEmpty()) {
            throw new IllegalArgumentException("Invalid anime Id:" + id);
        }
        Anime anime = optionalAnime.get();

        List<CategoryAnime> categoryAnimes = categoryAnimeRepository.findAll();

        model.addAttribute("anime", anime);
        model.addAttribute("categoryAnimes", categoryAnimes);

        return "/Admin/admin-anime-edit";
    }


    @PostMapping("/admin/anime/update/{id}")
    public String saveUpdate(@PathVariable Integer id,
                             @RequestParam String animeName,
                             @RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String hashtag,
                             @RequestParam MultipartFile image,
                             @RequestParam MultipartFile trailer,
                             @RequestParam String link,
                             @RequestParam Integer category_anime_id) {
        String imageName = StringUtils.cleanPath(image.getOriginalFilename());
        String trailerName = StringUtils.cleanPath(trailer.getOriginalFilename());

        Optional<Anime> optionalAnime = animeRepo.findById(id);
        if (optionalAnime.isEmpty()) {
            throw new IllegalArgumentException("Invalid anime Id:" + id);
        }

        Anime theAnime = optionalAnime.get();
        theAnime.setAnimeName(animeName);
        theAnime.setTitle(title);
        theAnime.setDescription(description);
        theAnime.setHashtag(hashtag);
        theAnime.setLink(link);

        if (!image.isEmpty()) {
            String uploadDir = "D:\\DEMO\\src\\main\\resources\\static\\images\\anime";
            try {
                Path uploadPathImage = Paths.get(uploadDir);
                if (!Files.exists(uploadPathImage)) {
                    Files.createDirectories(uploadPathImage);
                }
                Files.copy(image.getInputStream(), uploadPathImage.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
                theAnime.setImage(imageName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!trailer.isEmpty()) {
            String uploadTrailerDir = "D:\\DEMO\\src\\main\\resources\\static\\videos";
            try {
                Path uploadPathTrailer = Paths.get(uploadTrailerDir);
                if (!Files.exists(uploadPathTrailer)) {
                    Files.createDirectories(uploadPathTrailer);
                }
                Files.copy(trailer.getInputStream(), uploadPathTrailer.resolve(trailerName), StandardCopyOption.REPLACE_EXISTING);
                theAnime.setTrailer(trailerName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        CategoryAnime categoryAnimes = categoryAnimeRepository.findById(category_anime_id).orElse(null);
        theAnime.setCategoryAnime(categoryAnimes);

        animeRepo.save(theAnime);

        return "redirect:/admin/anime";
    }

    @GetMapping("/admin/anime/delete/{id}")
    public String delete(@PathVariable Integer id) {
        Optional<Anime> optionalAnime = animeRepo.findById(id);
        if (optionalAnime.isEmpty()) {
            System.out.println("Not found Anime with id = " + id);
        }
        animeRepo.deleteById(id);

        return "redirect:/admin/anime";
    }
}

