package stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.client.UserClient;
import stellarburgers.model.User;
import stellarburgers.model.UserCredentials;
import static org.apache.http.HttpStatus.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginUserTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.randomUser();

        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Вход существующего пользователя")
    @Description("Проверяем успешный вход с правильными email и паролем")
    public void loginExistingUserReturnsSuccess() {
        userClient.login(UserCredentials.from(user))
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    @Description("Проверяем ошибку входа при неверном пароле")
    public void loginWithWrongPasswordReturnsError() {
        UserCredentials wrongCredentials =
                new UserCredentials(
                        user.getEmail(),
                        "wrong-password"
                );

        userClient.login(wrongCredentials)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body(
                        "message",
                        equalTo("email or password are incorrect")
                );
    }

    @Test
    @DisplayName("Вход с неверным логином")
    @Description("Проверяем ошибку входа при неверном email")
    public void loginWithWrongEmailReturnsError() {
        UserCredentials wrongCredentials =
                new UserCredentials(
                        "wrong-" + user.getEmail(),
                        user.getPassword()
                );

        userClient.login(wrongCredentials)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body(
                        "message",
                        equalTo("email or password are incorrect")
                );
    }
}
