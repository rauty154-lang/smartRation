package com.smartration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class history {
    
    Scene historyScene;
    Stage historyStage;
    
    // Firebase Configuration - IMPORTANT: Move this to a config file for security!
    private final static String FIREBASE_API_KEY = "AIzaSyDWIPwQ5nn65g2f5WKgdQ6LnQsa2rH0NF0";
    private final static String PROJECT_ID = "smart-ration-550f9";
    
    // UI Components
    private VBox historyContainer;
    private Label statusLabel;
    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;
    private TextField searchField;
    private List<DistributionRecord> historyData = new ArrayList<>();
    
    public void setHistoryScene(Scene historyScene) {
        this.historyScene = historyScene;
    }

    public void setHistoryStage(Stage historyStage) {
        this.historyStage = historyStage;
    }

    public VBox createScene1(Runnable back) {
        // Main container with gradient background
        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background: linear-gradient(to bottom, #667eea, #764ba2);");
        
        // Header section
        VBox headerSection = createHeaderSection(back);
        
        // Content area
        VBox contentArea = new VBox(20);
        contentArea.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 20 20 0 0; " +
            "-fx-padding: 30; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, -5);"
        );
        
        // Filter section
        VBox filterSection = createFilterSection();
        
        // Action buttons section (only refresh button now)
        HBox actionSection = createActionSection();
        
        // History display area
        ScrollPane historyScrollPane = createHistoryDisplayArea();
        
        contentArea.getChildren().addAll(filterSection, actionSection, historyScrollPane);
        
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        mainContainer.getChildren().addAll(headerSection, contentArea);
        
        // Load history data on initialization
        loadHistoryData();
        
        return mainContainer;
    }

    private VBox createHeaderSection(Runnable back) {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 30, 20, 30));
        
        // Back button
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
        backButton.setOnAction(e -> back.run());
        
        // Title
        Text title = new Text("📊 Distribution History");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setFill(Color.WHITE);
        
        Text subtitle = new Text("View complete ration distribution records");
        subtitle.setFont(Font.font("System", 16));
        subtitle.setFill(Color.rgb(255, 255, 255, 0.8));
        
        HBox titleContainer = new HBox();
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleContainer.getChildren().addAll(new VBox(5, title, subtitle), spacer, backButton);
        
        header.getChildren().add(titleContainer);
        return header;
    }

    private VBox createFilterSection() {
        VBox filterSection = new VBox(15);
        filterSection.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-background-radius: 12; " +
            "-fx-padding: 20;"
        );
        
        Text filterTitle = new Text("🔍 Filter Options");
        filterTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        filterTitle.setFill(Color.web("#2c3e50"));
        
        // Date range filters
        HBox dateFilters = new HBox(15);
        dateFilters.setAlignment(Pos.CENTER_LEFT);
        
        VBox fromDateBox = new VBox(5);
        Label fromLabel = new Label("From Date:");
        fromLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        fromDatePicker = new DatePicker(LocalDate.now().minusMonths(1));
        fromDatePicker.setStyle("-fx-pref-width: 150;");
        fromDateBox.getChildren().addAll(fromLabel, fromDatePicker);
        
        VBox toDateBox = new VBox(5);
        Label toLabel = new Label("To Date:");
        toLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        toDatePicker = new DatePicker(LocalDate.now());
        toDatePicker.setStyle("-fx-pref-width: 150;");
        toDateBox.getChildren().addAll(toLabel, toDatePicker);
        
        dateFilters.getChildren().addAll(fromDateBox, toDateBox);
        
        // Search field
        searchField = new TextField();
        searchField.setPromptText("🔍 Search by user name, email, or ration card...");
        searchField.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 25; " +
            "-fx-padding: 12 20; " +
            "-fx-font-size: 14px; " +
            "-fx-border-color: #ddd; " +
            "-fx-border-radius: 25;"
        );
        searchField.setPrefHeight(40);
        
        // Filter buttons
        HBox filterButtons = new HBox(10);
        filterButtons.setAlignment(Pos.CENTER_LEFT);
        
        Button applyFilterBtn = createStyledButton("🔍 Apply Filters", "#2196F3");
        Button clearFilterBtn = createStyledButton("🗑 Clear Filters", "#6c757d");
        
        applyFilterBtn.setOnAction(e -> applyFilters());
        clearFilterBtn.setOnAction(e -> clearFilters());
        
        filterButtons.getChildren().addAll(applyFilterBtn, clearFilterBtn);
        
        filterSection.getChildren().addAll(filterTitle, dateFilters, searchField, filterButtons);
        return filterSection;
    }

    private HBox createActionSection() {
        HBox actionSection = new HBox(15);
        actionSection.setAlignment(Pos.CENTER_LEFT);
        
        // Only refresh button remains
        Button refreshBtn = createStyledButton("🔄 Refresh Data", "#28a745");
        refreshBtn.setOnAction(e -> loadHistoryData());
        
        statusLabel = new Label("Loading history data...");
        statusLabel.setFont(Font.font("System", 12));
        statusLabel.setTextFill(Color.web("#6c757d"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        actionSection.getChildren().addAll(refreshBtn, spacer, statusLabel);
        return actionSection;
    }

    private ScrollPane createHistoryDisplayArea() {
        historyContainer = new VBox(0);
        historyContainer.setPadding(new Insets(0));
        historyContainer.setFillWidth(true);
        historyContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        historyContainer.setMinWidth(Region.USE_PREF_SIZE);
        
        ScrollPane scrollPane = new ScrollPane(historyContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setPrefHeight(400);
        scrollPane.setMinHeight(400);
        scrollPane.setMaxHeight(600);
        scrollPane.setStyle(
            "-fx-background-color: white; " +
            "-fx-background: white; " +
            "-fx-border-color: #ddd; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8;"
        );
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return scrollPane;
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
        
        button.setOnMouseEntered(e -> 
            button.setStyle(button.getStyle() + "-fx-opacity: 0.9;")
        );
        button.setOnMouseExited(e -> 
            button.setStyle(button.getStyle().replace("-fx-opacity: 0.9;", ""))
        );
        
        return button;
    }

    private void loadHistoryData() {
        statusLabel.setText("🔄 Loading history data...");
        statusLabel.setTextFill(Color.web("#fd7e14"));
        
        Task<List<DistributionRecord>> loadTask = new Task<List<DistributionRecord>>() {
            @Override
            protected List<DistributionRecord> call() throws Exception {
                return fetchDistributionHistory();
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    historyData = getValue();
                    System.out.println("Retrieved " + historyData.size() + " records from Firebase");
                    
                    displayHistoryData(historyData);
                    statusLabel.setText("✅ Loaded " + historyData.size() + " distribution records");
                    statusLabel.setTextFill(Color.web("#28a745"));
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Throwable exception = getException();
                    System.out.println("Failed to load data: " + exception.getMessage());
                    exception.printStackTrace();
                    
                    statusLabel.setText("❌ Failed to load history data");
                    statusLabel.setTextFill(Color.web("#dc3545"));
                    displayErrorMessage();
                });
            }
        };
        
        new Thread(loadTask).start();
    }

    private List<DistributionRecord> fetchDistributionHistory() {
        List<DistributionRecord> records = new ArrayList<>();
        
        try {
            // FIXED: Search ONLY in distributionHistory collection for perfect tracking
            String urlStr = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + 
                           "/databases/(default)/documents/distributionHistory?key=" + FIREBASE_API_KEY;
            
            System.out.println("Fetching from distributionHistory collection");
            System.out.println("Request URL: " + urlStr);
            
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                
                String responseBody = sb.toString();
                System.out.println("Response Body Length: " + responseBody.length());
                
                JSONObject response = new JSONObject(responseBody);
                if (response.has("documents")) {
                    JSONArray documents = response.getJSONArray("documents");
                    System.out.println("Found " + documents.length() + " distribution records");
                    
                    for (int i = 0; i < documents.length(); i++) {
                        JSONObject doc = documents.getJSONObject(i);
                        JSONObject fields = doc.getJSONObject("fields");
                        
                        DistributionRecord record = new DistributionRecord();
                        record.userName = getStringValue(fields, "userName");
                        record.userEmail = getStringValue(fields, "userEmail");
                        record.rationCard = getStringValue(fields, "rationCard");
                        record.distributionDate = getStringValue(fields, "distributionDate");
                        record.itemsDistributed = getStringValue(fields, "itemsDistributed");
                        record.distributedBy = getStringValue(fields, "distributedBy");
                        record.distributionType = getStringValue(fields, "distributionType");
                        record.timestamp = getStringValue(fields, "timestamp");
                        
                        // Clean up the data for display
                        if (record.userName == null || record.userName.trim().isEmpty()) {
                            record.userName = "Unknown User";
                        }
                        if (record.userEmail == null || record.userEmail.trim().isEmpty()) {
                            record.userEmail = "No Email";
                        }
                        if (record.rationCard == null || record.rationCard.trim().isEmpty()) {
                            record.rationCard = "No Card";
                        }
                        if (record.itemsDistributed == null || record.itemsDistributed.trim().isEmpty()) {
                            record.itemsDistributed = "No Items";
                        } else {
                            // Remove question marks and clean up items
                            record.itemsDistributed = record.itemsDistributed.replace("?", "").trim();
                        }
                        if (record.distributionDate == null || record.distributionDate.trim().isEmpty()) {
                            record.distributionDate = LocalDate.now().toString();
                        }
                        
                        // Clean up "Distributed By" field
                        if (record.distributedBy == null || record.distributedBy.trim().isEmpty() 
                            || record.distributedBy.contains("null")) {
                            record.distributedBy = "System";
                        } else if (record.distributedBy.contains("@")) {
                            // If it contains email, convert to appropriate role
                            if (record.distributedBy.toLowerCase().contains("admin")) {
                                record.distributedBy = "Admin";
                            } else {
                                record.distributedBy = "Admin";
                            }
                        } else if (record.distributedBy.toLowerCase().contains("scanner")) {
                            record.distributedBy = "Scanner System";
                        } else if (record.distributedBy.toLowerCase().contains("admin")) {
                            record.distributedBy = "Admin";
                        }
                        
                        // Set distribution type for display
                        if (record.distributionType == null || record.distributionType.trim().isEmpty()) {
                            record.distributionType = "manual";
                        }
                        
                        records.add(record);
                    }
                } else {
                    System.out.println("No 'documents' field in distributionHistory response");
                }
            } else {
                // Print error response
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                System.out.println("Error Response: " + errorResponse.toString());
            }
            
        } catch (Exception e) {
            System.out.println("Exception in fetchDistributionHistory: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Total records to return: " + records.size());
        return records;
    }

    private void displayHistoryData(List<DistributionRecord> records) {
        // Clear existing content
        historyContainer.getChildren().clear();
        
        System.out.println("Displaying " + records.size() + " records");
        
        if (records.isEmpty()) {
            displayEmptyState();
            return;
        }
        
        // Create table wrapper for better organization
        VBox tableWrapper = new VBox(0);
        tableWrapper.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8;");
        tableWrapper.setPrefWidth(Region.USE_COMPUTED_SIZE);
        tableWrapper.setFillWidth(true);
        
        // Create table header
        HBox headerBox = createTableHeader();
        tableWrapper.getChildren().add(headerBox);
        
        // Add data rows
        for (int i = 0; i < records.size(); i++) {
            DistributionRecord record = records.get(i);
            System.out.println("Creating row " + i + " for: " + record.userName);
            
            HBox recordBox = createRecordRow(record, i);
            tableWrapper.getChildren().add(recordBox);
        }
        
        // Add the table wrapper to the main container
        historyContainer.getChildren().add(tableWrapper);
        
        // Force layout refresh
        Platform.runLater(() -> {
            historyContainer.requestLayout();
            tableWrapper.requestLayout();
            if (historyContainer.getParent() != null) {
                historyContainer.getParent().requestLayout();
            }
        });
    }

    // FIXED: Changed header background color from black to dark blue for better visibility
    private HBox createTableHeader() {
        HBox header = new HBox(0);
        header.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-padding: 15 10; " +
            "-fx-background-radius: 8 8 0 0; " +
            "-fx-border-color: #667eea;"
        );
        
        header.setPrefHeight(50);
        header.setMinHeight(50);
        header.setMaxHeight(50);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setFillHeight(true);
        
        String[] headerTexts = {"User Name", "Email", "Ration Card", "Items", "Date", "Distributed By", "Type"};
        double[] columnWidths = {120, 150, 100, 200, 100, 120, 80}; // Fixed column widths
        
        for (int i = 0; i < headerTexts.length; i++) {
            Label label = new Label(headerTexts[i]);
            label.setTextFill(Color.WHITE);
            label.setFont(Font.font("System", FontWeight.BOLD, 12));
            label.setPrefWidth(columnWidths[i]);
            label.setMinWidth(columnWidths[i]);
            label.setMaxWidth(columnWidths[i]);
            label.setAlignment(Pos.CENTER_LEFT);
            label.setPadding(new Insets(0, 5, 0, 5));
            label.setStyle("-fx-background-color: transparent;");
            header.getChildren().add(label);
        }
        
        return header;
    }

    private HBox createRecordRow(DistributionRecord record, int index) {
        HBox row = new HBox(0);
        
        // Alternate row colors for better readability
        String backgroundColor = (index % 2 == 0) ? "#f8f9fa" : "white";
        row.setStyle(
            "-fx-background-color: " + backgroundColor + "; " +
            "-fx-padding: 12 10; " +
            "-fx-border-color: #dee2e6; " +
            "-fx-border-width: 0 0 1 0;"
        );
        row.setPrefHeight(50);
        row.setMinHeight(50);
        row.setMaxHeight(50);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setFillHeight(true);
        
        // Column data with consistent widths
        String[] rowData = {
            record.userName,
            record.userEmail,
            record.rationCard,
            record.itemsDistributed,
            record.distributionDate,
            record.distributedBy,
            getDisplayType(record.distributionType)
        };
        
        double[] columnWidths = {120, 150, 100, 200, 100, 120, 80}; // Same as header
        
        for (int i = 0; i < rowData.length; i++) {
            Label label = createDataLabel(rowData[i], columnWidths[i]);
            row.getChildren().add(label);
        }
        
        // Add hover effect
        row.setOnMouseEntered(e -> {
            if (index % 2 == 0) {
                row.setStyle(row.getStyle().replace("#f8f9fa", "#e9ecef"));
            } else {
                row.setStyle(row.getStyle().replace("white", "#f8f9fa"));
            }
        });
        
        row.setOnMouseExited(e -> {
            if (index % 2 == 0) {
                row.setStyle(row.getStyle().replace("#e9ecef", "#f8f9fa"));
            } else {
                row.setStyle(row.getStyle().replace("#f8f9fa", "white"));
            }
        });
        
        return row;
    }
    
    private String getDisplayType(String distributionType) {
        if (distributionType == null || distributionType.trim().isEmpty()) {
            return "Manual";
        }
        
        switch (distributionType.toLowerCase()) {
            case "admin_allocation":
                return "📦 Admin";
            case "scanner":
                return "📱 Scanner";
            case "scanner_allocation":
                return "📱 Scanner";
            case "monthly":
                return "📅 Monthly";
            default:
                return "📋 Manual";
        }
    }
    
    private Label createDataLabel(String text, double width) {
        Label label = new Label(text);
        label.setFont(Font.font("System", 11));
        label.setTextFill(Color.web("#495057"));
        label.setPrefWidth(width);
        label.setMinWidth(width);
        label.setMaxWidth(width);
        label.setAlignment(Pos.CENTER_LEFT);
        label.setWrapText(false); // Disable wrap for better table appearance
        label.setPadding(new Insets(0, 5, 0, 5));
        label.setStyle("-fx-background-color: transparent;");
        
        // Truncate long text
        if (text != null && text.length() > 25) {
            label.setText(text.substring(0, 22) + "...");
            label.setTooltip(new Tooltip(text)); // Show full text on hover
        }
        
        return label;
    }

    private void displayEmptyState() {
        VBox emptyState = new VBox(20);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setPadding(new Insets(50));
        emptyState.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        
        Text emptyIcon = new Text("📊");
        emptyIcon.setFont(Font.font(48));
        
        Text emptyText = new Text("No distribution history found");
        emptyText.setFont(Font.font("System", FontWeight.BOLD, 18));
        emptyText.setFill(Color.web("#6c757d"));
        
        Text emptySubtext = new Text("Distribution records will appear here once users receive rations via Admin Allocation or Scanner System");
        emptySubtext.setFont(Font.font("System", 14));
        emptySubtext.setFill(Color.web("#adb5bd"));
        emptySubtext.setWrappingWidth(300);
        emptySubtext.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        emptyState.getChildren().addAll(emptyIcon, emptyText, emptySubtext);
        historyContainer.getChildren().add(emptyState);
    }

    private void displayErrorMessage() {
        VBox errorState = new VBox(20);
        errorState.setAlignment(Pos.CENTER);
        errorState.setPadding(new Insets(50));
        errorState.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        
        Text errorIcon = new Text("⚠");
        errorIcon.setFont(Font.font(48));
        
        Text errorText = new Text("Unable to load distribution history");
        errorText.setFont(Font.font("System", FontWeight.BOLD, 18));
        errorText.setFill(Color.web("#dc3545"));
        
        Text errorSubtext = new Text("Please check your connection and try again");
        errorSubtext.setFont(Font.font("System", 14));
        errorSubtext.setFill(Color.web("#6c757d"));
        
        Button retryButton = createStyledButton("🔄 Retry", "#28a745");
        retryButton.setOnAction(e -> loadHistoryData());
        
        errorState.getChildren().addAll(errorIcon, errorText, errorSubtext, retryButton);
        historyContainer.getChildren().add(errorState);
    }

    private void applyFilters() {
        List<DistributionRecord> filteredData = new ArrayList<>();
        String searchText = searchField.getText().toLowerCase().trim();
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        
        for (DistributionRecord record : historyData) {
            boolean matchesSearch = searchText.isEmpty() || 
                                  (record.userName != null && record.userName.toLowerCase().contains(searchText)) ||
                                  (record.userEmail != null && record.userEmail.toLowerCase().contains(searchText)) ||
                                  (record.rationCard != null && record.rationCard.toLowerCase().contains(searchText));
            
            boolean matchesDate = true;
            if (fromDate != null || toDate != null) {
                try {
                    LocalDate recordDate = LocalDate.parse(record.distributionDate);
                    if (fromDate != null && recordDate.isBefore(fromDate)) {
                        matchesDate = false;
                    }
                    if (toDate != null && recordDate.isAfter(toDate)) {
                        matchesDate = false;
                    }
                } catch (Exception e) {
                    matchesDate = false;
                }
            }
            
            if (matchesSearch && matchesDate) {
                filteredData.add(record);
            }
        }
        
        displayHistoryData(filteredData);
        statusLabel.setText("🔍 Showing " + filteredData.size() + " filtered records");
        statusLabel.setTextFill(Color.web("#2196F3"));
    }

    private void clearFilters() {
        searchField.clear();
        fromDatePicker.setValue(LocalDate.now().minusMonths(1));
        toDatePicker.setValue(LocalDate.now());
        displayHistoryData(historyData);
        statusLabel.setText("✅ Showing all " + historyData.size() + " records");
        statusLabel.setTextFill(Color.web("#28a745"));
    }

    private String getStringValue(JSONObject fields, String key) {
        try {
            if (fields.has(key)) {
                JSONObject fieldObj = fields.getJSONObject(key);
                
                // Try different value types that Firestore might use
                if (fieldObj.has("stringValue")) {
                    return fieldObj.getString("stringValue");
                } else if (fieldObj.has("integerValue")) {
                    return fieldObj.getString("integerValue");
                } else if (fieldObj.has("doubleValue")) {
                    return String.valueOf(fieldObj.getDouble("doubleValue"));
                } else if (fieldObj.has("booleanValue")) {
                    return String.valueOf(fieldObj.getBoolean("booleanValue"));
                } else if (fieldObj.has("timestampValue")) {
                    return fieldObj.getString("timestampValue");
                } else if (fieldObj.has("nullValue")) {
                    return "";
                }
            }
            return "";
        } catch (Exception e) {
            System.out.println("Error getting field '" + key + "': " + e.getMessage());
            return "";
        }
    }

    // Data class for distribution records
    private static class DistributionRecord {
        String userName = "";
        String userEmail = "";
        String rationCard = "";
        String distributionDate = "";
        String itemsDistributed = "";
        String distributedBy = "";
        String distributionType = "";
        String timestamp = "";
    }
}