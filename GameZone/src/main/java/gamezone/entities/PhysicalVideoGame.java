package gamezone.entities;

import gamezone.services.Sellable;
import gamezone.services.Displayable;

public class PhysicalVideoGame extends VideoGame implements Sellable, Displayable {

    private String condition;
    private String distributor;

    public PhysicalVideoGame(String title, double price, String platform,
                             int stock, String genre,
                             String condition, String distributor) {
        super(title, price, platform, stock, genre);
        this.condition   = condition;
        this.distributor = distributor;
    }

    public String getCondition()   { return condition; }
    public String getDistributor() { return distributor; }

    public void setCondition(String condition)     { this.condition   = condition; }
    public void setDistributor(String distributor) { this.distributor = distributor; }

    @Override
    public double calculateFinalPrice() {
        return "usado".equalsIgnoreCase(condition) ? price * 0.75 : price;
    }

    @Override
    public double sell(int qty) {
        this.stock -= qty;
        return calculateFinalPrice() * qty;
    }

    @Override
    public String getDisplayInfo() {
        return String.format(
            "📦 [FÍSICO] %s | Plataforma: %s | Género: %s | Condición: %s | Distribuidor: %s | Precio final: $%.2f | Stock: %d",
            title, platform, genre, condition, distributor, calculateFinalPrice(), stock
        );
    }

    @Override
    public Object[] toTableRow() {
        return new Object[]{
            title,
            platform,
            genre,
            condition,
            distributor,
            String.format("$%.2f", calculateFinalPrice()),
            stock,
            "Físico"
        };
    }

    @Override
    public String toString() {
        return "PhysicalVideoGame{" +
                "title='"       + title       + '\'' +
                ", price="      + price       +
                ", platform='"  + platform    + '\'' +
                ", stock="      + stock       +
                ", genre='"     + genre       + '\'' +
                ", condition='" + condition   + '\'' +
                ", distributor='" + distributor + '\'' +
                '}';
    }
}
