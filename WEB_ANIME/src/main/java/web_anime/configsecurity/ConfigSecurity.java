package web_anime.configsecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import web_anime.service.AccountServiceImpl;

@Configuration
public class ConfigSecurity {

    private String[] arrPath = {"/", "/client-index", "/webjars/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationProvider authenticationProvider) throws Exception {
        httpSecurity.csrf().disable();

        httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(arrPath).permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/fonts/**", "/sass/**", "/Source/**", "/videos/**").permitAll()
                        .requestMatchers("/signup", "/signup/save").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                         .loginPage("/login")
                         .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        httpSecurity.authenticationProvider(authenticationProvider);

        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService userDetailsService
            (AccountServiceImpl accountService) {
        return accountService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}

