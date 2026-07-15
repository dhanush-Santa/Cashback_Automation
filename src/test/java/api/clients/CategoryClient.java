package api.clients;

import static io.restassured.RestAssured.given;

import java.util.List;

import api.base.BaseAPITest;
import api.models.Category;
import io.restassured.response.Response;

public class CategoryClient {

    public Response getCategoryDetails() {

        Response response =
                given()
                        .log()
                        .all()
                        .header("Authorization", BaseAPITest.getToken())
                        .header("Content-Type", "application/json")
                        .header("country-code", "IN")
                        .when()
                        .get("/data/categories");

        return response;
    }

    public List<Category> getCategories() {

        Response response =
                getCategoryDetails();

        List<Category> categories =
                response.jsonPath()
                        .getList(
                                "data",
                                Category.class
                        );

        return categories;
    }
}