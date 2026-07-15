package api.tests;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import api.base.BaseAPITest;
import api.clients.StoreClient;
import api.models.*;
import io.restassured.response.Response;

public class StoreApiTest extends BaseAPITest {

    @Test
    public void testGetStoreDetails() {
        StoreClient storeClient = new StoreClient();

        // ---- 1. Response-level checks ----
        Response response = storeClient.getStoreDetails();

        Assert.assertEquals(response.getStatusCode(), 200,
                "Expected 200 OK. Body: " + response.getBody().asString());

        Integer success = response.jsonPath().getInt("success");
        Assert.assertEquals(success, Integer.valueOf(1),
                "API did not report success=1. Body: " + response.getBody().asString());

        // ---- 2. Deserialize + list-level checks ----
        List<Store> stores = storeClient.getStores();
        System.out.println("Total stores retrieved: " + stores.size());

        Assert.assertNotNull(stores, "Store list was null - response shape may have changed.");
        Assert.assertFalse(stores.isEmpty(), "Store list was empty.");

        Integer total = response.jsonPath().getInt("data.total");
        if (total != null) {
            Assert.assertEquals(stores.size(), total.intValue(),
                    "Deserialized store count does not match data.total from the response.");
        }

        // ---- 3. Per-store field validation (soft asserts so one bad record doesn't hide the rest) ----
        SoftAssert softAssert = new SoftAssert();
        int missingLogoCount = 0;
        int missingDomainCount = 0;
        Set<Integer> seenIds = new HashSet<>();
        Set<Integer> duplicateIds = new TreeSet<>();

        for (Store store : stores) {
            String label = "Store[id=" + store.getId() + ", name=" + store.getName() + "] ";

            softAssert.assertNotNull(store.getId(), label + "id is null");
            if (store.getId() != null) {
                softAssert.assertTrue(store.getId() > 0, label + "id should be positive");
                if (!seenIds.add(store.getId())) {
                    duplicateIds.add(store.getId());
                }
            }

            softAssert.assertTrue(isNotBlank(store.getName()), label + "name is missing/blank");
            softAssert.assertTrue(isNotBlank(store.getSlug()), label + "slug is missing/blank");

            // Known to be occasionally missing in the source data - log only, don't fail the test.
            if (!isNotBlank(store.getLogo())) {
                missingLogoCount++;
                System.out.println("[WARN] " + label + "logo is missing/blank");
            }
            if (!isNotBlank(store.getDomainName())) {
                missingDomainCount++;
                System.out.println("[WARN] " + label + "domain_name is missing/blank");
            }

            softAssert.assertTrue(isNotBlank(store.getHomepage()), label + "homepage is missing/blank");
            if (isNotBlank(store.getHomepage())) {
                softAssert.assertTrue(
                        store.getHomepage().startsWith("http://") || store.getHomepage().startsWith("https://"),
                        label + "homepage is not a valid URL: " + store.getHomepage());
            }

            if (store.getCashbackEnabled() != null && store.getCashbackEnabled() == 1) {
                softAssert.assertTrue(isNotBlank(store.getCashbackAmount()),
                        label + "cashback_enabled=1 but cashback_amount is blank");
            }

            // Mandatory fields regardless of cashback_enabled
            softAssert.assertTrue(isNotBlank(store.getCashbackType()), label + "cashback_type is missing/blank");
            softAssert.assertTrue(isNotBlank(store.getAmountType()), label + "amount_type is missing/blank");
            softAssert.assertTrue(isNotBlank(store.getRateType()), label + "rate_type is missing/blank");
            softAssert.assertNotNull(store.getIsClaimable(), label + "is_claimable is missing (null)");
            softAssert.assertTrue(isNotBlank(store.getCashbackString()), label + "cashback_string is missing/blank");

            if (store.getVisits() != null) {
                softAssert.assertTrue(store.getVisits() >= 0, label + "visits cannot be negative");
            }
            if (store.getClicks() != null) {
                softAssert.assertTrue(store.getClicks() >= 0, label + "clicks cannot be negative");
            }
            if (store.getOffersCount() != null) {
                softAssert.assertTrue(store.getOffersCount() >= 0, label + "offers_count cannot be negative");
            }

            softAssert.assertTrue(store.getCountries() != null && !store.getCountries().isEmpty(),
                    label + "countries list is missing/empty");
        }

        System.out.println("[SUMMARY] " + missingLogoCount + " store(s) missing logo, "
                + missingDomainCount + " store(s) missing domain_name (non-blocking).");

        if (!duplicateIds.isEmpty()) {
            softAssert.fail("Duplicate store id(s) found: " + duplicateIds);
        }

        softAssert.assertAll();
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}