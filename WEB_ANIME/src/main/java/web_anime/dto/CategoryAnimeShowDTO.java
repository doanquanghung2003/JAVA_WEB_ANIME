package web_anime.dto;

import lombok.Data;
import web_anime.entity.Anime;

import java.util.List;

@Data
public class CategoryAnimeShowDTO {

    private Integer id;

    private String categoryName;

    private String title;

    private String description;

    private String hashtag;

    private String image;

    private String trailer;

    private String link;

    private List<Anime> anime;

}

