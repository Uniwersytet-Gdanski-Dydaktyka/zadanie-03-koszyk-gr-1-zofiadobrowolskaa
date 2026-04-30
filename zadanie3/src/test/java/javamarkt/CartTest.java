package javamarkt;

import javamarkt.promotions.BuyTwoGetOneFreePromotion;
import javamarkt.promotions.CouponDiscountPromotion;
import javamarkt.promotions.FreeMugPromotion;
import javamarkt.promotions.PromotionStrategy;
import javamarkt.promotions.ValueDiscountPromotion;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CartTest {

    @Test
    void testEmptyCartEdgeCase() {
        // testowanie sytuacji brzegowej pusty koszyk
        Cart cart = new Cart(null);
        assertNull(cart.getCheapest());
        assertEquals(0.0, cart.getTotalPrice());

        // aplikowanie promocji na pusty koszyk nie rzuca bledu
        cart.applyPromotion(new ValueDiscountPromotion());
        assertEquals(0, cart.getProducts().size());
    }

    @Test
    void testZeroPriceProduct() {
        // test produktu z cena zerowa co bylo w wymaganiach
        Product p1 = new Product("1", "gratis", 0.0);
        Cart cart = new Cart(Arrays.asList(p1));

        assertEquals(0.0, cart.getTotalPrice());
    }

    @Test
    void testValueDiscountPromotion() {
        // ponad 300 zl daje 5 procent znizki
        List<Product> products = Arrays.asList(
                new Product("P1", "laptop", 350.0)
        );
        Cart cart = new Cart(products);
        cart.applyPromotion(new ValueDiscountPromotion());

        assertEquals(332.5, cart.getTotalPrice());
    }

    @Test
    void testBuyTwoGetOneFreePromotion() {
        // trzeci najtanszy produkt za darmo
        List<Product> products = Arrays.asList(
                new Product("P1", "gra", 100.0),
                new Product("P2", "pad", 200.0),
                new Product("P3", "kabel", 50.0)
        );
        Cart cart = new Cart(products);
        cart.applyPromotion(new BuyTwoGetOneFreePromotion());

        assertEquals(300.0, cart.getTotalPrice());
    }

    @Test
    void testCouponDiscountPromotion() {
        // sprawdzamy czy kupon dziala tylko raz na dany produkt
        List<Product> products = Arrays.asList(
                new Product("P1", "myszka", 100.0),
                new Product("P2", "klawiatura", 200.0),
                new Product("P2", "klawiatura", 200.0)
        );
        Cart cart = new Cart(products);

        // znizka 30 procent na produkt o kodzie P2
        cart.applyPromotion(new CouponDiscountPromotion("P2"));

        // pierwsza klawiatura tanieje druga nie wiec suma to 440
        assertEquals(440.0, cart.getTotalPrice());
    }

    @Test
    void testFreeMugPromotion() {
        // test darmowego kubka przy zakupach powyzej 200 zl
        List<Product> products = Arrays.asList(
                new Product("P1", "klawiatura", 250.0)
        );
        Cart cart = new Cart(products);
        cart.applyPromotion(new FreeMugPromotion());

        // darmowy kubek wchodzi do koszyka wiec sa w nim dwa przedmioty
        assertEquals(2, cart.getProducts().size());
        assertEquals(250.0, cart.getTotalPrice());
    }

    @Test
    void testBestPromotionCombination() {
        // test wyboru optymalnej kolejnosci dwoch promocji
        List<Product> products = Arrays.asList(
                new Product("P1", "myszka", 150.0),
                new Product("P2", "klawiatura", 250.0),
                new Product("P3", "podkladka", 50.0)
        );
        Cart cart = new Cart(products);

        List<PromotionStrategy> strategies = Arrays.asList(
                new BuyTwoGetOneFreePromotion(),
                new ValueDiscountPromotion()
        );

        cart.applyBestPromotions(strategies);

        // najlepsza wersja to najpierw 2 plus 1 potem 5 procent
        assertEquals(380.0, cart.getTotalPrice());
    }

    @Test
    void testSortingLogic() {
        // test domyslnego sortowania cena malejaco nazwa rosnaco
        Product p1 = new Product("1", "z", 100.0);
        Product p2 = new Product("2", "a", 100.0);
        Product p3 = new Product("3", "b", 150.0);

        Cart cart = new Cart(Arrays.asList(p1, p2, p3));
        cart.defaultSort();

        assertEquals("b", cart.getProducts().get(0).getName());
        // remis kwotowy wygrywa litera a przed z
        assertEquals("a", cart.getProducts().get(1).getName());
    }

    @Test
    void testCustomSort() {
        // test wlasnego sortowania tylko po nazwie
        Product p1 = new Product("1", "z", 10.0);
        Product p2 = new Product("2", "a", 100.0);
        Cart cart = new Cart(Arrays.asList(p1, p2));

        cart.customSort(Comparator.comparing(Product::getName));

        assertEquals("a", cart.getProducts().get(0).getName());
    }

    @Test
    void testFindingProducts() {
        // testowanie metod do szukania konkretnych produktow
        Product p1 = new Product("1", "tani", 10.0);
        Product p2 = new Product("2", "sredni", 50.0);
        Product p3 = new Product("3", "drogi", 100.0);
        Cart cart = new Cart(Arrays.asList(p1, p2, p3));

        assertEquals("tani", cart.getCheapest().getName());
        assertEquals("drogi", cart.getMostExpensive().getName());

        List<Product> twoCheapest = cart.getNCheapest(2);
        assertEquals(2, twoCheapest.size());
        assertEquals("tani", twoCheapest.get(0).getName());

        List<Product> twoMostExpensive = cart.getNMostExpensive(2);
        assertEquals(2, twoMostExpensive.size());
        assertEquals("drogi", twoMostExpensive.get(0).getName());
    }

    @Test
    void testValueDiscountNotAppliedWhenPriceTooLow() {
        // test czy program nie daje rabatu 5 procent gdy koszyk jest ponizej 300 zl
        List<Product> products = Arrays.asList(
                new Product("P1", "myszka", 299.0)
        );
        Cart cart = new Cart(products);
        cart.applyPromotion(new ValueDiscountPromotion());

        // cena musi zostac bez zmian
        assertEquals(299.0, cart.getTotalPrice());
    }

    @Test
    void testBuyTwoGetOneFreeNotEnoughProducts() {
        // test czy program nie wybucha gdy odpalamy 2 plus 1 a w koszyku sa tylko 2 rzeczy
        List<Product> products = Arrays.asList(
                new Product("P1", "gra", 100.0),
                new Product("P2", "pad", 200.0)
        );
        Cart cart = new Cart(products);
        cart.applyPromotion(new BuyTwoGetOneFreePromotion());

        // brak darmowego produktu cena zostaje normalna
        assertEquals(300.0, cart.getTotalPrice());
    }

    @Test
    void testCouponDiscountProductNotInCart() {
        // test czy kupon ignoruje koszyk jesli nie ma w nim pasujacego produktu
        List<Product> products = Arrays.asList(
                new Product("P1", "myszka", 100.0)
        );
        Cart cart = new Cart(products);

        // probujemy obnizyc cene produktu ktorego nie ma
        cart.applyPromotion(new CouponDiscountPromotion("P99"));

        // cena musi zostac bazowa
        assertEquals(100.0, cart.getTotalPrice());
    }

    @Test
    void testGetNCheapestMoreThanAvailable() {
        // test zabezpieczenia math min gdy prosimy o 5 produktow a sa tylko 2
        Product p1 = new Product("1", "tani", 10.0);
        Product p2 = new Product("2", "drogi", 100.0);
        Cart cart = new Cart(Arrays.asList(p1, p2));

        List<Product> result = cart.getNCheapest(5);

        // program ma bezpiecznie zwrocic tylko 2 istniejace elementy
        assertEquals(2, result.size());
    }

    @Test
    void testNullInsideCollection() {
        // test zachowania gdy ktos wrzuci nulla miedzy poprawne produkty
        Product p1 = new Product("1", "myszka", 100.0);
        Cart cart = new Cart(Arrays.asList(p1, null));

        // koszyk powinien zignorowac nulla i widziec tylko jeden produkt
        assertEquals(1, cart.getProducts().size());
        assertEquals(100.0, cart.getTotalPrice());
    }
}