package org.codeforamerica.shiba.pages.enrichment.smartystreets;

import org.codeforamerica.shiba.pages.enrichment.Address;
import org.codeforamerica.shiba.pages.enrichment.LocationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SmartyStreetClient implements LocationClient {
    private final String authId;
    private final String authToken;
    private final String smartyStreetUrl;

    public SmartyStreetClient(
            @Value("${smarty-street-auth-id}") String authId,
            @Value("${smarty-street-auth-token}") String authToken,
            @Value("${smarty-street-url}") String smartyStreetUrl) {
        this.authId = authId;
        this.authToken = authToken;
        this.smartyStreetUrl = smartyStreetUrl;
    }

    @Override
    public Optional<Address> validateAddress(Address address) {
        WebClient webClient = WebClient.builder().baseUrl(smartyStreetUrl).build();
        Optional<SmartyStreetVerifyStreetResponse> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("auth-id", authId)
                        .queryParam("auth-token", authToken)
                        .queryParam("street", address.getStreet())
                        .queryParam("city", address.getCity())
                        .queryParam("state", address.getState())
                        .queryParam("zipcode", address.getZipcode())
                        .queryParam("secondary", address.getApartmentNumber())
                        .queryParam("candidates", 1).build())
                .retrieve()
                .bodyToMono(SmartyStreetVerifyStreetResponse.class)
                .onErrorResume(error -> Mono.empty())
                .blockOptional();
        return response
                .flatMap(verifyStreetResponse -> verifyStreetResponse.stream().findFirst())
                .map(addressCandidate -> {
                    Components components = addressCandidate.getComponents();
                    return new Address(
                            addressCandidate.getDeliveryLine1(),
                            components.getCityName(),
                            components.getStateAbbreviation(),
                            components.getZipcode() + "-" + components.getPlus4Code(),
                            Stream.of(components.getSecondaryDesignator(), components.getSecondaryNumber())
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.joining(" ")),
                            addressCandidate.getMetadata().getCountyName());
                });
    }
}
