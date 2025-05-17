package web_anime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web_anime.entity.Anime;
import web_anime.entity.Account;
import web_anime.service.ViewTrackingService;
import web_anime.service.AnimeService;
import web_anime.service.AccountService;

@RestController
@RequestMapping("/api/view-tracking")
public class ViewTrackingController {

    @Autowired
    private ViewTrackingService viewTrackingService;

    @Autowired
    private AnimeService animeService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/start/{animeId}")
    public ResponseEntity<?> startWatching(@PathVariable Integer animeId, @RequestParam Integer accountId) {
        Anime anime = animeService.findById(animeId);
        Account account = accountService.findById(accountId);
        
        viewTrackingService.startWatching(anime, account);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update/{animeId}")
    public ResponseEntity<?> updateWatchDuration(
            @PathVariable Integer animeId,
            @RequestParam Integer accountId,
            @RequestParam int duration) {
        
        Anime anime = animeService.findById(animeId);
        Account account = accountService.findById(accountId);
        
        viewTrackingService.updateWatchDuration(anime, account, duration);
        return ResponseEntity.ok().build();
    }
} 