package com.github.searcher.service;

import com.github.searcher.entity.RepositoryEntity;
import com.github.searcher.repository.GithubRepoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GithubServiceTest {

    @Mock
    private GithubRepoRepository repository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private GithubServiceImpl githubService;

    // Skipping WebClient mocking for deep chains in simple unit test as it's
    // brittle.
    // Focusing on Repository interaction for getStoredRepositories.
    // Ideally, we'd use WireMock for WebClient or abstract the client.

    @Test
    public void testGetStoredRepositories_Filters() {
        // Given
        String language = "Java";
        Integer minStars = 100;
        List<RepositoryEntity> mockEntities = Collections.singletonList(new RepositoryEntity());

        when(repository.findByLanguageAndStarsGreaterThanEqual(eq(language), eq(minStars), any(Sort.class)))
                .thenReturn(mockEntities);

        // When
        List<RepositoryEntity> result = githubService.getStoredRepositories(language, minStars, "stars");

        // Then
        assertEquals(1, result.size());
        verify(repository, times(1)).findByLanguageAndStarsGreaterThanEqual(eq(language), eq(minStars),
                any(Sort.class));
    }

    @Test
    public void testGetStoredRepositories_NoFilters() {
        // Given
        List<RepositoryEntity> mockEntities = Collections.singletonList(new RepositoryEntity());
        when(repository.findAll(any(Sort.class))).thenReturn(mockEntities);

        // When
        List<RepositoryEntity> result = githubService.getStoredRepositories(null, null, null);

        // Then
        assertEquals(1, result.size());
        verify(repository, times(1)).findAll(any(Sort.class));
    }
}
