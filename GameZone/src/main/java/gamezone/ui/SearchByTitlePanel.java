package gamezone.ui;

import gamezone.entities.DigitalVideoGame;
import gamezone.entities.PhysicalVideoGame;
import gamezone.entities.VideoGame;
import gamezone.services.GameService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SearchByTitlePanel extends VBox {

    private final GameService gameService;
    private TextField         tfSearch;
    private VBox              resultBox;

    public SearchByTitlePanel(GameService gameService) {
        this.gameService = gameService;
        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #1a1a2e;");

        Label header = new Label("🔍 Buscar por Título");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        header.setTextFill(Color.web("#e94560"));

        tfSearch = new TextField();
        tfSearch.setPromptText("Escribe el título del videojuego...");
        tfSearch.setPrefWidth(400);
        tfSearch.setStyle(
            "-fx-background-color: #0f3460; -fx-text-fill: #ffffff;" +
            "-fx-prompt-text-fill: #6a6a9a; -fx-background-radius: 8; -fx-padding: 10;"
        );
        tfSearch.setOnAction(e -> search());

        Button btnSearch = new Button("Buscar");
        btnSearch.setStyle(
            "-fx-background-color: #e94560; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 24;"
        );
        btnSearch.setOnAction(e -> search());

        HBox searchRow = new HBox(12, tfSearch, btnSearch);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        resultBox = new VBox(12);
        resultBox.setPadding(new Insets(16));
        resultBox.setStyle("-fx-background-color: #16213e; -fx-background-radius: 12;");

        getChildren().addAll(header, searchRow, resultBox);
    }

    private void search() {
        resultBox.getChildren().clear();
        String title = tfSearch.getText().trim();

        if (title.isEmpty()) {
            showMsg("⚠️ Por favor ingresa un título para buscar.", "#e67e22");
            return;
        }

        try {
            VideoGame game = gameService.findByTitle(title);
            if (game == null) {
                showMsg("❌ No se encontró ningún videojuego con ese título.", "#e74c3c");
            } else {
                showGameCard(game);
            }
        } catch (Exception ex) {
            showMsg("Error al buscar: " + ex.getMessage(), "#e74c3c");
        }
    }

    private void showGameCard(VideoGame game) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: #0f3460; -fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.4),8,0,0,3);"
        );

        String type = game instanceof DigitalVideoGame ? "🎮 Digital" : "📦 Físico";
        addField(card, "Tipo",         type);
        addField(card, "Título",       game.getTitle());
        addField(card, "Plataforma",   game.getPlatform());
        addField(card, "Género",       game.getGenre());
        addField(card, "Precio base",  String.format("$%.2f", game.getPrice()));
        addField(card, "Precio final", String.format("$%.2f", game.calculateFinalPrice()));
        addField(card, "Stock",        String.valueOf(game.getStock()));

        if (game instanceof DigitalVideoGame dg) {
            addField(card, "Tamaño",          dg.getSizeGB() + " GB");
            addField(card, "Plataforma desc.", dg.getDownloadPlatform());
        } else if (game instanceof PhysicalVideoGame pg) {
            addField(card, "Condición",   pg.getCondition());
            addField(card, "Distribuidor", pg.getDistributor());
        }

        resultBox.getChildren().add(card);
    }

    private void addField(VBox parent, String key, String value) {
        HBox row = new HBox(8);
        Label keyLbl = new Label(key + ":");
        keyLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        keyLbl.setTextFill(Color.web("#a0a0c0"));
        keyLbl.setMinWidth(140);

        Label valLbl = new Label(value);
        valLbl.setFont(Font.font("Arial", 13));
        valLbl.setTextFill(Color.web("#ffffff"));

        row.getChildren().addAll(keyLbl, valLbl);
        parent.getChildren().add(row);
    }

    private void showMsg(String text, String color) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", 14));
        lbl.setTextFill(Color.web(color));
        resultBox.getChildren().add(lbl);
    }
}
