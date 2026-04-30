package javamarkt.promotions;

import javamarkt.Product;

import java.util.ArrayList;
import java.util.List;

public class CouponDiscountPromotion implements PromotionStrategy {

    // kod wybranego produktu na ktory dziala kupon
    private final String targetProductCode;

    // konstruktor wymusza podanie kodu przy tworzeniu promocji
    public CouponDiscountPromotion(String targetProductCode) {
        this.targetProductCode = targetProductCode;
    }

    @Override
    public List<Product> apply(List<Product> products) {

        List<Product> result = new ArrayList<>();
        boolean isUsed = false;

        for (Product p : products) {

            // jesli kod produktu sie zgadza i kuponu jeszcze nie uzylismy
            if (!isUsed && p.getCode().equals(targetProductCode)) {

                double newPrice = p.getDiscountPrice() * 0.70;

                // wrzucamy do pudelka przeceniony produkt
                result.add(p.withDiscountPrice(newPrice));

                isUsed = true;

            } else {
                result.add(p);
            }
        }
        return result;
    }
}