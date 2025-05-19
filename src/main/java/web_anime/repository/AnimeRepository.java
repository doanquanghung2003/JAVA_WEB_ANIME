package web_anime.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web_anime.entity.Anime;
import web_anime.entity.CategoryAnime;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Integer> {

//    @Query(value = "FROM Anime WHERE animeName LIKE CONCAT('%' , :animeName, '%')")
//    List<Anime> getData( String animeName);

    @Query("SELECT a FROM Anime a WHERE LOWER(a.animeName) LIKE LOWER(CONCAT('%', :animeName, '%'))")
    List<Anime> getData(@Param("animeName") String animeName);

    @Query("SELECT a FROM Anime a LEFT JOIN FETCH a.episodes WHERE a.id = :id")
    Optional<Anime> findByIdWithEpisodes(@Param("id") Integer id);

    Page<Anime> findByAnimeNameContaining(String animeName, Pageable pageable);

    List<Anime> findAll();

    @Modifying
    @Query("DELETE FROM Anime a WHERE a.id = :animeId")
    void deleteAnimeById(Integer animeId);

    Page<Anime> findByCategoryAnime(CategoryAnime categoryAnime, Pageable pageable);

    List<Anime> findByCategoryAnime_Id(Integer categoryId);

    @Query(value = "SELECT * FROM anime ORDER BY view_count DESC LIMIT 5", nativeQuery = true)
    List<Anime> findTop5ByOrderByViewCountDesc();

}
