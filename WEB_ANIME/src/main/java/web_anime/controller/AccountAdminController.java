package web_anime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import web_anime.entity.Account;
import web_anime.repository.AccountRepository;
import web_anime.service.AccountService;

import java.util.List;
import java.util.Optional;

@Controller
public class AccountAdminController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepo;

    @GetMapping("/admin/account")
    public String getAll(Model model , Pageable pageable) {

        Page<Account> pageAccount = accountRepo.findAll(pageable);
        List<Account> accounts = pageAccount.toList();
        System.out.println(accounts);

        model.addAttribute("accounts", accounts);
        model.addAttribute("totalPage", pageAccount.getTotalPages());
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("page" , "/Admin/account-list");

        return "/Admin/admin-index";
        // "đúng tên file html"
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

    @GetMapping("/admin/account/add")
    public String showAddForm(Model model) {
        model.addAttribute("account", new Account());
        return "/Admin/account-list";
    }

    @PostMapping("/admin/account/add")
    public String addAccount(@ModelAttribute("account") Account account) {
        accountService.add(account);
        return "redirect:/admin/account";
    }
}
