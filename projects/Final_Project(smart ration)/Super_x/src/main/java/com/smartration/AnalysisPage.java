package com.smartration;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AnalysisPage {
    
    Scene analysiScene;
    Stage analysisStage;
    
    // Firebase Configuration - Same as AdminDashboard
    private final static String FIREBASE_API_KEY = "AIzaSyDWIPwQ5nn65g2f5WKgdQ6LnQsa2rH0NF0";
    private final static String PROJECT_ID = "smart-ration-550f9";

    
    // UI Components
    private VBox mainContainer;
    private GridPane statsGrid;
    private VBox chartsContainer;
    private Label statusLabel;
    private ProgressIndicator loadingIndicator;
    private Timeline autoRefreshTimer;
    
    // Data holders - Only Rice and Wheat
    private List<DistributionData> distributionHistory = new ArrayList<>();
    private Map<String, Integer> monthlyStats = new LinkedHashMap<>();
    private int totalUsers = 0;
    private int totalDistributions = 0;
    private int activeUsers = 0;
    private int currentRationLogs = 0;
    private int pendingAllocations = 0;
    private double distributionEfficiency = 0.0;
    private int wheatDistributed = 0;
    private int riceDistributed = 0;

    public void setAnalysiScene(Scene analysiScene) {
        this.analysiScene = analysiScene;
    }

    public void setAnalysisStage(Stage analysisStage) {
        this.analysisStage = analysisStage;
    }

    public VBox createScene(Runnable back) {
        // Main container with gradient background
        mainContainer = new VBox();
        mainContainer.setStyle("-fx-background: linear-gradient(to bottom, #667eea, #764ba2);");
        
        // Header section with animation
        VBox headerSection = createAnimatedHeader(back);
        
        // Content area with white background
        VBox contentArea = new VBox(20);
        contentArea.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 20 20 0 0; " +
            "-fx-padding: 30; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, -5);"
        );
        
        // Filter section
        HBox filterSection = createFilterSection();
        
        // Stats cards with animation - Updated for Rice & Wheat only
        statsGrid = createAnimatedStatsGrid();
        
        // Charts container
        chartsContainer = new VBox(25);
        
        // Loading indicator
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);
        loadingIndicator.setMaxSize(50, 50);
        
        // Status label
        statusLabel = new Label("Ready to load analytics data from Firebase");
        statusLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
        
        // Export buttons
        HBox exportSection = createExportSection();
        
        // Scroll pane for content
        ScrollPane scrollPane = new ScrollPane();
        VBox scrollContent = new VBox(20);
        scrollContent.getChildren().addAll(filterSection, statsGrid, chartsContainer, exportSection);
        scrollPane.setContent(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        contentArea.getChildren().addAll(scrollPane, 
                                       new HBox(10) {{ 
                                           setAlignment(Pos.CENTER); 
                                           getChildren().addAll(loadingIndicator, statusLabel); 
                                       }});
        
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        mainContainer.getChildren().addAll(headerSection, contentArea);
        
        // Load real data automatically with animation
        loadAnalyticsData();
        
        // Setup auto-refresh
        setupAutoRefresh();
        
        return mainContainer;
    }

    private VBox createAnimatedHeader(Runnable back) {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 30, 20, 30));
        
        // Back button with hover animation
        Button backButton = new Button("Back");
        backButton.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 8 16; " +
            "-fx-border-color: rgba(255,255,255,0.3); " +
            "-fx-border-radius: 20; " +
            "-fx-cursor: hand;"
        );
        
        // Add hover animation
        backButton.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), backButton);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });
        
        backButton.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), backButton);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
        
        backButton.setOnAction(e -> {
            stopAutoRefresh();
            back.run();
        });
        
        // Animated title
        Text title = new Text("📊 Analytics Dashboard");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setFill(Color.WHITE);
        
        Text subtitle = new Text(" Rice & Wheat distribution insights");
        subtitle.setFont(Font.font("System", 16));
        subtitle.setFill(Color.rgb(255, 255, 255, 0.8));
        
        // Add fade-in animation to title
        FadeTransition titleFade = new FadeTransition(Duration.millis(800), title);
        titleFade.setFromValue(0);
        titleFade.setToValue(1);
        titleFade.play();
        
        FadeTransition subtitleFade = new FadeTransition(Duration.millis(1000), subtitle);
        subtitleFade.setFromValue(0);
        subtitleFade.setToValue(1);
        subtitleFade.setDelay(Duration.millis(300));
        subtitleFade.play();
        
        HBox titleContainer = new HBox();
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleContainer.getChildren().addAll(new VBox(5, title, subtitle), spacer, backButton);
        
        header.getChildren().add(titleContainer);
        return header;
    }

    private HBox createFilterSection() {
        HBox filterSection = new HBox(15);
        filterSection.setAlignment(Pos.CENTER_LEFT);
        filterSection.setPadding(new Insets(0, 0, 10, 0));
        
        // Date range filters
        Label fromLabel = new Label("From:");
        fromLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        DatePicker fromDate = new DatePicker(LocalDate.now().minusMonths(1));
        fromDate.setStyle("-fx-pref-width: 140;");
        
        Label toLabel = new Label("To:");
        toLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        DatePicker toDate = new DatePicker(LocalDate.now());
        toDate.setStyle("-fx-pref-width: 140;");
        
        // Filter type
        Label filterLabel = new Label("View:");
        filterLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All Data", "Last 7 Days", "Last 30 Days", "This Month", "Last Month");
        filterCombo.setValue("All Data");
        filterCombo.setStyle("-fx-pref-width: 120;");
        
        // Apply filter button
        Button applyButton = createStyledButton("🔍 Apply Filter", "#2196F3");
        applyButton.setOnAction(e -> loadAnalyticsData());
        
        // Refresh button
        Button refreshButton = createStyledButton("🔄 Refresh Data", "#28a745");
        refreshButton.setOnAction(e -> {
            // Add spin animation
            Timeline spin = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(refreshButton.rotateProperty(), 0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(refreshButton.rotateProperty(), 360))
            );
            spin.play();
            loadAnalyticsData();
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        filterSection.getChildren().addAll(fromLabel, fromDate, toLabel, toDate, 
                                          filterLabel, filterCombo, applyButton, refreshButton, spacer);
        return filterSection;
    }

    private GridPane createAnimatedStatsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);
        
        // Create animated stat cards - Updated for Rice & Wheat only (2x3 grid)
        VBox totalUsersCard = createAnimatedStatCard("👥", "Total Users", "Loading...", "#4CAF50", 0);
        VBox totalDistCard = createAnimatedStatCard("📦", "Total Distributions", "Loading...", "#2196F3", 200);
        VBox wheatCard = createAnimatedStatCard("🌾", "Wheat Distributed", "Loading...", "#FF9800", 400);
        VBox riceCard = createAnimatedStatCard("🍚", "Rice Distributed", "Loading...", "#9C27B0", 600);
        VBox pendingCard = createAnimatedStatCard("⏳", "Pending Allocations", "Loading...", "#F44336", 800);
        VBox efficiencyCard = createAnimatedStatCard("📊", "Distribution Efficiency", "Loading...", "#00BCD4", 1000);
        
        // Add to grid (2x3 layout)
        grid.add(totalUsersCard, 0, 0);
        grid.add(totalDistCard, 1, 0);
        grid.add(wheatCard, 2, 0);
        grid.add(riceCard, 0, 1);
        grid.add(pendingCard, 1, 1);
        grid.add(efficiencyCard, 2, 1);
        
        return grid;
    }

    private VBox createAnimatedStatCard(String icon, String title, String value, String color, long delay) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(180, 120);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-padding: 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); " +
            "-fx-border-color: " + color + "; " +
            "-fx-border-width: 0 0 3 0;"
        );
        
        // Icon with pulse animation
        Text iconText = new Text(icon);
        iconText.setFont(Font.font(28));
        
        // Create pulse animation for icon
        ScaleTransition pulse = new ScaleTransition(Duration.millis(1000), iconText);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.2);
        pulse.setToY(1.2);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.setDelay(Duration.millis(delay));
        
        Text titleText = new Text(title);
        titleText.setFont(Font.font("System", FontWeight.NORMAL, 12));
        titleText.setFill(Color.rgb(100, 100, 100));
        
        Text valueText = new Text(value);
        valueText.setFont(Font.font("System", FontWeight.BOLD, 18));
        valueText.setFill(Color.web(color));
        
        card.getChildren().addAll(iconText, titleText, valueText);
        
        // Add slide-in animation
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), card);
        slideIn.setFromY(50);
        slideIn.setToY(0);
        slideIn.setDelay(Duration.millis(delay));
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), card);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setDelay(Duration.millis(delay));
        
        ParallelTransition animation = new ParallelTransition(slideIn, fadeIn);
        animation.play();
        
        // Add hover effect
        card.setOnMouseEntered(e -> {
            ScaleTransition hover = new ScaleTransition(Duration.millis(200), card);
            hover.setToX(1.05);
            hover.setToY(1.05);
            hover.play();
            pulse.play();
        });
        
        card.setOnMouseExited(e -> {
            ScaleTransition unhover = new ScaleTransition(Duration.millis(200), card);
            unhover.setToX(1.0);
            unhover.setToY(1.0);
            unhover.play();
            pulse.stop();
        });
        
        return card;
    }

    private HBox createExportSection() {
        HBox exportSection = new HBox(15);
        exportSection.setAlignment(Pos.CENTER_LEFT);
        exportSection.setPadding(new Insets(20, 0, 0, 0));
        
        Text exportLabel = new Text("📤 Export Reports:");
        exportLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        exportLabel.setFill(Color.web("#2c3e50"));
        
        Button pdfButton = createStyledButton("📄 Export PDF", "#dc3545");
        Button csvButton = createStyledButton("📊 Export CSV", "#fd7e14");
        Button summaryButton = createStyledButton("📋 Summary Report", "#6f42c1");
        
        pdfButton.setOnAction(e -> exportToPDF());
        csvButton.setOnAction(e -> exportToCSV());
        summaryButton.setOnAction(e -> generateSummaryReport());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        exportSection.getChildren().addAll(exportLabel, pdfButton, csvButton, summaryButton, spacer);
        return exportSection;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 8 16; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"
        );
        
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });
        
        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
        
        return button;
    }

    // Real Data Loading Methods
    private void loadAnalyticsData() {
        loadingIndicator.setVisible(true);
        statusLabel.setText("🔄 Loading Rice & Wheat analytics");
        
        Task<Boolean> loadTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    // Initialize with zero values
                    initializeDataHolders();
                    
                    // Fetch real data from Firebase
                    fetchDistributionData();
                    fetchUserStats();
                    
                    // Calculate analytics from real data
                    calculateAnalytics();
                    
                    return true;
                } catch (Exception e) {
                    System.out.println("Error loading analytics data: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    if (totalDistributions > 0 || totalUsers > 0) {
                        statusLabel.setText("✅ Real Firebase Rice & Wheat data loaded successfully");
                        System.out.println("✅ Analytics dashboard updated with real Firebase data");
                    } else {
                        statusLabel.setText("⚠ No data found in Firebase - showing zero values");
                        System.out.println("⚠ No data found in Firebase - analytics showing zero values");
                    }
                    
                    updateStatsCards();
                    createCharts();
                    
                    // Debug current state
                    debugCurrentData();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    statusLabel.setText("❌ Failed to connect to Firebase - showing zero values");
                    System.out.println("❌ Failed to load Firebase analytics data");
                    updateStatsCards();
                    createCharts();
                });
            }
        };
        
        new Thread(loadTask).start();
    }

    private void initializeDataHolders() {
        totalUsers = 0;
        totalDistributions = 0;
        activeUsers = 0;
        currentRationLogs = 0;
        pendingAllocations = 0;
        distributionEfficiency = 0.0;
        wheatDistributed = 0;
        riceDistributed = 0;
        
        distributionHistory.clear();
        monthlyStats.clear();
    }

    // Same Firebase methods as AdminDashboard for consistency
    private void fetchDistributionData() {
        try {
            String urlStr = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + 
                           "/databases/(default)/documents/distributionHistory";
            
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000); // 15 second timeout
            conn.setReadTimeout(15000);    // 15 second read timeout
            
            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                
                JSONObject response = new JSONObject(sb.toString());
                if (response.has("documents")) {
                    JSONArray documents = response.getJSONArray("documents");
                    distributionHistory.clear();
                    
                    System.out.println("📦 Analytics: Found " + documents.length() + " distribution documents in Firebase");
                    
                    for (int i = 0; i < documents.length(); i++) {
                        JSONObject doc = documents.getJSONObject(i);
                        if (doc.has("fields")) {
                            JSONObject fields = doc.getJSONObject("fields");
                            
                            DistributionData data = new DistributionData();
                            data.userName = getStringValue(fields, "userName");
                            data.userEmail = getStringValue(fields, "userEmail");
                            data.distributionDate = getStringValue(fields, "distributionDate");
                            data.itemsDistributed = getStringValue(fields, "itemsDistributed");
                            data.distributedBy = getStringValue(fields, "distributedBy");
                            
                            // Validate and add data
                            if (!data.userEmail.trim().isEmpty() || !data.distributionDate.trim().isEmpty()) {
                                distributionHistory.add(data);
                            }
                        }
                    }
                    
                    System.out.println("📊 Analytics: Loaded " + distributionHistory.size() + " valid distribution records");
                } else {
                    System.out.println("⚠ Analytics: No distribution documents found in Firebase");
                }
            } else {
                System.out.println("❌ Analytics: Firebase API returned HTTP " + conn.getResponseCode());
            }
            conn.disconnect();
            
        } catch (Exception e) {
            System.out.println("❌ Analytics: Error fetching distribution data: " + e.getMessage());
            e.printStackTrace();
            distributionHistory.clear();
        }
    }

    private void fetchUserStats() {
        try {
            String urlStr = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + 
                           "/databases/(default)/documents/users";
            
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            
            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                
                JSONObject response = new JSONObject(sb.toString());
                if (response.has("documents")) {
                    JSONArray documents = response.getJSONArray("documents");
                    totalUsers = documents.length();
                    System.out.println("👥 Analytics: Found " + totalUsers + " users in Firebase");
                } else {
                    System.out.println("⚠ Analytics: No user documents found in Firebase");
                    totalUsers = 0;
                }
            } else {
                System.out.println("❌ Analytics: User fetch returned HTTP " + conn.getResponseCode());
                totalUsers = 0;
            }
            conn.disconnect();
            
        } catch (Exception e) {
            System.out.println("❌ Analytics: Error fetching user stats: " + e.getMessage());
            totalUsers = 0;
        }
    }

    // Calculate analytics - Only Rice and Wheat
    private void calculateAnalytics() {
        totalDistributions = distributionHistory.size();
        
        // Current ration logs (distributions in current month)
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        currentRationLogs = (int) distributionHistory.stream()
            .filter(d -> d.distributionDate != null && d.distributionDate.startsWith(currentMonth))
            .count();
        
        // Active users (unique users who received distributions)
        Set<String> uniqueUsers = new HashSet<>();
        for (DistributionData data : distributionHistory) {
            if (data.userEmail != null && !data.userEmail.trim().isEmpty()) {
                uniqueUsers.add(data.userEmail.toLowerCase().trim());
            }
        }
        activeUsers = uniqueUsers.size();
        
        // Calculate item-wise distributions - Only Rice and Wheat
        wheatDistributed = 0;
        riceDistributed = 0;
        
        for (DistributionData data : distributionHistory) {
            if (data.itemsDistributed != null && !data.itemsDistributed.trim().isEmpty()) {
                String items = data.itemsDistributed.toLowerCase();
                
                // Extract quantities for each item type
                if (items.contains("wheat")) {
                    wheatDistributed += extractQuantity(items, "wheat");
                }
                if (items.contains("rice")) {
                    riceDistributed += extractQuantity(items, "rice");
                }
            }
        }
        
        // Calculate pending allocations
        pendingAllocations = Math.max(0, totalUsers - activeUsers);
        
        // Calculate distribution efficiency
        if (totalUsers > 0) {
            distributionEfficiency = (activeUsers * 100.0) / totalUsers;
        } else {
            distributionEfficiency = 0.0;
        }
        
        System.out.println("📊 Analytics calculated from real Firebase data (Rice & Wheat only):");
        System.out.println("   Total Users: " + totalUsers);
        System.out.println("   Total Distributions: " + totalDistributions);
        System.out.println("   Current Month Logs: " + currentRationLogs);
        System.out.println("   Active Users: " + activeUsers);
        System.out.println("   Pending Allocations: " + pendingAllocations);
        System.out.println("   Distribution Efficiency: " + String.format("%.1f%%", distributionEfficiency));
        System.out.println("   Items - Wheat: " + wheatDistributed + "kg, Rice: " + riceDistributed + "kg");
    }

    private int extractQuantity(String items, String itemType) {
        try {
            // Look for patterns like "wheat - 10kg" or "wheat 10kg" or just "10" near itemType
            String[] parts = items.split(",");
            for (String part : parts) {
                if (part.contains(itemType)) {
                    // Extract numbers from the part containing the item
                    String[] words = part.trim().split("\\s+");
                    for (String word : words) {
                        // Remove common suffixes and extract number
                        String cleanWord = word.replaceAll("[^0-9.]", "");
                        if (!cleanWord.isEmpty() && cleanWord.matches("\\d+(\\.\\d+)?")) {
                            return (int) Math.round(Double.parseDouble(cleanWord));
                        }
                    }
                }
            }
            return 1; // Default quantity if no specific number found
        } catch (Exception e) {
            return 1;
        }
    }

    private void updateStatsCards() {
        // Update the stat cards with real data - Rice & Wheat only
        updateStatCard(0, 0, String.valueOf(totalUsers));                    // Total Users
        updateStatCard(1, 0, String.valueOf(totalDistributions));           // Total Distributions
        updateStatCard(2, 0, String.valueOf(wheatDistributed) + " kg");     // Wheat Distributed
        updateStatCard(0, 1, String.valueOf(riceDistributed) + " kg");      // Rice Distributed
        updateStatCard(1, 1, String.valueOf(pendingAllocations));          // Pending Allocations
        updateStatCard(2, 1, String.format("%.1f%%", distributionEfficiency)); // Distribution Efficiency
    }

    private void updateStatCard(int col, int row, String value) {
        try {
            VBox card = (VBox) statsGrid.getChildren().get(row * 3 + col);
            Text valueText = (Text) card.getChildren().get(2);
            
            // Animate the value change
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), valueText);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                valueText.setText(value);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(200), valueText);
                fadeIn.setToValue(1);
                fadeIn.play();
            });
            fadeOut.play();
        } catch (Exception e) {
            System.out.println("Error updating stat card: " + e.getMessage());
        }
    }

    private void createCharts() {
        chartsContainer.getChildren().clear();
        
        // Distribution Status Pie Chart
        PieChart distributionPieChart = createDistributionPieChart();
        
        // Stock Display Container
        VBox stockDisplayContainer = createStockDisplayContainer();
        
        // Add charts with animation - REMOVED the bar chart section
        VBox pieChartContainer = createChartContainer("📊 Distribution Status", distributionPieChart);
        
        chartsContainer.getChildren().addAll(pieChartContainer, stockDisplayContainer);
        
        // Animate charts appearance
        animateChartsIn();
    }

    private VBox createStockDisplayContainer() {
        VBox stockContainer = new VBox(20);
        stockContainer.setAlignment(Pos.CENTER);
        stockContainer.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-padding: 25; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        // Title
        Text titleText = new Text("📦 Current Rice & Wheat Stock Inventory");
        titleText.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleText.setFill(Color.web("#2c3e50"));
        
        // Separator line
        Region separator = new Region();
        separator.setPrefHeight(2);
        separator.setStyle("-fx-background-color: linear-gradient(to right, transparent, #e0e0e0, transparent);");
        
        // Stock items grid - Only Rice & Wheat (1x2 layout)
        HBox stockGrid = new HBox(30);
        stockGrid.setAlignment(Pos.CENTER);
        
        // Create stock item cards - Only Rice & Wheat
        VBox wheatStockCard = createStockItemCard("🌾", "Wheat", wheatDistributed, "kg", "#FF9800");
        VBox riceStockCard = createStockItemCard("🍚", "Rice", riceDistributed, "kg", "#9C27B0");
        
        // Add to horizontal layout
        stockGrid.getChildren().addAll(wheatStockCard, riceStockCard);
        
        // Total stock summary
        HBox totalSummary = createTotalStockSummary();
        
        stockContainer.getChildren().addAll(titleText, separator, stockGrid, totalSummary);
        return stockContainer;
    }

    private VBox createStockItemCard(String icon, String itemName, int quantity, String unit, String color) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(220, 160); // Slightly larger since only 2 cards
        card.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-background-radius: 15; " +
            "-fx-padding: 25; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);"
        );
        
        // Icon
        Text iconText = new Text(icon);
        iconText.setFont(Font.font(36)); // Larger icon
        iconText.setFill(Color.WHITE);
        
        // Item name
        Text nameText = new Text(itemName);
        nameText.setFont(Font.font("System", FontWeight.BOLD, 18)); // Larger text
        nameText.setFill(Color.WHITE);
        
        // Quantity with unit
        Text quantityText = new Text(quantity + " " + unit);
        quantityText.setFont(Font.font("System", FontWeight.BOLD, 24)); // Larger quantity
        quantityText.setFill(Color.WHITE);
        
        // Status indicator
        Text statusText;
        if (quantity > 0) {
            statusText = new Text("✅ In Stock");
            statusText.setFill(Color.rgb(200, 255, 200));
        } else {
            statusText = new Text("❌ Out of Stock");
            statusText.setFill(Color.rgb(255, 200, 200));
        }
        statusText.setFont(Font.font("System", FontWeight.NORMAL, 14));
        
        card.getChildren().addAll(iconText, nameText, quantityText, statusText);
        
        // Add hover effect
        card.setOnMouseEntered(e -> {
            ScaleTransition hover = new ScaleTransition(Duration.millis(200), card);
            hover.setToX(1.05);
            hover.setToY(1.05);
            hover.play();
        });
        
        card.setOnMouseExited(e -> {
            ScaleTransition unhover = new ScaleTransition(Duration.millis(200), card);
            unhover.setToX(1.0);
            unhover.setToY(1.0);
            unhover.play();
        });
        
        return card;
    }

    private HBox createTotalStockSummary() {
        HBox summaryBox = new HBox(20);
        summaryBox.setAlignment(Pos.CENTER);
        summaryBox.setPadding(new Insets(15, 0, 0, 0));
        
        // Total distributed - Only Rice & Wheat
        int totalDistributedItems = wheatDistributed + riceDistributed;
        
        VBox totalCard = new VBox(5);
        totalCard.setAlignment(Pos.CENTER);
        totalCard.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 15 25; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );
        
        Text totalLabel = new Text("📊 Total Rice & Wheat Distributed");
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        totalLabel.setFill(Color.WHITE);
        
        Text totalValue = new Text(totalDistributedItems + " kg");
        totalValue.setFont(Font.font("System", FontWeight.BOLD, 18));
        totalValue.setFill(Color.WHITE);
        
        totalCard.getChildren().addAll(totalLabel, totalValue);
        
        // Stock status summary
        VBox statusCard = new VBox(5);
        statusCard.setAlignment(Pos.CENTER);
        statusCard.setStyle(
            "-fx-background-color: " + (totalDistributedItems > 0 ? "#28a745" : "#dc3545") + "; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 15 25; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );
        
        Text statusLabel = new Text("🎯 Distribution Status");
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        statusLabel.setFill(Color.WHITE);
        
        Text statusValue;
        if (totalDistributedItems > 0) {
            statusValue = new Text("✅ Active");
        } else {
            statusValue = new Text("⚠ No Distributions");
        }
        statusValue.setFont(Font.font("System", FontWeight.BOLD, 18));
        statusValue.setFill(Color.WHITE);
        
        statusCard.getChildren().addAll(statusLabel, statusValue);
        
        summaryBox.getChildren().addAll(totalCard, statusCard);
        return summaryBox;
    }

    private PieChart createDistributionPieChart() {
        PieChart pieChart = new PieChart();
        
        int distributed = activeUsers;  // Users who received distributions
        int pending = pendingAllocations; // Users who haven't received distributions
        
        if (distributed > 0) {
            PieChart.Data distributedData = new PieChart.Data("Distributed (" + distributed + ")", distributed);
            pieChart.getData().add(distributedData);
        }
        
        if (pending > 0) {
            PieChart.Data pendingData = new PieChart.Data("Pending (" + pending + ")", pending);
            pieChart.getData().add(pendingData);
        }
        
        // If no real data, show "No Data Available"
        if (pieChart.getData().isEmpty()) {
            pieChart.getData().add(new PieChart.Data("No Data Available", 1));
        }
        
        pieChart.setTitle("Distribution Status (Real Firebase Data)");
        pieChart.setPrefSize(400, 300);
        pieChart.setStyle("-fx-background-color: transparent;");
        pieChart.setLegendVisible(true);
        
        return pieChart;
    }

    private VBox createChartContainer(String title, javafx.scene.Node chart) {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-padding: 25; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        Text titleText = new Text(title);
        titleText.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleText.setFill(Color.web("#2c3e50"));
        
        // Add a subtle separator line
        Region separator = new Region();
        separator.setPrefHeight(2);
        separator.setStyle("-fx-background-color: linear-gradient(to right, transparent, #e0e0e0, transparent);");
        
        container.getChildren().addAll(titleText, separator, chart);
        return container;
    }

    private void animateChartsIn() {
        for (int i = 0; i < chartsContainer.getChildren().size(); i++) {
            javafx.scene.Node chart = chartsContainer.getChildren().get(i);
            
            // Set initial state
            chart.setOpacity(0);
            chart.setTranslateX(50);
            
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(800), chart);
            slideIn.setFromX(50);
            slideIn.setToX(0);
            slideIn.setDelay(Duration.millis(i * 300));
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(800), chart);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setDelay(Duration.millis(i * 300));
            
            ParallelTransition animation = new ParallelTransition(slideIn, fadeIn);
            animation.play();
        }
    }

    private void setupAutoRefresh() {
        // Auto-refresh every 45 seconds
        autoRefreshTimer = new Timeline(
            new javafx.animation.KeyFrame(Duration.seconds(45), e -> {
                System.out.println("🔄 Auto-refreshing Rice & Wheat analytics data...");
                loadAnalyticsData();
            })
        );
        autoRefreshTimer.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimer.play();
    }

    private void stopAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.stop();
        }
    }

    // Export methods - Updated for Rice & Wheat only
    private void exportToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF Report");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("rice_wheat_analytics_report_" + 
                                     LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf");
        
        java.io.File file = fileChooser.showSaveDialog(analysisStage);
        if (file != null) {
            generatePDFReport(file);
        }
    }

    private void exportToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV Report");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        fileChooser.setInitialFileName("rice_wheat_analytics_data_" + 
                                     LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv");
        
        java.io.File file = fileChooser.showSaveDialog(analysisStage);
        if (file != null) {
            generateCSVReport(file);
        }
    }

    private void generateSummaryReport() {
        Alert summaryAlert = new Alert(Alert.AlertType.INFORMATION);
        summaryAlert.setTitle("Rice & Wheat Analytics Summary Report");
        summaryAlert.setHeaderText("Smart Ration System - Rice & Wheat Stock Summary");
        
        StringBuilder summary = new StringBuilder();
        summary.append("📊 RICE & WHEAT STOCK ANALYTICS SUMMARY\n");
        summary.append("Generated on: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        summary.append("Data Source: Firebase Firestore Database\n");
        summary.append("Focus: Rice & Wheat Distribution Only\n");
        summary.append("=".repeat(50)).append("\n\n");
        
        summary.append("👥 USER STATISTICS:\n");
        summary.append("Total Registered Users: ").append(totalUsers).append("\n");
        summary.append("Active Users (Received Distributions): ").append(activeUsers).append("\n");
        summary.append("Pending Allocations: ").append(pendingAllocations).append("\n");
        summary.append("Distribution Efficiency: ").append(String.format("%.1f%%", distributionEfficiency)).append("\n\n");
        
        summary.append("📦 DISTRIBUTION STATISTICS:\n");
        summary.append("Total Distributions: ").append(totalDistributions).append("\n");
        summary.append("Current Month Distributions: ").append(currentRationLogs).append("\n\n");
        
        summary.append("📋 CURRENT RICE & WHEAT STOCK INVENTORY:\n");
        summary.append("🌾 Wheat Stock: ").append(wheatDistributed).append(" kg ").append(wheatDistributed > 0 ? "✅ Available" : "❌ Out of Stock").append("\n");
        summary.append("🍚 Rice Stock: ").append(riceDistributed).append(" kg ").append(riceDistributed > 0 ? "✅ Available" : "❌ Out of Stock").append("\n");
        summary.append("📊 Total Rice & Wheat Stock: ").append(wheatDistributed + riceDistributed).append(" kg\n\n");
        
        summary.append("🎯 STOCK STATUS OVERVIEW:\n");
        int itemsInStock = 0;
        if (wheatDistributed > 0) itemsInStock++;
        if (riceDistributed > 0) itemsInStock++;
        
        summary.append("Items in Stock: ").append(itemsInStock).append(" out of 2 categories (Rice & Wheat)\n");
        summary.append("Stock Status: ");
        if (itemsInStock == 2) {
            summary.append("🟢 Fully Stocked (Both Rice & Wheat Available)\n");
        } else if (itemsInStock == 1) {
            summary.append("🟡 Partially Stocked (One Item Available)\n");
        } else {
            summary.append("🔴 Critical - No Rice or Wheat Stock Available\n");
        }
        
        summary.append("\n").append("=".repeat(50)).append("\n");
        summary.append("⚡ Data synchronized with AdminDashboard\n");
        summary.append("📦 Rice & Wheat inventory updated \n");
        summary.append("Report generated by Smart Ration Analytics System");
        
        summaryAlert.setContentText(summary.toString());
        summaryAlert.getDialogPane().setPrefWidth(650);
        summaryAlert.getDialogPane().setPrefHeight(550);
        summaryAlert.showAndWait();
    }

    private void generatePDFReport(java.io.File file) {
        Task<Boolean> pdfTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    StringBuilder content = new StringBuilder();
                    content.append("SMART RATION SYSTEM - RICE & WHEAT ANALYTICS REPORT\n");
                    content.append("Generated on: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
                    content.append("Data Source: Firebase Firestore Database \n");
                    content.append("Focus: Rice & Wheat Distribution Only\n");
                    content.append("=".repeat(80)).append("\n\n");
                    
                    // Executive Summary
                    content.append("EXECUTIVE SUMMARY\n");
                    content.append("-".repeat(40)).append("\n");
                    content.append("Total Users: ").append(totalUsers).append("\n");
                    content.append("Total Distributions: ").append(totalDistributions).append("\n");
                    content.append("Active Users: ").append(activeUsers).append("\n");
                    content.append("Distribution Efficiency: ").append(String.format("%.1f%%", distributionEfficiency)).append("\n");
                    content.append("Current Month Distributions: ").append(currentRationLogs).append("\n");
                    content.append("Pending Allocations: ").append(pendingAllocations).append("\n\n");
                    
                    // Rice & Wheat Stock Inventory Summary
                    content.append("RICE & WHEAT STOCK INVENTORY\n");
                    content.append("-".repeat(40)).append("\n");
                    content.append("Wheat Stock: ").append(wheatDistributed).append(" kg").append(wheatDistributed > 0 ? " (Available)" : " (Out of Stock)").append("\n");
                    content.append("Rice Stock: ").append(riceDistributed).append(" kg").append(riceDistributed > 0 ? " (Available)" : " (Out of Stock)").append("\n");
                    content.append("Total Rice & Wheat Stock: ").append(wheatDistributed + riceDistributed).append(" kg\n\n");
                    
                    // Detailed Distribution History
                    content.append("DETAILED DISTRIBUTION HISTORY\n");
                    content.append("-".repeat(40)).append("\n");
                    content.append(String.format("%-20s %-25s %-15s %-25s %-15s\n", 
                                                "User Name", "Email", "Date", "Items", "Distributed By"));
                    content.append("-".repeat(100)).append("\n");
                    
                    for (DistributionData record : distributionHistory) {
                        content.append(String.format("%-20s %-25s %-15s %-25s %-15s\n",
                                                    truncate(record.userName, 20),
                                                    truncate(record.userEmail, 25),
                                                    record.distributionDate,
                                                    truncate(record.itemsDistributed, 25),
                                                    truncate(record.distributedBy, 15)));
                    }
                    
                    content.append("\n").append("=".repeat(80)).append("\n");
                    content.append("End of Rice & Wheat Analytics Report\n");
                    content.append("Smart Ration System - Rice & Wheat Analytics Module\n");
                    content.append("Data synchronized with AdminDashboard\n");
                    
                    fos.write(content.toString().getBytes());
                    return true;
                }
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("PDF Export");
                    alert.setHeaderText("Export Successful");
                    alert.setContentText("Rice & Wheat PDF report has been saved successfully!\n\nFile: " + file.getName());
                    alert.showAndWait();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("PDF Export Error");
                    alert.setHeaderText("Export Failed");
                    alert.setContentText("Failed to generate PDF report. Please try again.");
                    alert.showAndWait();
                });
            }
        };
        
        new Thread(pdfTask).start();
    }

    private void generateCSVReport(java.io.File file) {
        Task<Boolean> csvTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    StringBuilder csv = new StringBuilder();
                    
                    // CSV Headers
                    csv.append("User Name,Email,Distribution Date,Items Distributed,Distributed By,Month,Year\n");
                    
                    // CSV Data
                    for (DistributionData record : distributionHistory) {
                        csv.append("\"").append(escapeForCsv(record.userName)).append("\",");
                        csv.append("\"").append(escapeForCsv(record.userEmail)).append("\",");
                        csv.append("\"").append(escapeForCsv(record.distributionDate)).append("\",");
                        csv.append("\"").append(escapeForCsv(record.itemsDistributed)).append("\",");
                        csv.append("\"").append(escapeForCsv(record.distributedBy)).append("\",");
                        
                        try {
                            LocalDate date = LocalDate.parse(record.distributionDate);
                            csv.append("\"").append(date.format(DateTimeFormatter.ofPattern("MMM"))).append("\",");
                            csv.append("\"").append(date.getYear()).append("\"");
                        } catch (Exception e) {
                            csv.append("\"\",\"\"");
                        }
                        csv.append("\n");
                    }
                    
                    // Add summary data
                    csv.append("\n\nSUMMARY DATA (RICE & WHEAT)\n");
                    csv.append("Metric,Value\n");
                    csv.append("Total Users,").append(totalUsers).append("\n");
                    csv.append("Total Distributions,").append(totalDistributions).append("\n");
                    csv.append("Active Users,").append(activeUsers).append("\n");
                    csv.append("Current Month Distributions,").append(currentRationLogs).append("\n");
                    csv.append("Pending Allocations,").append(pendingAllocations).append("\n");
                    csv.append("Distribution Efficiency,").append(String.format("%.1f%%", distributionEfficiency)).append("\n");
                    
                    // Rice & Wheat stock inventory data
                    csv.append("\n\nRICE & WHEAT STOCK INVENTORY \n");
                    csv.append("Item,Quantity,Unit,Status\n");
                    csv.append("Wheat,").append(wheatDistributed).append(",kg,").append(wheatDistributed > 0 ? "In Stock" : "Out of Stock").append("\n");
                    csv.append("Rice,").append(riceDistributed).append(",kg,").append(riceDistributed > 0 ? "In Stock" : "Out of Stock").append("\n");
                    csv.append("Total Rice & Wheat,").append(wheatDistributed + riceDistributed).append(",kg,").append((wheatDistributed + riceDistributed) > 0 ? "Available" : "Empty").append("\n");
                    
                    fos.write(csv.toString().getBytes());
                    return true;
                }
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("CSV Export");
                    alert.setHeaderText("Export Successful");
                    alert.setContentText("Rice & Wheat CSV report has been saved successfully!\n\nFile: " + file.getName());
                    alert.showAndWait();
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("CSV Export Error");
                    alert.setHeaderText("Export Failed");
                    alert.setContentText("Failed to generate CSV report. Please try again.");
                    alert.showAndWait();
                });
            }
        };
        
        new Thread(csvTask).start();
    }

    // Utility methods
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

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    private String escapeForCsv(String text) {
        if (text == null) return "";
        return text.replace("\"", "\"\"");
    }

    private void debugCurrentData() {
        System.out.println("=== 📊 RICE & WHEAT ANALYTICS DEBUG DATA ===");
        System.out.println("🔥 Firebase Connection: SUCCESS");
        System.out.println("👥 Total Users: " + totalUsers);
        System.out.println("📦 Total Distributions: " + totalDistributions);
        System.out.println("✅ Active Users: " + activeUsers);
        System.out.println("📋 Current Month Logs: " + currentRationLogs);
        System.out.println("⏳ Pending Allocations: " + pendingAllocations);
        System.out.println("📊 Distribution Efficiency: " + String.format("%.1f%%", distributionEfficiency));
        System.out.println("🌾 Wheat Stock: " + wheatDistributed + " kg");
        System.out.println("🍚 Rice Stock: " + riceDistributed + " kg");
        System.out.println("📋 Distribution History Size: " + distributionHistory.size());
        System.out.println("=== END RICE & WHEAT ANALYTICS DEBUG ===");
    }

    // Data class for distribution records
    private static class DistributionData {
        String userName = "";
        String userEmail = "";
        String distributionDate = "";
        String itemsDistributed = "";
        String distributedBy = "";
    }
}