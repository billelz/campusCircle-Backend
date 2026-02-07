package com.example.campusCircle.controller;

import com.example.campusCircle.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Test controller for AI moderation - useful for testing toxicity detection
 */
@RestController
@RequestMapping("/api/test/moderation")
public class ModerationTestController {

    @Autowired
    private AIService aiService;

    @Value("${ai.moderation.huggingface.api-key:}")
    private String huggingFaceApiKey;

    /**
     * Test endpoint to analyze text for toxicity
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeText(@RequestBody Map<String, String> request) {
        String text = request.get("text");

        if (text == null || text.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "error", "Text field is required",
                            "example", Map.of("text", "Your content to analyze")));
        }

        Map<String, Object> results = aiService.analyzeContent(text);
        return ResponseEntity.ok(results);
    }

    /**
     * Quick test with examples
     */
    @GetMapping("/examples")
    public ResponseEntity<Map<String, Object>> getExamples() {
        String[] testCases = {
                "I hate Mondays",
                "You are stupid",
                "This is a great post!",
                "I want to kill myself",
                "Buy now! Click here!",
                "This movie is killer!"
        };

        Map<String, Object> results = new HashMap<>();

        for (String testCase : testCases) {
            results.put(testCase, aiService.analyzeContent(testCase));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Test results for various scenarios",
                "results", results,
                "note", "Check if word boundaries prevent false positives"));
    }

    /**
     * Generate demo data for university analytics testing
     */
    @Autowired
    private com.example.campusCircle.repository.ChannelRepository channelRepository;

    @Autowired
    private com.example.campusCircle.repository.PostRepository postRepository;

    @Autowired
    private com.example.campusCircle.service.PostContentService postContentService;

    @PostMapping("/setup-demo")
    public ResponseEntity<String> setupDemoData() {
        // Create a test channel for University 1
        com.example.campusCircle.model.Channel channel = new com.example.campusCircle.model.Channel();
        channel.setName("Campus Pulse Demo");
        channel.setDescription("Test channel for AI analytics");
        channel.setUniversityId(1L);
        channel.setCategory(com.example.campusCircle.model.Channel.ChannelCategory.SOCIAL);
        channel.setCreatedBy("system");

        com.example.campusCircle.model.Channel savedChannel = channelRepository.save(channel);

        // Create some posts with varying sentiment
        String[] titles = { "Great day!", "Wifi issues", "Stressful exams", "Love the library",
                "Cafeteria food is bad" };
        String[] bodies = {
                "I had a wonderful day at the campus today! The weather is perfect.",
                "The wifi in the engineering building is always dropping. So annoying!",
                "Exam season is here and everyone is so stressed. I can't sleep.",
                "The new library is amazing! Great place for studying.",
                "The food today was absolutely terrible. I found a hair in my soup."
        };

        for (int i = 0; i < titles.length; i++) {
            com.example.campusCircle.model.Post post = new com.example.campusCircle.model.Post();
            post.setTitle(titles[i]);
            post.setChannelId(savedChannel.getId());
            post.setAuthorUsername("demo_user");
            post.setCommentCount(0);
            post.setUpvoteCount(5);
            post.setCreatedAt(java.time.LocalDateTime.now());

            com.example.campusCircle.model.Post savedPost = postRepository.save(post);

            com.example.campusCircle.model.nosql.PostContent content = new com.example.campusCircle.model.nosql.PostContent();
            content.setPostId(String.valueOf(savedPost.getId()));
            content.setBodyText(bodies[i]);
            postContentService.createPostContent(content);
        }

        return ResponseEntity.ok("Demo data generated for University 1 in channel: " + savedChannel.getName());
    }

    /**
     * Health check for the moderation service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        boolean isApiActive = huggingFaceApiKey != null && !huggingFaceApiKey.trim().isEmpty();

        status.put("status", "operational");
        status.put("apiConfigured", isApiActive);
        status.put("detectionMethod", isApiActive ? "HUGGINGFACE_API" : "PATTERN_MATCHING");
        status.put("message", isApiActive ? "AI Core Active - Using Hugging Face"
                : "Using fallback pattern matching (No API key found)");

        return ResponseEntity.ok(status);
    }
}
