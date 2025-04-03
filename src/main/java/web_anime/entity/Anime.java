package web_anime.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
    private String link;
    private int viewCount;
    private double rating;

    private String imageUrl;
    private String videoUrl;
    private String trailerUrl;

    @OneToMany(mappedBy = "anime", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Episode> episodes = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_anime_id", referencedColumnName = "id")
    private CategoryAnime categoryAnime;


}

