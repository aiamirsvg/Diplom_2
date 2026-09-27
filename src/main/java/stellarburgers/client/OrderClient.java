package stellarburgers.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import stellarburgers.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseClient {

    private static final String INGREDIENTS_PATH = "/api/ingredients";
    private static final String ORDERS_PATH = "/api/orders";

    @Step("Получить список ингредиентов")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(requestSpecification())
                .when()
                .get(INGREDIENTS_PATH)
                .then();
    }

    @Step("Создать заказ без авторизации")
    public ValidatableResponse create(Order order) {
        return given()
                .spec(requestSpecification())
                .body(order)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    @Step("Создать заказ с авторизацией")
    public ValidatableResponse create(
            Order order,
            String accessToken
    ) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDERS_PATH)
                .then();
    }
}