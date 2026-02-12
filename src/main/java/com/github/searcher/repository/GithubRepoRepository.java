package com.github.searcher.repository;

import com.github.searcher.entity.RepositoryEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GithubRepoRepository extends JpaRepository<RepositoryEntity, Long> {

    List<RepositoryEntity> findByLanguage(String language, Sort sort);

    List<RepositoryEntity> findByStarsGreaterThanEqual(int minStars, Sort sort);

    List<RepositoryEntity> findByLanguageAndStarsGreaterThanEqual(String language, int minStars, Sort sort);
}
