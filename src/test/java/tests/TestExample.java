package tests;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class TestExample {
    @Test
    void checkStatusCode() {
        given()
                .log().all()
        .when()
                .get("http://localhost:8080/todos")
        .then()
                .log().all()
                .statusCode(200)
                .body("todos.size()", equalTo(2));
    }
}
