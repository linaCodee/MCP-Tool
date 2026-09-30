package mk.ukim.finki.mcptoolbackend.integration.vezilka;

import mk.ukim.finki.mcptoolbackend.model.enums.DonationStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Placeholder so the application boots before the assignment is implemented.
 * TODO(student): Replace this bean with a real doniraj.vezilka.ai client.
 */
@Component
public class StubVezilkaClient implements VezilkaClient {
    private final VezilkaProperties vezilkaProperties;
    private final RestClient restClient;

    private record VezilkaTextItem(String source_url,String page_title,String text){}
    private record VezilkaTextDonationRequest(List<VezilkaTextItem> items){}
    private record VezilkaTextDonationResult(String id,String status,String contentType,Boolean deduped,Integer awardedPoints,String rejectionReason){}
    private record VezilkaTextDonationResponse(List<VezilkaTextDonationResult> results,Integer total,Integer accepted,Integer rejected,Integer duplicates){}
    private record VezilkaTextDonationStatusResponse(String id,String status,String contentType,Boolean deduped,Integer awardedPoints,String rejectionReason){}

    public StubVezilkaClient(VezilkaProperties vezilkaProperties) {
        this.vezilkaProperties = vezilkaProperties;
        this.restClient = RestClient.builder().baseUrl(vezilkaProperties.baseUrl()).build();
    }

    @Override
    public DonationReceipt submitTextDonation(TextDonationRequest request) {
        VezilkaTextDonationRequest body=new VezilkaTextDonationRequest(List.of(new VezilkaTextItem(request.sourceUrl(),request.title(),request.content())));
        VezilkaTextDonationResponse response=restClient
                .post()
                .uri("/api/public/v1/donations/text/")
                .header("X-Donation-Api-Key",vezilkaProperties.apiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(VezilkaTextDonationResponse.class);
        System.out.println("Vezilka post response: "+response);

        if (response==null || response.results()==null || response.results().isEmpty()){
            throw new RuntimeException("Empty response from Vezilka");
        }

        VezilkaTextDonationResult result=response.results().getFirst();


        return new DonationReceipt(result.id(),result.status());

    }

    @Override
    public DonationStatus checkStatus(String vezilkaReference) {
        try {
            VezilkaTextDonationStatusResponse response=restClient
                    .get()
                    .uri("/api/public/v1/donations/{id}/",vezilkaReference)
                    .header("X-Donation-Api-Key",vezilkaProperties.apiKey())
                    .retrieve()
                    .body(VezilkaTextDonationStatusResponse.class);

            if (response==null || response.status()==null){
                return DonationStatus.FAILED;
            }
            return switch (response.status().toLowerCase()){
                case "rejected"->DonationStatus.REJECTED;
                case "accepted"->DonationStatus.ACCEPTED;
                default -> DonationStatus.SUBMITTED;
            };
        } catch (Exception err){
            return DonationStatus.FAILED;
        }
    }
}
