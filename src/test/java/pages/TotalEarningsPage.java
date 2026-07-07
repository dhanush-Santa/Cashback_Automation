package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.Assert;

public class TotalEarningsPage {

    private final Page page;

    public TotalEarningsPage(Page page) {
        this.page = page;
    }

    public void clickTotalEarnings() {
        page.getByText("TOTAL EARNINGS").click();
        page.waitForURL("**/earnings/**", new Page.WaitForURLOptions().setTimeout(10000));
    }

    public void clickClicksTab() {

        page.locator(".history_tabs button")
                .filter(new Locator.FilterOptions().setHasText("Clicks"))
                .click();
    }

    public boolean isClicksDataDisplayed() {

        Locator rows = page.locator(".history_table_body");

        rows.first().waitFor();

        return rows.count() > 0;
    }
public boolean validateClicksTableScrolling() {

    Locator tableWrapper =
            page.locator(".history_table_wrapper");

    double scrollHeight =
            ((Number) tableWrapper.evaluate(
                    "e => e.scrollHeight"))
                    .doubleValue();

    double clientHeight =
            ((Number) tableWrapper.evaluate(
                    "e => e.clientHeight"))
                    .doubleValue();

    // No scrollbar present
    if (scrollHeight <= clientHeight) {

        System.out.println(
                "No scrolling required. All records are already visible.");

        return true;
    }

    double beforeScroll =
            ((Number) tableWrapper.evaluate(
                    "e => e.scrollTop"))
                    .doubleValue();

    tableWrapper.evaluate(
            "e => e.scrollTop = e.scrollHeight");

    page.waitForTimeout(1000);

    double afterScroll =
            ((Number) tableWrapper.evaluate(
                    "e => e.scrollTop"))
                    .doubleValue();

    return afterScroll > beforeScroll;
}

public void clickPendingTab() {

    page.getByRole(
            AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Pending")
    ).click();

    page.waitForTimeout(1000);
}

public double calculatePendingCashbackAmount() {

    double pendingCashbackAmount = 0.0;

    Locator tableWrapper =
            page.locator(".history_table_wrapper");

    Locator rows =
            page.locator(".history_table_body_pending");


    int processedRows = 0;


    while (true) {


        int currentRows = rows.count();


        // Calculate newly loaded rows only
        for (int i = processedRows; i < currentRows; i++) {


            Locator row = rows.nth(i);


            // Take only first cashback amount ($0.10)
            String cashback =
                    row.locator(".table_text_sale_amount")
                       .first()
                       .innerText()
                       .trim();


            if (cashback.matches(".*\\$[0-9]+\\.[0-9]{2}.*")) {


                cashback =
                        cashback.replace("$", "")
                                .replace("₹", "")
                                .replace(",", "")
                                .trim();


                pendingCashbackAmount +=
                        Double.parseDouble(cashback);


                System.out.println(
                        "Row " + (i + 1)
                        + " Cashback = "
                        + cashback
                );
            }
        }


        processedRows = currentRows;


        double currentScroll =
                ((Number)tableWrapper.evaluate(
                        "e => e.scrollTop"))
                        .doubleValue();


        double scrollHeight =
                ((Number)tableWrapper.evaluate(
                        "e => e.scrollHeight"))
                        .doubleValue();


        double clientHeight =
                ((Number)tableWrapper.evaluate(
                        "e => e.clientHeight"))
                        .doubleValue();


        // Reached bottom
        if (currentScroll + clientHeight >= scrollHeight) {
            break;
        }


        // Scroll down
        tableWrapper.evaluate(
                "e => e.scrollTop = e.scrollHeight"
        );


        page.waitForTimeout(1000);
    }


    System.out.println(
            "Total Pending Cashback Amount = "
            + pendingCashbackAmount
    );


    return pendingCashbackAmount;
}

public double getPendingPaymentsFromHead() {

    String amount =
            page.locator(".earnings_stat")
                .filter(new Locator.FilterOptions()
                .setHasText("Pending Payments"))
                .locator(".earnings_stat_value")
                .innerText()
                .trim();


    return Double.parseDouble(
            amount.replace("$", "")
                  .replace(",", "")
    );
}


public double calculateConfirmedCashbackAmount() {

    double totalCashback = 0.0;

    Locator tableWrapper =
            page.locator(".history_table_wrapper");

    Locator rows =
            page.locator(".history_table_body_confirmed");


    int processedRows = 0;


    while (true) {

        int currentRows = rows.count();


        for (int i = processedRows; i < currentRows; i++) {

            Locator row = rows.nth(i);


            // First cashback amount column only
            String cashback =
                    row.locator(".table_text_sale_amount")
                       .first()
                       .innerText();


            cashback = cashback
                    .replace("$", "")
                    .replace("₹", "")
                    .trim();


            if (!cashback.isEmpty()) {

                totalCashback +=
                        Double.parseDouble(cashback);
            }
        }


        processedRows = currentRows;


        double scrollTop =
                ((Number)tableWrapper.evaluate(
                        "e => e.scrollTop"))
                        .doubleValue();


        double scrollHeight =
                ((Number)tableWrapper.evaluate(
                        "e => e.scrollHeight"))
                        .doubleValue();


        double clientHeight =
                ((Number)tableWrapper.evaluate(
                        "e => e.clientHeight"))
                        .doubleValue();



        if (scrollTop + clientHeight >= scrollHeight) {
            break;
        }


        tableWrapper.evaluate(
                "e => e.scrollTop = e.scrollHeight");


        page.waitForTimeout(1000);
    }


    return roundToTwoDecimals(totalCashback);
}



private double roundToTwoDecimals(double value) {

    return Math.round(value * 100.0) / 100.0;
}


public double getMyEarningsAmount() {

    String amount =
            page.locator(".earnings_stat")
                .filter(new Locator.FilterOptions()
                .setHasText("My Earnings"))
                .locator(".earnings_stat_value")
                .innerText();


    amount = amount
            .replace("$", "")
            .trim();


    return roundToTwoDecimals(
            Double.parseDouble(amount));
}


public void clickConfirmedTab() {

    page.getByRole(
            com.microsoft.playwright.options.AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Confirmed")
    ).click();

    page.waitForTimeout(1000);
}


public double getTotalEarningsFromUI() {

    String amount =
            page.locator("p.sc-kwhYVV.fOcvop")
                .innerText();

    amount = amount
            .replace("$", "")
            .trim();


    return Double.parseDouble(amount);
}
public String clickRedeemButton() {

    Page rewardsPage = page.waitForPopup(() -> {

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Redeem")
        ).click();

    });


    rewardsPage.waitForLoadState();


    String rewardsUrl = rewardsPage.url();


    System.out.println(
            "Rewards URL = " + rewardsUrl
    );


    rewardsPage.close();


    return rewardsUrl;
}

public void clickDeclinedTab() {

    page.getByRole(
            AriaRole.BUTTON,
            new Page.GetByRoleOptions()
                    .setName("Declined")
    ).click();

    page.waitForTimeout(2000);
}

public int getDeclinedTransactionCount() {

    Locator declinedRows =
            page.locator("tr.history_table_body_declined");

    return declinedRows.count();
}

public double calculateDeclinedCashbackAmount() {

    double total = 0.0;


    Locator declinedRows =
            page.locator("tr.history_table_body_declined");


    int count = declinedRows.count();


    for(int i = 0; i < count; i++) {


        String cashback =
                declinedRows.nth(i)
                .locator(".table_text_sale_amount")
                .first()
                .innerText()
                .trim();


        cashback = cashback
                .replace("$", "")
                .replace(",", "")
                .trim();


        if(!cashback.isEmpty()) {

            total += Double.parseDouble(cashback);

            System.out.println(
                    "Declined Row "
                    + (i + 1)
                    + " Cashback = $"
                    + cashback
            );
        }
    }


    return Math.round(total * 100.0) / 100.0;
}

}
