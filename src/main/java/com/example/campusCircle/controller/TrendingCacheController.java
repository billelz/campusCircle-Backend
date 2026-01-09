package com.example.campusCircle.controller;

import com.example.campusCircle.model.nosql.TrendingCache;
import com.example.campusCircle.service.TrendingCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trending")
@CrossOrigin(origins = "*")
public class TrendingCacheController {

    @Autowired
    private TrendingCacheService trendingCacheService;

    @GetMapping
    public ResponseEntity<List<TrendingCache>> getAllCaches() {
        return ResponseEntity.ok(trendingCacheService.getAllCaches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrendingCache> getCacheById(@PathVariable String id) {
        return trendingCacheService.getCacheById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{cacheType}")
    public ResponseEntity<List<TrendingCache>> getCachesByType(@PathVariable String cacheType) {
        return ResponseEntity.ok(trendingCacheService.getCachesByType(cacheType));
    }

    @GetMapping("/university/{universityId}")
    public ResponseEntity<List<TrendingCache>> getCachesByUniversity(@PathVariable Long universityId) {
        return ResponseEntity.ok(trendingCacheService.getCachesByUniversity(universityId));
    }

    @GetMapping("/posts")
    public ResponseEntity<TrendingCache> getTrendingPosts(
            @RequestParam Long universityId,
            @RequestParam(defaultValue = "daily") String timeframe) {
        return trendingCacheService.getTrendingCache("posts", universityId, timeframe)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/channels")
    public ResponseEntity<TrendingCache> getTrendingChannels(
            @RequestParam Long universityId,
            @RequestParam(defaultValue = "daily") String timeframe) {
        return trendingCacheService.getTrendingCache("channels", universityId, timeframe)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/hashtags")
    public ResponseEntity<TrendingCache> getTrendingHashtags(
            @RequestParam Long universityId,
            @RequestParam(defaultValue = "daily") String timeframe) {
        return trendingCacheService.getTrendingCache("hashtags", universityId, timeframe)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/valid")
    public ResponseEntity<Map<String, Object>> isCacheValid(
            @RequestParam String cacheType,
            @RequestParam Long universityId,
            @RequestParam(defaultValue = "daily") String timeframe) {
        Map<String, Object> response = new HashMap<>();
        response.put("cacheType", cacheType);
        response.put("universityId", universityId);
        response.put("timeframe", timeframe);
        response.put("isValid", trendingCacheService.isCacheValid(cacheType, universityId, timeframe));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TrendingCache> createCache(@RequestBody TrendingCache cache) {
        TrendingCache saved = trendingCacheService.saveCache(cache);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/update")
    public ResponseEntity<TrendingCache> updateTrendingCache(@RequestBody UpdateCacheRequest request) {
        TrendingCache cache = trendingCacheService.createOrUpdateCache(
                request.getCacheType(),
                request.getUniversityId(),
                request.getUniversityName(),
                request.getTimeframe(),
                request.getItems(),
                request.getMetadata()
        );
        return ResponseEntity.ok(cache);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCache(@PathVariable String id) {
        trendingCacheService.deleteCache(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/expired")
    public ResponseEntity<Map<String, String>> deleteExpiredCaches() {
        trendingCacheService.deleteExpiredCaches();
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Expired caches deleted");
        return ResponseEntity.ok(response);
    }

    // DTO
    public static class UpdateCacheRequest {
        private String cacheType;
        private Long universityId;
        private String universityName;
        private String timeframe;
        private List<TrendingCache.TrendingItem> items;
        private TrendingCache.ComputationMetadata metadata;

        public String getCacheType() { return cacheType; }
        public void setCacheType(String cacheType) { this.cacheType = cacheType; }
        public Long getUniversityId() { return universityId; }
        public void setUniversityId(Long universityId) { this.universityId = universityId; }
        public String getUniversityName() { return universityName; }
        public void setUniversityName(String universityName) { this.universityName = universityName; }
        public String getTimeframe() { return timeframe; }
        public void setTimeframe(String timeframe) { this.timeframe = timeframe; }
        public List<TrendingCache.TrendingItem> getItems() { return items; }
        public void setItems(List<TrendingCache.TrendingItem> items) { this.items = items; }
        public TrendingCache.ComputationMetadata getMetadata() { return metadata; }
        public void setMetadata(TrendingCache.ComputationMetadata metadata) { this.metadata = metadata; }
    }
}
