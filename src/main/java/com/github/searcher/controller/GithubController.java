package com.github.searcher.controller;

import com.github.searcher.dto.SearchRequest;
import com.github.searcher.entity.RepositoryEntity;
import com.github.searcher.service.GithubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GithubController {

    private final GithubService githubService;

    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> searchRepositories(@Valid @RequestBody SearchRequest request) {
        List<RepositoryEntity> repositories = githubService.searchAndSaveRepositories(
                request.getQuery(), request.getLanguage(), request.getSort());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Repositories fetched and saved successfully");
        response.put("repositories", repositories);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/repositories")
    public ResponseEntity<Map<String, Object>> getRepositories(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer minStars,
            @RequestParam(required = false) String sort) {

        List<RepositoryEntity> repositories = githubService.getStoredRepositories(language, minStars, sort);

        Map<String, Object> response = new HashMap<>();
        response.put("repositories", repositories);

        return ResponseEntity.ok(response);
    }
}
