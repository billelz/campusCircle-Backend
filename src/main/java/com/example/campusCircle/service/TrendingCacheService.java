package com.example.campusCircle.service;

import com.example.campusCircle.model.nosql.TrendingCache;
import com.example.campusCircle.repository.TrendingCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TrendingCacheService {

    @Autowired
    private TrendingCacheRepository trendingCacheRepository;

    public List<TrendingCache> getAllCaches() {
        return trendingCacheRepository.findAll();
    }

    public Optional<TrendingCache> getCacheById(String id) {
        return trendingCacheRepository.findById(id);
    }

    public List<TrendingCache> getCachesByType(String cacheType) {
        return trendingCacheRepository.findByCacheType(cacheType);
    }

    public List<TrendingCache> getCachesByUniversity(Long universityId) {
        return trendingCacheRepository.findByUniversityId(universityId);
    }

    public Optional<TrendingCache> getTrendingCache(String cacheType, Long universityId, String timeframe) {
        return trendingCacheRepository.findByCacheTypeAndUniversityIdAndTimeframe(cacheType, universityId, timeframe);
    }

    public List<TrendingCache> getTrendingByTypeAndTimeframe(String cacheType, String timeframe) {
        return trendingCacheRepository.findByCacheTypeAndTimeframe(cacheType, timeframe);
    }

    public TrendingCache saveCache(TrendingCache cache) {
        cache.setComputedAt(LocalDateTime.now());
        return trendingCacheRepository.save(cache);
    }

    public TrendingCache createOrUpdateCache(String cacheType, Long universityId, String universityName,
                                              String timeframe, List<TrendingCache.TrendingItem> items,
                                              TrendingCache.ComputationMetadata metadata) {
        TrendingCache cache = trendingCacheRepository
                .findByCacheTypeAndUniversityIdAndTimeframe(cacheType, universityId, timeframe)
                .orElse(new TrendingCache());

        cache.setCacheType(cacheType);
        cache.setUniversityId(universityId);
        cache.setUniversityName(universityName);
        cache.setTimeframe(timeframe);
        cache.setItems(items);
        cache.setTotalItems(items != null ? items.size() : 0);
        cache.setComputedAt(LocalDateTime.now());
        cache.setMetadata(metadata);

        // Set expiration based on timeframe
        LocalDateTime expiresAt;
        switch (timeframe) {
            case "hourly":
                expiresAt = LocalDateTime.now().plusHours(1);
                break;
            case "daily":
                expiresAt = LocalDateTime.now().plusDays(1);
                break;
            case "weekly":
                expiresAt = LocalDateTime.now().plusWeeks(1);
                break;
            default:
                expiresAt = LocalDateTime.now().plusHours(6);
        }
        cache.setExpiresAt(expiresAt);

        return trendingCacheRepository.save(cache);
    }

    public void deleteCache(String id) {
        trendingCacheRepository.deleteById(id);
    }

    public void deleteCacheByTypeAndUniversity(String cacheType, Long universityId) {
        trendingCacheRepository.deleteByCacheTypeAndUniversityId(cacheType, universityId);
    }

    public void deleteExpiredCaches() {
        trendingCacheRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    public List<TrendingCache> getExpiredCaches() {
        return trendingCacheRepository.findByExpiresAtBefore(LocalDateTime.now());
    }

    public boolean isCacheValid(String cacheType, Long universityId, String timeframe) {
        Optional<TrendingCache> cache = getTrendingCache(cacheType, universityId, timeframe);
        return cache.isPresent() && cache.get().getExpiresAt().isAfter(LocalDateTime.now());
    }
}
