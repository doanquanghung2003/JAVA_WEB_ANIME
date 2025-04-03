package web_anime.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import web_anime.entity.Account;
import web_anime.repository.AccountRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String email = oauth2User.getAttribute("email");

        Optional<Account> existingAccount = accountRepo.findAccountByEmail(email);
        if (!existingAccount.isPresent()) {
            Account newAccount = new Account();
            newAccount.setUsername(email.split("@")[0]);
            newAccount.setDisplayName(oauth2User.getAttribute("name"));
            newAccount.setPassword(passwordEncoder.encode("default_password"));
            newAccount.setEmail(email);
            newAccount.setRole("CLIENT");
            newAccount.setAvatarUrl(oauth2User.getAttribute("picture"));
            newAccount.setCreateAt(LocalDateTime.now()); 
            accountRepo.save(newAccount);
        }

        return oauth2User;
    }
}