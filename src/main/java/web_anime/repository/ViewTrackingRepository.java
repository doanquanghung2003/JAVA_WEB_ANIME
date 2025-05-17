package web_anime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web_anime.entity.ViewTracking;
import web_anime.entity.Anime;
import web_anime.entity.Account;

import java.util.Optional;

public interface ViewTrackingRepository extends JpaRepository<ViewTracking, Long> {
    Optional<ViewTracking> findByAnimeAndAccount(Anime anime, Account account);
} 