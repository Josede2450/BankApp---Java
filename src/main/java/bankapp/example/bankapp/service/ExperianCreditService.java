// Service class for integrating with Experian's credit reporting API (sandbox)
package bankapp.example.bankapp.service;

// ===== Spring and HTTP dependencies =====
import org.springframework.beans.factory.annotation.Value;           // For injecting values from application.properties
import org.springframework.http.*;                                  // For HTTP headers and entities
import org.springframework.stereotype.Service;                      // Marks class as a service bean
import org.springframework.web.client.RestTemplate;                // Used to make REST API calls

import java.util.Collections; // For using empty lists/maps
import java.util.Map;         // Used for dynamic JSON parsing

// Marks this class as a Spring-managed service component
@Service
public class ExperianCreditService {

    // Injects client ID from application.properties or application.yml
    @Value("${experian.client-id}")
    private String clientId;

    // Injects client secret from config file
    @Value("${experian.client-secret}")
    private String clientSecret;

    // RestTemplate is a Spring utility for making HTTP requests
    private final RestTemplate restTemplate;

    // Constructor injection of RestTemplate
    public ExperianCreditService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches an OAuth2 access token from Experian's sandbox token endpoint
     *
     * @return access token as a String
     */
    public String getAccessToken() {
        // Experian's sandbox token URL
        String tokenUrl = "https://sandbox-us-api.experian.com/oauth2/v1/token";

        // Set HTTP headers (Content-Type: application/json)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // JSON body for token request with credentials and grant type
        Map<String, String> tokenRequest = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "grant_type", "client_credentials"
        );

        // Wrap request body and headers into one entity
        HttpEntity<Map<String, String>> request = new HttpEntity<>(tokenRequest, headers);

        try {
            // Send POST request to get access token, expecting a JSON response (Map)
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            // Extract and return the access_token from response body
            return (String) response.getBody().get("access_token");
        } catch (Exception e) {
            // Throw a descriptive error if something goes wrong
            throw new RuntimeException("Failed to get access token from Experian: " + e.getMessage(), e);
        }
    }

    /**
     * Calls Experian's prequalification API to simulate fetching credit data
     * Requires a valid access token
     *
     * @return the API response body as a Map
     */
    public Map<String, Object> fetchCreditData() {
        // Get bearer token from Experian
        String token = getAccessToken();

        // API endpoint for credit decision (sandbox)
        String creditUrl = "https://sandbox-us-api.experian.com/prequalification/v2/decision";

        // Set authorization and content-type headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); // Set Authorization: Bearer <token>
        headers.setContentType(MediaType.APPLICATION_JSON);

        // JSON request body as a string (sandbox test data from Experian documentation)
        String requestJson = """
        {
          "consumerPii": {
            "firstName": "John",
            "lastName": "Doe",
            "ssn": "111223333",
            "dob": "1985-01-15",
            "address": {
              "line1": "123 Main St",
              "city": "Irvine",
              "state": "CA",
              "zipCode": "92614"
            }
          },
          "vendorNumber": "100000",
          "subscriberCode": "2222222"
        }
        """;

        // Wrap JSON and headers into one HTTP entity
        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        // Send POST request and receive the response as a Map
        ResponseEntity<Map> response = restTemplate.exchange(creditUrl, HttpMethod.POST, request, Map.class);

        // Return the JSON response body
        return response.getBody();
    }
}
