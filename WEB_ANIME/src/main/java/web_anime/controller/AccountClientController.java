package web_anime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web_anime.entity.Account;
import web_anime.repository.AccountRepository;
import web_anime.service.AccountService;

@Controller
public class AccountClientController {

    @Autowired
    private AccountService accountService;

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

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        // Kiểm tra thông tin đăng nhập
        Account account = accountRepo.findByUsername(username).orElse(null);
        if (account != null && passwordEncoder.matches(password, account.getPassword())) {
            // Đăng nhập thành công
            return "redirect:/"; // Chuyển hướng đến trang chính
        } else {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
            return "Client/client-index"; // Trả về trang đăng nhập với thông báo lỗi
        }
    }
}
