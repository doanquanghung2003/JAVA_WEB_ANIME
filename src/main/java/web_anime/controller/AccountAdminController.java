package web_anime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import web_anime.entity.Account;
import web_anime.repository.AccountRepository;
import web_anime.service.AccountService;
import web_anime.service.CloudinaryService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class AccountAdminController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/admin/account")
    public String getAll(Model model, Pageable pageable) {

        Pageable pageableWithTwoItems = PageRequest.of(pageable.getPageNumber(), 2);
        Page<Account> pageAccount = accountRepo.findAll(pageableWithTwoItems);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<Account> optionalAccount = accountRepo.findByUsername(username);
        model.addAttribute("loggedInAccount", optionalAccount.orElse(null));

        List<Account> accountList = pageAccount.toList();
        System.out.println(accountList);

        model.addAttribute("accountList", accountList);
        model.addAttribute("totalPage", pageAccount.getTotalPages());
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("page", "/Admin/account-list");

        return "/Admin/account-list";
    }

    @GetMapping("/admin/account/add")
    public String showAddForm(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<Account> optionalAccount = accountRepo.findByUsername(username);
        model.addAttribute("loggedInAccount", optionalAccount.orElse(null));

        model.addAttribute("account", new Account());
        return "/Admin/admin-account-add";
    }

    @PostMapping("/admin/account/add")
    public String addAccount(@ModelAttribute("account") Account account,
                             @RequestParam("avatar") MultipartFile avatar) {
        try {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            String avatarUrl = cloudinaryService.uploadFile(avatar);
            account.setAvatarUrl(avatarUrl);
            accountService.add(account);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/admin/account?error=upload";
        }
        return "redirect:/admin/account";
    }

    @GetMapping("/admin/account/edit/{id}")
    public String editAccount(@PathVariable Integer id, Model model) {
        Optional<Account> optionalAccount = accountRepo.findById(id);
        if (optionalAccount.isEmpty()) {
            throw new IllegalArgumentException("Invalid account Id: " + id);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        model.addAttribute("loggedInAccount", optionalAccount.orElse(null));

        Account account = optionalAccount.get();
        model.addAttribute("admin", account);
        return "/Admin/admin-account-edit";
    }

    @PostMapping("/admin/update-account/{id}")
    public String updateAccount(@PathVariable Integer id,
                                @RequestParam String username,
                                @RequestParam String email,
                                @RequestParam String role,
                                @RequestParam String displayName,
                                @RequestParam(required = false) String password,
                                @RequestParam(required = false) MultipartFile avatar) {
        try {
            Optional<Account> optionalAccount = accountRepo.findById(id);
            if (optionalAccount.isEmpty()) {
                throw new IllegalArgumentException("Invalid admin Id: " + id);
            }
            Account account = optionalAccount.get();
            account.setUsername(username);
            account.setEmail(email);
            account.setRole(role);
            account.setDisplayName(displayName);

            if (password != null && !password.isEmpty()) {
                account.setPassword(passwordEncoder.encode(password));
            }

            if (avatar != null && !avatar.isEmpty()) {
                String avatarUrl = cloudinaryService.uploadFile(avatar);
                account.setAvatarUrl(avatarUrl);
            }

            accountRepo.save(account);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/admin/account?error=upload";
        }
        return "redirect:/admin/account";
    }

    @GetMapping("/admin/account/delete/{id}")
    public String delete(@PathVariable Integer id) {
        Optional<Account> opAccount = accountRepo.findById(id);
        if (opAccount.isEmpty()) {
            System.out.println("Not found Account with id = " + id);
        }

        accountRepo.deleteById(id);

        return "redirect:/admin/account";
    }


}

