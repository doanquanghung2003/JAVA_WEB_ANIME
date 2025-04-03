package web_anime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web_anime.entity.CategoryAnime;

import java.util.List;

@Repository
public interface CategoryAnimeRepository extends JpaRepository<CategoryAnime, Integer> {

    @Query(value = "FROM CategoryAnime WHERE categoryName LIKE CONCAT('%', :categoryName, '%')")
    List<CategoryAnime> getData(String categoryName);
    
    CategoryAnime findByCategoryName(String categoryName);

    List<CategoryAnime> findAll();
}
