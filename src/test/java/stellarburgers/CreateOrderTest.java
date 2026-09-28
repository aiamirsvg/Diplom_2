package stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.client.OrderClient;
import stellarburgers.client.UserClient;
import stellarburgers.model.Order;
import stellarburgers.model.User;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.apache.http.HttpStatus.*;

public class CreateOrderTest {

    private OrderClient orderClient;
    private UserClient userClient;
    private String accessToken;
    private List<String> ingredientIds;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();

        User user = User.randomUser();
        accessToken = userClient.create(user)
                .extract()
                .path("accessToken");

        ingredientIds = orderClient.getIngredients()
                .statusCode(SC_OK)
                .extract()
                .jsonPath()
                .getList("data._id");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    private Order orderWithValidIngredients() {
        return new Order(List.of(
                ingredientIds.get(0),
                ingredientIds.get(1)
        ));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Авторизованный пользователь создаёт заказ с ингредиентами")
    public void createOrderWithAuthorizationReturnsSuccess() {
        orderClient.create(orderWithValidIngredients(), accessToken)
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Заказ с ингредиентами можно создать без токена пользователя")
    public void createOrderWithoutAuthorizationReturnsSuccess() {
        orderClient.create(orderWithValidIngredients())
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("API возвращает ошибку, если список ингредиентов пуст")
    public void createOrderWithoutIngredientsReturnsError() {
        Order order = new Order(Collections.emptyList());

        orderClient.create(order, accessToken)
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body(
                        "message",
                        equalTo("Ingredient ids must be provided")
                );
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента")
    @Description("API возвращает ошибку для несуществующего идентификатора")
    public void createOrderWithInvalidIngredientHashReturnsError() {
        Order order = new Order(
                Collections.singletonList("invalid-ingredient-hash")
        );

        orderClient.create(order, accessToken)
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}