package mk.ukim.finki.mcptoolbackend.service;

import mk.ukim.finki.mcptoolbackend.integration.vezilka.DonationReceipt;
import mk.ukim.finki.mcptoolbackend.integration.vezilka.TextDonationRequest;
import mk.ukim.finki.mcptoolbackend.integration.vezilka.VezilkaClient;
import mk.ukim.finki.mcptoolbackend.model.domain.DonationBatch;
import mk.ukim.finki.mcptoolbackend.model.domain.Resource;
import mk.ukim.finki.mcptoolbackend.model.domain.SearchRun;
import mk.ukim.finki.mcptoolbackend.model.enums.DonationStatus;
import mk.ukim.finki.mcptoolbackend.repository.ResourceRepository;
import mk.ukim.finki.mcptoolbackend.repository.SearchRunRepository;
import mk.ukim.finki.mcptoolbackend.service.domain.DonationService;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;



import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * TODO(student): Implement an integration test for the donation workflow
 * (create -> approve -> submit) with a stubbed or fake VezilkaClient. Remove
 * the {@code @Disabled} once implemented.
 */
@SpringBootTest
@Testcontainers
public class DonationServiceIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("mcptool_test")
        .withUsername("test")
        .withPassword("test");
    @Autowired
    private ResourceRepository resourceRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @MockitoBean
    private VezilkaClient vezilkaClient;

    @Autowired
    private DonationService donationService;
    @Autowired
    private SearchRunRepository searchRunRepository;

    @Test
    void testDonationWorkflow() {
        // TODO(student): create a batch, approve it, submit it, and assert the
        //  resulting status transitions and the stored Vezilka reference.
        SearchRun searchRun=new SearchRun("тест");
        searchRun=searchRunRepository.save(searchRun);

        Resource resource=new Resource();
        resource.setSearchRun(searchRun);
        resource.setExternalId("test-id-1");
        resource.setTitle("Тест пример");
        resource.setContent("Тест за извршување на задачата.");
        resource.setSourceUrl("https://example.com/test");
        resource=resourceRepository.save(resource);

        DonationBatch created=donationService.create(List.of(resource.getId()));
        Assert.assertEquals(DonationStatus.DRAFT,created.getStatus());

        DonationBatch approved=donationService.approve(created.getId());
        Assert.assertEquals(DonationStatus.APPROVED,approved.getStatus());

        when(vezilkaClient.submitTextDonation(any(TextDonationRequest.class))).thenReturn(new DonationReceipt("test-vezilka-reference","accepted"));

        DonationBatch submitted=donationService.submit(approved.getId());
        Assert.assertEquals(DonationStatus.SUBMITTED,submitted.getStatus());
        Assert.assertEquals("test-vezilka-reference",submitted.getVezilkaReference());
    }
}
