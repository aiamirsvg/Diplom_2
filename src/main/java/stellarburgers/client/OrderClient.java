package stellarburgers.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import stellarburgers.model.Order;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class OrderClient {

    private static final String BASE_URL =
            "https://stellarburgers.education-services.ru";

    private static final String INGREDIENTS_PATH = "/api/ingredients";
    private static final String ORDERS_PATH = "/api/orders";

    private RequestSpecification requestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }

    public ValidatableResponse getIngredients() {
        return given()
                .spec(requestSpecification())
                .when()
                .get(INGREDIENTS_PATH)
                .then();
    }

    public ValidatableResponse create(Order order) {
        return given()
                .spec(requestSpecification())
                .body(order)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    public ValidatableResponse create(Order order, String accessToken) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDERS_PATH)
                .then();
    }
}
