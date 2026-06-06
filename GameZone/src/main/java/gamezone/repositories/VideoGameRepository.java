package gamezone.repositories;

import gamezone.entities.DigitalVideoGame;
import gamezone.entities.PhysicalVideoGame;
import gamezone.entities.VideoGame;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VideoGameRepository {

    private static final String FILE_PATH = "data/videogames.json";

    public VideoGameRepository() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try (FileWriter fw = new FileWriter(file)) {
                fw.write("[]");
            } catch (IOException e) {
                System.err.println("Error creating videogames JSON: " + e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void save(VideoGame game) {
        List<VideoGame> games = findAll();
        for (VideoGame g : games) {
            if (g.getTitle().equalsIgnoreCase(game.getTitle())) {
                throw new IllegalArgumentException("El videojuego ya existe en el catálogo");
            }
        }
        games.add(game);
        writeAll(games);
    }

    @SuppressWarnings("unchecked")
    public List<VideoGame> findAll() {
        List<VideoGame> games = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(FILE_PATH)) {
            JSONArray array = (JSONArray) parser.parse(reader);
            for (Object obj : array) {
                VideoGame game = deserialize((JSONObject) obj);
                if (game != null) games.add(game);
            }
        } catch (Exception e) {
            System.err.println("Error reading videogames JSON: " + e.getMessage());
        }
        return games;
    }

    public VideoGame findByTitle(String title) {
        for (VideoGame game : findAll()) {
            if (game.getTitle().equalsIgnoreCase(title)) return game;
        }
        return null;
    }

    public List<VideoGame> findByPlatform(String platform) {
        List<VideoGame> result = new ArrayList<>();
        for (VideoGame game : findAll()) {
            if (game.getPlatform().equalsIgnoreCase(platform)) result.add(game);
        }
        return result;
    }

    public boolean update(VideoGame updatedGame) {
        List<VideoGame> games = findAll();
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).getTitle().equalsIgnoreCase(updatedGame.getTitle())) {
                games.set(i, updatedGame);
                writeAll(games);
                return true;
            }
        }
        return false;
    }

    public boolean deleteByTitle(String title) {
        List<VideoGame> games = findAll();
        boolean removed = games.removeIf(g -> g.getTitle().equalsIgnoreCase(title));
        if (removed) writeAll(games);
        return removed;
    }

    @SuppressWarnings("unchecked")
    private void writeAll(List<VideoGame> games) {
        JSONArray array = new JSONArray();
        for (VideoGame game : games) array.add(serialize(game));
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.write(array.toJSONString());
        } catch (IOException e) {
            System.err.println("Error writing videogames JSON: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private JSONObject serialize(VideoGame game) {
        JSONObject json = new JSONObject();
        json.put("title",    game.getTitle());
        json.put("price",    game.getPrice());
        json.put("platform", game.getPlatform());
        json.put("stock",    (long) game.getStock());
        json.put("genre",    game.getGenre());

        if (game instanceof DigitalVideoGame dg) {
            json.put("type",             "digital");
            json.put("sizeGB",           dg.getSizeGB());
            json.put("downloadPlatform", dg.getDownloadPlatform());
        } else if (game instanceof PhysicalVideoGame pg) {
            json.put("type",        "physical");
            json.put("condition",   pg.getCondition());
            json.put("distributor", pg.getDistributor());
        }

        return json;
    }

    private VideoGame deserialize(JSONObject json) {
        try {
            String title    = (String) json.get("title");
            double price    = toDouble(json.get("price"));
            String platform = (String) json.get("platform");
            int    stock    = (int) (long) json.get("stock");
            String genre    = (String) json.get("genre");
            String type     = (String) json.get("type");

            if ("digital".equals(type)) {
                double sizeGB           = toDouble(json.get("sizeGB"));
                String downloadPlatform = (String) json.get("downloadPlatform");
                return new DigitalVideoGame(title, price, platform, stock, genre, sizeGB, downloadPlatform);
            } else if ("physical".equals(type)) {
                String condition   = (String) json.get("condition");
                String distributor = (String) json.get("distributor");
                return new PhysicalVideoGame(title, price, platform, stock, genre, condition, distributor);
            }
        } catch (Exception e) {
            System.err.println("Error deserializing game: " + e.getMessage());
        }
        return null;
    }

    private double toDouble(Object value) {
        if (value instanceof Long)   return ((Long)   value).doubleValue();
        if (value instanceof Double) return (Double)  value;
        return Double.parseDouble(value.toString());
    }
}
