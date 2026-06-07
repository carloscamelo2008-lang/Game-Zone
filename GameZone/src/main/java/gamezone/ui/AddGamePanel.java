package gamezone.ui;

import gamezone.entities.DigitalVideoGame;
import gamezone.entities.PhysicalVideoGame;
import gamezone.entities.VideoGame;
import gamezone.services.GameService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class AddGamePanel extends VBox {

    private final GameService gameService;

    private TextField   tfTitle, tfPrice, tfPlatform, tfStock, tfGenre;
    private TextField   tfSizeGB, tfDownloadPlatform;
    private TextField   tfCondition, tfDistributor;
    private ToggleGroup typeGroup;
    private RadioButton rdDigital, rdPhysical;
    private VBox        digitalFields, physicalFields;
    private TableView<VideoGame> table;

    public AddGamePanel(GameService gameService) {
        this.gameService = gameService;
        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #1a1a2e;");

        getChildren().addAll(
            buildHeader("➕ Gestión de Videojuegos"),
            buildForm(),
            buildTableSection()
        );

        refreshTable();
    }

    private Label buildHeader(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lbl.setTextFill(Color.web("#e94560"));
        return lbl;
    }

    private VBox buildForm() {
        VBox form = new VBox(14);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #16213e; -fx-background-radius: 12;");

        typeGroup  = new ToggleGroup();
        rdDigital  = new RadioButton("Digital");
        rdPhysical = new RadioButton("Físico");
        rdDigital.setToggleGroup(typeGroup);
        rdPhysical.setToggleGroup(typeGroup);
        rdDigital.setSelected(true);
        rdDigital.setStyle("-fx-text-fill: #c0c0d0;");
        rdPhysical.setStyle("-fx-text-fill: #c0c0d0;");

        HBox typeRow = new HBox(20, new Label("Tipo:"), rdDigital, rdPhysical);
        typeRow.setAlignment(Pos.CENTER_LEFT);
        styleFormLabel((Label) typeRow.getChildren().get(0));

        tfTitle    = styledField("Título");
        tfPrice    = styledField("Precio base");
        tfPlatform = styledField("Plataforma (PC, PS5, Xbox...)");
        tfStock    = styledField("Stock");
        tfGenre    = styledField("Género");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);
        addRow(grid, 0, "Título:",     tfTitle);
        addRow(grid, 1, "Precio:",     tfPrice);
        addRow(grid, 2, "Plataforma:", tfPlatform);
        addRow(grid, 3, "Stock:",      tfStock);
        addRow(grid, 4, "Género:",     tfGenre);

        tfSizeGB           = styledField("Tamaño (GB)");
        tfDownloadPlatform = styledField("Plataforma de descarga (Steam, Epic...)");
        digitalFields = new VBox(10);
        GridPane dGrid = new GridPane();
        dGrid.setHgap(16); dGrid.setVgap(12);
        addRow(dGrid, 0, "Tamaño GB:",   tfSizeGB);
        addRow(dGrid, 1, "Descarga en:", tfDownloadPlatform);
        digitalFields.getChildren().add(dGrid);

        tfCondition   = styledField("Condición (nuevo / usado)");
        tfDistributor = styledField("Distribuidor");
        physicalFields = new VBox(10);
        GridPane pGrid = new GridPane();
        pGrid.setHgap(16); pGrid.setVgap(12);
        addRow(pGrid, 0, "Condición:",    tfCondition);
        addRow(pGrid, 1, "Distribuidor:", tfDistributor);
        physicalFields.getChildren().add(pGrid);
        physicalFields.setVisible(false);
        physicalFields.setManaged(false);

        typeGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            boolean isDigital = n == rdDigital;
            digitalFields.setVisible(isDigital);
            digitalFields.setManaged(isDigital);
            physicalFields.setVisible(!isDigital);
            physicalFields.setManaged(!isDigital);
        });

        Button btnAdd    = actionButton("Agregar",    "#27ae60");
        Button btnUpdate = actionButton("Actualizar", "#2980b9");
        Button btnDelete = actionButton("Eliminar",   "#e74c3c");
        Button btnClear  = actionButton("Limpiar",    "#7f8c8d");

        btnAdd.setOnAction(e    -> handleAdd());
        btnUpdate.setOnAction(e -> handleUpdate());
        btnDelete.setOnAction(e -> handleDelete());
        btnClear.setOnAction(e  -> clearForm());

        HBox buttons = new HBox(12, btnAdd, btnUpdate, btnDelete, btnClear);
        buttons.setAlignment(Pos.CENTER_LEFT);

        form.getChildren().addAll(typeRow, grid, digitalFields, physicalFields, buttons);
        return form;
    }

    @SuppressWarnings("unchecked")
    private VBox buildTableSection() {
        Label lbl = new Label("Catálogo de Videojuegos");
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lbl.setTextFill(Color.web("#a0a0c0"));

        table = new TableView<>();
        table.setStyle("-fx-background-color: #16213e; -fx-control-inner-background: #16213e;" +
                       "-fx-text-fill: #c0c0d0;");
        table.setPrefHeight(280);

        table.getColumns().addAll(
            col("Título",     "title",    200),
            col("Plataforma", "platform", 120),
            col("Género",     "genre",    120),
            col("Precio",     "price",    100),
            col("Stock",      "stock",    80)
        );

        table.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> {
            if (sel != null) populateForm(sel);
        });

        VBox section = new VBox(10, lbl, table);
        section.setPadding(new Insets(10, 20, 20, 20));
        section.setStyle("-fx-background-color: #16213e; -fx-background-radius: 12;");
        return section;
    }

    private void handleAdd() {
        try {
            VideoGame game = buildGameFromForm();
            gameService.addVideoGame(game);
            showInfo("Videojuego agregado correctamente.");
            clearForm();
            refreshTable();
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
        } catch (Exception ex) {
            showAlert("Error inesperado: " + ex.getMessage());
        }
    }

    private void handleUpdate() {
        try {
            VideoGame game = buildGameFromForm();
            boolean ok = gameService.updateVideoGame(game);
            if (ok) {
                showInfo("Videojuego actualizado correctamente.");
                clearForm();
                refreshTable();
            } else {
                showAlert("No se encontró el videojuego para actualizar.");
            }
        } catch (IllegalArgumentException ex) {
            showAlert(ex.getMessage());
        }
    }

    private void handleDelete() {
        String title = tfTitle.getText().trim();
        if (title.isEmpty()) { showAlert("Ingresa el título del juego a eliminar."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar el videojuego '" + title + "'?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                boolean ok = gameService.deleteVideoGame(title);
                if (ok) { showInfo("Videojuego eliminado."); clearForm(); refreshTable(); }
                else      showAlert("No se encontró el videojuego.");
            }
        });
    }

    private VideoGame buildGameFromForm() {
        String title    = tfTitle.getText().trim();
        double price    = Double.parseDouble(tfPrice.getText().trim());
        String platform = tfPlatform.getText().trim();
        int    stock    = Integer.parseInt(tfStock.getText().trim());
        String genre    = tfGenre.getText().trim();

        if (rdDigital.isSelected()) {
            double sizeGB = Double.parseDouble(tfSizeGB.getText().trim());
            String dlPlat = tfDownloadPlatform.getText().trim();
            return new DigitalVideoGame(title, price, platform, stock, genre, sizeGB, dlPlat);
        } else {
            String condition   = tfCondition.getText().trim();
            String distributor = tfDistributor.getText().trim();
            return new PhysicalVideoGame(title, price, platform, stock, genre, condition, distributor);
        }
    }

    private void populateForm(VideoGame game) {
        tfTitle.setText(game.getTitle());
        tfPrice.setText(String.valueOf(game.getPrice()));
        tfPlatform.setText(game.getPlatform());
        tfStock.setText(String.valueOf(game.getStock()));
        tfGenre.setText(game.getGenre());

        if (game instanceof DigitalVideoGame dg) {
            rdDigital.setSelected(true);
            tfSizeGB.setText(String.valueOf(dg.getSizeGB()));
            tfDownloadPlatform.setText(dg.getDownloadPlatform());
        } else if (game instanceof PhysicalVideoGame pg) {
            rdPhysical.setSelected(true);
            tfCondition.setText(pg.getCondition());
            tfDistributor.setText(pg.getDistributor());
        }
    }

    private void refreshTable() {
        List<VideoGame> games = gameService.getAllVideoGames();
        table.getItems().setAll(games);
    }

    private void clearForm() {
        tfTitle.clear(); tfPrice.clear(); tfPlatform.clear();
        tfStock.clear(); tfGenre.clear();
        tfSizeGB.clear(); tfDownloadPlatform.clear();
        tfCondition.clear(); tfDistributor.clear();
        rdDigital.setSelected(true);
        table.getSelectionModel().clearSelection();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(260);
        tf.setStyle(
            "-fx-background-color: #0f3460; -fx-text-fill: #ffffff;" +
            "-fx-prompt-text-fill: #6a6a9a; -fx-background-radius: 6; -fx-padding: 8;"
        );
        return tf;
    }

    private Button actionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPadding(new Insets(9, 22, 9, 22));
        btn.setStyle(
            "-fx-background-color: " + color + "; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-font-size: 13; -fx-cursor: hand;"
        );
        return btn;
    }

    private void addRow(GridPane grid, int row, String labelText, TextField field) {
        Label lbl = new Label(labelText);
        styleFormLabel(lbl);
        grid.add(lbl, 0, row);
        grid.add(field, 1, row);
    }

    private void styleFormLabel(Label lbl) {
        lbl.setFont(Font.font("Arial", 13));
        lbl.setTextFill(Color.web("#a0a0c0"));
        lbl.setMinWidth(130);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T, S> TableColumn<T, S> col(String header, String property, double width) {
        TableColumn<T, S> col = new TableColumn<>(header);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        col.setStyle("-fx-text-fill: #c0c0d0;");
        return col;
    }
}
