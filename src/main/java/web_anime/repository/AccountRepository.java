package web_anime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web_anime.entity.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account , Integer> {

    Optional<Account> findByUsername(String username);

    @Query(value = "FROM Account where username = :username")
    Account getDataByUsername(String username);

    Optional<Account> findAccountByEmail(String email);

}