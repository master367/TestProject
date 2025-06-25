package com.company.testproject.controller;

import com.company.testproject.entity.Client;
import io.jmix.core.DataManager;
import io.jmix.core.security.SystemAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/public-api/registration")
public class ClientRegistrationController {
    private final DataManager dataManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private SystemAuthenticator systemAuthenticator;

    @Value("${spring.security.oauth2.authorizationserver.client.myclient.registration.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.authorizationserver.client.myclient.registration.client-secret}")
    private String clientSecret;

    public ClientRegistrationController(DataManager dataManager, PasswordEncoder passwordEncoder) {
        this.dataManager = dataManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        return systemAuthenticator.withSystem(() -> {
            if (dataManager.load(Client.class).query("select c from Client c where c.email = :email")
                    .parameter("email", request.email)
                    .optional()
                    .isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Client with this email already exists");
            }

            Client client = dataManager.create(Client.class);
            client.setEmail(request.email);
            client.setUsername(request.email);
            client.setPassword(passwordEncoder.encode(request.password));
            client.setActive(true);
            dataManager.save(client);

            try {
                Map<String, Object> tokenResponse = requestToken(request.email, request.password);
                return ResponseEntity.ok(tokenResponse);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registered, but failed to get token: " + e.getMessage());
            }
        });
    }

    public static class RegistrationRequest {
        public String email;
        public String password;
    }

    private Map<String, Object> requestToken(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();

        String tokenUrl = "http://localhost:8080/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String clientCredentials = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(clientCredentials.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);

        String body = "grant_type=password&username=" + username + "&password=" + password;

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, Map.class);

        return response.getBody();
    }
}