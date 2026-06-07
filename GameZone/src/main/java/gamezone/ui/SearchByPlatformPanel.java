package gamezone.ui;

import gamezone.entities.VideoGame;
import gamezone.services.GameService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class SearchByPlatformPanel extends VBox {

    private final GameService    gameService;
    private TextField            tfPlatform;
    private TableView<VideoGame> table;
    private Label                lblResult;

    public SearchByPlatformPanel(GameService gameService) {
        this.gameService = gameService;
        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #1a1a2e;");

        Label header = new Label("🕹️ Buscar por Plataforma");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        header.setTextFill(Color.web("#e94560"));

        tfPlatform = new TextField();
        tfPlatform.setPromptText("Ej: PC, PS5, Xbox, Nintendo Switch...");
        tfPlatform.setPrefWidth(380);
        tfPlatform.setStyle(
            "-fx-background-color: #0f3460; -fx-text-fill: #ffffff;" +
            "-fx-prompt-text-fill: #6a6a9a; -fx-background-radius: 8; -fx-padding: 10;"
        );
        tfPlatform.setOnAction(e -> search());

        Button btnSearch = new Button("Buscar");
        btnSearch.setStyle(
            "-fx-background-color: #e94560; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 24;"
        );
        btnSearch.setOnAction(e -> search());

        HBox row = new HBox(12, tfPlatform, btnSearch);
        row.setAlignment(Pos.CENTER_LEFT);

        lblResult = new Label();
        lblResult.setFont(Font.font("Arial", 13));
        lblResult.setTextFill(Color.web("#a0a0c0"));

        table = buildTable();

        VBox card = new VBox(12, lblResult, table);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #16213e; -fx-background-radius: 12;");

        getChildren().addAll(header, row, card);
    }

    @SuppressWarnings("unchecked")
    private TableView<VideoGame> buildTable() {
        TableView<VideoGame> tv = new TableView<>();
        tv.setStyle("-fx-background-color: #16213e; -fx-control-inner-background: #1a1a2e;");
        tv.setPrefHeight(380);

        tv.getColumns().addAll(
            col("Título",     "title",    220),
            col("Plataforma", "platform", 130),
            col("Género",     "genre",    130),
            col("Precio",     "price",    110),
            col("Stock",      "stock",    80)
        );
        return tv;
    }

    private void search() {
        String platform = tfPlatform.getText().trim();
        if (platform.isEmpty()) {
            lblResult.setText("⚠️ Ingresa una plataforma para buscar.");
            lblResult.setTextFill(Color.web("#e67e22"));
            return;
        }

        List<VideoGame> results = gameService.findByPlatform(platform);
        table.getItems().setAll(results);

        if (results.isEmpty()) {
            lblResult.setText("❌ No se encontraron juegos para la plataforma: " + platform);
            lblResult.setTextFill(Color.web("#e74c3c"));
        } else {
            lblResult.setText("✅ " + results.size() + " resultado(s) para: " + platform);
            lblResult.setTextFill(Color.web("#2ecc71"));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T, S> TableColumn<T, S> col(String header, String property, double width) {
        TableColumn<T, S> col = new TableColumn<>(header);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        return col;
    }
}
