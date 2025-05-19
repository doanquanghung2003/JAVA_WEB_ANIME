package web_anime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import web_anime.dto.CategoryAnimeShowDTO;
import web_anime.entity.Account;
import web_anime.entity.Anime;
import web_anime.entity.CategoryAnime;
import web_anime.repository.AccountRepository;
import web_anime.repository.AnimeRepository;
import web_anime.repository.CategoryAnimeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ClientController {

    @Autowired
    private CategoryAnimeRepository categoryAnimeRepo;

    @Autowired
    private AnimeRepository animeRepo;

    @Autowired
    private AccountRepository accountRepo;

    @ModelAttribute("loggedInAccount")
    public Account getLoggedInAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        // System.out.println("Principal: " + auth.getPrincipal());

        // If logged in by OAuth2
        if (auth instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) auth;
            OAuth2User oauth2User = oauthToken.getPrincipal();
            String email = oauth2User.getAttribute("email");
    
            Account account = accountRepo.findAccountByEmail(email).orElse(null);
            System.out.println("Account: " + (account != null ? account : "Notfound"));
    
            return account;
        }

        return accountRepo.findByUsername(auth.getName()).orElse(null);
    }

    @ModelAttribute("allCategories")
    public List<CategoryAnime> getAllCategories() {
        return categoryAnimeRepo.findAll();
    }

    @GetMapping({ "/", "/client-index" })
    public String clientIndex(Model model) {

        List<CategoryAnime> categoryAnimeList = categoryAnimeRepo.findAll();
        List<CategoryAnimeShowDTO> categoryAnimeShowDTOS = new ArrayList<>();

        for (CategoryAnime category : categoryAnimeList) {
            if (category.getAnimes() != null && !category.getAnimes().isEmpty()) {
                CategoryAnimeShowDTO dto = new CategoryAnimeShowDTO();
                dto.setCategoryName(category.getCategoryName());
                dto.setAnime(category.getAnimes());
                categoryAnimeShowDTOS.add(dto);
            }
        }

        model.addAttribute("categoryAnimeList", categoryAnimeShowDTOS);
        model.addAttribute("page", "client-index");
        model.addAttribute("topViewedAnimes", animeRepo.findTop5ByOrderByViewCountDesc());
        return "Client/client-index";
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam(defaultValue = "") String data) {
        data = data.trim();
        List<Anime> animeList;
        if (data.isEmpty()) {
            animeList = animeRepo.findAll();
        } else {
            animeList = animeRepo.getData(data);
        }
        model.addAttribute("animeList", animeList);
        model.addAttribute("isSearch", true);
        model.addAttribute("page", "Client/client-search");
        return "Client/client-index";
    }

    @GetMapping("/Client/client-blog")
    public String blog(Model model) {
        model.addAttribute("page", "Client/client-blog");
        return "Client/client-index";
    }

    @GetMapping("/Client/blogDetails")
    public String blogDetails(Model model) {
        model.addAttribute("page", "Client/blog-details");
        return "Client/client-index";
    }

    @GetMapping("/Client/client-contact")
    public String contact(Model model) {
        model.addAttribute("page", "Client/client-contact");
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
            model.addAttribute("categoryAnimeList", theCategoryAnimeShowDTOS);
            model.addAttribute("page", "Client/anime-details");
        } else {
            model.addAttribute("error", "Anime not found");
        }

        return "Client/client-index";
    }

    @GetMapping("/Client/anime-watching/{id}")
    public String animeWatching(@PathVariable Integer id, Model model) {
        Optional<Anime> optionalAnime = animeRepo.findByIdWithEpisodes(id);

        if (!optionalAnime.isPresent()) {
            return "redirect:/client-index?error=anime_not_found";
        }

        Anime anime = optionalAnime.get();
        System.out.println("Trailer URL: " + anime.getTrailerUrl());
        anime.getEpisodes().forEach(ep -> System.out.println("Episode URL: " + ep.getVideoUrl()));
        if (anime.getEpisodes() == null) {
            anime.setEpisodes(new ArrayList<>());
        }

        model.addAttribute("anime", anime);
        model.addAttribute("page", "Client/anime-watching");
        return "Client/client-index";
    }

    @GetMapping("/Client/client-categories/{categoryName}")
    public String getAnimeByCategory(@PathVariable("categoryName") String categoryName, Model model,
            Pageable pageable) {
        CategoryAnime categoryAnime = categoryAnimeRepo.findByCategoryName(categoryName);

        if (categoryAnime == null) {
            return "redirect:/Client/client-index";
        }

        Page<Anime> animePage = animeRepo.findByCategoryAnime(categoryAnime, pageable);
        List<Anime> animeList = animePage.getContent();

        model.addAttribute("categoryName", categoryName);
        model.addAttribute("animeList", animeList);
        model.addAttribute("totalPage", animePage.getTotalPages());
        model.addAttribute("currentPage", animePage.getNumber());
        model.addAttribute("page", "Client/client-categories");

        return "Client/client-index";
    }

}
