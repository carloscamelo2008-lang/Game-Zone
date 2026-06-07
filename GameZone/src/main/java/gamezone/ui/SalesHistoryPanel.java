package gamezone.ui;

import gamezone.entities.Sale;
import gamezone.services.GameService;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SalesHistoryPanel extends VBox {

    private final GameService gameService;
    private TableView<Sale>   table;
    private Label             lblTotal, lblCount;

    public SalesHistoryPanel(GameService gameService) {
        this.gameService = gameService;
        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #1a1a2e;");

        Label header = new Label("📊 Historial de Ventas");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        header.setTextFill(Color.web("#e94560"));

        lblCount = new Label();
        lblTotal = new Label();
        styleStatLabel(lblCount);
        styleStatLabel(lblTotal);

        HBox stats = new HBox(32, lblCount, lblTotal);
        stats.setPadding(new Insets(14));
        stats.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;");

        Button btnRefresh = new Button("🔄 Actualizar");
        btnRefresh.setStyle(
            "-fx-background-color: #0f3460; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 18;"
        );
        btnRefresh.setOnAction(e -> load());

        table = buildTable();

        VBox card = new VBox(12, btnRefresh, table);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #16213e; -fx-background-radius: 12;");

        getChildren().addAll(header, stats, card);
        load();
    }

    private TableView<Sale> buildTable() {
        TableView<Sale> tv = new TableView<>();
        tv.setStyle("-fx-background-color: #16213e; -fx-control-inner-background: #1a1a2e;");
        tv.setPrefHeight(400);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        TableColumn<Sale, String> colId = new TableColumn<>("ID");
        colId.setPrefWidth(90);
        colId.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(d.getValue().getId()));

        TableColumn<Sale, String> colGame = new TableColumn<>("Videojuego");
        colGame.setPrefWidth(200);
        colGame.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(d.getValue().getVideoGame().getTitle()));

        TableColumn<Sale, Integer> colQty = new TableColumn<>("Cantidad");
        colQty.setPrefWidth(90);
        colQty.setCellValueFactory(d ->
            new javafx.beans.property.SimpleIntegerProperty(d.getValue().getQuantity()).asObject());

        TableColumn<Sale, String> colUnit = new TableColumn<>("Precio Unit.");
        colUnit.setPrefWidth(120);
        colUnit.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(
                String.format("$%.2f", d.getValue().getUnitPrice())));

        TableColumn<Sale, String> colTotal = new TableColumn<>("Total");
        colTotal.setPrefWidth(120);
        colTotal.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(
                String.format("$%.2f", d.getValue().getTotal())));

        TableColumn<Sale, String> colDate = new TableColumn<>("Fecha");
        colDate.setPrefWidth(150);
        colDate.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(
                d.getValue().getSaleDate().format(fmt)));

        tv.getColumns().addAll(colId, colGame, colQty, colUnit, colTotal, colDate);
        return tv;
    }

    private void load() {
        List<Sale> sales = gameService.getAllSales();
        table.getItems().setAll(sales);
        lblCount.setText("Total de ventas: "   + gameService.totalSalesCount());
        lblTotal.setText("Ingresos totales: $" + String.format("%.2f", gameService.totalRevenue()));
    }

    private void styleStatLabel(Label lbl) {
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lbl.setTextFill(Color.web("#e94560"));
    }
}
