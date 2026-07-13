package api.clients;

import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

public class EarningsClient {

    public Response getUserEarnings(String uuid) {
        return given().header(
                        "Authorization",
                        "Bearer " +
                                "d011a89b3473ec1991e0b427609e36b69fe43ace-3a1ba10ecb8faf73d4a6fd2181652520"
                )
                .when()
                .get("/pull/user/earning?uuid=" + uuid);
    }

}
