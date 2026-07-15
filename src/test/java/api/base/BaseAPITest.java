package api.base;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import io.restassured.RestAssured;



public class BaseAPITest {

    public static final String CLID="2da68a9dd8c5a73";
        public static String getToken()
    {
        return "Bearer d011a89b3473ec1991e0b427609e36b69fe43ace-3a1ba10ecb8faf73d4a6fd2181652520";
    }

    @BeforeClass
    public void setUp()
    {
     RestAssured.baseURI="https://cbapi.santabrowser.com";
    }

}
