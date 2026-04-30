package javamarkt.promotions;

import javamarkt.Product;

import java.util.ArrayList;
import java.util.List;

public class FreeMugPromotion implements PromotionStrategy {

    @Override
    public List<Product> apply(List<Product> products) {

        double total = 0.0;
        boolean hasMug = false;

        for (Product p : products) {
            total += p.getDiscountPrice();

            // sprawdzenie czy ten produkt to nasz kubek
            if (p.getCode().equals("MUG-001")) {
                hasMug = true;
            }
        }

        // nowe pudelko na produkty by nie psuc oryginalu
        List<Product> result = new ArrayList<>(products);

        // dodanie darmowego kubek jesli suma przekracza 200 i kubka jeszcze nie ma
        if (total > 200.0 && !hasMug) {
            result.add(new Product("MUG-001", "Firmowy Kubek", 0.0));
        }

        return result;
    }
}