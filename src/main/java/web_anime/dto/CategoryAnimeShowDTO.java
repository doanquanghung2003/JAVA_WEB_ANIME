package web_anime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import web_anime.entity.Anime;

import java.util.List;

@Data
@NoArgsConstructor

public class CategoryAnimeShowDTO {
    private String categoryName;
    private List<Anime> anime;

    public CategoryAnimeShowDTO(String categoryName, List<Anime> anime) {
        this.categoryName = categoryName;
        this.anime = anime;
    }
}