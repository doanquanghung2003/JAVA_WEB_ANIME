package web_anime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web_anime.entity.Anime;
import web_anime.entity.Account;
import web_anime.entity.ViewTracking;
import web_anime.repository.ViewTrackingRepository;
import web_anime.repository.AnimeRepository;

import java.time.LocalDateTime;

@Service
public class ViewTrackingService {

    @Autowired
    private ViewTrackingRepository viewTrackingRepository;

    @Autowired
    private AnimeRepository animeRepository;

    private static final int MINIMUM_WATCH_TIME = 15; // Thời gian tối thiểu để tính lượt xem (giây)

    public void startWatching(Anime anime, Account account) {
        ViewTracking tracking = viewTrackingRepository.findByAnimeAndAccount(anime, account)
                .orElse(new ViewTracking());
        
        tracking.setAnime(anime);
        tracking.setAccount(account);
        tracking.setStartTime(LocalDateTime.now());
        tracking.setWatchDuration(0);
        tracking.setCounted(false);
        
        viewTrackingRepository.save(tracking);
    }

    public void updateWatchDuration(Anime anime, Account account, int duration) {
        ViewTracking tracking = viewTrackingRepository.findByAnimeAndAccount(anime, account)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi theo dõi"));

        tracking.setWatchDuration(duration);

        // Kiểm tra nếu đã xem đủ 15 giây và chưa tính lượt xem
        if (duration >= MINIMUM_WATCH_TIME && !tracking.isCounted()) {
            anime.setViewCount(anime.getViewCount() + 1);
            animeRepository.save(anime);
            tracking.setCounted(true);
        }

        viewTrackingRepository.save(tracking);
    }
} 