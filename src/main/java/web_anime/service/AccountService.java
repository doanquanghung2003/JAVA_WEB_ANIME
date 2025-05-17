package web_anime.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import web_anime.entity.Account;

import java.util.List;

public interface AccountService extends UserDetailsService {


    // abstract method , public access modifier
    List<Account> getAll();

    void add(Account account);

    Account findById(Integer id);
}
