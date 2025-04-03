package web_anime.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    @GetMapping("/admin")
    public String clientIndex(Model model){
        model.addAttribute("page" , "admin");
        return "Admin/admin-index";
    }
}
