package api.tests;

import api.base.BaseAPITest;
import api.clients.EarningsClient;
import api.models.Cashback;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;


public class EarningsApiTest extends BaseAPITest {

    @Test
    public void verifyUserEarningsAPI() {
        EarningsClient client =
                new EarningsClient();


        Response response =
                client.getUserEarnings(CLID);


        Assert.assertEquals(
                response.statusCode(),
                200
        );

        Cashback cashback =
                new Cashback();


        cashback.setPending(
                response.jsonPath()
                        .getDouble("data.pending")
        );


        cashback.setConfirmed(
                response.jsonPath()
                        .getDouble("data.confirmed")
        );


        cashback.setDeclined(
                response.jsonPath()
                        .getDouble("data.declined")
        );

        System.out.println(cashback);
        Assert.assertNotNull(cashback);

    }

    @Test
    public void calculateIndividualEarnings() {
        EarningsClient client =
                new EarningsClient();
    }


}