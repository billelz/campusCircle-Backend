package com.example.campusCircle.service;

import com.example.campusCircle.model.Channel;
import com.example.campusCircle.model.Post;
import com.example.campusCircle.model.nosql.PostContent;
import com.example.campusCircle.model.University;
import com.example.campusCircle.repository.UniversityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UniversityService {

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private AIService aiService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private PostContentService postContentService;

    @Autowired
    private PostService postService;

    public UniversityService(UniversityRepository universityRepository) {
        this.universityRepository = universityRepository;
    }

    public University createUniversity(University university) {
        return universityRepository.save(university);
    }

    public University getUniversity(Long id) {
        return universityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("University not found"));
    }

    public List<University> getAllUniversities() {
        return universityRepository.findAll();
    }

    public University updateUniversity(Long id, University updated) {
        University existing = getUniversity(id);

        existing.setName(updated.getName());
        existing.setDomain(updated.getDomain());
        existing.setLocation(updated.getLocation());
        existing.setStudentCount(updated.getStudentCount());
        existing.setActiveStatus(updated.getActiveStatus());

        return universityRepository.save(existing);
    }

    public void deleteUniversity(Long id) {
        universityRepository.deleteById(id);
    }

    /**
     * AI-Powered University Insights (Optimized)
     * Calculates aggregate metrics using pre-analyzed and stored toxicity scores.
     */
    public Map<String, Double> getUniversityInsights(Long universityId) {
        List<Channel> channels = channelService.getChannelsByUniversity(universityId);

        if (channels.isEmpty()) {
            return Map.of("average_toxicity", 0.0, "campus_pulse_score", 1.0, "analyzed_count", 0.0);
        }

        double totalToxicity = 0.0;
        int count = 0;

        for (Channel channel : channels) {
            List<Post> posts = postService.getPostsByChannel(channel.getId());
            for (Post post : posts) {
                try {
                    PostContent content = postContentService.getPostContent(post.getId());
                    // Use the stored score if available
                    if (content.getToxicityScore() != null) {
                        totalToxicity += content.getToxicityScore();
                        count++;
                    } else if (content.getBodyText() != null) {
                        // Fallback: analyze and save for next time
                        Map<String, Object> res = aiService.analyzeContent(content.getBodyText());
                        double score = (double) res.get("score");
                        content.setToxicityScore(score);
                        postContentService.savePost(content);
                        totalToxicity += score;
                        count++;
                    }
                } catch (Exception e) {
                    // Ignore errors
                }
            }
        }

        double avgToxicity = count > 0 ? totalToxicity / count : 0.0;

        Map<String, Double> metrics = new HashMap<>();
        metrics.put("average_toxicity", avgToxicity);
        metrics.put("campus_pulse_score", 1.0 - avgToxicity);
        metrics.put("analyzed_count", (double) count);

        return metrics;
    }
}
