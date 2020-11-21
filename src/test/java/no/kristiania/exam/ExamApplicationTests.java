package no.kristiania.exam;

import static io.restassured.RestAssured.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import no.kristiania.exam.user.UserJPAController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;


import java.util.logging.Logger;


@SpringBootTest()
class ExamApplicationTests {


		@Test
	void contextLoads() {
	}


}
