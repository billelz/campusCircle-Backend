package com.example.campusCircle;

import com.example.campusCircle.model.ContentType;
import com.example.campusCircle.model.Karma;
import com.example.campusCircle.model.Vote;
import com.example.campusCircle.repository.KarmaRepository;
import com.example.campusCircle.repository.VoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VotesAndKarmaTest {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private KarmaRepository karmaRepository;

    @Test
    void testVotePersistence() {
        Vote vote = new Vote();
        vote.setUserId(1L);
        vote.setContentId(101L);
        vote.setContentType(ContentType.POST);
        vote.setVoteValue(1);
        vote.setCreatedAt(LocalDateTime.now());

        Vote savedVote = voteRepository.save(vote);
        assertNotNull(savedVote.getId());

        Optional<Vote> retrievedVote = voteRepository.findById(savedVote.getId());
        assertTrue(retrievedVote.isPresent());
        assertEquals(ContentType.POST, retrievedVote.get().getContentType());
        assertEquals(1, retrievedVote.get().getVoteValue());
    }

    @Test
    void testKarmaPersistence() {
        Karma karma = new Karma();
        karma.setUserId(2L);
        karma.setKarmaScore(100L);
        karma.getKarmaByChannel().put(55L, 10);
        karma.getKarmaByChannel().put(56L, 20);

        Karma savedKarma = karmaRepository.save(karma);
        assertEquals(2L, savedKarma.getUserId());

        Optional<Karma> retrievedKarma = karmaRepository.findById(2L);
        assertTrue(retrievedKarma.isPresent());
        assertEquals(100L, retrievedKarma.get().getKarmaScore());
        assertEquals(2, retrievedKarma.get().getKarmaByChannel().size());
        assertEquals(10, retrievedKarma.get().getKarmaByChannel().get(55L));
    }
}
