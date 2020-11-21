package no.kristiania.exam;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import no.kristiania.exam.user.UserJPAController;
import no.kristiania.exam.user.UserRepository;
import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import javax.annotation.PostConstruct;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.Matchers.equalTo;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestTest {

    private static final Logger LOG = Logger.getLogger(UserJPAController.class.getName());
    private static final String host = "localhost";
    public static WireMockServer wireMockServer;
    //    public static WireMockRule wireMockRule = new WireMockRule(8080);
    public static UserRepository userRepository;
    public int newUserCounter = 1;
    public int currentUserCounter;

    @LocalServerPort
    protected int port = 0;

    @BeforeAll
    public static void setup() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)));
        wireMockServer.start();

//        ResponseDefinitionBuilder responseDefinitionBuilder = new ResponseDefinitionBuilder();
//        responseDefinitionBuilder.withStatus(200)
//                .withBody("[{\"id\":10001,\"name\":\"Adam\",\"birthDate\":\"2020-11-19T23:00:00.000+00:00\",\"posts\":[{\"id\":11002,\"description\":\"Woah!\"}]},{\"id\":10002,\"name\":\"Eva\",\"birthDate\":\"2020-11-19T23:00:00.000+00:00\",\"posts\":[{\"id\":11001,\"description\":\"First post!\"}]},{\"id\":10003,\"name\":\"Camilla\",\"birthDate\":\"2020-11-19T23:00:00.000+00:00\",\"posts\":[{\"id\":11003,\"description\":\"Okay, I`m typing now... Gosh darn it! How do I delete this?\"}]}]")
//                .withHeader("Content-Type", "application/json");

        WireMock.configureFor(host, wireMockServer.port());
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlMatching("/users.*"))
                        .willReturn(WireMock.aResponse())
        );
    }

    @AfterClass
    public static void tearDown() {
        if (null != wireMockServer && wireMockServer.isRunning()) {
            wireMockServer.shutdownServer();
        }
    }

    public int getCounter() {
        newUserCounter++;

        return newUserCounter;
    }


    @PostConstruct
    public void init() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
//        RestAssured.basePath = "/users";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testBasePathStatusCodePositive() throws URISyntaxException {
        RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .get(new URI("/users"))
                .then()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK);

    }

    //   "id": 10001
    // user with id 10001 is already in the database because of initialization in /resources/data.sql
    @Test
    public void testUser10001StatusCodePositive() throws URISyntaxException {
        RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .get(new URI("/users/10001"))
                .then()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK);
    }

    @Test
    public void deleteUser() {

        // user with id 10003 is already in the database because of initialization in /resources/data.sql
        RestAssured.given().delete("/users/10003")
                .then()
                .assertThat()
                .statusCode(200)
                .log().all();

        // since 10003 is deleted, it should return 404
        RestAssured.given().get("/users/10003")
                .then()
                .assertThat()
                .statusCode(404)
                .log().all();
    }

    @Test
    public void testGetAndDeleteNewUser() {

        String newUser = "Askeladden";
        String birthDate = "1950-11-19T23:00:00.000+00:00";

        Map<String, Object> jsonAsMap = new HashMap<>();
        jsonAsMap.put("name", newUser);
        jsonAsMap.put("birthDate", birthDate);

        RestAssured.given().
                accept(ContentType.JSON).
                contentType(ContentType.JSON).
                body(jsonAsMap).
                when().
                post("/users")
                .then()
                .assertThat()
                .statusCode(201)
                .log().all();


        // since there is no users created yet (other than hardcoded users in /resources/data.sql)
        // this will be the first user with a generated id. Therefore it will be 1.
        RestAssured.given().get("/users/1")
                .then()
                .assertThat()
                .statusCode(200)
                .log().all();

        RestAssured.given().delete("/users/1")
                .then()
                .assertThat()
                .statusCode(200)
                .log().all();

        // since it is deleted, it should return 404
        RestAssured.given().get("/users/1")
                .then()
                .assertThat()
                .statusCode(404)
                .log().all();

    }

    @Test
    public void testGetFirstElementInUsersBody() throws URISyntaxException {
        RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .get(new URI("/users"))
                .then()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .and()

                .body("[0].id", (equalTo(10001)))
                .body("[0].name", (equalTo("Adam")))
                .log().all();
    }


    @Test
    public void testGetUserPost() throws URISyntaxException {

        // this post has already been created in /resources/data.sql
        //[{"id":11002,"description":"Woah!"}]
        MyObject expectedObject = new MyObject(11002, "Woah!");

        RestAssured.given()
                .get("/users/10001/posts")
                .then()
                .assertThat()
                .statusCode(200)
                .body("[0].id", equalTo(expectedObject.getId()))
                .body("[0].description", equalTo(expectedObject.getDescription()));

    }

}

//test