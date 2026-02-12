package com.github.searcher.service;

import com.github.searcher.entity.RepositoryEntity;
import java.util.List;

public interface GithubService {
    List<RepositoryEntity> searchAndSaveRepositories(String query, String language, String sort);

    List<RepositoryEntity> getStoredRepositories(String language, Integer minStars, String sort);
}
