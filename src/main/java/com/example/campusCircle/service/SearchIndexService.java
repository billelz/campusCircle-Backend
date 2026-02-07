package com.example.campusCircle.service;

import com.example.campusCircle.model.nosql.SearchIndex;
import com.example.campusCircle.repository.SearchIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SearchIndexService {

    @Autowired
    private SearchIndexRepository searchIndexRepository;

    public List<SearchIndex> getAllSearchIndices() {
        return searchIndexRepository.findAll();
    }

    public Optional<SearchIndex> getSearchIndexById(String id) {
        return searchIndexRepository.findById(id);
    }

    public Optional<SearchIndex> getSearchIndexByContentId(String contentId) {
        return searchIndexRepository.findByContentId(contentId);
    }

    public List<SearchIndex> getSearchIndicesByContentType(String contentType) {
        return searchIndexRepository.findByContentType(contentType);
    }

    public List<SearchIndex> getSearchIndicesByAuthor(String authorUsername) {
        return searchIndexRepository.findByAuthorUsername(authorUsername);
    }

    public List<SearchIndex> getSearchIndicesByChannel(String channel) {
        return searchIndexRepository.findByChannel(channel);
    }

    public List<SearchIndex> searchByKeyword(String keyword) {
        return searchIndexRepository.findByKeywordsContaining(keyword);
    }

    public List<SearchIndex> searchByText(String text) {
        return searchIndexRepository.findByIndexedTextContaining(text);
    }

    public List<SearchIndex> getSearchIndicesCreatedAfter(LocalDateTime date) {
        return searchIndexRepository.findByCreatedAtAfter(date);
    }

    public SearchIndex saveSearchIndex(SearchIndex searchIndex) {
        if (searchIndex.getCreatedAt() == null) {
            searchIndex.setCreatedAt(LocalDateTime.now());
        }
        searchIndex.setUpdatedAt(LocalDateTime.now());
        return searchIndexRepository.save(searchIndex);
    }

    public SearchIndex updateSearchIndex(String id, SearchIndex searchIndex) {
        searchIndex.setId(id);
        searchIndex.setUpdatedAt(LocalDateTime.now());
        return searchIndexRepository.save(searchIndex);
    }

    public void deleteSearchIndex(String id) {
        searchIndexRepository.deleteById(id);
    }

    public void deleteSearchIndexByContentId(String contentId) {
        searchIndexRepository.deleteByContentId(contentId);
    }
}
