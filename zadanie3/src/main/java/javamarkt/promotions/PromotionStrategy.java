package javamarkt.promotions;

import javamarkt.Product;
import java.util.List;

// wzorzec Strategy: pozwala na latwe dodawanie nowych promocji w przyszlosci
// bez modyfikowania głównego kodu koszyka (zasada open/closed)

public interface PromotionStrategy {
    // metoda aplikujaca promocje na liscie produktow
    List<Product> apply(List<Product> products);
}