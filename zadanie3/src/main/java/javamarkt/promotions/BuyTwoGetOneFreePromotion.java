package javamarkt.promotions;

import javamarkt.Product;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BuyTwoGetOneFreePromotion implements javamarkt.promotions.PromotionStrategy {

    @Override
    public List<Product> apply(List<Product> products) {

        // jesli produktow jest mniej niż 3, nic nie robimy
        if (products.size() < 3) {
            return products;
        }

        List<Product> result = new ArrayList<>(products);

        // sortowanie skopiowanej listy po cenie z ewentualną promocją rosnąco
        result.sort(Comparator.comparingDouble(Product::getDiscountPrice));

        // wyciagniecie najtanszego produktu (pozycja 0)
        // zerowanie jego ceny i wstawienie z powrotem na pozycję 0
        Product cheapest = result.get(0);
        result.set(0, cheapest.withDiscountPrice(0.0));

        return result;
    }
}