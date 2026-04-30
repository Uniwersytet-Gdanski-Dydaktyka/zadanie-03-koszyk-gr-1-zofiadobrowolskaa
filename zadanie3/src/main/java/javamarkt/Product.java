package javamarkt;

import java.util.Objects;

// implementacja interfejsu comparable pozwala na domyslne sortowanie produktow
public class Product implements Comparable<Product> {

    // wybralam niemutowalnosc aby chronic oryginalne produkty przed przypadkowa zmiana
    // dzieki temu podczas naliczania promocji tworzona jest zawsze nowa bezpieczna kopia
    // finalne pola gwarantuja niemutowalnosc
    private final String code;
    private final String name;
    private final double price;
    private final double discountPrice;

    // glowny konstruktor do tworzenia bazowych produktow przed rabatami
    public Product(String code, String name, double price) {

        if (price < 0) {
            throw new IllegalArgumentException("Cena nie moze byc ujemna");
        }
        this.code = code;
        this.name = name;
        this.price = price;
        // na start cena z rabatem to cena podstawowa
        this.discountPrice = price;
    }

    // prywatny konstruktor uzywany tylko do kopiowania produktu
    private Product(String code, String name, double price, double discountPrice) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.discountPrice = discountPrice;
    }

    // metoda tworzaca nowa kopie produktu z podana nowa cena
    public Product withDiscountPrice(double newDiscountPrice) {
        return new Product(this.code, this.name, this.price, newDiscountPrice);
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getDiscountPrice() { return discountPrice; }

    // nadpisana metoda z interfejsu comparable ustalajaca zasady sortowania
    @Override
    public int compareTo(Product other) {
        if (other == null) return 1;

        // sortowanie malejaco wedlug ceny
        int priceComparison = Double.compare(other.price, this.price);
        if (priceComparison != 0) {
            return priceComparison;
        }
        // w razie remisu sortowanie alfabetycznie po nazwie
        return this.name.compareToIgnoreCase(other.name);
    }

    // metoda sprawdzajaca czy dwa produkty sa takie same na podstawie ich kodu
    @Override
    public boolean equals(Object o) {

        // ten sam obiekt w pamieci -> prawda
        if (this == o) return true;

        // obiekt nie istnieje / jest innej klasy -> falsz
        if (o == null || getClass() != o.getClass()) return false;

        // jesli to na pewno produkt to narzucamy mu ten typ
        Product product = (Product) o;

        // porownanie kodow
        return Objects.equals(code, product.code);
    }

    // metoda generujaca id na podstawie kodu
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}