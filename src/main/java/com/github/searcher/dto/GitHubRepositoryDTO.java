package com.github.searcher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GitHubRepositoryDTO {
    private Long id;
    private String name;
    private String description;

    @JsonProperty("owner")
    private OwnerDTO owner;

    private String language;

    @JsonProperty("stargazers_count")
    private int stars;

    @JsonProperty("forks_count")
    private int forks;

    @JsonProperty("updated_at")
    private LocalDateTime lastUpdated;
}
