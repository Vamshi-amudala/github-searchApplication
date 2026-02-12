package com.github.searcher.service;

import com.github.searcher.dto.GitHubSearchResponse;
import com.github.searcher.entity.RepositoryEntity;
import com.github.searcher.repository.GithubRepoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubServiceImpl implements GithubService {

    private final GithubRepoRepository repository;
    private final WebClient webClient;

    @Value("${github.api.url}")
    private String githubApiUrl;

    @Override
    @Transactional
    public List<RepositoryEntity> searchAndSaveRepositories(String query, String language, String sort) {
        StringBuilder qBuilder = new StringBuilder(query);
        if (language != null && !language.isEmpty()) {
            qBuilder.append(" language:").append(language);
        }
        String finalQuery = qBuilder.toString();

        log.info("Fetching repositories from GitHub with query: {}", finalQuery);
        GitHubSearchResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/repositories")
                        .queryParam("q", finalQuery)
                        .queryParam("sort", sort)
                        .build())
                .retrieve()
                .bodyToMono(GitHubSearchResponse.class)
                .block();

        if (response == null || response.getItems() == null) {
            return new ArrayList<>();
        }

        // Map DTO to Entity
        List<RepositoryEntity> entities = response.getItems().stream().map(dto -> {
            return RepositoryEntity.builder()
                    .id(dto.getId())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .ownerName(dto.getOwner().getLogin())
                    .language(dto.getLanguage())
                    .stars(dto.getStars())
                    .forks(dto.getForks())
                    .lastUpdated(dto.getLastUpdated())
                    .build();
        }).collect(Collectors.toList());

        return repository.saveAll(entities);
    }

    @Override
    public List<RepositoryEntity> getStoredRepositories(String language, Integer minStars, String sortParam) {
        Sort sort = Sort.by(Sort.Direction.DESC, "stars");
        if (sortParam != null) {
            if ("forks".equalsIgnoreCase(sortParam)) {
                sort = Sort.by(Sort.Direction.DESC, "forks");
            } else if ("updated".equalsIgnoreCase(sortParam)) {
                sort = Sort.by(Sort.Direction.DESC, "lastUpdated");
            }
        }

        if (language != null && minStars != null) {
            return repository.findByLanguageAndStarsGreaterThanEqual(language, minStars, sort);
        } else if (language != null) {
            return repository.findByLanguage(language, sort);
        } else if (minStars != null) {
            return repository.findByStarsGreaterThanEqual(minStars, sort);
        } else {
            return repository.findAll(sort);
        }
    }
}
