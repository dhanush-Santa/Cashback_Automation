package tests;

import base.BaseTest;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;
import pages.BrandPage;
import pages.AllCategoriesPage;
import pages.HomePage;

import java.util.ArrayList;
import java.util.List;

public class CashbackFlowTest extends BaseTest {

    @Test
    public void cashback_homepage_test() throws InterruptedException {

        // 1. Open home page
        HomePage homePage = new HomePage(page);
        homePage.open();

        // 2. Open Menu → All Categories
        homePage.clickMenu();
        AllCategoriesPage AllcategoriesPage = homePage.clickAllCategories();

        // 3. Search for brand and open first result
         BrandPage brandPage = AllcategoriesPage.searchAndOpenBrand("myn");

        // 4. Activate cashback → opens external page → close it
         brandPage.activateCashbackAndClose(context, 5000);

        // 5. Expand cashback rates (twice, as per original flow)
         brandPage.expandCashbackRates();
         brandPage.expandCashbackRates();

        // 6. Close the brand popup
         brandPage.closeBrandPopup();
//check the filters for each category and verify that the campaigns are sorted A-Z
 AllcategoriesPage.clickPopularFilter();

AllcategoriesPage.clickAToZFilter();

List<String> names = AllcategoriesPage.A_Z_filter_getCampaignNames();

String previousPrefix = null;

String previousName = null;

for (String name : names) {

// Skip names that do not start with A-Z

if (!name.matches("^[A-Za-z].*")) {

continue;

}

// Get first two characters (or full name if shorter)

String currentPrefix = name.length() >= 2

? name.substring(0, 2).toLowerCase()

: name.toLowerCase();

if (previousPrefix != null) {

System.out.println("Comparing: " + previousName + " (" + previousPrefix + ")" +

" -> " + name + " (" + currentPrefix + ")");

Assertions.assertThat(currentPrefix.compareTo(previousPrefix))

.as("Campaign names are not sorted A-Z. Previous: '%s', Current: '%s'",

previousName, name)

.isGreaterThanOrEqualTo(0);

}

previousPrefix = currentPrefix;

previousName = name;

}

AllcategoriesPage.clickBackButton();

    }

   
}