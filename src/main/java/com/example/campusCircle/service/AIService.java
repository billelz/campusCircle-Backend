package com.example.campusCircle.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Advanced AI Service for content moderation using Hugging Face's free
 * inference API.
 * Falls back to sophisticated pattern matching when API is unavailable.
 */
@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    @Value("${ai.moderation.huggingface.api-key:}")
    private String huggingFaceApiKey;

    @Value("${ai.moderation.huggingface.model:unitary/toxic-bert}")
    private String modelName;

    @Value("${ai.moderation.huggingface.api-url:https://api-inference.huggingface.co/models}")
    private String apiUrl;

    @Value("${ai.moderation.toxicity-threshold:0.7}")
    private double toxicityThreshold;

    @Value("${ai.moderation.use-fallback:true}")
    private boolean useFallback;

    @Value("${ai.moderation.timeout-ms:5000}")
    private int timeoutMs;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Enhanced toxicity patterns with word boundaries for better accuracy
    private static final List<Pattern> TOXIC_PATTERNS = Arrays.asList(
            // Offensive language
            Pattern.compile("\\b(fuck|shit|bitch|asshole|bastard|damn|crap)\\b", Pattern.CASE_INSENSITIVE),
            // Hate speech
            Pattern.compile("\\b(hate|hatred|despise)\\s+(you|them|everyone|people)\\b", Pattern.CASE_INSENSITIVE),
            // Threats
            Pattern.compile("\\b(kill|murder|hurt|harm|destroy|attack)\\s+(you|him|her|them)\\b",
                    Pattern.CASE_INSENSITIVE),
            // Slurs and discriminatory language
            Pattern.compile("\\b(stupid|idiot|moron|retard|dumb|loser)\\s+(people|person|you)\\b",
                    Pattern.CASE_INSENSITIVE),
            // Sexual harassment
            Pattern.compile("\\b(slut|whore|pervert|creep)\\b", Pattern.CASE_INSENSITIVE));

    private static final List<Pattern> CRISIS_PATTERNS = Arrays.asList(
            Pattern.compile("\\b(want to|going to|gonna)\\s+(kill|end)\\s+(myself|my life)\\b",
                    Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(suicide|suicidal|end it all|ending it)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(can't go on|no reason to live|better off dead)\\b", Pattern.CASE_INSENSITIVE));

    private static final List<Pattern> SPAM_PATTERNS = Arrays.asList(
            Pattern.compile("\\b(buy now|click here|limited time|act now|free money)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(https?://[^\\s]+){3,}", Pattern.CASE_INSENSITIVE), // Multiple URLs
            Pattern.compile("\\b(cash|prize|winner|congratulations)\\s+(now|today|immediately)\\b",
                    Pattern.CASE_INSENSITIVE));

    public AIService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Main content analysis method - uses Hugging Face API with fallback
     */
    public Map<String, Object> analyzeContent(String text) {
        if (text == null || text.trim().isEmpty()) {
            return createEmptyResult();
        }

        Map<String, Object> results = new HashMap<>();
        double toxicityScore = 0.0;
        boolean usedApi = false;

        // Try Hugging Face API first
        if (huggingFaceApiKey != null && !huggingFaceApiKey.isEmpty()) {
            try {
                toxicityScore = analyzeToxicityWithHuggingFace(text);
                usedApi = true;
                logger.info("Successfully analyzed content with Hugging Face API");
            } catch (Exception e) {
                logger.warn("Hugging Face API failed, using fallback: {}", e.getMessage());
                if (useFallback) {
                    toxicityScore = analyzeToxicityWithFallback(text);
                }
            }
        } else {
            // No API key, use fallback
            logger.info("No Hugging Face API key configured, using fallback detection");
            toxicityScore = analyzeToxicityWithFallback(text);
        }

        List<String> flags = detectFlags(text, toxicityScore);

        results.put("score", toxicityScore);
        results.put("flags", flags);
        results.put("isToxic", toxicityScore > toxicityThreshold);
        results.put("isCrisis", detectCrisis(text));
        results.put("isSpam", detectSpam(text));
        results.put("sentiment", analyzeSentiment(toxicityScore));
        results.put("detectionMethod", usedApi ? "HUGGINGFACE_API" : "PATTERN_MATCHING");

        return results;
    }

    /**
     * Analyze toxicity using OpenAI-compatible Chat Completions API (Hugging Face
     * Router)
     */
    private double analyzeToxicityWithHuggingFace(String text) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(huggingFaceApiKey);

        // OpenAI-compatible Chat Message format
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", "Is the following text toxic, hateful, or offensive? Answer only 'Yes' or 'No'. Text: \""
                + text + "\"");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);
        requestBody.put("messages", List.of(message));
        requestBody.put("temperature", 0.0); // Predictable output

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseToxicityFromResponse(response.getBody());
            }
        } catch (Exception e) {
            logger.warn("AI API call failed: {}. Falling back.", e.getMessage());
            throw e;
        }
        return 0.0;
    }

    /**
     * Parse toxicity from OpenAI-compatible response format
     */
    private double parseToxicityFromResponse(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // Check for OpenAI choices[0].message.content
            if (jsonNode.has("choices") && jsonNode.get("choices").isArray() && jsonNode.get("choices").size() > 0) {
                JsonNode firstChoice = jsonNode.get("choices").get(0);
                if (firstChoice.has("message") && firstChoice.get("message").has("content")) {
                    String content = firstChoice.get("message").get("content").asText().toLowerCase();

                    if (content.contains("yes")) {
                        return 0.95; // High toxicity
                    } else if (content.contains("no")) {
                        return 0.05; // Clean
                    }
                }
            }

            // Fallback for legacy BERT/Classification scores if the router returns them
            if (jsonNode.isArray() && jsonNode.size() > 0) {
                JsonNode first = jsonNode.get(0);
                if (first.has("score"))
                    return first.get("score").asDouble();
            }

        } catch (Exception e) {
            logger.error("Error parsing AI response: {}", e.getMessage());
        }
        return 0.0;
    }

    /**
     * Enhanced standalone toxicity detection using pattern matching
     * This is production-ready and doesn't require any external API
     */
    private double analyzeToxicityWithFallback(String text) {
        if (text == null || text.isEmpty()) {
            return 0.0;
        }

        double score = 0.0;
        String lowerText = text.toLowerCase();
        int toxicPatternMatches = 0;

        // Check toxic patterns (each match adds to score)
        for (Pattern pattern : TOXIC_PATTERNS) {
            if (pattern.matcher(text).find()) {
                toxicPatternMatches++;
                score += 0.30; // Increased weight for pattern matches
            }
        }

        // Multiple pattern matches indicate severe toxicity
        if (toxicPatternMatches > 2) {
            score += 0.2; // Bonus for multiple violations
        }

        // Check for ALL CAPS (aggression indicator) - must be significant text
        long upperCaseCount = text.chars().filter(Character::isUpperCase).count();
        long letterCount = text.chars().filter(Character::isLetter).count();
        if (letterCount > 10 && upperCaseCount > letterCount * 0.7) {
            score += 0.15;
        }

        // Check for excessive punctuation (aggression indicators)
        long exclamationCount = text.chars().filter(ch -> ch == '!').count();
        long questionCount = text.chars().filter(ch -> ch == '?').count();
        if (exclamationCount > 3 || questionCount > 5) {
            score += 0.1;
        }

        // Repeated punctuation (!!!!, ????)
        if (text.matches(".*[!?]{3,}.*")) {
            score += 0.1;
        }

        // Check for personal attacks (very specific patterns)
        if (lowerText.matches(".*\\byou\\s+(are|r|re)\\s+(stupid|dumb|idiot|moron|trash|garbage).*")) {
            score += 0.35;
        }

        // Check for threats directed at someone
        if (lowerText.matches(".*\\b(gonna|going to|will)\\s+(kill|hurt|destroy|beat)\\s+(you|u).*")) {
            score += 0.4;
        }

        // Check for hate speech patterns
        if (lowerText.matches(".*\\b(i\\s+)?(hate|despise|detest)\\s+(you|everyone|people|them).*")) {
            score += 0.3;
        }

        // Excessive negative words
        String[] negativeWords = { "terrible", "horrible", "awful", "disgusting", "pathetic", "worthless" };
        int negativeCount = 0;
        for (String word : negativeWords) {
            if (lowerText.contains(word)) {
                negativeCount++;
            }
        }
        if (negativeCount >= 3) {
            score += 0.15;
        }

        // Derogatory terms in direct address
        if (lowerText.matches(".*\\byou('re|r)\\s+(a\\s+)?(loser|failure|nobody|nothing).*")) {
            score += 0.3;
        }

        // Normalize score to 0.0-1.0 range
        return Math.min(score, 1.0);
    }

    /**
     * Detect various content flags
     */
    private List<String> detectFlags(String text, double score) {
        List<String> flags = new ArrayList<>();

        if (score > 0.9) {
            flags.add("CRITICAL_TOXICITY");
        } else if (score > 0.7) {
            flags.add("HIGH_TOXICITY");
        } else if (score > 0.5) {
            flags.add("MODERATE_TOXICITY");
        }

        if (detectCrisis(text)) {
            flags.add("CRISIS_POTENTIAL");
        }

        if (detectSpam(text)) {
            flags.add("SPAM_PROBABLE");
        }

        // Check for hate speech
        if (TOXIC_PATTERNS.stream().anyMatch(p -> p.matcher(text).find())) {
            flags.add("OFFENSIVE_LANGUAGE");
        }

        return flags;
    }

    /**
     * Enhanced crisis detection
     */
    private boolean detectCrisis(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        return CRISIS_PATTERNS.stream()
                .anyMatch(pattern -> pattern.matcher(text).find());
    }

    /**
     * Enhanced spam detection
     */
    private boolean detectSpam(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        return SPAM_PATTERNS.stream()
                .anyMatch(pattern -> pattern.matcher(text).find());
    }

    /**
     * Analyze sentiment based on toxicity score
     */
    private String analyzeSentiment(double toxicityScore) {
        if (toxicityScore < 0.2) {
            return "POSITIVE";
        } else if (toxicityScore < 0.5) {
            return "NEUTRAL";
        } else if (toxicityScore < 0.8) {
            return "NEGATIVE";
        } else {
            return "HIGHLY_NEGATIVE";
        }
    }

    /**
     * Aggregated Sentiment Analysis for University Dashboards
     */
    public Map<String, Double> analyzeAggregatedSentiment(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return Map.of(
                    "average_toxicity", 0.0,
                    "campus_pulse_score", 1.0);
        }

        double totalScore = 0.0;
        int processedTexts = 0;

        for (String text : texts) {
            if (text != null && !text.trim().isEmpty()) {
                Map<String, Object> analysis = analyzeContent(text);
                totalScore += (double) analysis.get("score");
                processedTexts++;
            }
        }

        double avgToxicity = processedTexts > 0 ? totalScore / processedTexts : 0.0;

        Map<String, Double> metrics = new HashMap<>();
        metrics.put("average_toxicity", avgToxicity);
        metrics.put("campus_pulse_score", 1.0 - avgToxicity);
        metrics.put("analyzed_count", (double) processedTexts);

        return metrics;
    }

    /**
     * Create empty result for null/empty text
     */
    private Map<String, Object> createEmptyResult() {
        Map<String, Object> results = new HashMap<>();
        results.put("score", 0.0);
        results.put("flags", new ArrayList<>());
        results.put("isToxic", false);
        results.put("isCrisis", false);
        results.put("isSpam", false);
        results.put("sentiment", "NEUTRAL");
        results.put("detectionMethod", "NONE");
        return results;
    }
}
