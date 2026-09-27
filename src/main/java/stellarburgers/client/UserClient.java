package stellarburgers.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import stellarburgers.model.User;
import stellarburgers.model.UserCredentials;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseClient {

    private static final String REGISTER_PATH = "/api/auth/register";
    private static final String LOGIN_PATH = "/api/auth/login";
    private static final String USER_PATH = "/api/auth/user";

    @Step("Создать пользователя")
    public ValidatableResponse create(User user) {
        return given()
                .spec(requestSpecification())
                .body(user)
                .when()
                .post(REGISTER_PATH)
                .then();
    }

    @Step("Выполнить вход пользователя")
    public ValidatableResponse login(UserCredentials credentials) {
        return given()
                .spec(requestSpecification())
                .body(credentials)
                .when()
                .post(LOGIN_PATH)
                .then();
    }

    @Step("Удалить пользователя")
    public ValidatableResponse delete(String accessToken) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", accessToken)
                .when()
                .delete(USER_PATH)
                .then();
    }
}