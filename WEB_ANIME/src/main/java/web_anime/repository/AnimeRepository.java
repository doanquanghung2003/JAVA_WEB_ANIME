package web_anime.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web_anime.entity.Anime;

import java.util.List;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Integer> {

    @Query(value = "FROM Anime WHERE animeName LIKE CONCAT('%' , :animeName, '%')")
    List<Anime> getData(String animeName);

    Page<Anime> findByAnimeNameContaining(String animeName, Pageable pageable);

    List<Anime> findAll();

    @Modifying
    @Query("DELETE FROM Anime a WHERE a.id = :animeId")
    void deleteAnimeById(Integer animeId);

}
