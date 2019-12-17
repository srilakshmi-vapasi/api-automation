package com.thoughtworks.spree.api.test;

import com.thoughtworks.spree.api.test.dataproviders.ProductIdsList;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class SpreeAPITestCollection {
    private static String auth_token;
    private static final int productId = 5;
    private static List<String> lineItemIds;
    private static Map<String, String> headersList;

    //get authtoken to post requests to the server
    //Assignment Tests
    @BeforeClass
    public void getAuthToken() {
        Response resp = given().
                formParam("grant_type","password")
                .formParam("username","test123@gmail.com")
                .formParam("password","test123")
                .post("https://spree-vapasi-prod.herokuapp.com/spree_oauth/token");

        auth_token = "Bearer "+resp.path("access_token");

        System.out.println("Auth Token is ::"+resp.jsonPath().prettify());
    }

    @BeforeClass
    public void getHeadersList() {
        headersList = new HashMap<String, String>();
        headersList.put("Content-Type","application/json");
        headersList.put("Authorization", auth_token);
    }

    /*
    Count number of products per page.
    Get the map <meta> which has count of list of products displaying on the page
     */
    @Test
    public void testProductCountPerPage() {
        Map count = given().get("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/products/?page=1")
                .body().jsonPath().getMap("meta");

        //Both the values are same in the application
        System.out.println(":::::Product count per page :::::::"+count.get("count"));
        System.out.println("***** Product total count ******"+count.get("total_count"));

        Assert.assertEquals(16,count.get("count"));
        Assert.assertEquals(16,count.get("total_count"));
    }
    /*
      Filter products by productID
      Verify total number of products count displayed in the response
      Verify expected product id details are displayed in the response
     */
    @Test
    public void testProdById() {
        Response resp = given().log().all().queryParam("filter[ids]", productId)
                .get("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/products");

        //System.out.println("Products filtered by Id :::::"+resp.jsonPath().prettify());

        Map fileteredProd = resp.body().jsonPath().getMap("meta");
        //Product count should be 1
        Assert.assertEquals(1,fileteredProd.get("count"));
        List<Map> prodId = resp.body().jsonPath().getList("data");

        //Product Id passed in the query param should match with Product Id in the response
        //Typecast product id captured from the response and validate the value.
        for(Map prodIdMap : prodId) {
            System.out.println("Product id is >>>>>"+prodIdMap.get("id"));
            Assert.assertEquals(productId, Integer.parseInt((String)(prodIdMap.get("id"))));
        }
    }
    /*
    test create cart
    send a post request
    verify status code
     */
    @Test
    public void testCreateCart() {
        Response resp = given().log().all()
                .post("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/cart");

        System.out.println("Cart created successfully:::::"+resp.getStatusCode());
        Assert.assertEquals(201, resp.getStatusCode());

        System.out.println("Cart details ...."+resp.jsonPath().prettify());

        System.out.println("Product type is ....."+resp.jsonPath().getMap("data").get("type"));
        Assert.assertEquals("cart", resp.jsonPath().getMap("data").get("type"));

    }
    /*
    Add products to the cart
    Read productId, product count from ProductIdsList data provider class
    Pass the parameters to this method and send in the post request
     */
    @Test(dataProvider = "ProductIdsList", dataProviderClass = ProductIdsList.class)
    public void addItemToCart(int variant, int qty) {
        System.out.println("variant id :"+variant+"  quantity ::"+qty);

        JSONObject requestParams = new JSONObject();
        requestParams.put("variant_id", variant);
        requestParams.put("quantity", qty);


        Response resp = given().log().all().headers(headersList).body(requestParams.toString())
                .when()
                .post("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/cart/add_item");
        Assert.assertEquals(resp.statusCode(),200);
    }

    /*
    View cart , get the products list
    Read a product id from the response
    Add list of product ids from the response to ArrayList
     */
    @Test
    public void getCart() {
        Response resp = given().log().all().headers(headersList)
                .get("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/cart/");

        Assert.assertEquals(resp.statusCode(), 200);

        System.out.println("Products in the cart are ::::" + resp.jsonPath().prettify());

        Map rel = (Map) resp.jsonPath().getMap("data").get("relationships");
        List<Map> lineItems = (List<Map>) ((Map) rel.get("line_items")).get("data");

        lineItemIds = new ArrayList<String>();
        for (Map m : lineItems) {
            System.out.println("Line item id added in the cart is ::::" + m.get("id"));
            lineItemIds.add(m.get("id").toString());
        }

    }
    /*
    Delete a product from the cart
    Verify response code is 200
     */
    @Test(dependsOnMethods = "getCart")
     public void testRemoveLineItemFromCart() {
        if(lineItemIds.size()>0){
            String url = "https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/cart/remove_line_item/"+lineItemIds.get(0);

            System.out.println("Product ID to remove from the cart is >>>>>"+lineItemIds.get(0));

            Response res = given().log().all().headers(headersList).delete(url);

            Assert.assertEquals(res.statusCode(),200);

            System.out.println("Products in the cart after delete operation are ::::"+res.jsonPath().prettify());
        }

    }

    @Test(dependsOnMethods = {"getCart","testRemoveLineItemFromCart"})
    public void testEmptyCart() {
        if(lineItemIds.size()>0){
            Response res = given().log().all().headers(headersList)
                    .patch("https://spree-vapasi-prod.herokuapp.com/api/v2/storefront/cart/empty");

            Assert.assertEquals(res.statusCode(),200);

            /*Map<String, String> products = (Map)res.body().jsonPath().getMap("data").get("attributes");

            System.out.println("Product count in the cart is :::"+products.get("item_count"));

            Assert.assertEquals(0,Integer.parseInt(products.get("item_count")));*/

            System.out.println("Removed all the products from the Cart ::::"+res.body().jsonPath().prettify());
        } else {
            System.out.println("No products has been added to the cart to remove");
        }

    }
}
