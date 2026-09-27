package stellarburgers.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import static io.restassured.http.ContentType.JSON;

public abstract class BaseClient {

    private static final String BASE_URL =
            "https://stellarburgers.education-services.ru";

    protected RequestSpecification requestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }
}