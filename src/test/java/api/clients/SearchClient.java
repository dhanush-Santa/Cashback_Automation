package api.clients;

import static io.restassured.RestAssured.given;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import api.base.BaseAPITest;
import api.models.Store;
import io.restassured.response.Response;

public class SearchClient {

    List<Store> stores;

    /**
     * Fetch all store names for a country, safely filtering out
     * null/blank names so downstream logic (random pick, longest name,
     * substring operations) never blows up on bad data.
     */
    public List<String> getStoreNames(String countryCode) {

        stores = new StoreClient().getStores(countryCode);

        if (stores == null) {
            System.out.println("Store list came back null - returning empty list.");
            return List.of();
        }

        return stores.stream()
                .map(Store::getName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .toList();
    }

    /**
     * Executes the search API safely.
     * - Skips the call entirely for null / blank / <2-char search terms.
     * - Trims the search term before sending (server-side trim assumption
     *   is not relied upon).
     * - Handles HTTP-200-but-application-error responses without throwing
     *   a JSON parsing exception.
     * - Duplicate store names in the response are perfectly fine; this
     *   method returns them as-is (callers decide how to validate counts).
     */
    public List<String> searchStoreNames(String countryCode, String searchTerm) {

        if (searchTerm == null || searchTerm.trim().length() < 2) {
            System.out.println(
                    "Search skipped. Minimum 2 non-space characters required. Received: ["
                            + searchTerm + "]"
            );
            return List.of();
        }

        String trimmedTerm = searchTerm.trim();
        String encodedTerm = URLEncoder.encode(trimmedTerm, StandardCharsets.UTF_8);

        Response response;
        try {
            response = given()
                    .log()
                    .all()
                    .header("Authorization", BaseAPITest.getToken())
                    .header("Content-Type", "application/json")
                    .header("country-code", countryCode)
                    .when()
                    .get("/data/stores?search=" + encodedTerm + "&catId=0");
        } catch (Exception e) {
            System.out.println("Search API call failed with exception: " + e.getMessage());
            return List.of();
        }

        System.out.println(response.asPrettyString());

        int statusCode = response.getStatusCode();
        if (statusCode != 200) {
            System.out.println("Search API returned non-200 status: " + statusCode
                    + " for term: " + trimmedTerm);
            return List.of();
        }

        Integer error;
        try {
            error = response.jsonPath().getInt("error");
        } catch (Exception e) {
            // "error" field missing/non-numeric - treat as no application error
            error = null;
        }

        if (error != null && error == 1) {

            String message = null;
            try {
                message = response.jsonPath().getString("data.message");
            } catch (Exception e) {
                message = "Unavailable - could not parse error message";
            }

            System.out.println("Search API Error : " + message);

            // Return empty list when API reports an application-level error
            return List.of();
        }

        List<String> searchResults;
        try {
            searchResults = response.jsonPath().getList("data.data.name", String.class);
        } catch (Exception e) {
            System.out.println("Failed to parse search results: " + e.getMessage());
            return List.of();
        }

        if (searchResults == null) {
            return List.of();
        }

        // Duplicate brand names in results are expected/allowed - no dedup here.
        return searchResults.stream()
                .filter(name -> name != null)
                .toList();
    }
}