package gamezone.entities;

import gamezone.services.Sellable;
import gamezone.services.Displayable;

public class DigitalVideoGame extends VideoGame implements Sellable, Displayable {

    private double sizeGB;
    private String downloadPlatform;

    public DigitalVideoGame(String title, double price, String platform,
                            int stock, String genre,
                            double sizeGB, String downloadPlatform) {
        super(title, price, platform, stock, genre);
        this.sizeGB           = sizeGB;
        this.downloadPlatform = downloadPlatform;
    }

    public double getSizeGB()           { return sizeGB; }
    public String getDownloadPlatform() { return downloadPlatform; }

    public void setSizeGB(double sizeGB)                   { this.sizeGB           = sizeGB; }
    public void setDownloadPlatform(String downloadPlatform) { this.downloadPlatform = downloadPlatform; }

    @Override
    public double calculateFinalPrice() {
        return sizeGB > 50 ? price + 5000 : price;
    }

    @Override
    public double sell(int qty) {
        this.stock -= qty;
        return calculateFinalPrice() * qty;
    }

    @Override
    public String getDisplayInfo() {
        return String.format(
            "🎮 [DIGITAL] %s | Plataforma: %s | Género: %s | Tamaño: %.1f GB | Precio final: $%.2f | Stock: %d",
            title, platform, genre, sizeGB, calculateFinalPrice(), stock
        );
    }

    @Override
    public Object[] toTableRow() {
        return new Object[]{
            title,
            platform,
            genre,
            String.format("%.1f GB", sizeGB),
            downloadPlatform,
            String.format("$%.2f", calculateFinalPrice()),
            stock,
            "Digital"
        };
    }

    @Override
    public String toString() {
        return "DigitalVideoGame{" +
                "title='"              + title            + '\'' +
                ", price="             + price            +
                ", platform='"         + platform         + '\'' +
                ", stock="             + stock            +
                ", genre='"            + genre            + '\'' +
                ", sizeGB="            + sizeGB           +
                ", downloadPlatform='" + downloadPlatform + '\'' +
                '}';
    }
}
