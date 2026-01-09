package com.example.campusCircle.repository;

import com.example.campusCircle.model.nosql.TrendingCache;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrendingCacheRepository extends MongoRepository<TrendingCache, String> {

    List<TrendingCache> findByCacheType(String cacheType);

    List<TrendingCache> findByUniversityId(Long universityId);

    List<TrendingCache> findByTimeframe(String timeframe);

    Optional<TrendingCache> findByCacheTypeAndUniversityIdAndTimeframe(
            String cacheType, Long universityId, String timeframe);

    List<TrendingCache> findByCacheTypeAndTimeframe(String cacheType, String timeframe);

    List<TrendingCache> findByExpiresAtBefore(LocalDateTime dateTime);

    void deleteByCacheTypeAndUniversityId(String cacheType, Long universityId);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);

    boolean existsByCacheTypeAndUniversityIdAndTimeframe(String cacheType, Long universityId, String timeframe);
}
