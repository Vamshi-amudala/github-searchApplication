package com.github.searcher.exception;

import com.github.searcher.controller.GithubController;
import com.github.searcher.service.GithubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubController.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GithubService githubService;

    @Test
    public void testHandleValidationException() throws Exception {
        // Empty query should trigger validation error
        String jsonRequest = "{\"query\":\"\", \"language\":\"Java\"}";

        mockMvc.perform(post("/api/github/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.query").value("Query cannot be empty"));
    }

    @Test
    public void testHandleWebClientException() throws Exception {
        when(githubService.searchAndSaveRepositories(any(), any(), any()))
                .thenThrow(new WebClientResponseException(HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, null));

        String jsonRequest = "{\"query\":\"spring\"}";

        mockMvc.perform(post("/api/github/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("External API Error: Forbidden"));
    }

    @Test
    public void testHandleGeneralException() throws Exception {
        when(githubService.searchAndSaveRepositories(any(), any(), any()))
                .thenThrow(new RuntimeException("Unexpected db error"));

        String jsonRequest = "{\"query\":\"spring\"}";

        mockMvc.perform(post("/api/github/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An unexpected error occurred: Unexpected db error"));
    }
}
