package steps;

import entity.GetCatalogResponse;
import entity.GetTokenBody;
import entity.TokenResponse;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;

import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetCatalogSteps {

    private static RequestSpecification specification;
    private String authToken;
    private ValidatableResponse response;

    private static StringWriter stringWriter;
    private Scenario scenario;

    @BeforeAll
    public static void setup() {
        stringWriter = new StringWriter();
        specification = RestAssured.given()
                .baseUri("https://sbx-api-sec.ziniopro.com")
                .contentType(ContentType.JSON);
    }

    @Before
    public void setupScenario(Scenario scenario) {
        this.scenario = scenario;
        PrintStream printStream = new PrintStream(new WriterOutputStream(stringWriter, Charset.defaultCharset()), true);
        specification.filters(
                RequestLoggingFilter.logRequestTo(printStream), ResponseLoggingFilter.logResponseTo(printStream));
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
        writeLog();
    }

    @When("Get catalog request is sent without authorization")
    public void getCatalogWithoutAuth() {
        response = specification.when()
                .body("")
                .get("/catalog/v2/catalogs?access_token=" + RandomStringUtils.random(10))
                .then();
        writeLog();
    }

    @Then("List of catalogs is received")
    public void checkCatalogs() {
        GetCatalogResponse catalogsResponse = response.statusCode(HttpStatus.SC_OK)
                .extract()
                .as(GetCatalogResponse.class);

        assertThat(catalogsResponse.isStatus(), equalTo(true));
        assertThat(catalogsResponse.getData().isEmpty(), equalTo(false));
    }

    @Then("Unauthorized error is received")
    public void checkUnauthorizedError() {
        response.statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    private void writeLog() {
        scenario.log(stringWriter.toString());
        stringWriter.getBuffer().setLength(0);
    }
}
