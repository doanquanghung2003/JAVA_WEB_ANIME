package web_anime.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "episode")
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int episodeNumber;
    private String videoUrl;

    @ManyToOne
    @JoinColumn(name = "anime_id", referencedColumnName = "id")
    private Anime anime;
}