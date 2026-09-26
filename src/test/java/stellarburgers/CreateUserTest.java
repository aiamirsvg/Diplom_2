package stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.client.UserClient;
import stellarburgers.model.User;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateUserTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.randomUser();
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверяем успешное создание пользователя с уникальным email")
    public void createUniqueUserReturnsSuccess() {
        ValidatableResponse response = userClient.create(user);

        response
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue());

        accessToken = response.extract().path("accessToken");
    }
    @Test
    @DisplayName("Нельзя создать уже зарегистрированного пользователя")
    @Description("Повторная регистрация с теми же данными возвращает ошибку 403")
    public void createExistingUserReturnsError() {
        ValidatableResponse firstResponse = userClient.create(user);
        accessToken = firstResponse.extract().path("accessToken");

        userClient.create(user)
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }
}