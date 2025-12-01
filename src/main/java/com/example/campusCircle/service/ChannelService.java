package com.example.campuscircle.service;

import com.example.campuscircle.model.Channel;
import com.example.campuscircle.repository.ChannelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ChannelService {

    private final ChannelRepository channelRepository;

    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    // Créer un nouveau channel
    public Channel createChannel(Channel channel) {
        // Vérifier si le nom du channel existe déjà
        if (channelRepository.existsByName(channel.getName())) {
            throw new RuntimeException("Un channel avec ce nom existe déjà");
        }
        return channelRepository.save(channel);
    }

    // Récupérer un channel par ID
    public Channel getChannel(Long id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Channel non trouvé avec l'ID: " + id));
    }

    // Récupérer tous les channels
    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    // Récupérer les channels par université
    public List<Channel> getChannelsByUniversity(Long universityId) {
        return channelRepository.findByUniversityId(universityId);
    }

    // Récupérer les channels créés par un utilisateur
    public List<Channel> getChannelsByCreator(String createdBy) {
        return channelRepository.findByCreatedBy(createdBy);
    }

    // Rechercher des channels par mot-clé
    public List<Channel> searchChannels(String keyword) {
        return channelRepository.searchChannels(keyword);
    }

    // Récupérer les channels les plus populaires
    public List<Channel> getTopChannels() {
        return channelRepository.findTopChannelsBySubscribers();
    }

    // Mettre à jour un channel
    public Channel updateChannel(Long id, Channel updatedChannel) {
        Channel existingChannel = getChannel(id);
        
        if (updatedChannel.getName() != null) {
            existingChannel.setName(updatedChannel.getName());
        }
        if (updatedChannel.getDescription() != null) {
            existingChannel.setDescription(updatedChannel.getDescription());
        }
        if (updatedChannel.getRules() != null) {
            existingChannel.setRules(updatedChannel.getRules());
        }
        
        return channelRepository.save(existingChannel);
    }

    // Supprimer un channel
    public void deleteChannel(Long id) {
        if (!channelRepository.existsById(id)) {
            throw new RuntimeException("Channel non trouvé avec l'ID: " + id);
        }
        channelRepository.deleteById(id);
    }

    // S'abonner à un channel
    public void subscribeToChannel(Long channelId, Long userId) {
        Channel channel = getChannel(channelId);
        channel.setSubscriberCount(channel.getSubscriberCount() + 1);
        channelRepository.save(channel);
        
        // TODO: Ajouter l'entrée dans la table Subscriptions
    }

    // Se désabonner d'un channel
    public void unsubscribeFromChannel(Long channelId, Long userId) {
        Channel channel = getChannel(channelId);
        if (channel.getSubscriberCount() > 0) {
            channel.setSubscriberCount(channel.getSubscriberCount() - 1);
            channelRepository.save(channel);
        }
        
        // TODO: Supprimer l'entrée de la table Subscriptions
    }

    // Compter les channels d'une université
    public long countChannelsByUniversity(Long universityId) {
        return channelRepository.countByUniversityId(universityId);
    }
}