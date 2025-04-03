package web_anime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import web_anime.entity.Account;
import web_anime.repository.AccountRepository;
import web_anime.service.AccountService;
import web_anime.service.CloudinaryService;

import java.io.IOException;
import java.util.Optional;

@Controller
public class AccountClientController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String accountClientLogin(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("page", "login");
        return "Client/client-index";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        Account account = accountRepo.findByUsername(username).orElse(null);
        if (account != null && passwordEncoder.matches(password, account.getPassword())) {
            return "redirect:/";
        } else {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
            return "Client/client-index";
        }
    }

    @GetMapping("/signup")
    public String accountClientSignup(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("page", "signup");
        return "Client/client-index";
    }

    @PostMapping("/signup/save")
    public String accountClientSignupSave(@RequestParam String username,
                                          @RequestParam String email,
                                          @RequestParam String password,
                                          Model model) {
        Account theAccount = new Account();
        theAccount.setUsername(username);
        theAccount.setEmail(email);
        theAccount.setPassword(passwordEncoder.encode(password));
        accountRepo.save(theAccount);

        model.addAttribute("message", "Đăng ký thành công!");
        return "redirect:/login";
    }

    @GetMapping("/information-manager")
    public String information(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<Account> optionalAccount = accountRepo.findByUsername(username);
        model.addAttribute("loggedInAccount", optionalAccount.orElse(null));

        model.addAttribute("page", "information-manager");
        return "Client/client-index";
    }

    @PostMapping("/information-manager/{id}")
    public String updateAccount(@PathVariable Integer id,
                                @RequestParam String username,
                                @RequestParam String email,
                                @RequestParam String displayName,
                                @RequestParam(required = false) String password,
                                @RequestParam(required = false) MultipartFile avatar) {
        try {
            Optional<Account> optionalAccount = accountRepo.findById(id);
            if (optionalAccount.isEmpty()) {
                throw new IllegalArgumentException("Không tìm thấy tài khoản có ID: " + id);
            }
            Account account = optionalAccount.get();
            account.setUsername(username);
            account.setEmail(email);
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
            return "redirect:/information-manager?error=upload";
        }
        return "redirect:/information-manager?success";
    }

}
