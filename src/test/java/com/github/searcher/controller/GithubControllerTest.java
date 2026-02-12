package com.github.searcher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.searcher.dto.SearchRequest;
import com.github.searcher.entity.RepositoryEntity;
import com.github.searcher.service.GithubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubController.class)
public class GithubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GithubService githubService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSearchRepositories() throws Exception {
        SearchRequest request = new SearchRequest();
        request.setQuery("spring");
        request.setLanguage("java");
        request.setSort("stars");

        List<RepositoryEntity> mockResponse = Collections.singletonList(
                RepositoryEntity.builder().name("spring-boot").build());

        when(githubService.searchAndSaveRepositories(any(), any(), any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/github/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.repositories[0].name").value("spring-boot"));
    }

    @Test
    public void testGetRepositories() throws Exception {
        List<RepositoryEntity> mockResponse = Collections.singletonList(
                RepositoryEntity.builder().name("spring-boot").build());

        when(githubService.getStoredRepositories(any(), any(), any())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/github/repositories")
                .param("language", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repositories[0].name").value("spring-boot"));
    }
}
