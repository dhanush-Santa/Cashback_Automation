package api.clients;

import static io.restassured.RestAssured.given;

import java.util.List;

import api.base.BaseAPITest;
import api.models.Store;
import io.restassured.response.Response;

public class StoreClient {

    public Response getStoreDetails(String countryCode) {

        Response response =
                given()
                        .log()
                        .all()
                        .header("Authorization", BaseAPITest.getToken())
                        .header("Content-Type", "application/json")
                        .header("country-code", countryCode)
                        .when()
                        .get("/data/stores?page=all");

        return response;

    }
   

    public List<Store> getStores(String countryCode) {

        Response response =
                getStoreDetails(countryCode);


        List<Store> stores =
                response.jsonPath()
                        .getList(
                                "data.data",
                                Store.class
                        );


        return stores;
    }
    public Response getStoreDetailsById(String countryCode, int storeId) {

        Response response =
                given()
                        .log()
                        .all()
                        .header("Authorization", BaseAPITest.getToken())
                        .header("Content-Type", "application/json")
                        .header("country-code", countryCode)
                        .when()
                        .get("/data/storeInfo/" + storeId);

        return response;

    }



}