package gamezone.ui;

import gamezone.services.GameService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainMenu extends Application {

    private GameService gameService;
    private BorderPane  root;
    private Label       titleLabel;

    @Override
    public void start(Stage primaryStage) {
        gameService = new GameService();
        root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        root.setTop(buildTopBar());
        root.setLeft(buildSidebar());
        showDashboard();

        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setTitle("GameZone – Sistema de Gestión");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(14, 24, 14, 24));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #16213e; -fx-effect: dropshadow(gaussian,rgba(0,0,0,0.5),10,0,0,3);");

        Label logo = new Label("🎮 GAMEZONE");
        logo.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 22));
        logo.setTextFill(Color.web("#e94560"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        titleLabel = new Label("Sistema de Gestión");
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        titleLabel.setTextFill(Color.web("#a0a0c0"));

        bar.getChildren().addAll(logo, spacer, titleLabel);
        return bar;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #16213e;");

        String[][] menu = {
            {"🏠", "Inicio"},
            {"➕", "Agregar videojuego"},
            {"📋", "Listar videojuegos"},
            {"🔍", "Buscar por título"},
            {"🕹️", "Buscar por plataforma"},
            {"💰", "Realizar venta"},
            {"📊", "Mostrar ventas"},
            {"🚪", "Salir"}
        };

        for (String[] item : menu) {
            Button btn = buildMenuButton(item[0] + "  " + item[1]);
            String label = item[1];
            btn.setOnAction(e -> handleMenuAction(label));
            sidebar.getChildren().add(btn);
        }

        return sidebar;
    }

    private Button buildMenuButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(200);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 16, 12, 16));
        btn.setFont(Font.font("Arial", 13));
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #c0c0d0;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 8;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #0f3460;" +
            "-fx-text-fill: #ffffff;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 8;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #c0c0d0;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 8;"
        ));
        return btn;
    }

    private void handleMenuAction(String option) {
        switch (option) {
            case "Inicio"               -> showDashboard();
            case "Agregar videojuego"   -> showPanel(new AddGamePanel(gameService));
            case "Listar videojuegos"   -> showPanel(new ListGamesPanel(gameService));
            case "Buscar por título"    -> showPanel(new SearchByTitlePanel(gameService));
            case "Buscar por plataforma"-> showPanel(new SearchByPlatformPanel(gameService));
            case "Realizar venta"       -> showPanel(new SellGamePanel(gameService));
            case "Mostrar ventas"       -> showPanel(new SalesHistoryPanel(gameService));
            case "Salir" -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Salir");
                confirm.setHeaderText("¿Desea salir del sistema?");
                confirm.showAndWait().ifPresent(r -> {
                    if (r == ButtonType.OK) System.exit(0);
                });
            }
        }
        titleLabel.setText(option);
    }

    private void showPanel(Region panel) {
        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #1a1a2e; -fx-background: #1a1a2e;");
        root.setCenter(scroll);
    }

    private void showDashboard() {
        titleLabel.setText("Inicio");
        VBox dash = new VBox(24);
        dash.setPadding(new Insets(40));
        dash.setAlignment(Pos.CENTER);
        dash.setStyle("-fx-background-color: #1a1a2e;");

        Label welcome = new Label("Bienvenido al Sistema de Gestión GameZone");
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        welcome.setTextFill(Color.web("#e94560"));

        Label sub = new Label("Selecciona una opción del menú lateral para comenzar.");
        sub.setFont(Font.font("Arial", 15));
        sub.setTextFill(Color.web("#a0a0c0"));

        HBox cards = new HBox(20);
        cards.setAlignment(Pos.CENTER);

        int    games   = gameService.getAllVideoGames().size();
        int    sales   = gameService.totalSalesCount();
        double revenue = gameService.totalRevenue();

        cards.getChildren().addAll(
            buildCard("🎮", "Videojuegos", String.valueOf(games)),
            buildCard("💰", "Ventas",      String.valueOf(sales)),
            buildCard("📈", "Ingresos",    String.format("$%.2f", revenue))
        );

        dash.getChildren().addAll(welcome, sub, cards);
        root.setCenter(dash);
    }

    private VBox buildCard(String icon, String label, String value) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(24, 36, 24, 36));
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: #16213e;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.4),10,0,0,4);"
        );

        Label ico = new Label(icon);
        ico.setFont(Font.font(30));

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Arial", 13));
        lbl.setTextFill(Color.web("#a0a0c0"));

        Label val = new Label(value);
        val.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        val.setTextFill(Color.web("#e94560"));

        card.getChildren().addAll(ico, lbl, val);
        return card;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
