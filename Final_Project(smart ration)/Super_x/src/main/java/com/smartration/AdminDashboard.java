package com.smartration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AdminDashboard {
    Scene dashboardScene, viewQuataScene, usermanagementScene, rationallocaScene, scanScene, hisScene, analysisScene;
    Stage dashboardStage;

    // Firebase Configuration
    private final static String FIREBASE_API_KEY = "AIzaSyCw_hJmevrQsmsQ1Mg0LKBylH7z6zLcsNw";
    private final static String PROJECT_ID = "new-ration";

    // Data holders for real-time data
    private int totalUsers = 0;
    private int totalDistributions = 0;
    private int activeUsers = 0;
    private double monthlyUsage = 0.0;
    private Map<String, Integer> monthlyDistributions = new LinkedHashMap<>();
    private List<DistributionData> distributionHistory = new ArrayList<>();
    
    // UI Components for dynamic updates
    private VBox card1, card2, card3;
    private BarChart<String, Number> barChart;
    private ProgressIndicator loadingIndicator;
    private Label statusLabel;

    // Color scheme constants
    private static final String PRIMARY_COLOR = "#2C3E50";
    private static final String SECONDARY_COLOR = "#3498DB";
    private static final String ACCENT_COLOR = "#E74C3C";
    private static final String SUCCESS_COLOR = "#27AE60";
    private static final String WARNING_COLOR = "#F39C12";
    private static final String CARD_COLOR = "#FFFFFF";
    private static final String BACKGROUND_COLOR = "#ECF0F1";
    private static final String TEXT_DARK = "#2C3E50";
    private static final String TEXT_LIGHT = "#7F8C8D";

    public void initialize(Stage adminStage) {
        // Create enhanced sidebar
        VBox sidebar = createEnhancedSidebar();
        // Create modern top bar
        HBox topbar = createModernTopBar();
        // Create main dashboard content
        VBox dashboardContent = createDashboardContent();
        // Assemble main layout
        BorderPane layout = new BorderPane();
        layout.setLeft(sidebar);
        layout.setTop(topbar);
        layout.setCenter(dashboardContent);
        layout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        Scene sc = new Scene(layout, 1400, 900);

        dashboardScene = sc;
        dashboardStage = adminStage;
        adminStage.setScene(sc);
        adminStage.setTitle("Smart Ration - Admin Dashboard");
        adminStage.show();
        
        // Load real data after UI is initialized
        loadDashboardData();
    }

    private VBox createEnhancedSidebar() {
        VBox sidebar = new VBox(12);
        sidebar.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-padding: 20 15 20 15;");
        sidebar.setPrefWidth(240);
        sidebar.setMaxWidth(240);

        // App logo and title
        VBox logoSection = new VBox(8);
        logoSection.setAlignment(Pos.CENTER);
        // Create circular logo background
        Circle logoCircle = new Circle(20);
        logoCircle.setFill(Color.web(SECONDARY_COLOR));
        StackPane logoContainer = new StackPane();
        logoContainer.getChildren().add(logoCircle);

        Label logoText = new Label("SR");
        logoText.setTextFill(Color.WHITE);
        logoText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        logoContainer.getChildren().add(logoText);

        Label appTitle = new Label("Smart Ration");
        appTitle.setTextFill(Color.WHITE);
        appTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label appSubtitle = new Label("Admin Dashboard");
        appSubtitle.setTextFill(Color.web(TEXT_LIGHT));
        appSubtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 10));

        logoSection.getChildren().addAll(logoContainer, appTitle, appSubtitle);

        // Create navigation buttons with icons
        Button usermanagementButton = createEnhancedSidebarButton("👥 User Management", SUCCESS_COLOR);
        usermanagementButton.setOnMouseClicked(event -> {
            System.out.println("User Management clicked");
            initilizUsermanagementpagedemo();
            dashboardStage.setScene(usermanagementScene);
        });

        Button inventoryButton = createEnhancedSidebarButton("📦 Inventory Management", WARNING_COLOR);
        inventoryButton.setOnMouseClicked(event -> {
            System.out.println("Inventory Management clicked");
            initilizedViewQuatapage();
            dashboardStage.setScene(viewQuataScene);
        });

        Button allocationBtn = createEnhancedSidebarButton("🍽 Ration Allocation", SECONDARY_COLOR);
        allocationBtn.setOnAction(e -> {
            rationAllocation();
            dashboardStage.setScene(rationallocaScene);
        });

        Button scanButton = createEnhancedSidebarButton(" Verify & Issue Panel ", SECONDARY_COLOR);
        scanButton.setOnAction(e -> {
             scannsystem();
            dashboardStage.setScene(scanScene);
        });

        Button historyButton = createEnhancedSidebarButton("  📜 History", SECONDARY_COLOR);
        historyButton.setOnAction(e -> {
             history();
            dashboardStage.setScene(hisScene);
        });
        Button analysisButton = createEnhancedSidebarButton("Analytics Dashboard ", SECONDARY_COLOR);
        analysisButton.setOnAction(e -> {
              Analysis();
            dashboardStage.setScene(analysisScene);
});
        
      
        
        // Refresh Data Button
        Button refreshButton = createEnhancedSidebarButton("🔄 Refresh Data", "#17A2B8");
        refreshButton.setOnAction(e -> loadDashboardData());

        // Add hover effect
          Button logoutBtn = new Button("Logout");
            logoutBtn.setOnMouseEntered(e ->
            logoutBtn.setStyle(
                "-fx-background-color: " + ACCENT_COLOR +  ";" +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 25; " +
                "-fx-cursor: hand;"
            )
        );
        
        logoutBtn.setOnMouseExited(e ->
            logoutBtn.setStyle(
                "-fx-background-color: " + ACCENT_COLOR + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 25; " +
                "-fx-cursor: hand;"
            )
        );
           
        logoutBtn.setOnMouseClicked(event -> {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Logout Confirmation");
    alert.setHeaderText(null);
    alert.setContentText("Are you sure you want to logout?");
    ButtonType yesButton = new ButtonType("Yes");
    ButtonType noButton = new ButtonType("No");
    alert.getButtonTypes().setAll(yesButton, noButton);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == yesButton) {
        WelcomePage wp = new WelcomePage();
        Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
        wp.show(currentStage);
    }
});

        // Add separator
        Rectangle separator = new Rectangle(200, 1);
        separator.setFill(Color.web("#34495E"));
        VBox separatorBox = new VBox();
        separatorBox.setAlignment(Pos.CENTER);
        separatorBox.setPadding(new Insets(15, 0, 15, 0));
        separatorBox.getChildren().add(separator);

        sidebar.getChildren().addAll(
            logoSection,
            separatorBox,
            usermanagementButton,
            inventoryButton,
            allocationBtn,
            scanButton,
            historyButton, 
            analysisButton,
            refreshButton,
            separator,
            logoutBtn
        );

        return sidebar;
    }

   private HBox createModernTopBar() {
    HBox topbar = new HBox();
    topbar.setStyle(
        "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2); " +
        "-fx-padding: 20 30;"        // Add vertical & horizontal padding
    );
    topbar.setAlignment(Pos.CENTER_LEFT);

    Label topTitle = new Label("Dashboard Overview");
    topTitle.setTextFill(Color.WHITE);
    topTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));  // Slightly bigger font
    topTitle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    loadingIndicator = new ProgressIndicator();
    loadingIndicator.setMaxSize(20, 20);
    loadingIndicator.setVisible(false);

    statusLabel = new Label("Ready");
    statusLabel.setTextFill(Color.WHITE);
    statusLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 15));
    statusLabel.setPadding(new Insets(0, 0, 0, 10));

    HBox userSection = new HBox(10);
    userSection.setAlignment(Pos.CENTER);
    userSection.setPadding(new Insets(0, 0, 0, 10));

    Label welcomeLabel = new Label("Welcome, Admin");
    welcomeLabel.setTextFill(Color.WHITE);
    welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 18));

    userSection.getChildren().addAll(welcomeLabel, loadingIndicator, statusLabel);

    topbar.getChildren().addAll(topTitle, spacer, userSection);

    return topbar;
}


    private VBox createDashboardContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Stats cards section
        Label statsTitle = new Label("Key Metrics ");
        statsTitle.setTextFill(Color.web(TEXT_DARK));
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        HBox cardsBox = new HBox(15);
        cardsBox.setAlignment(Pos.CENTER);

        // Create cards with loading state - Updated to show total users and removed active users
        card1 = createModernDashboardCard("Current Ration Logs", "Loading...", "📋", SUCCESS_COLOR);
        card2 = createModernDashboardCard("Distribution Logs", "Loading...", "📊", SECONDARY_COLOR);
        card3 = createModernDashboardCard("Monthly Usage", "Loading...", "📈", "#9B59B6");

        cardsBox.getChildren().addAll(card1, card2, card3);

        // Chart section
        Label chartTitle = new Label("Distribution Analytics ");
        chartTitle.setTextFill(Color.web(TEXT_DARK));
        chartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        VBox chartContainer = createEnhancedChart();

        content.getChildren().addAll(statsTitle, cardsBox, chartTitle, chartContainer);

        return content;
    }

    private VBox createModernDashboardCard(String title, String value, String emoji, String accentColor) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefSize(210, 100);
        card.setMaxWidth(210);
        card.setStyle(
            "-fx-background-color: " + CARD_COLOR + "; " +
            "-fx-padding: 20; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.2, 0, 3);"
        );

        // Icon and value section
        HBox topSection = new HBox(12);
        topSection.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(emoji);
        iconLabel.setFont(Font.font("Arial", 20));
        iconLabel.setStyle("-fx-background-color: " + accentColor + "20; -fx-padding: 8; -fx-background-radius: 8;");

        VBox valueSection = new VBox(3);
        valueSection.setAlignment(Pos.CENTER_LEFT);

        Label valueLabel = new Label(value);
        valueLabel.setTextFill(Color.web(TEXT_DARK));
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web(TEXT_LIGHT));
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 11));

        valueSection.getChildren().addAll(valueLabel, titleLabel);
        topSection.getChildren().addAll(iconLabel, valueSection);

        // Trend indicator (optional)
        Rectangle trendBar = new Rectangle(170, 3);
        trendBar.setFill(Color.web(accentColor));
        trendBar.setArcWidth(3);
        trendBar.setArcHeight(3);

        card.getChildren().addAll(topSection, trendBar);

        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: " + CARD_COLOR + "; " +
                "-fx-padding: 20; " +
                "-fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0.3, 0, 5); " +
                "-fx-scale-x: 1.02; -fx-scale-y: 1.02;"
            );
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: " + CARD_COLOR + "; " +
                "-fx-padding: 20; " +
                "-fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.2, 0, 3); " +
                "-fx-scale-x: 1.0; -fx-scale-y: 1.0;"
            );
        });

        return card;
    }

    private VBox createEnhancedChart() {
        VBox chartContainer = new VBox();
        chartContainer.setStyle(
            "-fx-background-color: " + CARD_COLOR + "; " +
            "-fx-padding: 20; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.2, 0, 3);"
        );

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Monthly Distribution Overview ");
        barChart.setTitleSide(javafx.geometry.Side.TOP);
        xAxis.setLabel("Month");
        yAxis.setLabel("Users");

        // Style the chart
        barChart.setLegendVisible(true);
        barChart.setAnimated(true);
        barChart.setCategoryGap(25);
        barChart.setBarGap(8);

        // Initial loading state
        XYChart.Series<String, Number> loadingSeries = new XYChart.Series<>();
        loadingSeries.setName("Loading...");
        loadingSeries.getData().add(new XYChart.Data<>("Loading", 0));
        barChart.getData().add(loadingSeries);

        barChart.setPrefHeight(280);
        barChart.setMaxHeight(280);

        chartContainer.getChildren().add(barChart);
        return chartContainer;
    }

    private Button createEnhancedSidebarButton(String text, String accentColor) {
        Button btn = new Button(text);
        btn.setPrefWidth(200);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #BDC3C7; " +
            "-fx-font-size: 12; " +
            "-fx-font-weight: normal; " +
            "-fx-padding: 12 15; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );

        btn.setOnMouseEntered(e ->
            btn.setStyle(
                "-fx-background-color: " + accentColor + "30; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 12; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12 15; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            )
        );

        btn.setOnMouseExited(e ->
            btn.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: #BDC3C7; " +
                "-fx-font-size: 12; " +
                "-fx-font-weight: normal; " +
                "-fx-padding: 12 15; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            )
        );

        return btn;
    }

    // Real Data Loading Methods
    private void loadDashboardData() {
        loadingIndicator.setVisible(true);
        statusLabel.setText("Loading real-time data...");
        
        Task<Boolean> loadTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    // Fetch distribution data
                    fetchDistributionData();
                    // Fetch user data
                    fetchUserData();
                    // Calculate analytics
                    calculateDashboardMetrics();
                    return true;
                } catch (Exception e) {
                    System.out.println("Error loading data: " + e.getMessage());
                    // Use sample data as fallback
                    generateFallbackData();
                    return false;
                }
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    statusLabel.setText("Data updated successfully");
                    updateDashboardCards();
                    updateChart();
                    
                    // Hide status after 3 seconds
                    Timeline hideStatus = new Timeline();
                    hideStatus.getKeyFrames().add(
                        new javafx.animation.KeyFrame(Duration.seconds(3), e -> statusLabel.setText("Ready"))
                    );
                    hideStatus.play();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    statusLabel.setText("Using sample data");
                    updateDashboardCards();
                    updateChart();
                });
            }
        };
        
        new Thread(loadTask).start();
    }

    private void fetchDistributionData() {
        try {
            String urlStr = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + 
                           "/databases/(default)/documents/distributionHistory";
            
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                
                JSONObject response = new JSONObject(sb.toString());
                if (response.has("documents")) {
                    JSONArray documents = response.getJSONArray("documents");
                    distributionHistory.clear();
                    
                    for (int i = 0; i < documents.length(); i++) {
                        JSONObject doc = documents.getJSONObject(i);
                        JSONObject fields = doc.getJSONObject("fields");
                        
                        DistributionData data = new DistributionData();
                        data.userName = getStringValue(fields, "userName");
                        data.userEmail = getStringValue(fields, "userEmail");
                        data.distributionDate = getStringValue(fields, "distributionDate");
                        data.itemsDistributed = getStringValue(fields, "itemsDistributed");
                        data.distributedBy = getStringValue(fields, "distributedBy");
                        
                        distributionHistory.add(data);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Firebase distribution fetch error: " + e.getMessage());
            throw new RuntimeException("Failed to fetch distribution data");
        }
    }

    private void fetchUserData() {
        try {
            String urlStr = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + 
                           "/databases/(default)/documents/users";
            
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                
                JSONObject response = new JSONObject(sb.toString());
                if (response.has("documents")) {
                    JSONArray documents = response.getJSONArray("documents");
                    totalUsers = documents.length();
                    
                    // Count active users (users who have received distributions)
                    activeUsers = (int) distributionHistory.stream()
                        .map(d -> d.userEmail)
                        .distinct()
                        .count();
                }
            }
        } catch (Exception e) {
            System.out.println("Firebase user fetch error: " + e.getMessage());
            throw new RuntimeException("Failed to fetch user data");
        }
    }

    private void calculateDashboardMetrics() {
        // Calculate total distributions
        totalDistributions = distributionHistory.size();
        
        // Monthly usage percentage (distribution efficiency)
        if (totalUsers > 0) {
            monthlyUsage = (activeUsers * 100.0) / totalUsers;
        } else {
            monthlyUsage = 0.0;
        }
        
        // Calculate monthly distribution data for chart
        monthlyDistributions.clear();
        LocalDate currentDate = LocalDate.now();
        
        for (int i = 5; i >= 0; i--) {
            LocalDate month = currentDate.minusMonths(i);
            String monthKey = month.format(DateTimeFormatter.ofPattern("MMM"));
            String fullMonthKey = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            
            int monthCount = (int) distributionHistory.stream()
                .filter(d -> d.distributionDate.startsWith(fullMonthKey))
                .count();
            
            monthlyDistributions.put(monthKey, monthCount);
        }
    }

    private void generateFallbackData() {
        // Generate sample data if Firebase is not available
        totalUsers = 85;
        totalDistributions = 67;
        activeUsers = 45;
        monthlyUsage = 75.3;
        
        // Sample monthly data
        monthlyDistributions.clear();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};
        int[] values = {15, 22, 18, 25, 20, 12};
        
        for (int i = 0; i < months.length; i++) {
            monthlyDistributions.put(months[i], values[i]);
        }
    }

    private void updateDashboardCards() {
        // Update card 1: Current Ration Logs - Now shows total users
        updateCardValue(card1, String.valueOf(totalUsers));
        
        // Update card 2: Distribution Logs
        updateCardValue(card2, String.valueOf(totalDistributions));
        
        // Update card 3: Monthly Usage (Distribution Efficiency)
        updateCardValue(card3, String.format("%.1f%%", monthlyUsage));
    }

    private void updateCardValue(VBox card, String newValue) {
        try {
            HBox topSection = (HBox) card.getChildren().get(0);
            VBox valueSection = (VBox) topSection.getChildren().get(1);
            Label valueLabel = (Label) valueSection.getChildren().get(0);
            
            // Animate the value change
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), valueLabel);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                valueLabel.setText(newValue);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), valueLabel);
                fadeIn.setToValue(1);
                fadeIn.play();
            });
            fadeOut.play();
        } catch (Exception e) {
            System.out.println("Error updating card: " + e.getMessage());
        }
    }

    private void updateChart() {
        if (barChart != null) {
            barChart.getData().clear();
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("2025 Distribution ");
            
            for (Map.Entry<String, Integer> entry : monthlyDistributions.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            
            barChart.getData().add(series);
        }
    }

    private String getStringValue(JSONObject fields, String key) {
        try {
            if (fields.has(key)) {
                JSONObject fieldObj = fields.getJSONObject(key);
                if (fieldObj.has("stringValue")) {
                    return fieldObj.getString("stringValue");
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    // Data class for distribution records
    private static class DistributionData {
        String userName = "";
        String userEmail = "";
        String distributionDate = "";
        String itemsDistributed = "";
        String distributedBy = "";
    }

    // Existing methods remain the same...
    private void initilizedViewQuatapage() {
        displayStock pg = new displayStock();
        pg.setQuatapStage(dashboardStage);
        viewQuataScene = new Scene(pg.viewQuataScene(this::HandleBackButton), 1400, 900);
        pg.setQuataScene(viewQuataScene);
    }

    private void initilizUsermanagementpagedemo() {
        userdemo pg = new userdemo();
        pg.setUsermanagementStage(dashboardStage);
        usermanagementScene = new Scene(pg.UserManagementScene(this::HandleBackButton), 1400, 900);
        pg.setUsermanagementScene(usermanagementScene);
    }

    private void rationAllocation() {
        rationAllocation pg = new rationAllocation();
        pg.setRationAllocationStage(dashboardStage);
        rationallocaScene = new Scene(pg.RationAllocationMethod(this::HandleBackButton), 1400, 900);
        pg.setRationAllocationScene(rationallocaScene);
    }
    
     private void scannsystem() {
        scaningmethod pg = new scaningmethod();
        pg.setScStage(dashboardStage);
        scanScene = new Scene(pg.createscanBox(this::HandleBackButton), 1400, 900);
        pg.setScanniScene(scanScene);
    }
    
    private void history() {
        history pg = new history();
        pg.setHistoryStage(dashboardStage);
        hisScene = new Scene(pg.createScene1(this::HandleBackButton), 1400, 900);
        pg.setHistoryScene(hisScene);
    }
    
      private void Analysis() {
        AnalysisPage pg = new AnalysisPage();
        pg.setAnalysisStage(dashboardStage);
        analysisScene = new Scene(pg.createScene(this::HandleBackButton), 1400, 900);
        pg.setAnalysiScene(analysisScene);
    }

    private void HandleBackButton() {
        dashboardStage.setScene(dashboardScene);
        // Refresh data when returning to dashboard
        loadDashboardData();
    }
}