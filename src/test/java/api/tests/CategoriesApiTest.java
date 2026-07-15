package api.tests;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import api.base.BaseAPITest;
import api.clients.CategoryClient;
import api.models.*;
import io.restassured.response.Response;

public class CategoriesApiTest extends BaseAPITest {

    @Test
    public void testGetCategories() {
        CategoryClient categoryClient = new CategoryClient();

        // ---- 1. Response-level checks ----
        Response response = categoryClient.getCategoryDetails();

        Assert.assertEquals(response.getStatusCode(), 200,
                "Expected 200 OK. Body: " + response.getBody().asString());

        Integer success = response.jsonPath().getInt("success");
        Assert.assertEquals(success, Integer.valueOf(1),
                "API did not report success=1. Body: " + response.getBody().asString());

        // ---- 2. Deserialize + list-level checks ----
        List<Category> categories = categoryClient.getCategories();
        System.out.println("Total categories retrieved: " + categories.size());

        Assert.assertNotNull(categories, "Category list was null - response shape may have changed.");
        Assert.assertFalse(categories.isEmpty(), "Category list was empty.");

      
        SoftAssert softAssert = new SoftAssert();
        Set<Integer> seenCategoryIds = new HashSet<>();
        Set<Integer> duplicateCategoryIds = new TreeSet<>();
        int missingIconCount = 0;
       

        for (Category category : categories) {
            String catLabel = "Category[id=" + category.getId() + ", name=" + category.getName() + "] ";

            softAssert.assertNotNull(category.getId(), catLabel + "id is null");
            if (category.getId() != null) {
                softAssert.assertTrue(category.getId() > 0, catLabel + "id should be positive");
                if (!seenCategoryIds.add(category.getId())) {
                    duplicateCategoryIds.add(category.getId());
                }
            }

            softAssert.assertTrue(isNotBlank(category.getName()), catLabel + "name is missing/blank");

            if (!isNotBlank(category.getIcon())) {
                missingIconCount++;
                System.out.println("[WARN] " + catLabel + "icon is missing/blank");
            }

        }

        System.out.println("[SUMMARY] " + missingIconCount + " categor(y/ies) missing icon (non-blocking), "
                 + "  checked across " + categories.size() + " categories.");

        if (!duplicateCategoryIds.isEmpty()) {
            softAssert.fail("Duplicate category id(s) found: " + duplicateCategoryIds);
        }

        softAssert.assertAll();
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}