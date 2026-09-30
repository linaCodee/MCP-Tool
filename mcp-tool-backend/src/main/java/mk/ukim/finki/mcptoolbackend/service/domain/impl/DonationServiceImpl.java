package mk.ukim.finki.mcptoolbackend.service.domain.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mk.ukim.finki.mcptoolbackend.integration.vezilka.DonationReceipt;
import mk.ukim.finki.mcptoolbackend.integration.vezilka.TextDonationRequest;
import mk.ukim.finki.mcptoolbackend.integration.vezilka.VezilkaClient;
import mk.ukim.finki.mcptoolbackend.model.domain.DonationBatch;
import mk.ukim.finki.mcptoolbackend.model.domain.Resource;
import mk.ukim.finki.mcptoolbackend.model.enums.DonationStatus;
import mk.ukim.finki.mcptoolbackend.model.exception.DonationBatchNotFoundException;
import mk.ukim.finki.mcptoolbackend.model.exception.InvalidDonationStateException;
import mk.ukim.finki.mcptoolbackend.repository.DonationBatchRepository;
import mk.ukim.finki.mcptoolbackend.repository.ResourceRepository;
import mk.ukim.finki.mcptoolbackend.service.domain.DonationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TODO(student): Implement this service.
 *
 * <p>Enforce the DRAFT -> APPROVED -> SUBMITTED lifecycle (throw
 * {@code InvalidDonationStateException} on illegal transitions), and on
 * {@code submit} build a {@code TextDonationRequest} per resource and call
 * {@link VezilkaClient}. Store the returned reference on the batch.</p>
 */
@Service
@Transactional
public class DonationServiceImpl implements DonationService {
    private final DonationBatchRepository donationBatchRepository;
    private final ResourceRepository resourceRepository;
    private final VezilkaClient vezilkaClient;

    public DonationServiceImpl(DonationBatchRepository donationBatchRepository,
                               ResourceRepository resourceRepository,
                               VezilkaClient vezilkaClient) {
        this.donationBatchRepository = donationBatchRepository;
        this.resourceRepository = resourceRepository;
        this.vezilkaClient = vezilkaClient;
    }

    @Override
    public List<DonationBatch> findAll() {
        return donationBatchRepository.findAll();
    }

    @Override
    public Optional<DonationBatch> findById(Long id) {
        return donationBatchRepository.findById(id);
    }

    @Override
    public DonationBatch create(List<Long> resourceIds) {
        List<Resource> resources=resourceRepository.findAllById(resourceIds);

        DonationBatch donationBatch=new DonationBatch(DonationStatus.DRAFT);
        donationBatch=donationBatchRepository.save(donationBatch);

        for (Resource resource:resources){
            resource.setDonationBatch(donationBatch);
        }

        resourceRepository.saveAll(resources);

        donationBatch.getResources().addAll(resources);
        return donationBatchRepository.save(donationBatch);
    }

    @Override
    public DonationBatch approve(Long id) {
        DonationBatch donationBatch=donationBatchRepository.findById(id).orElseThrow(()->new DonationBatchNotFoundException(id));

        if (donationBatch.getStatus()!=DonationStatus.DRAFT){
            throw new InvalidDonationStateException(id,donationBatch.getStatus());
        }

        donationBatch.setStatus(DonationStatus.APPROVED);
        return donationBatchRepository.save(donationBatch);
    }

    @Override
    public DonationBatch submit(Long id) {
        DonationBatch donationBatch=donationBatchRepository.findById(id).orElseThrow(()->new DonationBatchNotFoundException(id));

        if (donationBatch.getStatus()!=DonationStatus.APPROVED){
            throw new InvalidDonationStateException(id,donationBatch.getStatus());
        }

        List<String> references=new ArrayList<>();

        for (Resource resource:donationBatch.getResources()){
            TextDonationRequest textDonationRequest=new TextDonationRequest(
                    resource.getTitle(),
                    resource.getContent(),
                    resource.getSourceUrl()
            );

            DonationReceipt donationReceipt=vezilkaClient.submitTextDonation(textDonationRequest);
            references.add(donationReceipt.reference());
        }

        donationBatch.setVezilkaReference(String.join(",",references));
        donationBatch.setSubmittedAt(LocalDateTime.now());
        donationBatch.setStatus(DonationStatus.SUBMITTED);

        return donationBatchRepository.save(donationBatch);
    }


    @Override
    public void refreshSubmittedStatuses() {
        List<DonationBatch> donationBatches=donationBatchRepository.findAll().stream().filter(b->b.getStatus()==DonationStatus.SUBMITTED).toList();


        for (DonationBatch donationBatch:donationBatches){
            if (donationBatch.getVezilkaReference()==null || donationBatch.getVezilkaReference().isBlank()){
                donationBatch.setStatus(DonationStatus.FAILED);
                donationBatchRepository.save(donationBatch);
                continue;
            }
            String[] references=donationBatch.getVezilkaReference().split(",");

            boolean allAccepted=true;
            boolean anyRejected=false;
            boolean anyFailed=false;

            for (String reference:references){
                DonationStatus status=vezilkaClient.checkStatus(reference);
                if (status==DonationStatus.FAILED){
                    anyFailed=true;
                }
                if (status==DonationStatus.REJECTED){
                    anyRejected=true;
                }

                if (status!=DonationStatus.ACCEPTED){
                    allAccepted=false;
                }
            }
            if (anyFailed){
                donationBatch.setStatus(DonationStatus.FAILED);
            } else if (anyRejected){
                donationBatch.setStatus(DonationStatus.REJECTED);
            } else if (allAccepted){
                donationBatch.setStatus(DonationStatus.ACCEPTED);
            } else {
                donationBatch.setStatus(DonationStatus.SUBMITTED);
            }
            donationBatchRepository.save(donationBatch);

        }
    }
}
