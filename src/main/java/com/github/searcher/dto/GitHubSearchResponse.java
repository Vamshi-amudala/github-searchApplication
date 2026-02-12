package com.github.searcher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class GitHubSearchResponse {
    @JsonProperty("total_count")
    private int totalCount;

    @JsonProperty("items")
    private List<GitHubRepositoryDTO> items;
}
