package mk.ukim.finki.mcptoolbackend.repository;

import jakarta.transaction.Transactional;
import mk.ukim.finki.mcptoolbackend.config.JpaConfig;
import mk.ukim.finki.mcptoolbackend.model.domain.SearchRun;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

/**
 * TODO(student): Implement repository tests for search runs, following the
 * working {@link UserRepositoryTest} example (real Postgres via Testcontainers,
 * migrated by Flyway). Remove the {@code @Disabled} once you add assertions.
 */
@DataJpaTest
@Import(JpaConfig.class)
@Transactional
@Testcontainers
public class SearchRunRepositoryTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("mcptool_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private SearchRunRepository searchRunRepository;

    @Test
    void testFindAllByOrderByCreatedAtDesc() {
        // TODO(student): save a few SearchRuns and assert the ordering.
        SearchRun searchRun1=new SearchRun("научници");
        SearchRun searchRun2=new SearchRun("финки");
        SearchRun searchRun3=new SearchRun("укло");

        searchRunRepository.save(searchRun1);
        searchRunRepository.save(searchRun2);
        searchRunRepository.save(searchRun3);
        List<SearchRun> result=searchRunRepository.findAllByOrderByCreatedAtDesc();

        Assert.assertEquals(3,result.size());
        Assert.assertEquals(result.get(0).getId(),searchRun3.getId());
        Assert.assertEquals(result.get(1).getId(),searchRun2.getId());
        Assert.assertEquals(result.get(2).getId(),searchRun1.getId());
    }
}
