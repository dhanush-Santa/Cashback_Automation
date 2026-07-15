package api.clients;

import static io.restassured.RestAssured.given;

import java.util.List;

import api.base.BaseAPITest;
import api.models.Store;
import io.restassured.response.Response;

public class StoreClient {

    public Response getStoreDetails()
    {


        Response response =
                given()
                        .log()
                        .all()
                        .header("Authorization", BaseAPITest.getToken())
                        .header("Content-Type", "application/json")
                        .header("country-code", "US")
                        .when()
                        .get("/data/stores?page=all");

        return response;

    }


    public List<Store> getStores(){

        Response response =
                getStoreDetails();


        List<Store> stores =
                response.jsonPath()
                        .getList(
                                "data.data",
                                Store.class
                        );


        return stores;
    }
}