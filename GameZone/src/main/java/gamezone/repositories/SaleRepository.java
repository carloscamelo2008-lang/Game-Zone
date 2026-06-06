package gamezone.repositories;

import gamezone.entities.Sale;
import gamezone.entities.VideoGame;
import gamezone.entities.DigitalVideoGame;
import gamezone.entities.PhysicalVideoGame;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleRepository {

    private static final String FILE_PATH = "data/sales.json";
    private final VideoGameRepository videoGameRepository;

    public SaleRepository(VideoGameRepository videoGameRepository) {
        this.videoGameRepository = videoGameRepository;

        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try (FileWriter fw = new FileWriter(file)) {
                fw.write("[]");
            } catch (IOException e) {
                System.err.println("Error creating sales JSON: " + e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void save(Sale sale) {
        List<Sale> sales = findAll();
        sales.add(sale);
        writeAll(sales);
    }

    public List<Sale> findAll() {
        List<Sale> sales = new ArrayList<>();
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(FILE_PATH)) {
            JSONArray array = (JSONArray) parser.parse(reader);
            for (Object obj : array) {
                JSONObject json = (JSONObject) obj;
                Sale sale = deserialize(json);
                if (sale != null) sales.add(sale);
            }
        } catch (Exception e) {
            System.err.println("Error reading sales JSON: " + e.getMessage());
        }

        return sales;
    }

    public int count() {
        return findAll().size();
    }

    public double totalRevenue() {
        return findAll().stream().mapToDouble(Sale::getTotal).sum();
    }

    @SuppressWarnings("unchecked")
    private void writeAll(List<Sale> sales) {
        JSONArray array = new JSONArray();
        for (Sale sale : sales) {
            array.add(serialize(sale));
        }
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.write(array.toJSONString());
        } catch (IOException e) {
            System.err.println("Error writing sales JSON: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private JSONObject serialize(Sale sale) {
        JSONObject json = new JSONObject();
        json.put("id",         sale.getId());
        json.put("gameTitle",  sale.getVideoGame().getTitle());
        json.put("quantity",   (long) sale.getQuantity());
        json.put("unitPrice",  sale.getUnitPrice());
        json.put("total",      sale.getTotal());
        json.put("saleDate",   sale.getSaleDate().toString());
        return json;
    }

    private Sale deserialize(JSONObject json) {
        try {
            String id         = (String) json.get("id");
            String gameTitle  = (String) json.get("gameTitle");
            int    quantity   = (int) (long) json.get("quantity");
            double unitPrice  = toDouble(json.get("unitPrice"));
            double total      = toDouble(json.get("total"));
            String dateStr    = (String) json.get("saleDate");

            VideoGame game = videoGameRepository.findByTitle(gameTitle);
            if (game == null) {
                game = new DigitalVideoGame(gameTitle, unitPrice, "N/A", 0, "N/A", 0, "N/A");
            }

            return Sale.restore(id, game, quantity, unitPrice, total,
                                LocalDateTime.parse(dateStr));
        } catch (Exception e) {
            System.err.println("Error deserializing sale: " + e.getMessage());
            return null;
        }
    }

    private double toDouble(Object value) {
        if (value instanceof Long)   return ((Long)   value).doubleValue();
        if (value instanceof Double) return (Double)  value;
        return Double.parseDouble(value.toString());
    }
}
