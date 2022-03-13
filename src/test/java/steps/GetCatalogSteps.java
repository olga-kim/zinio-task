package steps;

import entity.GetCatalogResponse;
import entity.GetTokenBody;
import entity.TokenResponse;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetCatalogSteps {

    private static RequestSpecification specification;
    private String authToken;
    private ValidatableResponse response;

    @BeforeAll
    public static void setup() {
        specification = RestAssured.given()
                .baseUri("https://sbx-api-sec.ziniopro.com")
                .contentType(ContentType.JSON)
                .filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Given("Authentication token is generated")
    public void getAuthToken() {
        GetTokenBody getTokenBody = GetTokenBody.builder()
                .clientId("C9EeT8cWZ7u7LcYYvOTlFyONeHCN3OZp")
                .clientSecret("ns1huoaQM3aCPq1yh6dcizirP99cMrsc")
                .grantType("client_credentials")
                .build();

        authToken = specification.when()
                .body(getTokenBody)
                .post("/oauth/v2/tokens")
                .as(TokenResponse.class)
                .getAccessToken();
    }

    @When("Get catalog request is sent")
    public void getCatalog() {
        response = specification.when()
                .body("")
                .get("/catalog/v2/catalogs?access_token=" + authToken)
                .then();
    }

    @Then("List of catalogs is received")
    public void checkCatalogs() {
        GetCatalogResponse catalogsResponse = response.statusCode(HttpStatus.SC_OK)
                .extract()
                .as(GetCatalogResponse.class);

        assertThat(catalogsResponse.isStatus(), equalTo(true));
        assertThat(catalogsResponse.getData().isEmpty(), equalTo(false));
    }
}
