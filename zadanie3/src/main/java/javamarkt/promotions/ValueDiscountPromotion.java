package javamarkt.promotions;

import javamarkt.Product;

import java.util.ArrayList;
import java.util.List;

public class ValueDiscountPromotion implements javamarkt.promotions.PromotionStrategy {

    @Override
    public List<Product> apply(List<Product> products) {

        double total = 0.0;
        for (Product p : products) {
            total += p.getDiscountPrice();
        }

        if (total <= 300.0) {
            return products;
        }

        // utworzenie nowej listy na przecenione produkty
        List<Product> discountedProducts = new ArrayList<>();

        // zastosowanie zniżki 5% na każdy produkt
        for (Product p : products) {
            double newPrice = p.getDiscountPrice() * 0.95;
            discountedProducts.add(p.withDiscountPrice(newPrice));
        }

        return discountedProducts;
    }
}