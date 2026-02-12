package com.github.searcher.service;

import com.github.searcher.dto.GitHubRepositoryDTO;
import com.github.searcher.dto.GitHubSearchResponse;
import com.github.searcher.dto.OwnerDTO;
import com.github.searcher.entity.RepositoryEntity;
import com.github.searcher.repository.GithubRepoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

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

    @Test
    public void testSearchAndSaveRepositories_Mapping() {
        // Given
        String query = "spring";
        GitHubSearchResponse mockResponse = new GitHubSearchResponse();
        GitHubRepositoryDTO item = new GitHubRepositoryDTO();
        item.setId(123L);
        item.setName("spring-boot");
        OwnerDTO owner = new OwnerDTO();
        owner.setLogin("spring-projects");
        item.setOwner(owner);
        mockResponse.setItems(Collections.singletonList(item));

        // Mock WebClient chain
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(GitHubSearchResponse.class))
                .thenReturn(reactor.core.publisher.Mono.just(mockResponse));

        when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        List<RepositoryEntity> result = githubService.searchAndSaveRepositories(query, null, null);

        // Then
        assertEquals(1, result.size());
        assertEquals(123L, result.get(0).getId());
        assertEquals("spring-boot", result.get(0).getName());
        assertEquals("spring-projects", result.get(0).getOwnerName());
        verify(repository, times(1)).saveAll(anyList());
    }

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
