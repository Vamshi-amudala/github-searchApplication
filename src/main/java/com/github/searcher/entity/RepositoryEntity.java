package com.github.searcher.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "repositories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryEntity {

    @Id
    private Long id;

    private String name;
    @jakarta.persistence.Column(columnDefinition = "TEXT")
    private String description;

    private String ownerName;
    private String language;
    private int stars;
    private int forks;
    private LocalDateTime lastUpdated;
}
