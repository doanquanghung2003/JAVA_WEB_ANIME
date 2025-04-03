package web_anime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import web_anime.entity.Account;
import web_anime.repository.AccountRepository;

import java.util.Optional;

@Controller
public class AdminController {

    private final AccountRepository accountRepo;

    @Autowired
    public AdminController(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    @GetMapping("/admin")
    public String adminIndex(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<Account> optionalAccount = accountRepo.findByUsername(username);
        model.addAttribute("loggedInAccount", optionalAccount.orElse(null));

        model.addAttribute("page", "admin");
        return "Admin/admin-index";
    }
}
