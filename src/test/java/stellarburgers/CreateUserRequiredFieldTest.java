package stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import stellarburgers.client.UserClient;
import stellarburgers.model.User;
import static org.apache.http.HttpStatus.*;


import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserRequiredFieldTest {

    private final String missingField;
    private final User user;

    public CreateUserRequiredFieldTest(String missingField, User user) {
        this.missingField = missingField;
        this.user = user;
    }

    @Parameterized.Parameters(name = "Отсутствует поле: {0}")
    public static Object[][] requiredFields() {
        return new Object[][]{
                {
                        "email",
                        new User(null, "password123", "Aiman")
                },
                {
                        "password",
                        new User("user-without-password@example.com", null, "Aiman")
                },
                {
                        "name",
                        new User("user-without-name@example.com", "password123", null)
                }
        };
    }

    @Test
    @DisplayName("Нельзя создать пользователя без обязательного поля")
    @Description("API должно вернуть ошибку, если не передано обязательное поле")
    public void createUserWithoutRequiredFieldReturnsError() {
        new UserClient()
                .create(user)
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body(
                        "message",
                        equalTo("Email, password and name are required fields")
                );
    }
}