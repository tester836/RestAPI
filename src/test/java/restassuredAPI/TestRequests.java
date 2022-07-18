package test.java.restassuredAPI;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.text.SimpleDateFormat;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestRequests {
//    Logger logger = LogManager.getRootLogger();
    String usersURL = "https://reqres.in/api/users";
    String queryURL = "?page=1";
    int expectedPage = 1;
    int expectedPer_page = 6;
    int expectedTotal = 12;
    int expectedTotalPages = 2;
    int userID = 2;
    String expectedFirstName = "Janet";
    String expectedLastName = "Weaver";
    int emptyUserID = 23;

    class UsersPage {
        public int page;
        public int per_page;
        public int total;
        public int total_pages;
    }

    class Person {
        public int id;
//        public String email;
        public String first_name;
        public String last_name;
//        public String avatar;
        public List<Person> data;
//        public SimpleDateFormat createdAt;
//        public SimpleDateFormat updatedAt;
    }

    @Test (description = "Get the values of page, per_page, total, total_pages, users data.")
    public void pageData() {

        Gson g = new Gson();
        Response response = RestAssured.get(usersURL + queryURL);
        String jsonString = (response.getBody().asString());

        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200);

        UsersPage pageValue = g.fromJson(jsonString, UsersPage.class);
        System.out.println("\nreqres.in/api/users \nPage number is: " + pageValue.page);

        UsersPage usersPerPage = g.fromJson(jsonString, UsersPage.class);
        System.out.println("Count of users per page: " + usersPerPage.per_page);

        UsersPage usersTotal = g.fromJson(jsonString, UsersPage.class);
        System.out.println("Total count of users: " + usersTotal.total);

        UsersPage pagesTotal = g.fromJson(jsonString, UsersPage.class);
        System.out.println("Total count of pages: " + pagesTotal.total_pages + "\n");

        Person person = g.fromJson(jsonString, Person.class);
        for (Person user : person.data) {
            System.out.println("id" + user.id + "- Name: " + user.first_name + " " + user.last_name);
        }

        given().get(usersURL + queryURL)
        .then().statusCode(200)
                .body("page", equalTo(expectedPage))
                .body("per_page", equalTo(expectedPer_page))
                .body("total", equalTo(expectedTotal))
                .body("total_pages", equalTo(expectedTotalPages))
                .body("data.id[1]", equalTo(userID))
                .log().body();
    }

    @Test (description = "Get and compare the user data.")
    public void compareName() {
        given().get(usersURL + "/" + userID)
        .then().statusCode(200)
                .body("data.id", equalTo(userID))
                .body("data.first_name", equalTo(expectedFirstName))
                .body("data.last_name", equalTo(expectedLastName))
                .log().body();
    }

    @Test (description = "Get the empty response and check the status.")
    public void emptyResponse() {
        given().get(usersURL + "/" + emptyUserID)
        .then().statusCode(404)
                .body(equalTo("{}"))
                .log().body();
    }

    @Test (description = "Post the user data, check the createdAt parameter.")
    public void createUser() {

        JSONObject request = new JSONObject();
        request.put("name", "morpheus");
        request.put("job", "leader");
        System.out.println(request);

        given().body(request.toJSONString())
        .when().post(usersURL)
        .then().statusCode(201).log().body();
    }

    @Test (description = "Patch the user data and check the updatedAt parameter.")
    public void updateUser() {

        JSONObject request = new JSONObject();
        request.put("name", "morpheus");
        request.put("job", "zion resident");
        System.out.println(request);

        given().body(request.toJSONString())
        .when().put(usersURL + "/" + userID)
        .then().statusCode(200).log().body();
    }

    @Test (description = "Delete the user.")
    public void deleteUser() {
        JSONObject request = new JSONObject();

        given().body(request.toJSONString())
        .when().delete(usersURL + "/" + userID)
        .then().statusCode(204).log().body();
    }

}
