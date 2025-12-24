package com.example.campusCircle.service;

import com.example.campusCircle.model.Vote;
import com.example.campusCircle.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;

    public Vote castVote(Vote vote) {
        return voteRepository.save(vote);
    }

    public List<Vote> getAllVotes() {
        return voteRepository.findAll();
    }

    public Vote getVote(Long id) {
        return voteRepository.findById(id).orElseThrow(() -> new RuntimeException("Vote not found"));
    }

    public Vote updateVote(Long id, Vote updatedVote) {
        Vote existingVote = getVote(id);
        existingVote.setVoteValue(updatedVote.getVoteValue());
        existingVote.setContentType(updatedVote.getContentType());
        // Add other fields to update as needed
        return voteRepository.save(existingVote);
    }

    public void deleteVote(Long id) {
        voteRepository.deleteById(id);
    }
}
