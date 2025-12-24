package com.example.campusCircle.service;

import com.example.campusCircle.model.Vote;
import com.example.campusCircle.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private KarmaService karmaService;

    public List<Vote> getAllVotes() {
        return voteRepository.findAll();
    }

    public Optional<Vote> getVoteById(Long id) {
        return voteRepository.findById(id);
    }

    public Optional<Vote> getUserVote(Long userId, Long contentId, Vote.ContentType contentType) {
        return voteRepository.findByUserIdAndContentIdAndContentType(userId, contentId, contentType);
    }

    public Integer getVoteCount(Long contentId, Vote.ContentType contentType) {
        Integer count = voteRepository.getVoteCount(contentId, contentType);
        return count != null ? count : 0;
    }

    public Long getUpvoteCount(Long contentId, Vote.ContentType contentType) {
        return voteRepository.countUpvotes(contentId, contentType);
    }

    public Long getDownvoteCount(Long contentId, Vote.ContentType contentType) {
        return voteRepository.countDownvotes(contentId, contentType);
    }

    @Transactional
    public Vote vote(Long userId, Long contentId, Vote.ContentType contentType, int voteValue, Long authorUserId, Long channelId) {
        // Validate vote value
        if (voteValue != 1 && voteValue != -1) {
            throw new IllegalArgumentException("Vote value must be +1 or -1");
        }

        Optional<Vote> existingVote = voteRepository.findByUserIdAndContentIdAndContentType(userId, contentId, contentType);

        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            int oldValue = vote.getVoteValue();
            
            if (oldValue == voteValue) {
                // Same vote, remove it (toggle off)
                voteRepository.delete(vote);
                // Update karma (reverse the old vote)
                updateKarma(authorUserId, contentType, -oldValue, channelId);
                return null;
            } else {
                // Change vote direction
                vote.setVoteValue(voteValue);
                voteRepository.save(vote);
                // Update karma (reverse old + add new = 2x change)
                updateKarma(authorUserId, contentType, voteValue * 2, channelId);
                return vote;
            }
        } else {
            // New vote
            Vote vote = new Vote();
            vote.setUserId(userId);
            vote.setContentId(contentId);
            vote.setContentType(contentType);
            vote.setVoteValue(voteValue);
            voteRepository.save(vote);
            // Update karma
            updateKarma(authorUserId, contentType, voteValue, channelId);
            return vote;
        }
    }

    private void updateKarma(Long authorUserId, Vote.ContentType contentType, int amount, Long channelId) {
        if (authorUserId != null) {
            if (contentType == Vote.ContentType.POST) {
                karmaService.addPostKarma(authorUserId, amount, channelId);
            } else {
                karmaService.addCommentKarma(authorUserId, amount, channelId);
            }
        }
    }

    @Transactional
    public void removeVote(Long userId, Long contentId, Vote.ContentType contentType) {
        voteRepository.deleteByUserIdAndContentIdAndContentType(userId, contentId, contentType);
    }
}
