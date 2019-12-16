package com.thoughtworks.spree.java;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CollectionExamples {
    private static List<String> str;
    @Test
    public static void testArrayList() {
        str = new ArrayList<String>();
        System.out.println("Check whether list is empty :::"+str.isEmpty());
        str.add("Google");
        str.add("Amazon");
        str.add("Microsoft");
        str.add("Service Now");
        str.add("Qualcomm");
        str.add("DBS");

        System.out.println("Size of the array"+str.size());
       str.forEach((name) ->{
           System.out.println("name is ::::"+name);
       });

        for (String name: str) {
              System.out.println("Companies are ::"+name);
        }
    }

    @Test
    public void testLinkedList() {
        List<String> humanSpecies = new LinkedList<String>();
        humanSpecies.add("Home Sapiens");
        humanSpecies.add("Homo Erectus");
        humanSpecies.add("home habits");

        Iterator<String> it = ((LinkedList<String>) humanSpecies).descendingIterator();
        while (it.hasNext()){
            System.out.println(it.next());
        }
    }
}
