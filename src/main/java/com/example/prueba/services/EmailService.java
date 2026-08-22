package com.example.prueba.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.remitente.email:arceflyy@gmail.com}")
    private String remitenteEmail;

    @Value("${brevo.remitente.nombre:Arcefly}")
    private String remitenteNombre;

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestTemplate restTemplate = new RestTemplate();

    public void enviarEmailDeIncidente(String destinatario, String asunto, String mensaje) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", brevoApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> cuerpo = Map.of(
                "sender", Map.of("name", remitenteNombre, "email", remitenteEmail),
                "to", List.of(Map.of("email", destinatario)),
                "subject", asunto,
                "textContent", mensaje
        );

        HttpEntity<Map<String, Object>> peticion = new HttpEntity<>(cuerpo, headers);

        var respuesta = restTemplate.postForEntity(BREVO_URL, peticion, String.class);

        if (respuesta.getStatusCode() != HttpStatus.CREATED) {
            throw new RuntimeException("Brevo respondió con estado: " + respuesta.getStatusCode()
                    + " - " + respuesta.getBody());
        }
    }
}