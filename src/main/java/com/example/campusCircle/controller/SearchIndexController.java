package com.example.campusCircle.controller;

import com.example.campusCircle.model.nosql.SearchIndex;
import com.example.campusCircle.service.SearchIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search-index")
@CrossOrigin(origins = "*")
public class SearchIndexController {

    @Autowired
    private SearchIndexService searchIndexService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<SearchIndex> indices = searchIndexService.getAllSearchIndices();
            response.put("status", "success");
            response.put("message", "MongoDB SearchIndex collection is working!");
            response.put("totalIndices", indices.size());
            response.put("data", indices);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to connect to MongoDB: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<SearchIndex>> getAllSearchIndices() {
        return ResponseEntity.ok(searchIndexService.getAllSearchIndices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SearchIndex> getSearchIndexById(@PathVariable String id) {
        return searchIndexService.getSearchIndexById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/content/{contentId}")
    public ResponseEntity<SearchIndex> getSearchIndexByContentId(@PathVariable String contentId) {
        return searchIndexService.getSearchIndexByContentId(contentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{contentType}")
    public ResponseEntity<List<SearchIndex>> getSearchIndicesByContentType(@PathVariable String contentType) {
        return ResponseEntity.ok(searchIndexService.getSearchIndicesByContentType(contentType));
    }

    @GetMapping("/author/{authorUsername}")
    public ResponseEntity<List<SearchIndex>> getSearchIndicesByAuthor(@PathVariable String authorUsername) {
        return ResponseEntity.ok(searchIndexService.getSearchIndicesByAuthor(authorUsername));
    }

    @GetMapping("/channel/{channel}")
    public ResponseEntity<List<SearchIndex>> getSearchIndicesByChannel(@PathVariable String channel) {
        return ResponseEntity.ok(searchIndexService.getSearchIndicesByChannel(channel));
    }

    @GetMapping("/search/keyword")
    public ResponseEntity<List<SearchIndex>> searchByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(searchIndexService.searchByKeyword(keyword));
    }

    @GetMapping("/search/text")
    public ResponseEntity<List<SearchIndex>> searchByText(@RequestParam String text) {
        return ResponseEntity.ok(searchIndexService.searchByText(text));
    }

    @GetMapping("/created-after")
    public ResponseEntity<List<SearchIndex>> getSearchIndicesCreatedAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(searchIndexService.getSearchIndicesCreatedAfter(date));
    }

    @PostMapping
    public ResponseEntity<SearchIndex> createSearchIndex(@RequestBody SearchIndex searchIndex) {
        SearchIndex savedIndex = searchIndexService.saveSearchIndex(searchIndex);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedIndex);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SearchIndex> updateSearchIndex(
            @PathVariable String id,
            @RequestBody SearchIndex searchIndex) {
        SearchIndex updatedIndex = searchIndexService.updateSearchIndex(id, searchIndex);
        return ResponseEntity.ok(updatedIndex);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSearchIndex(@PathVariable String id) {
        searchIndexService.deleteSearchIndex(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/content/{contentId}")
    public ResponseEntity<Void> deleteSearchIndexByContentId(@PathVariable String contentId) {
        searchIndexService.deleteSearchIndexByContentId(contentId);
        return ResponseEntity.noContent().build();
    }
}
