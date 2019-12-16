package com.thoughtworks.spree.api.test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BaseTest {
    public static String auth_token;
    @Test
    public void testStatusCode() {
        given().get("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/products").then().statusCode(200);
    }

    @Test
    public void testLogging() {
        given().log().all().get("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/products");
    }

    @Test
    public void printResponse() {
        Response resp = given().when().log().all().get("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/products");
        //System.out.println("Response String >>>>>>"+resp.asString());
        System.out.println("Response String >>>>>>"+resp.prettyPrint());
    }

    @Test
    public void testCurrencyValue() {
        given().get("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/products")
        .then()
        .body("data[0].attributes.currency",equalTo("USD"));
    }

    @Test
    public void testProductCountPerPage() {
        List<Map> prod = given().get("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/products/?page=1")
                .body().jsonPath().getList("data");

        Assert.assertEquals(16,prod.size());
        System.out.println(prod.size());
        for(Map m:prod) {
            System.out.println("Product Details is==========>"+m);
        }
    }

    @Test
    public void testProductDetails() {
        JsonPath path = given().get("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/products/?page=1").body()
                .jsonPath();

        List<Map> products = path.getList("data");

        for(Map m:products) {
            Map prod = (Map)m.get("attributes");
           // System.out.println(m.get("attributes"));
            Assert.assertTrue(prod.get("currency").equals("USD"));
        }
    }

    @Test
    public void testFilterProdByName() {
        Response resp = given().log().all().queryParam("filter[name]", "bag")
                .get("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/products");

        System.out.println("Products filtered by name :::::"+resp.prettyPrint());

    }

    @Test
    public void testFilterProdByIds() {
        Response resp = given().log().all().queryParam("filter[ids]", 2)
                .get("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/products");

        System.out.println("Products filtered by Id :::::"+resp.prettyPeek());

    }

    @Test
    public void testFilterProdBySKUs() {
        Response resp = given().log().all().queryParam("filter[skus]", "bag")
                .get("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/products");

        System.out.println("Products filtered by sku :::::"+resp.prettyPrint());

    }

    @Test
    public void testFilterProdByPrice() {
        Response resp = given().log().all().queryParam("filter[price]", 1.99 - 19.99)
                .get("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/products");

        System.out.println("Products filtered by name :::::"+resp.prettyPrint());
    }

    @Test
    public void testFilterProdByOptions() {
        Response resp = given().log().all().queryParam("filter[options][tshirt-color]", "s","Red")
                .get("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/products");

        System.out.println("Products filtered by name :::::"+resp.jsonPath().prettify());
    }

    @BeforeClass
    public void authToken() {
        Response resp = given().
                formParam("grant_type","password")
                .formParam("username","test123@gmail.com")
                .formParam("password","test123")
                .post("https://spree-vapasi-prod.herokuapp.com/spree_oauth/token");
        auth_token = "Bearer "+resp.path("access_token");
        System.out.println("Auth Token is :::::::"+auth_token);
        System.out.println("Auth Token is ::"+resp.jsonPath().prettify());
    }

    @Test
    public void addItemToCart() {
        Map<String, String> headersList = new HashMap<String, String>();
        headersList.put("Content-Type","application/json");
        headersList.put("Authorization", auth_token);

        String createBody = "{\n" +
                "  \"variant_id\": \"17\",\n" +
                "  \"quantity\": 5\n" +
                "}";
        Response resp = given().headers(headersList).body(createBody)
                        .when()
                        .post("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/cart/add_item");
        Assert.assertEquals(resp.statusCode(),200);

    }

    //Add item to the cart first and then remove
    @Test(dependsOnMethods = "addItemToCart")
    public void removeItemFromCart() {


    }

}
