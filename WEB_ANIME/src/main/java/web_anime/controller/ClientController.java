package web_anime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import web_anime.dto.CategoryAnimeShowDTO;
import web_anime.entity.Anime;
import web_anime.entity.CategoryAnime;
import web_anime.repository.AnimeRepository;
import web_anime.repository.CategoryAnimeRepository;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ClientController {

    @Autowired
    private CategoryAnimeRepository categoryAnimeRepo;

    @Autowired
    private AnimeRepository animeRepo;

    @GetMapping({"/", "/client-index"})
    public String clientIndex(Model model) {
        List<CategoryAnime> categoryAnimeList = categoryAnimeRepo.findAll();
        List<CategoryAnimeShowDTO> categoryAnimeShowDTOS = new ArrayList<>();

        categoryAnimeList.forEach(category -> {
            CategoryAnimeShowDTO dto = new CategoryAnimeShowDTO();
            dto.setCategoryName(category.getCategoryName());
            dto.setTitle(category.getTitle());
            dto.setDescription(category.getDescription());
            dto.setHashtag(category.getHashtag());
            dto.setImage(category.getImage());
            dto.setTrailer(category.getTrailer());
            dto.setLink(category.getLink());
            dto.setAnime(category.getAnimes());
            categoryAnimeShowDTOS.add(dto);
        });

        model.addAttribute("categoryAnimeList", categoryAnimeShowDTOS);
        model.addAttribute("page", "client-index");
        return "Client/client-index";
    }



    @GetMapping("/search")
    public String search(Model model ,@RequestParam String data){

        data = data.trim();
        List<Anime> anime = new ArrayList<>();
        if(data.equals("")){
            anime = animeRepo.findAll();
        }else{
            anime = animeRepo.getData(data);
        }
        model.addAttribute("anime" , anime);
        model.addAttribute("page" , "client-index");


        return "Client/client-index.html";
    }


    @GetMapping("/Client/client-blog")
    public String blog(Model model) {
        model.addAttribute("page", "Client/client-blog");
        return "Client/client-index";
    }

    @GetMapping("/Client/blogDetails")
    public String blogDetails(Model model) {
        model.addAttribute("page", "Client/blogDetails");
        return "Client/client-index";
    }

    @GetMapping("/Client/client-contact")
    public String contact(Model model) {
        model.addAttribute("page", "Client/client-contact");
        return "Client/client-index";
    }
    @GetMapping("/Client/client-categories")
    public String categories(Model model, Pageable pageable) {
        Page<CategoryAnime> pageCategoryAnime = categoryAnimeRepo.findAll(pageable);

        List<CategoryAnimeShowDTO> categoryAnimeShowDTOS = new ArrayList<>();
        pageCategoryAnime.forEach(obj -> {
            CategoryAnimeShowDTO dto = new CategoryAnimeShowDTO();
            dto.setCategoryName(obj.getCategoryName());
            dto.setAnime(obj.getAnimes());
            categoryAnimeShowDTOS.add(dto);
        });
        model.addAttribute("categoryAnimeList", categoryAnimeShowDTOS);
        model.addAttribute("totalPage", pageCategoryAnime.getTotalPages());
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("page", "Client/client-categories");

        return "Client/client-index";
    }

    @GetMapping("/Client/animeDetail/{id}")
    public String animeDetail(@PathVariable Integer id, Model model) {
        List<CategoryAnime> categoryAnimeList = categoryAnimeRepo.findAll();
        List<CategoryAnimeShowDTO> theCategoryAnimeShowDTOS = new ArrayList<>();
        categoryAnimeList.forEach(obj -> {
            CategoryAnimeShowDTO theCategoryAnimeShowDTO = new CategoryAnimeShowDTO();
            theCategoryAnimeShowDTO.setCategoryName(obj.getCategoryName());
            theCategoryAnimeShowDTO.setAnime(obj.getAnimes());
            theCategoryAnimeShowDTOS.add(theCategoryAnimeShowDTO);
        });

        Anime anime = animeRepo.findById(id).orElse(null);
        if (anime != null) {
            model.addAttribute("anime", anime);
        }

        model.addAttribute("categoryAnimeList", theCategoryAnimeShowDTOS);
        model.addAttribute("page", "Client/animeDetail");

        return "Client/client-index";
    }

    @GetMapping("/Client/anime-watching/{id}")
    public String animeWatching(@PathVariable Integer id, Model model) {
        List<CategoryAnime> categoryAnimeList = categoryAnimeRepo.findAll();

        List<CategoryAnimeShowDTO> theCategoryAnimeShowDTOS = new ArrayList<>();

        categoryAnimeList.forEach(obj -> {
            CategoryAnimeShowDTO theCategoryAnimeShowDTO = new CategoryAnimeShowDTO();
            theCategoryAnimeShowDTO.setCategoryName(obj.getCategoryName());
            theCategoryAnimeShowDTO.setAnime(obj.getAnimes());
            theCategoryAnimeShowDTOS.add(theCategoryAnimeShowDTO);
        });

        Anime anime = animeRepo.findById(id).orElse(null);
        if (anime != null) {
            model.addAttribute("anime", anime);
        }

        model.addAttribute("categoryAnimeList", theCategoryAnimeShowDTOS);
        model.addAttribute("page", "Client/anime-watching");
        return "Client/client-index";
    }

    @GetMapping("/category/{categoryName}")
    public String getAnimeByCategory(@PathVariable("categoryName") String categoryName, Model model) {
        CategoryAnime categoryAnime = categoryAnimeRepo.findByCategoryName(categoryName);

        if (categoryAnime == null) {
            return "redirect:/Client/client-index";
        }

        List<Anime> animeList = categoryAnime.getAnimes();
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("animeList", animeList);

        return "Client/category-list";
    }

}

