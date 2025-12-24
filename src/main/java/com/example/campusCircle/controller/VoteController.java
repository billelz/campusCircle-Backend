package com.example.campusCircle.controller;

import com.example.campusCircle.model.Vote;
import com.example.campusCircle.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public Vote castVote(@RequestBody Vote vote) {
        return voteService.castVote(vote);
    }

    @GetMapping
    public List<Vote> getAllVotes() {
        return voteService.getAllVotes();
    }

    @GetMapping("/{id}")
    public Vote getVote(@PathVariable Long id) {
        return voteService.getVote(id);
    }

    @PutMapping("/{id}")
    public Vote updateVote(@PathVariable Long id, @RequestBody Vote vote) {
        return voteService.updateVote(id, vote);
    }

    @DeleteMapping("/{id}")
    public void deleteVote(@PathVariable Long id) {
        voteService.deleteVote(id);
    }
}
