package tests;

import java.text.Normalizer;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import pages.HomePage;

public class HomePageTests extends BaseTest {

    private HomePage homePage;

    @BeforeMethod
    public void initializePageObject() {

        homePage = new HomePage(page);

    }

    @Test
    public void OpenHomePageTest_001() {

        Assert.assertTrue(
                page.url().contains("shopping.santabrowser.com"),
                "Home page URL is incorrect");

        Assert.assertEquals(
                page.title(),
                "Cashback");
    }

    @Test
    public void IterateCategoriesAndCheckCampaignsTest_002() {

        Assert.assertTrue(
                homePage.iterateCategoriesAndCheckCampaigns(),
                "campaigns are not available for some of the categories"
        );
  
    }

   @Test
public void VerifyCategoryFiltersTest_003() {

    String[] categories = {
            "Fashion",
            "Beauty",
            "Gaming",
            "Food & Grocery",
            "Electronics",
            "Travel",
            "Health & Fitness",
            "Home & Kitchen"
    };

    for (String category : categories) {
        homePage.openCategory(category);

        // Apply A-Z filter
        homePage.clickAZFilter();

        List<String> campaignNames = homePage.A_Z_filter_getCampaignNames();

        assertAToZSortOrder(campaignNames);

        // Apply Cashback % filter
        homePage.clickCashbackFilter();

        List<String> commissionRates = homePage.cashback_filter_getCommissionRates();

        assertCashbackDescendingOrder(commissionRates);

        homePage.closeFilterPopup();
    } 
}
@Test
public void VerifyCountryFilterCampaignsTest_004() {

    homePage.openAllCategories();

    List<String> indiaCampaigns = homePage.getCampaignNames();

    homePage.changeCountryToUnitedStates();

    List<String> usCampaigns = homePage.getCampaignNames();

    assertCountrySpecificCampaignExists(indiaCampaigns, usCampaigns);
}

@Test
public void VerifyCategoriesMenuTest_005() {

    homePage.openCategoriesMenu();

    List<String> categories = homePage.getAllCategories();

    assertCategoriesDisplayed(categories);

    homePage.clickCategory("Fashion");

    homePage.closeCategoryPopup();
}

private void assertCategoriesDisplayed(List<String> categories) {

    Assert.assertFalse(
            categories.isEmpty(),
            "Categories menu is empty.");

    System.out.println("Categories displayed : " + categories.size());

    for (String category : categories) {

        Assert.assertFalse(
                category.trim().isEmpty(),
                "One of the categories is blank.");
    }
}

private void assertCountrySpecificCampaignExists(
        List<String> indiaCampaigns,
        List<String> usCampaigns) {

    Assert.assertFalse(
            indiaCampaigns.isEmpty(),
            "India campaign list is empty.");

    Assert.assertFalse(
            usCampaigns.isEmpty(),
            "United States campaign list is empty.");

    for (String usCampaign : usCampaigns) {

        if (!indiaCampaigns.contains(usCampaign)) {

            System.out.println(
                    "US Exclusive Campaign Found : " + usCampaign+" Test Passed");

            return;
        }
    }

    Assert.fail(
            "No United States specific campaign was found. "
                    + "Both countries returned identical campaign lists.");
}

 private void assertAToZSortOrder(List<String> names) {
        Assertions.assertThat(names)
                .as("A-Z filter should return at least one campaign name")
                .isNotEmpty();

        String  previousName           = null;
        char    previousFirstChar      = 0;
        boolean previousWasLatinLetter = false;

        for (String name : names) {

            // Normalize: strip accents, remove spaces, lowercase
            String normalized = java.text.Normalizer
                    .normalize(name, java.text.Normalizer.Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "")
                    .replaceAll("\\s+", "")
                    .toLowerCase()
                    .trim();

            if (normalized.isEmpty()) {
                System.out.println("Skipping non-ASCII/Cyrillic entry: " + name);
                continue;
            }

            char firstChar = normalized.charAt(0);

            // Skip special characters (not letter or digit)
            if (!Character.isLetterOrDigit(firstChar)) {
                System.out.println("Skipping special character entry: " + name + " [" + firstChar + "]");
                continue;
            }

            // Skip entries whose original name starts with non-Latin script (Cyrillic, Arabic, etc.)
            // These appear at the end in the app and break Latin A-Z ordering
            char originalFirst = name.trim().charAt(0);
            if (!Character.isLetterOrDigit(originalFirst) || originalFirst > 127) {
                System.out.println("Skipping non-Latin script entry: " + name);
                continue;
            }

            if (previousName != null && previousWasLatinLetter) {

                System.out.println("Comparing: " + previousName + " [" + previousFirstChar + "]"
                        + " -> " + name + " [" + firstChar + "]");

                boolean previousIsDigit = Character.isDigit(previousFirstChar);
                boolean currentIsDigit  = Character.isDigit(firstChar);

                if (previousIsDigit && !currentIsDigit) {
                    System.out.println("OK: number -> letter");

                } else if (!previousIsDigit && currentIsDigit) {
                    Assertions.fail(
                        "Sort order invalid: letter before number. " +
                        "Previous: '" + previousName + "' [" + previousFirstChar + "], " +
                        "Current: '"  + name         + "' [" + firstChar + "]"
                    );

                } else {
                    Assertions.assertThat((int) firstChar)
                            .as("Not sorted A-Z. Previous: '%s' [%s], Current: '%s' [%s]",
                                    previousName, previousFirstChar, name, firstChar)
                            .isGreaterThanOrEqualTo((int) previousFirstChar);
                }
            }

            previousName           = name;
            previousFirstChar      = firstChar;
            previousWasLatinLetter = true;
        }

        System.out.println("A-Z sort validation PASSED for " + names.size() + " campaigns");
    }


private void assertCashbackDescendingOrder(List<String> rates) {

    Assert.assertFalse(
            rates.isEmpty(),
            "No cashback rates found after applying Cashback % filter.");

    Double previousRate = null;

    for (String rateText : rates) {

        String numeric = rateText.replaceAll("[^0-9.]", "").trim();

        if (numeric.isEmpty()) {
            continue;
        }

        double currentRate = Double.parseDouble(numeric);

        if (previousRate != null && currentRate > previousRate) {

            Assert.fail(
                    "Cashback rates are not sorted in descending order.\n"
                            + "Previous : " + previousRate + "%\n"
                            + "Current  : " + currentRate + "%");
        }

        previousRate = currentRate;
    }
}











}