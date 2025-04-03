package web_anime.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "category_anime")
public class CategoryAnime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_name", unique = true, nullable = false)
    private String categoryName;

    private String title;
    private String description;
    private String hashtag;
    private String image;
    private String trailer;
    private String link;

    @OneToMany(mappedBy = "categoryAnime", fetch = FetchType.LAZY)
    private List<Anime> animes;

}

