package com.thoughtworks.spree.api.test.dataproviders;

import org.testng.annotations.DataProvider;

public class AddProductsDataProvider {

        @DataProvider(name = "provideNumbers")
        public Object[][] provideData() {

            return new Object[][] {
                    { 10, 20 },
                    { 100, 110 },
                    { 200, 210 }
            };
        }
}
