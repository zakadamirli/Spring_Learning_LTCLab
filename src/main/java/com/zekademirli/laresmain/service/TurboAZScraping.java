package com.zekademirli.laresmain.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class TurboAZScraping {
    public static void main(String[] args) {
        try {
            Document document = Jsoup.connect("https://turbo.az/").get();
            Elements elements = document.getElementsByClass("products-i");

            for (Element element : elements) {
                String name = element.select(".products-i__name").text();
                String price = element.select(".products-i__price").text();
                Elements attributes = element.getElementsByClass("products-i__bottom-text");

                String desc = !attributes.isEmpty() ? attributes.get(2).text() : "Not found";

                System.out.println("Name: " + name);
                System.out.println("Price: " + price);

                String[] descParts = desc.split(",");
                if (descParts.length > 2) {
                    System.out.println("Engine: " + descParts[1]);
                    System.out.println("Mile: " + descParts[2]);
                } else {
                    System.out.println("Engine or Mile info not available.");
                }

                System.out.println("-----------------------------------");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage()
            );
        }
    }
}