package tests;

import api.utils.EarningsDataStore;
import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.TotalEarningsPage;

import java.util.List;

public class TotalEarningsTests extends BaseTest {
    private TotalEarningsPage totalEarningsPage;

    Double Totalearnings;
    Double My_Earnings;
    Double Pending_Payments;
    Double Declined_Amount;

  @BeforeMethod
  public void initializeTotalearningsPageObject()
  {
   totalEarningsPage=new TotalEarningsPage(page);
   totalEarningsPage.clickTotalEarnings();
  }


@Test
public void validateClicksTab() {

    totalEarningsPage.clickClicksTab();

    Assert.assertTrue(
            totalEarningsPage.isClicksDataDisplayed(),
            "No records found in Clicks tab");

    Assert.assertTrue(
            totalEarningsPage.validateClicksTableScrolling(),
            "History table scrolling is not working");
}

@Test(groups="ui")
public void validatePendingCashbackAmount() {


    totalEarningsPage.clickPendingTab();


    double calculatedAmount =
            totalEarningsPage.calculatePendingCashbackAmount();


    double pendingPaymentsAmount =
            totalEarningsPage.getPendingPaymentsFromHead();


    // format both values to 2 decimals
    calculatedAmount =
            Math.round(calculatedAmount * 100.0) / 100.0;


    pendingPaymentsAmount =
            Math.round(pendingPaymentsAmount * 100.0) / 100.0;


    System.out.println(
            "Calculated Sum = " + calculatedAmount
    );


    System.out.println(
            "Pending Payments UI = " + pendingPaymentsAmount
    );

Pending_Payments=calculatedAmount;
EarningsDataStore.uiPendingPayments = calculatedAmount;
    Assert.assertEquals(
            calculatedAmount,
            pendingPaymentsAmount,
            "Pending Payments amount is not matching"
    );

}

@Test(groups="ui")
public void validateConfirmedCashbackAmount() {


    totalEarningsPage.clickConfirmedTab();


    double calculatedAmount =
            totalEarningsPage.calculateConfirmedCashbackAmount();


    double myEarnings =
            totalEarningsPage.getMyEarningsAmount();


    System.out.println(
            "Calculated Confirmed Cashback = "
            + calculatedAmount);


    System.out.println(
            "My Earnings = "
            + myEarnings);

My_Earnings=calculatedAmount;
EarningsDataStore.uiConfirmedEarnings = calculatedAmount;
    Assert.assertEquals(
            calculatedAmount,
            myEarnings,
            "Confirmed cashback amount mismatch");
}

@Test(
    dependsOnMethods = {
        "validatePendingCashbackAmount",
        "validateConfirmedCashbackAmount"
    }
)
public void validate_TotalEarnings() {

    double totalEarningsFromUI =
            totalEarningsPage.getTotalEarningsFromUI();


    double calculatedTotal =
            Pending_Payments + My_Earnings;



    calculatedTotal =
            Math.round(calculatedTotal * 100.0) / 100.0;


    totalEarningsFromUI =
            Math.round(totalEarningsFromUI * 100.0) / 100.0;


    System.out.println(
            "Pending Payments + My Earnings = "
            + calculatedTotal
    );


    System.out.println(
            "Total Earnings UI = "
            + totalEarningsFromUI
    );


    Assert.assertEquals(
            calculatedTotal,
            totalEarningsFromUI,
            "Total Earnings is not matching with Pending Payments + My Earnings"
    );
}
@Test(dependsOnMethods = "validateConfirmedCashbackAmount")
public void validateRedeemRedirectBasedOnMyEarnings() {



    double myEarnings =
            totalEarningsPage.getMyEarningsAmount();


    System.out.println(
            "My Earnings = " + myEarnings
    );


    if(myEarnings > 0.10) {


        String rewardsUrl =
                totalEarningsPage.clickRedeemButton();


        boolean validRewardsPage =
                rewardsUrl.equals("https://q.santabrowser.com/home")
                ||
                rewardsUrl.contains("https://q.santabrowser.com/?tab=rewards");


        Assert.assertTrue(
                validRewardsPage,
                "User is not redirected to Rewards page. Actual URL: "
                        + rewardsUrl
        );


        System.out.println(
                "Rewards page validation passed"
        );


    } else {

        System.out.println(
                "My Earnings <= 0.10, Redeem validation skipped"
        );
    }
}
@Test(groups="ui")
public void validateDeclinedCashbackHistory() {


    totalEarningsPage.clickDeclinedTab();


    int declinedCount =
            totalEarningsPage.getDeclinedTransactionCount();


    double declinedAmount =
            totalEarningsPage.calculateDeclinedCashbackAmount();
Declined_Amount = declinedAmount;
EarningsDataStore.uiDeclinedAmount = declinedAmount;

    System.out.println(
            "Total Declined Transactions = "
            + declinedCount
    );


    System.out.println(
            "Total Declined Cashback Amount = $"
            + declinedAmount
    );


    if(declinedCount == 0) {

        System.out.println(
                "No declined cashback records found"
        );

    } else {

        System.out.println(
                "Declined cashback records available"
        );
    }



    Assert.assertTrue(
            declinedCount >= 0,
            "Declined transaction validation failed"
    );

}

}