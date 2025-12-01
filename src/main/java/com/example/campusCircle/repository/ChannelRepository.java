package com.example.campuscircle.repository;

import com.example.campuscircle.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {

    // Trouver un channel par nom
    Optional<Channel> findByName(String name);

    // Trouver tous les channels d'une université
    List<Channel> findByUniversityId(Long universityId);

    // Trouver les channels créés par un utilisateur spécifique
    List<Channel> findByCreatedBy(String createdBy);

    // Trouver les channels les plus populaires (par nombre d'abonnés)
    @Query("SELECT c FROM Channel c ORDER BY c.subscriberCount DESC")
    List<Channel> findTopChannelsBySubscribers();

    // Vérifier si un channel existe par nom
    boolean existsByName(String name);

    // Rechercher des channels par mot-clé dans le nom ou la description
    @Query("SELECT c FROM Channel c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Channel> searchChannels(@Param("keyword") String keyword);

    // Compter le nombre de channels par université
    long countByUniversityId(Long universityId);
}