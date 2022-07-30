import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

import models.Pet;

import org.hamcrest.Matchers;
import org.json.JSONObject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

public class ApiTest {
    Response response;
    Pet pet;
    HttpHeaders headers;
    RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    //create object mapper for serialization/deserialization Json Object

    //set construct object
    public ApiTest(){
        baseURI = "https://petstore.swagger.io/v2"; //set base uri for api
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);//set header content type media type
        restTemplate = new RestTemplate();
        pet = new Pet();
    }

    //Create test case by test annotation
    @Test
    @DisplayName("Post Method Successfully")//Create Test Name
    public void Test1() throws UnirestException, JsonProcessingException {

        //Assigning the elements of the pet object
        pet.setId(1);
        pet.setPetId(pet.getId());
        pet.setQuantity(1);
        pet.setShipDate(new Date());
        pet.setStatus("placed");
        pet.setComplete("true");

        //Converting the Object to JSONString
        String jsonString = objectMapper.writeValueAsString(pet);

        //Unirest is a lightweight HTTP client library from Mashape
        Unirest.setTimeouts(0, 0);

        //HttpResponse describes the result of an HttpRequest call and get HTTP response body as a string
        HttpResponse<String> response = Unirest.post(baseURI+"/store/order")
                .header("Content-Type", "application/json")
                .body(jsonString)
                .asString();

        //Checks if the response is 200
        Assertions.assertEquals(200, response.getStatus());
    }

    //Create test case by test annotation
    @Test
    @DisplayName("Post Method By Using Json Body")//Create Test Name
    public void Test2() throws UnirestException {

        //A body object of type Json is created for the Post Method
        String postBody = "{\r\n  \"id\": 2,\r\n  \"petId\": 2,\r\n  \"quantity\": 1,\r\n  \"shipDate\": \"2022-07-26T12:17:53.762Z\",\r\n  \"status\": \"placed\",\r\n  \"complete\": true\r\n}";

        //Unirest is a lightweight HTTP client library from Mashape
        Unirest.setTimeouts(0, 0);

        //HttpResponse describes the result of an HttpRequest call and get HTTP response body as a JsonNode
        HttpResponse<JsonNode> response = Unirest.post(baseURI+"/store/order")
                .header("Content-Type", "application/json")
                .body(postBody)
                .asJson();

        //Check if the response status code is 200
        Assertions.assertEquals(200, response.getStatus());
        //Checking if the response object has an id value of 2
        Assertions.assertEquals(response.getBody().getObject().get("id"), 2);
        //Checking status title is it 2 or not for response object
        Assertions.assertEquals(response.getBody().getObject().get("status"), "placed");

    }
    //Create test case by test annotation
    @Test
    @DisplayName("Post Method By Using RestAssured")//Set the test case name by DisplayName annotation
    public void Test3() throws JsonProcessingException, ParseException {

        String date_string = "26-09-2022";
        //Instantiating the SimpleDateFormat class
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //Parsing the given String to Date object
        Date date = formatter.parse(date_string);

        //Assigning the elements of the pet object
        pet.setId(3);
        pet.setPetId(pet.getId());
        pet.setQuantity(1);
        pet.setShipDate(date);
        pet.setStatus("placed");
        pet.setComplete("true");

        //Converting the Object to JSONString
        String jsonString = objectMapper.writeValueAsString(pet);

        //RestAssured class to start creating response specifications with when() and then() methods
        response = given()
                .header("Content-Type","application/json")
                .body(jsonString)
                .when()
                .post(baseURI+"/store/order")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", Matchers.equalTo(pet.getId()))
                .extract().response();

        //Getting the JsonPath object instance from the Response interface
        JsonPath postJson =  response.jsonPath();

        //Controlling whether the response body is the same as the comparative Json object
        Assertions.assertEquals(response.getBody().asString(), "{\"id\":3,\"petId\":3,\"quantity\":1,\"shipDate\":\"2022-09-25T21:00:00.000+0000\",\"status\":\"placed\",\"complete\":true}");
        //Node queries the JsonPath object for the String value.
        Assertions.assertEquals(postJson.get("complete").toString(), "true");
        //Validate the response
        Assertions.assertNotNull(postJson.get("petId"));
    }
    //Create test case by test annotation
    @Test
    @DisplayName("Post Method By Using Model")//Set the test case name by DisplayName annotation
    public void Test4() throws JsonProcessingException {

        //Assigning the elements of the pet object
        pet.setId(4);
        pet.setPetId(pet.getId());
        pet.setQuantity(1);
        pet.setShipDate(new Date());
        pet.setStatus("placed");
        pet.setComplete("true");

        //Converting the Object to JSONString
        String jsonString = objectMapper.writeValueAsString(pet);

        //Creating request object and the headers of type HttpEntity based on PetObject
        HttpEntity<Pet> request = new HttpEntity<>(pet, headers);

        //Creating a new resource by sending an object to the given URI template, using the RestTemplate postForObject method
        String resultAsJsonStr =
                restTemplate.postForObject(baseURI+"/store/order", request, String.class);

        //Deserialize JSON and create a tree of JsonNode instances with the ObjectMapper.readTree() method
        com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(resultAsJsonStr);

        //Checking valid or not
        Assertions.assertNotNull(resultAsJsonStr);
        Assertions.assertNotNull(root);
        //Checking if roots contain the id
        Assertions.assertNotNull(root.path("id").asText());
        //Checking whether the petId contained in the roots is equal to the id in the assigning object
        Assertions.assertEquals(root.path("petId").asInt(), pet.getPetId());
    }
    //Create test case by test annotation
    @Test
    @DisplayName("Get Order By Using Id")//Set the test case name by DisplayName annotation
    public void Test5() throws UnirestException{

        //**For Order1**
        //Unirest is a lightweight HTTP client library from Mashape
        Unirest.setTimeouts(0, 0);
        //HttpResponse describes the result of an HttpRequest call and get HTTP response body as a string
        HttpResponse<String> response = Unirest.get(baseURI+"/store/order/1")
                .queryString("petId", "1")
                .asString();

        //Checking response body is null or not
        Assertions.assertNotNull(response.getBody());
        //Checking response status code is okey(200) or not
        Assertions.assertEquals(200, response.getStatus());//expected-actual


    }
    //Create test case by test annotation
    @Test
    @DisplayName("Get Inventory Successfully")//Set the test case name by DisplayName annotation
    public void Test6() throws UnirestException {

        //Unirest is a lightweight HTTP client library from Mashape
        Unirest.setTimeouts(0, 0);
        //HttpResponse describes the result of an HttpRequest call and get HTTP response body as a string
        HttpResponse<String> response = Unirest.get(baseURI+"/store/inventory")
                .header("Content-Type","application/json")
                .asString();

        //Checking HttpStatus successful or not
        Assertions.assertNotEquals(false,HttpStatus.OK.is2xxSuccessful());
        //Comparing Http status code and Response status code
        Assertions.assertEquals(HttpStatus.OK.toString(), response.getStatus()+" "+response.getStatusText());//expected-actual
        //Checking response is it 200(ok) or not
        Assertions.assertEquals(200, response.getStatus());//expected-actual
        //Checking response body headers content type
        Assertions.assertEquals("[application/json]", response.getHeaders().get("Content-Type").toString());
        //Checking response body is null or not
        Assertions.assertNotNull(response.getBody());
    }

    //Create test case by test annotation
    @Test
    @DisplayName("Delete Method Successfully")//Set the test case name by DisplayName annotation
    public void Test7() throws UnirestException{

        //Unirest is a lightweight HTTP client library from Mashape
        Unirest.setTimeouts(0, 0);
        //HttpResponse describes the result of an HttpRequest call and get HTTP response body as a string
        HttpResponse<String> response = Unirest.delete(baseURI+"/store/order/3")
                .body("")
                .asString();

        //RequestSpecification is an interface that allows you to specify how the request will look like
        //Request object creating
        RequestSpecification httpRequest = given();

        // Request payload sending along with post request
        JSONObject requestParams = new JSONObject();
        //requestParams.put() overwrites the previous presence
        requestParams.put("id",3);
        //Setting request headers
        httpRequest.header("Content-Type","application/json");
        //Attach above data to the request
        httpRequest.body(requestParams.toString());
        //Converting response to string
        String responseBody = response.getBody();

        //status code validation
        Assertions.assertEquals(200, response.getStatus());
        //Checking response body is empty or not
        Assertions.assertFalse(responseBody.isEmpty());


    }
    //Create test case by test annotation
    @Test
    @DisplayName("Delete Method Successfully Test")//Set the test case name by DisplayName annotation
    public void Test8() {

        //RestAssured class to start creating response specifications with when() and then() methods
        response = given()
                .header("Content-Type","application/json")
                .when()
                .delete(baseURI+"/store/order/4")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("message", Matchers.equalTo("4"))
                .extract().response();

        //Checking status code 200 or not
        Assertions.assertEquals(200, response.statusCode());
        //Checking response body contains code header or not
        Assertions.assertNotEquals(false,response.getBody().asString().contains("code"));
        //Checking response body contains type header or not
        Assertions.assertNotEquals(false,response.getBody().asString().contains("type"));
        //Checking response body contains unknown or not
        Assertions.assertNotEquals(false,response.getBody().asString().contains("unknown"));
        //Checking response body contains order4 id or not
        Assertions.assertNotEquals(false,response.getBody().asString().contains("4"));


    }

}
