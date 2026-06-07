package gamezone.ui;

import gamezone.services.GameService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SellGamePanel extends VBox {

    private final GameService gameService;
    private TextField tfTitle, tfQuantity;
    private Label     lblResult;

    public SellGamePanel(GameService gameService) {
        this.gameService = gameService;
        setSpacing(22);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #1a1a2e;");

        Label header = new Label("💰 Realizar Venta");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        header.setTextFill(Color.web("#e94560"));

        getChildren().addAll(header, buildForm());
    }

    private VBox buildForm() {
        VBox form = new VBox(16);
        form.setPadding(new Insets(24));
        form.setMaxWidth(480);
        form.setStyle("-fx-background-color: #16213e; -fx-background-radius: 12;");

        Label instruction = new Label("Ingresa los datos para procesar la venta:");
        instruction.setFont(Font.font("Arial", 13));
        instruction.setTextFill(Color.web("#a0a0c0"));

        tfTitle    = styledField("Título del videojuego");
        tfQuantity = styledField("Cantidad");

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(14);
        addRow(grid, 0, "Título:",   tfTitle);
        addRow(grid, 1, "Cantidad:", tfQuantity);

        Button btnSell = new Button("Confirmar Venta");
        btnSell.setPadding(new Insets(11, 28, 11, 28));
        btnSell.setStyle(
            "-fx-background-color: #27ae60; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-font-size: 14; -fx-cursor: hand;" +
            "-fx-font-weight: bold;"
        );
        btnSell.setOnAction(e -> processSale());

        Button btnClear = new Button("Limpiar");
        btnClear.setPadding(new Insets(11, 22, 11, 22));
        btnClear.setStyle(
            "-fx-background-color: #7f8c8d; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-cursor: hand;"
        );
        btnClear.setOnAction(e -> { tfTitle.clear(); tfQuantity.clear(); lblResult.setText(""); });

        HBox buttons = new HBox(12, btnSell, btnClear);
        buttons.setAlignment(Pos.CENTER_LEFT);

        lblResult = new Label();
        lblResult.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        lblResult.setWrapText(true);

        form.getChildren().addAll(instruction, grid, buttons, lblResult);
        return form;
    }

    private void processSale() {
        lblResult.setText("");
        String titleText = tfTitle.getText().trim();
        String qtyText   = tfQuantity.getText().trim();

        if (titleText.isEmpty() || qtyText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos vacíos", "Por favor completa todos los campos.");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyText);
            double total = gameService.sellVideoGame(titleText, qty);

            lblResult.setTextFill(Color.web("#2ecc71"));
            lblResult.setText("✅ Venta realizada con éxito!\n" +
                              "   Juego: "     + titleText + "\n" +
                              "   Cantidad: "  + qty       + "\n" +
                              "   Total: $"    + String.format("%.2f", total));

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error de formato",
                "La cantidad debe ser un número entero válido.");

        } catch (ArithmeticException e) {
            showAlert(Alert.AlertType.ERROR, "Error de stock", e.getMessage());

        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Venta no realizada", e.getMessage());

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error inesperado",
                "No se pudo completar la venta.\n" + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(280);
        tf.setStyle(
            "-fx-background-color: #0f3460; -fx-text-fill: #ffffff;" +
            "-fx-prompt-text-fill: #6a6a9a; -fx-background-radius: 6; -fx-padding: 9;"
        );
        return tf;
    }

    private void addRow(GridPane grid, int row, String labelText, TextField field) {
        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("Arial", 13));
        lbl.setTextFill(Color.web("#a0a0c0"));
        lbl.setMinWidth(100);
        grid.add(lbl, 0, row);
        grid.add(field, 1, row);
    }
}
