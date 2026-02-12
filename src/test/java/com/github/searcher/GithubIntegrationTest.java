package com.github.searcher;

import com.github.searcher.entity.RepositoryEntity;
import com.github.searcher.repository.GithubRepoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class GithubIntegrationTest {

    @Autowired
    private GithubRepoRepository repository;

    @Test
    public void testUpsertBehavior() {
        // 1. Create a repository
        RepositoryEntity repo = RepositoryEntity.builder()
                .id(1L)
                .name("test-repo")
                .stars(10)
                .lastUpdated(LocalDateTime.now())
                .build();

        repository.save(repo);

        assertEquals(1, repository.count());
        assertEquals(10, repository.findById(1L).get().getStars());

        // 2. Update same repository (Upsert)
        RepositoryEntity updatedRepo = RepositoryEntity.builder()
                .id(1L)
                .name("test-repo")
                .stars(20)
                .lastUpdated(LocalDateTime.now())
                .build();

        repository.save(updatedRepo);

        // Still should only be 1 record
        assertEquals(1, repository.count());
        assertEquals(20, repository.findById(1L).get().getStars());
    }

    @Test
    public void testFiltering() {
        repository.save(RepositoryEntity.builder().id(1L).language("Java").stars(100).build());
        repository.save(RepositoryEntity.builder().id(2L).language("Python").stars(200).build());
        repository.save(RepositoryEntity.builder().id(3L).language("Java").stars(50).build());

        List<RepositoryEntity> javaRepos = repository.findByLanguage("Java", null);
        assertEquals(2, javaRepos.size());

        List<RepositoryEntity> starsRepos = repository.findByStarsGreaterThanEqual(100, null);
        assertEquals(2, starsRepos.size());
    }
}
