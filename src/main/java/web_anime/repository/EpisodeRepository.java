package web_anime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web_anime.entity.Anime;
import web_anime.entity.Episode;
import java.util.List;

public interface EpisodeRepository extends JpaRepository<Episode, Integer> {

    boolean existsByAnimeAndEpisodeNumber(Anime anime, int episodeNumber);

    List<Episode> findByAnimeId(Integer animeId);
}
