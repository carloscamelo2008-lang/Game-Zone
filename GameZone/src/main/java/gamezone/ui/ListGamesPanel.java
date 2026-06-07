package gamezone.ui;

import gamezone.entities.VideoGame;
import gamezone.services.GameService;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class ListGamesPanel extends VBox {

    private final GameService    gameService;
    private TableView<VideoGame> table;
    private Label                lblCount;

    public ListGamesPanel(GameService gameService) {
        this.gameService = gameService;
        setSpacing(18);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #1a1a2e;");

        Label header = new Label("📋 Todos los Videojuegos");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        header.setTextFill(Color.web("#e94560"));

        lblCount = new Label();
        lblCount.setFont(Font.font("Arial", 13));
        lblCount.setTextFill(Color.web("#a0a0c0"));

        Button btnRefresh = new Button("🔄 Actualizar");
        btnRefresh.setStyle(
            "-fx-background-color: #0f3460; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 18;"
        );
        btnRefresh.setOnAction(e -> loadTable());

        table = buildTable();
        loadTable();

        VBox card = new VBox(12, lblCount, btnRefresh, table);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #16213e; -fx-background-radius: 12;");

        getChildren().addAll(header, card);
    }

    @SuppressWarnings("unchecked")
    private TableView<VideoGame> buildTable() {
        TableView<VideoGame> tv = new TableView<>();
        tv.setStyle("-fx-background-color: #16213e; -fx-control-inner-background: #1a1a2e;");
        tv.setPrefHeight(450);

        TableColumn<VideoGame, String> colFinal = new TableColumn<>("Precio Final");
        colFinal.setPrefWidth(120);
        colFinal.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                String.format("$%.2f", data.getValue().calculateFinalPrice())
            )
        );

        TableColumn<VideoGame, String> colType = new TableColumn<>("Tipo");
        colType.setPrefWidth(90);
        colType.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getClass().getSimpleName()
                    .replace("DigitalVideoGame",  "Digital")
                    .replace("PhysicalVideoGame", "Físico")
            )
        );

        tv.getColumns().addAll(
            col("Título",      "title",    220),
            col("Plataforma",  "platform", 130),
            col("Género",      "genre",    130),
            col("Precio",      "price",    110),
            col("Stock",       "stock",    80),
            colFinal,
            colType
        );
        return tv;
    }

    private void loadTable() {
        List<VideoGame> games = gameService.getAllVideoGames();
        table.getItems().setAll(games);
        lblCount.setText("Total en catálogo: " + games.size() + " videojuego(s)");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T, S> TableColumn<T, S> col(String header, String property, double width) {
        TableColumn<T, S> col = new TableColumn<>(header);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        return col;
    }
}
