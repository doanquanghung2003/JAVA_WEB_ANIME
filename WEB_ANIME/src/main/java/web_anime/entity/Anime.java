package web_anime.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "anime")
public class Anime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;

    private String animeName;
    private String title;
    private String description;
    private String hashtag;
    private String image;
    private String trailer;
    private String link;
    private int viewCount;
    private double rating;
    private String category;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_anime_id", referencedColumnName = "id")
    private CategoryAnime categoryAnime;

}

