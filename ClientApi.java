package com.example.car.services;

import com.example.car.entities.Client;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class ClientApi {
    private final WebClient.Builder builder;

    public ClientApi(WebClient.Builder builder) {
        this.builder = builder;
    }

    /**
     * Appelle service-client par son nom Eureka (résolu par LoadBalancer).
     * Renvoie null si le client n'existe pas (404), pour ne pas casser
     * toute la liste des voitures.
     */
    public Client findClientById(Long id) {
        return builder.build()
                .get()
                .uri("http://SERVICE-CLIENT/api/clients/" + id)
                .retrieve()
                .bodyToMono(Client.class)
                .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.empty())
                .block(); // bloquant : simple pour un TP
    }
}
