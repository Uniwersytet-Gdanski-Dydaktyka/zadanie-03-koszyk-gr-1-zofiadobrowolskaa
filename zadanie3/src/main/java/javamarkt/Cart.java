package javamarkt;

import javamarkt.promotions.PromotionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Cart {

    private List<Product> products;

    public Cart(List<Product> products) {
        // jesli to null tworzymy bezpieczne puste pudelko
        // w przeciwnym razie robimy kopie oryginalnej listy zeby nie psuc orygnialu
        this.products = products == null ? new ArrayList<>() : new ArrayList<>(products);

        // czyscimy liste z ewentualnych nulli przemyconych w srodku
        this.products.removeIf(p -> p == null);
    }

    public List<Product> getProducts() {
        // blokada zeby nikt z zewnatrz nie zmienil zawartosci naszego koszyka
        return Collections.unmodifiableList(products);
    }

    public double getTotalPrice() {
        double total = 0.0;
        for (Product p : products) {
            total += p.getDiscountPrice();
        }
        return total;
    }

    public Product getCheapest() {
        if (products.isEmpty()) {
            return null;
        }
        return Collections.min(products, Comparator.comparingDouble(Product::getDiscountPrice));
    }

    public Product getMostExpensive() {
        if (products.isEmpty()) {
            return null;
        }
        return Collections.max(products, Comparator.comparingDouble(Product::getDiscountPrice));
    }

    public List<Product> getNCheapest(int n) {
        List<Product> copy = new ArrayList<>(products);

        // sortowanie od najtanszego do najdrozszego
        copy.sort(Comparator.comparingDouble(Product::getDiscountPrice));

        // zabezpieczenie by nie pobrac wiecej elementow niz w ogole mamy
        int limit = Math.min(n, copy.size());

        return copy.subList(0, limit);
    }

    public List<Product> getNMostExpensive(int n) {
        List<Product> copy = new ArrayList<>(products);

        // sortowanie od najdrozszego do najtanszego
        copy.sort(Comparator.comparingDouble(Product::getDiscountPrice).reversed());

        // zabezpieczenie by nie pobrac wiecej elementow niz w ogole mamy
        int limit = Math.min(n, copy.size());

        return copy.subList(0, limit);
    }

    public void defaultSort() {
        // uzycie domyslnego sortowania z klasy Product
        Collections.sort(products);
    }

    // otwarcie na sortowanie po dowolnych kryteriach w przyszlosci
    public void customSort(Comparator<Product> comparator) {
        // mozliwosc narzucenia wlasnego sposobu sortowania
        products.sort(comparator);
    }

    public void applyPromotion(PromotionStrategy strategy) {
        if (!products.isEmpty() && strategy != null) {
            this.products = strategy.apply(this.products);
        }
    }

    // zadanie dodatkowe
    // testowanie roznych kolejnosci promocji, szukanie najlepszego sposobu
    public void applyBestPromotions(List<PromotionStrategy> strategies) {
        if (products.isEmpty() || strategies == null || strategies.isEmpty()) {
            return;
        }

        // generowanie wszystkich mozliwych kombinacji kolejnosci
        List<List<PromotionStrategy>> permutations = generatePermutations(strategies);

        List<Product> bestState = this.products;
        double bestPrice = Double.MAX_VALUE;

        // sprawdzanie kazdej kombinacji
        for (List<PromotionStrategy> permutation : permutations) {

            List<Product> tempState = new ArrayList<>(this.products);

            // nakladanie promocji w aktualnej kolejnosci
            for (PromotionStrategy strategy : permutation) {
                tempState = strategy.apply(tempState);
            }

            // koszt koszyka w danym wariancie
            double tempTotalPrice = 0.0;
            for (Product p : tempState) {
                tempTotalPrice += p.getDiscountPrice();
            }

            // jesli znalezlismy tanszy wariant to zapamietujemy ten stan
            if (tempTotalPrice < bestPrice) {
                bestPrice = tempTotalPrice;
                bestState = tempState;
            }
        }

        // na koniec zastepujemy koszyk z tym najtanszym wariantem
        this.products = bestState;
    }

    // generowanie wszystkich mozliwych ukladanek
    private List<List<PromotionStrategy>> generatePermutations(List<PromotionStrategy> original) {

        // warunek zakonczenia kiedy nie ma juz co mieszac
        if (original.isEmpty()) {
            // glowne pudelko na gotowe kombinacje
            List<List<PromotionStrategy>> result = new ArrayList<>();

            // wkladamy do srodka jedna pusta liste jako fundament do doklejania
            result.add(new ArrayList<>());

            // zwracamy ten fundament do wczesniejszego etapu programu
            return result;
        }

        List<PromotionStrategy> copyList = new ArrayList<>(original);

        PromotionStrategy firstElement = copyList.remove(0);

        // tworzymy docelowe duze pudelko na gotowe wymieszane listy
        List<List<PromotionStrategy>> returnValue = new ArrayList<>();

        // kazemy programowi wymieszac cala reszte ktora zostala
        List<List<PromotionStrategy>> permutations = generatePermutations(copyList);

        // bierzemy po kolei kazda mala wymieszana liste ktora zwrocila funkcja
        for (List<PromotionStrategy> smallerPermutated : permutations) {

            // sprawdzamy kazde wolne miejsce w tej malej liscie wlacznie z poczatkiem i koncem
            for (int index = 0; index <= smallerPermutated.size(); index++) {

                // robimy robocza kopie tej malej listy
                List<PromotionStrategy> temp = new ArrayList<>(smallerPermutated);

                // wciskamy w sprawdzane wolne miejsce element ktory wyciagnelismy
                temp.add(index, firstElement);

                // dorzucamy gotowa i dluzsza ukladanke do glownego pudelka
                returnValue.add(temp);
            }
        }

        // zwracamy glowne pudelko ze wszystkimi nowymi ukladankami
        return returnValue;
    }
}