package com.bhft.todo.get;


import com.bhft.todo.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import com.todo.models.Todo;


public class GetTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    @Test
    public void testGetTodosWhenDatabaseIsEmpty() {
        given()
                .when()
                .get("/todos")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("", hasSize(0));
    }

    @Test
    public void testGetTodosWithExistingEntries() {
        // Предварительно создать несколько TODO
        Todo todo1 = new Todo(1, "Task 1", false);
        Todo todo2 = new Todo(2, "Task 2", true);

        createTodo(todo1);
        createTodo(todo2);

        Response response =
                given()
                        .when()
                        .get("/todos")
                        .then()
                        .statusCode(200)
                        .contentType("application/json")
                        .body("", hasSize(2))
                        .extract().response();

        // Дополнительная проверка содержимого
        Todo[] todos = response.getBody().as(Todo[].class);

        Assertions.assertEquals(1, todos[0].getId());
        Assertions.assertEquals("Task 1", todos[0].getText());
        Assertions.assertFalse(todos[0].isCompleted());

        Assertions.assertEquals(2, todos[1].getId());
        Assertions.assertEquals("Task 2", todos[1].getText());
        Assertions.assertTrue(todos[1].isCompleted());
    }

    @Test
    public void testGetTodosWithOffsetAndLimit() {
        // Создаем 5 TODO
        for (int i = 1; i <= 5; i++) {
            createTodo(new Todo(i, "Task " + i, i % 2 == 0));
        }

        Response response =
                given()
                        .queryParam("offset", 2)
                        .queryParam("limit", 2)
                        .when()
                        .get("/todos")
                        .then()
                        .statusCode(200)
                        .contentType("application/json")
                        .body("", hasSize(2))
                        .extract().response();

        // Проверяем, что получили задачи с id 3 и 4
        Todo[] todos = response.getBody().as(Todo[].class);

        Assertions.assertEquals(3, todos[0].getId());
        Assertions.assertEquals("Task 3", todos[0].getText());

        Assertions.assertEquals(4, todos[1].getId());
        Assertions.assertEquals("Task 4", todos[1].getText());
    }
}
