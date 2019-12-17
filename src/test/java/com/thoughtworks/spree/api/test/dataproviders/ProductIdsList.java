package com.thoughtworks.spree.api.test.dataproviders;

import org.testng.annotations.DataProvider;

public class ProductIdsList {

        @DataProvider(name = "ProductIdsList")
        public static Object[][] productIdsList() {
            return new Object[][] {
                    { 4, 2 },
                    { 12, 3 },
                    { 13, 4 }
            };
        }
}
