package com.smartration;

import javafx.event.ActionEvent;
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
import javafx.concurrent.Task;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class scaningmethod {
    Scene scanniScene;
    Stage scStage;
    
    // Firebase Configuration
    private final static String FIREBASE_API_KEY = "AIzaSyCw_hJmevrQsmsQ1Mg0LKBylH7z6zLcsNw";
    private final static String PROJECT_ID = "new-ration";
    
    // Enhanced Color scheme constants
    private static final String PRIMARY_COLOR = "#1a237e";
    private static final String SECONDARY_COLOR = "#3949ab";
    private static final String ACCENT_COLOR = "#e53935";
    private static final String SUCCESS_COLOR = "#43a047";
    private static final String WARNING_COLOR = "#fb8c00";
    private static final String CARD_COLOR = "#ffffff";
    private static final String BACKGROUND_COLOR = "#f5f7fa";
    private static final String TEXT_DARK = "#263238";
    private static final String TEXT_LIGHT = "#546e7a";
    private static final String GRADIENT_START = "#667eea";
    private static final String GRADIENT_END = "#764ba2";
    
    // Food item constants - MATCHING RATION ALLOCATION
    private static final String WHEAT_ITEM = "🌾 Wheat: 5kg";
    private static final String RICE_ITEM = "🍚 Rice: 3kg";
    
    // UI Components
    private TextField barcodeInput;
    private TextArea scanResultArea;
    private Label statusLabel;
    private Label userInfoLabel;
    private Label foodInfoLabel;
    private Button scanButton;
    private VBox scanResultContainer;
    private Button giveWheatButton;
    private Button giveRiceButton;
    private Button giveBothButton;
    private HBox actionButtonsContainer;
    
    // Current scanned user data
    private UserData currentScannedUser;

    // CORRECTED METHOD NAMES TO MATCH AdminDashboard CALLS
    public void setScanniScene(Scene scanniScene) {
        this.scanniScene = scanniScene;
    }

    public void setScStage(Stage scStage) {
        this.scStage = scStage;
    }

    // CORRECTED METHOD NAME: createscanBox (matching AdminDashboard call)
    public VBox createscanBox(Runnable back) {
        // Main scroll container
        ScrollPane mainScrollPane = new ScrollPane();
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setFitToHeight(true);
        mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainScrollPane.setStyle("-fx-background: " + BACKGROUND_COLOR + "; -fx-background-color: " + BACKGROUND_COLOR + ";");
        
        VBox mainContainer = new VBox(0);
        mainContainer.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Enhanced Header Section (Made more compact)
        VBox header = createCompactHeader(back);
        
        // Main Content with improved layout
        HBox contentContainer = new HBox(20);
        contentContainer.setPadding(new Insets(20, 30, 20, 30));
        contentContainer.setAlignment(Pos.TOP_CENTER);
        
        // Left Panel - Scanner Interface and Results (75% width)
        VBox leftPanel = createEnhancedLeftPanel();
        
        // Right Panel - Instructions (25% width, made more compact)
        VBox rightPanel = createCompactRightPanel();
        
        // Set preferred widths for better proportions
        leftPanel.setPrefWidth(900);
        rightPanel.setPrefWidth(300);
        
        contentContainer.getChildren().addAll(leftPanel, rightPanel);
        
        mainContainer.getChildren().addAll(header, contentContainer);
        
        // Set the scroll content
        mainScrollPane.setContent(mainContainer);
        
        // Return wrapper VBox containing ScrollPane
        VBox wrapper = new VBox();
        wrapper.getChildren().add(mainScrollPane);
        VBox.setVgrow(mainScrollPane, Priority.ALWAYS);
        
        return wrapper;
    }
    
    private VBox createCompactHeader(Runnable back) {
        VBox headerContainer = new VBox();
        
        HBox header = new HBox();
        header.setPrefHeight(70); // Reduced from 90
        header.setMinHeight(70);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 40, 0, 40));
        header.setStyle(
            "-fx-background-color: linear-gradient(to right, " + GRADIENT_START + ", " + GRADIENT_END + "); " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0, 0, 4);"
        );

        // Header Left - Title Section (More compact)
        HBox headerLeft = new HBox(15);
        headerLeft.setAlignment(Pos.CENTER_LEFT);

        Label headerIcon = new Label("📱");
        headerIcon.setFont(Font.font(24)); // Reduced from 32

        VBox headerTextBox = new VBox(2);
        headerTextBox.setAlignment(Pos.CENTER_LEFT);
        
        Label headerTitle = new Label("Smart Ration Scanner System");
        headerTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22)); // Reduced from 28
        headerTitle.setTextFill(Color.WHITE);
        headerTitle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 0, 1);");

        String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        Label headerSubtitle = new Label("Real-time Scanner - " + currentMonth);
        headerSubtitle.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13)); // Reduced and simplified
        headerSubtitle.setTextFill(Color.rgb(255, 255, 255, 0.95));
        
        headerTextBox.getChildren().addAll(headerTitle, headerSubtitle);
        headerLeft.getChildren().addAll(headerIcon, headerTextBox);

        // Header Right - Back button (More compact)
        HBox headerRight = new HBox();
        headerRight.setAlignment(Pos.CENTER_RIGHT);

        Button backButton = new Button("Back");
        backButton.setFont(Font.font("Segoe UI", 14)); // Slightly smaller
        backButton.setTextFill(Color.WHITE);
        backButton.setStyle(
            "-fx-background-color: rgba(255,255,255,0.15); " +
            "-fx-background-radius: 25; " +
            "-fx-padding: 10 20; " + // Reduced padding
            "-fx-cursor: hand; " +
            "-fx-border-color: rgba(255,255,255,0.3); " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 25;"
        );
        
        backButton.setOnMouseEntered(e -> 
            backButton.setStyle(
                "-fx-background-color: rgba(255,255,255,0.25); " +
                "-fx-background-radius: 25; " +
                "-fx-padding: 10 20; " +
                "-fx-cursor: hand; " +
                "-fx-border-color: rgba(255,255,255,0.5); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 25; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"
            )
        );
        
        backButton.setOnMouseExited(e -> 
            backButton.setStyle(
                "-fx-background-color: rgba(255,255,255,0.15); " +
                "-fx-background-radius: 25; " +
                "-fx-padding: 10 20; " +
                "-fx-cursor: hand; " +
                "-fx-border-color: rgba(255,255,255,0.3); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 25;"
            )
        );
        
        backButton.setOnAction(e -> back.run());
        headerRight.getChildren().add(backButton);
        
        // Create spacer region
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(headerLeft, spacer, headerRight);
        headerContainer.getChildren().add(header);
        
        return headerContainer;
    }
    
    private VBox createEnhancedLeftPanel() {
        VBox leftPanel = new VBox(10); // Reduced spacing from 15 to 10
        leftPanel.setAlignment(Pos.TOP_CENTER);
        
        // Compact Scanner Interface Section
        VBox scannerSection = createCompactScannerInterface();
        
        // Enhanced Results Section (This will be the main focus)
        VBox resultsSection = createEnhancedResultsSection();
        
        leftPanel.getChildren().addAll(scannerSection, resultsSection);
        
        return leftPanel;
    }
    
    private VBox createCompactRightPanel() {
        VBox rightPanel = new VBox(15);
        rightPanel.setAlignment(Pos.TOP_CENTER);
        
        // Compact Instructions Section
        VBox instructionsSection = createCompactInstructionsSection();
        
        rightPanel.getChildren().add(instructionsSection);
        
        return rightPanel;
    }
    
    private VBox createCompactScannerInterface() {
        VBox scannerBox = new VBox(8); // Further reduced spacing
        scannerBox.setAlignment(Pos.CENTER);
        scannerBox.setStyle(
            "-fx-background-color: " + CARD_COLOR + "; " +
            "-fx-padding: 15; " + // Further reduced padding
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.3, 0, 3); " +
            "-fx-border-color: #e8eaf6; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 12;"
        );
        
        // More compact title with icon
        HBox titleBox = new HBox(8);
        titleBox.setAlignment(Pos.CENTER);
        
        Label scanIcon = new Label("🔍");
        scanIcon.setFont(Font.font(16)); // Further reduced
        
        Label scanTitle = new Label("Scanner Interface");
        scanTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16)); // Further reduced
        scanTitle.setTextFill(Color.web(TEXT_DARK));
        
        titleBox.getChildren().addAll(scanIcon, scanTitle);
        
        // More compact input section
        VBox inputSection = new VBox(6); // Further reduced spacing
        inputSection.setAlignment(Pos.CENTER);
        
        Label inputLabel = new Label("Enter Ration Card Number:");
        inputLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12)); // Further reduced
        inputLabel.setTextFill(Color.web(TEXT_DARK));
        
        HBox inputBox = new HBox(8);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setMaxWidth(500);
        
        barcodeInput = new TextField();
        barcodeInput.setPromptText("Enter ration card number (e.g., RC123456)");
        barcodeInput.setPrefWidth(320); // Slightly reduced
        barcodeInput.setPrefHeight(35); // Further reduced
        barcodeInput.setStyle(
            "-fx-font-size: 13; " + // Further reduced
            "-fx-padding: 8 10; " + // Further reduced
            "-fx-background-radius: 8; " +
            "-fx-border-color: #c5cae9; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-background-color: #fafafa;"
        );
        
        // Enhanced focus effects (same logic)
        barcodeInput.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                barcodeInput.setStyle(
                    "-fx-font-size: 13; " +
                    "-fx-padding: 8 10; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-color: " + SECONDARY_COLOR + "; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 8; " +
                    "-fx-background-color: white; " +
                    "-fx-effect: dropshadow(gaussian, rgba(57,73,171,0.3), 8, 0, 0, 2);"
                );
            } else {
                barcodeInput.setStyle(
                    "-fx-font-size: 13; " +
                    "-fx-padding: 8 10; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-color: #c5cae9; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 8; " +
                    "-fx-background-color: #fafafa;"
                );
            }
        });
        
        // Real-time scanning on text change (same logic)
        barcodeInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty() && newValue.length() >= 3) {
                Platform.runLater(() -> handleScan(newValue.trim()));
            } else {
                clearScanResults();
            }
        });
        
        // Also support Enter key
        barcodeInput.setOnAction(e -> handleScan(barcodeInput.getText().trim()));
        
        scanButton = createCompactButton("🔍 Scan", SUCCESS_COLOR, 100); // More compact
        scanButton.setOnAction(this::handleScan);
        
        inputBox.getChildren().addAll(barcodeInput, scanButton);
        inputSection.getChildren().addAll(inputLabel, inputBox);
        
        // More compact status indicator
        statusLabel = new Label("💡 Start typing to scan");
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11)); // Further reduced
        statusLabel.setTextFill(Color.web(TEXT_LIGHT));
        statusLabel.setStyle("-fx-padding: 5; -fx-background-color: #f3e5f5; -fx-background-radius: 12;"); // Reduced padding
        
        scannerBox.getChildren().addAll(titleBox, inputSection, statusLabel);
        
        return scannerBox;
    }
    
    private VBox createEnhancedResultsSection() {
        scanResultContainer = new VBox(10); // Reduced spacing from 15 to 10
        scanResultContainer.setAlignment(Pos.CENTER);
        scanResultContainer.setStyle(
            "-fx-background-color: " + CARD_COLOR + "; " +
            "-fx-padding: 20; " + // Slightly reduced padding
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 15, 0.3, 0, 5); " +
            "-fx-border-color: #e8eaf6; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 12;"
        );
        
        // Enhanced title
        HBox titleBox = new HBox(10); // Reduced spacing
        titleBox.setAlignment(Pos.CENTER);
        
        Label resultsIcon = new Label("📋");
        resultsIcon.setFont(Font.font(18)); // Slightly reduced
        
        Label resultsTitle = new Label("Scan Results");
        resultsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18)); // Slightly reduced
        resultsTitle.setTextFill(Color.web(TEXT_DARK));
        
        titleBox.getChildren().addAll(resultsIcon, resultsTitle);
        
        // User info section (more compact)
        VBox infoSection = new VBox(5); // Reduced spacing from 8 to 5
        infoSection.setAlignment(Pos.CENTER);
        
        userInfoLabel = new Label("Enter a ration card number to see results");
        userInfoLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13)); // Slightly reduced  
        userInfoLabel.setTextFill(Color.web(TEXT_LIGHT));
        userInfoLabel.setWrapText(true);
        userInfoLabel.setAlignment(Pos.CENTER);
        
        foodInfoLabel = new Label();
        foodInfoLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12)); // Slightly reduced
        foodInfoLabel.setWrapText(true);
        foodInfoLabel.setAlignment(Pos.CENTER);
        
        infoSection.getChildren().addAll(userInfoLabel, foodInfoLabel);
        
        // MAIN FOCUS: Enhanced scan result area (MUCH LARGER)
        scanResultArea = new TextArea();
        scanResultArea.setPrefRowCount(25); // Increased significantly from 15
        scanResultArea.setPrefColumnCount(80); // Increased
        scanResultArea.setMinHeight(400); // Set minimum height
        scanResultArea.setPrefHeight(500); // Set preferred height  
        scanResultArea.setMaxHeight(600); // Set maximum height
        scanResultArea.setEditable(false);
        scanResultArea.setWrapText(true);
        scanResultArea.setStyle(
            "-fx-font-family: 'Consolas', 'Monaco', monospace; " +
            "-fx-font-size: 13; " + // Slightly smaller font for more content
            "-fx-background-color: #f8f9fa; " +
            "-fx-control-inner-background: #f8f9fa; " +
            "-fx-text-fill: " + TEXT_DARK + "; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #e9ecef; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8; " +
            "-fx-padding: 12;"
        );
        scanResultArea.setText("Waiting for ration card input...\n\n📱 This scanner will show:\n• User details and verification status\n• Monthly ration distribution status\n• Available actions for food distribution\n• Real-time database scanning\n\n🔍 Start typing a ration card number to begin scanning...");
        
        // Make TextArea grow to fill available space
        VBox.setVgrow(scanResultArea, Priority.ALWAYS);
        
        // Enhanced action buttons (well organized)
        actionButtonsContainer = createWellOrganizedButtonContainer();
        
        scanResultContainer.getChildren().addAll(titleBox, infoSection, scanResultArea, actionButtonsContainer);
        
        // Make the results container grow
        VBox.setVgrow(scanResultContainer, Priority.ALWAYS);
        
        return scanResultContainer;
    }
    
    private HBox createWellOrganizedButtonContainer() {
        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setVisible(false);
        buttonContainer.setPadding(new Insets(15, 0, 5, 0));
        buttonContainer.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 15;"
        );
        
        // Create enhanced buttons with consistent styling
        giveWheatButton = createActionButton("🌾 Give Wheat (5kg)", WARNING_COLOR, 160);
        giveWheatButton.setOnAction(e -> distributeFood("wheat"));
        
        giveRiceButton = createActionButton("🍚 Give Rice (3kg)", SUCCESS_COLOR, 150);
        giveRiceButton.setOnAction(e -> distributeFood("rice"));
        
        giveBothButton = createActionButton("🍽 Give Both Items", PRIMARY_COLOR, 160);
        giveBothButton.setOnAction(e -> distributeFood("both"));
        
        buttonContainer.getChildren().addAll(giveWheatButton, giveRiceButton, giveBothButton);
        
        return buttonContainer;
    }
    
    private VBox createCompactInstructionsSection() {
        VBox instructionsBox = new VBox(15);
        instructionsBox.setAlignment(Pos.TOP_LEFT);
        instructionsBox.setStyle(
            "-fx-background-color: " + CARD_COLOR + "; " +
            "-fx-padding: 20; " + // Reduced
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.3, 0, 3); " +
            "-fx-border-color: #e8eaf6; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 12;"
        );
        
        // Compact title
        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label instructionsIcon = new Label("📋");
        instructionsIcon.setFont(Font.font(16)); // Reduced
        
        Label instructionsTitle = new Label("How to Use");
        instructionsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16)); // Reduced
        instructionsTitle.setTextFill(Color.WHITE);
        
        titleBox.getChildren().addAll(instructionsIcon, instructionsTitle);
        
        // Compact instructions list
        VBox instructionsList = new VBox(10); // Reduced spacing
        instructionsList.setPadding(new Insets(5, 0, 0, 0));
        
        String[] instructions = {
            "Type ration card number - results appear automatically",
            "View user info and monthly status",
            "Use action buttons if food not distributed",
            "System updates status automatically"
        };
        
        String[] stepIcons = {"🔍", "👤", "📦", "💾"};
        
        for (int i = 0; i < instructions.length; i++) {
            HBox stepBox = new HBox(8); // Reduced spacing
            stepBox.setAlignment(Pos.TOP_LEFT);
            stepBox.setStyle(
                "-fx-padding: 8; " + // Reduced
                "-fx-background-color: #f8f9fa; " +
                "-fx-background-radius: 6; " +
                "-fx-border-color: #e9ecef; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 6;"
            );
            
            VBox stepNumberBox = new VBox();
            stepNumberBox.setAlignment(Pos.CENTER);
            stepNumberBox.setStyle(
                "-fx-background-color: " + SECONDARY_COLOR + "; " +
                "-fx-background-radius: 12; " +
                "-fx-min-width: 24; " +
                "-fx-min-height: 24; " +
                "-fx-max-width: 24; " +
                "-fx-max-height: 24;"
            );
            
            Label stepNumber = new Label(String.valueOf(i + 1));
            stepNumber.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10)); // Reduced
            stepNumber.setTextFill(Color.WHITE);
            stepNumber.setAlignment(Pos.CENTER);
            
            stepNumberBox.getChildren().add(stepNumber);
            
            VBox stepContent = new VBox(3);
            
            Label stepIcon = new Label(stepIcons[i]);
            stepIcon.setFont(Font.font(12)); // Reduced
            
            Label instructionLabel = new Label(instructions[i]);
            instructionLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11)); // Reduced
            instructionLabel.setTextFill(Color.web(TEXT_DARK));
            instructionLabel.setWrapText(true);
            
            stepContent.getChildren().addAll(stepIcon, instructionLabel);
            
            stepBox.getChildren().addAll(stepNumberBox, stepContent);
            instructionsList.getChildren().add(stepBox);
        }
        
        instructionsBox.getChildren().addAll(titleBox, instructionsList);
        
        return instructionsBox;
    }
    
    // OVERLOADED METHOD - handles both ActionEvent and String (NO CHANGES)
    private void handleScan(ActionEvent event) {
        String rationCard = barcodeInput.getText().trim();
        handleScan(rationCard);
    }
    
    private void handleScan(String rationCard) {
        if (rationCard.isEmpty()) {
            clearScanResults();
            return;
        }
        
        statusLabel.setText("🔍 Searching...");
        statusLabel.setTextFill(Color.web(WARNING_COLOR));
        statusLabel.setStyle("-fx-padding: 5; -fx-background-color: #fff3e0; -fx-background-radius: 12;");
        
        // Search for user in background
        Task<UserData> searchTask = new Task<UserData>() {
            @Override
            protected UserData call() throws Exception {
                return searchUserByRationCard(rationCard);
            }
            
            @Override
            protected void succeeded() {
                UserData foundUser = getValue();
                Platform.runLater(() -> processScanResult(foundUser, rationCard));
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    statusLabel.setText("❌ Error searching for user");
                    statusLabel.setTextFill(Color.web(ACCENT_COLOR));
                    statusLabel.setStyle("-fx-padding: 8; -fx-background-color: #ffebee; -fx-background-radius: 15;");
                    showErrorScanResult("Error connecting to database");
                });
            }
        };
        
        new Thread(searchTask).start();
    }
    
    private void clearScanResults() {
        statusLabel.setText("💡 Start typing to scan");
        statusLabel.setTextFill(Color.web(TEXT_LIGHT));
        statusLabel.setStyle("-fx-padding: 5; -fx-background-color: #f3e5f5; -fx-background-radius: 12;");
        
        userInfoLabel.setText("Enter a ration card number to see results");
        userInfoLabel.setTextFill(Color.web(TEXT_LIGHT));
        
        foodInfoLabel.setText("");
        actionButtonsContainer.setVisible(false);
        
        scanResultArea.setText("Waiting for ration card input...\n\n📱 This scanner will show:\n• User details and verification status\n• Monthly ration distribution status\n• Available actions for food distribution\n\n🔍 Start typing a ration card number to begin scanning...");
        
        currentScannedUser = null;
    }
    
    private void showErrorScanResult(String message) {
        userInfoLabel.setText("❌ " + message);
        userInfoLabel.setTextFill(Color.web(ACCENT_COLOR));
        
        foodInfoLabel.setText("Please try again or contact administrator");
        foodInfoLabel.setTextFill(Color.web(TEXT_LIGHT));
        
        actionButtonsContainer.setVisible(false);
        
        scanResultArea.setText("=".repeat(60) + "\n" +
                              "          SCANNING ERROR\n" +
                              "=".repeat(60) + "\n" +
                              "Error: " + message + "\n" +
                              "Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                              "\nPlease try again or contact system administrator.\n" +
                              "=".repeat(60));
    }
    
    private UserData searchUserByRationCard(String rationCard) {
        try {
            String urlStr = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + 
                           "/databases/(default)/documents/users";
            
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                
                JSONObject response = new JSONObject(sb.toString());
                if (response.has("documents")) {
                    JSONArray documents = response.getJSONArray("documents");
                    String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
                    
                    for (int i = 0; i < documents.length(); i++) {
                        JSONObject doc = documents.getJSONObject(i);
                        JSONObject fields = doc.getJSONObject("fields");
                        
                        // Check if this user's ration card matches
                        String userRationCard = getStringValue(fields, "rationCard");
                        if (rationCard.equalsIgnoreCase(userRationCard)) {
                            // Don't return admin users
                            if (!getBooleanValue(fields, "isAdmin")) {
                                UserData user = new UserData();
                                user.name = getStringValue(fields, "name");
                                user.email = getStringValue(fields, "email");
                                user.scheme = getStringValue(fields, "scheme");
                                user.memberCount = getStringValue(fields, "memberCount");
                                user.phone = getStringValue(fields, "phone");
                                user.rationCard = userRationCard;
                                user.lastAllocationDate = getStringValue(fields, "lastAllocationDate");
                                user.lastAllocationMonth = getStringValue(fields, "lastAllocationMonth");
                                user.wheatReceivedDate = getStringValue(fields, "wheatReceivedDate");
                                user.riceReceivedDate = getStringValue(fields, "riceReceivedDate");
                                
                                // Check monthly status
                                user.hasRationThisMonth = getBooleanValue(fields, "hasRationThisMonth") && 
                                                        currentMonth.equals(user.lastAllocationMonth);
                                
                                // Check individual item status
                                user.wheatReceived = getBooleanValue(fields, "wheatReceived") &&
                                                   currentMonth.equals(getMonthFromDate(user.wheatReceivedDate));
                                user.riceReceived = getBooleanValue(fields, "riceReceived") &&
                                                  currentMonth.equals(getMonthFromDate(user.riceReceivedDate));
                                
                                return user;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null; // User not found
    }
    
    // Helper method to extract year-month from date string
    private String getMonthFromDate(String dateString) {
        try {
            if (dateString == null || dateString.isEmpty()) {
                return "";
            }
            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
            return YearMonth.from(date).format(DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (Exception e) {
            return "";
        }
    }
    
    private void processScanResult(UserData userData, String rationCard) {
        currentScannedUser = userData;
        
        StringBuilder resultText = new StringBuilder();
        String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        
        resultText.append("═".repeat(65)).append("\n");
        resultText.append("        SMART RATION DISTRIBUTION SYSTEM\n");
        resultText.append("═".repeat(65)).append("\n");
        resultText.append("📱 Ration Card: ").append(rationCard).append("\n");
        resultText.append("🕒 Scan Time: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        resultText.append("📅 Current Month: ").append(currentMonth).append("\n");
        resultText.append("👨‍💼 Scanned by: Admin Scanner\n");
        resultText.append("─".repeat(65)).append("\n");
        
        if (userData != null) {
            statusLabel.setText("✅ User found successfully!");
            statusLabel.setTextFill(Color.web(SUCCESS_COLOR));
            statusLabel.setStyle("-fx-padding: 8; -fx-background-color: #e8f5e8; -fx-background-radius: 15;");
            
            userInfoLabel.setText("👤 User: " + userData.name + " | 📧 " + userData.email);
            userInfoLabel.setTextFill(Color.web(SUCCESS_COLOR));
            
            resultText.append("👤 USER INFORMATION:\n");
            resultText.append("   ├─ Name: ").append(userData.name).append("\n");
            resultText.append("   ├─ Email: ").append(userData.email).append("\n");
            resultText.append("   ├─ Ration Card: ").append(userData.rationCard).append("\n");
            resultText.append("   ├─ Phone: ").append(userData.phone).append("\n");
            resultText.append("   ├─ Family Members: ").append(userData.memberCount).append("\n");
            resultText.append("   └─ Scheme: ").append(userData.scheme).append("\n");
            resultText.append("─".repeat(65)).append("\n");
            
            resultText.append("📦 MONTHLY FOOD DISTRIBUTION STATUS (").append(currentMonth).append("):\n");
            
            // Wheat status
            if (userData.wheatReceived) {
                resultText.append("🌾 WHEAT: ✅ ALREADY RECEIVED THIS MONTH\n");
                resultText.append("   └─ Received on: ").append(userData.wheatReceivedDate).append("\n");
            } else {
                resultText.append("🌾 WHEAT: ❌ NOT RECEIVED THIS MONTH\n");
                resultText.append("   └─ Quantity: 5kg available\n");
            }
            
            resultText.append("\n");
            
            // Rice status
            if (userData.riceReceived) {
                resultText.append("🍚 RICE: ✅ ALREADY RECEIVED THIS MONTH\n");
                resultText.append("   └─ Received on: ").append(userData.riceReceivedDate).append("\n");
            } else {
                resultText.append("🍚 RICE: ❌ NOT RECEIVED THIS MONTH\n");
                resultText.append("   └─ Quantity: 3kg available\n");
            }
            
            resultText.append("─".repeat(65)).append("\n");
            
            // Show appropriate action buttons
            boolean canGiveWheat = !userData.wheatReceived;
            boolean canGiveRice = !userData.riceReceived;
            
            giveWheatButton.setDisable(!canGiveWheat);
            giveRiceButton.setDisable(!canGiveRice);
            giveBothButton.setDisable(!canGiveWheat && !canGiveRice);
            
            if (canGiveWheat || canGiveRice) {
                actionButtonsContainer.setVisible(true);
                foodInfoLabel.setText("📦 Items available for distribution this month - Use buttons below");
                foodInfoLabel.setTextFill(Color.web(SUCCESS_COLOR));
                resultText.append("⚡ ACTION REQUIRED: Use buttons below to distribute food items\n");
                
                if (canGiveWheat && canGiveRice) {
                    resultText.append("   ├─ 🌾 Wheat (5kg) - Available\n");
                    resultText.append("   ├─ 🍚 Rice (3kg) - Available\n");
                    resultText.append("   └─ 🍽 Both items can be distributed\n");
                } else if (canGiveWheat) {
                    resultText.append("   └─ 🌾 Wheat (5kg) - Available\n");
                } else if (canGiveRice) {
                    resultText.append("   └─ 🍚 Rice (3kg) - Available\n");
                }
            } else {
                actionButtonsContainer.setVisible(false);
                foodInfoLabel.setText("✅ All monthly items already distributed");
                foodInfoLabel.setTextFill(Color.web(SUCCESS_COLOR));
                resultText.append("✅ STATUS: All food items already distributed for ").append(currentMonth).append("\n");
                resultText.append("   └─ User has received complete monthly allocation\n");
            }
            
        } else {
            statusLabel.setText("❌ User not found!");
            statusLabel.setTextFill(Color.web(ACCENT_COLOR));
            statusLabel.setStyle("-fx-padding: 8; -fx-background-color: #ffebee; -fx-background-radius: 15;");
            
            userInfoLabel.setText("❌ Ration Card Not Found");
            userInfoLabel.setTextFill(Color.web(ACCENT_COLOR));
            foodInfoLabel.setText("This ration card is not registered in the system");
            foodInfoLabel.setTextFill(Color.web(TEXT_LIGHT));
            
            actionButtonsContainer.setVisible(false);
            
            resultText.append("❌ ERROR: RATION CARD NOT FOUND\n");
            resultText.append("─".repeat(65)).append("\n");
            resultText.append("The ration card '").append(rationCard).append("' is not registered.\n");
            resultText.append("\n🔍 Troubleshooting Steps:\n");
            resultText.append("   ├─ Verify the ration card number\n");
            resultText.append("   ├─ Check if user is registered in system\n");
            resultText.append("   ├─ Contact administrator if needed\n");
            resultText.append("   └─ Ensure proper card number format\n");
        }
        
        resultText.append("═".repeat(65)).append("\n");
        scanResultArea.setText(resultText.toString());
    }
    
    private void distributeFood(String foodType) {
        if (currentScannedUser == null) {
            showError("No user currently scanned");
            return;
        }
        
        String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Food Distribution");
        confirmAlert.setHeaderText("Distribute monthly ration to " + currentScannedUser.name + "?");
        
        String items = "";
        switch (foodType) {
            case "wheat":
                items = "🌾 Wheat: 5kg";
                break;
            case "rice":
                items = "🍚 Rice: 3kg";
                break;
            case "both":
                items = "🌾 Wheat: 5kg + 🍚 Rice: 3kg";
                break;
        }
        
        confirmAlert.setContentText("Month: " + currentMonth + "\nItems to distribute: " + items + 
                                   "\n\nThis action will be recorded and will update the monthly ration allocation count.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response.getButtonData().isDefaultButton()) {
                performFoodDistribution(foodType);
            }
        });
    }
    
    private void performFoodDistribution(String foodType) {
        statusLabel.setText("📦 Distributing monthly ration...");
        statusLabel.setTextFill(Color.web(WARNING_COLOR));
        statusLabel.setStyle("-fx-padding: 8; -fx-background-color: #fff3e0; -fx-background-radius: 15;");
        
        Task<Boolean> distributionTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return updateFoodDistributionInFirebase(currentScannedUser, foodType);
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (getValue()) {
                        String message = "";
                        String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                        
                        switch (foodType) {
                            case "wheat":
                                message = "✅ Wheat distributed successfully for " + currentMonth + "!";
                                currentScannedUser.wheatReceived = true;
                                currentScannedUser.wheatReceivedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                                break;
                            case "rice":
                                message = "✅ Rice distributed successfully for " + currentMonth + "!";
                                currentScannedUser.riceReceived = true;
                                currentScannedUser.riceReceivedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                                break;
                            case "both":
                                message = "✅ Both items distributed successfully for " + currentMonth + "!";
                                currentScannedUser.wheatReceived = true;
                                currentScannedUser.riceReceived = true;
                                currentScannedUser.wheatReceivedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                                currentScannedUser.riceReceivedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                                break;
                        }
                        
                        // Update monthly allocation status
                        currentScannedUser.hasRationThisMonth = true;
                        currentScannedUser.lastAllocationDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                        currentScannedUser.lastAllocationMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
                        
                        statusLabel.setText(message);
                        statusLabel.setTextFill(Color.web(SUCCESS_COLOR));
                        statusLabel.setStyle("-fx-padding: 8; -fx-background-color: #e8f5e8; -fx-background-radius: 15;");
                        
                        // Refresh the scan result
                        processScanResult(currentScannedUser, currentScannedUser.rationCard);
                        
                        showInfo("Distribution Complete", message + "\nUser: " + currentScannedUser.name + 
                                "\n\nThis will be reflected in the Ration Allocation system.");
                    } else {
                        statusLabel.setText("❌ Distribution failed");
                        statusLabel.setTextFill(Color.web(ACCENT_COLOR));
                        statusLabel.setStyle("-fx-padding: 8; -fx-background-color: #ffebee; -fx-background-radius: 15;");
                        showError("Failed to update distribution status in database");
                    }
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    statusLabel.setText("❌ Distribution error");
                    statusLabel.setTextFill(Color.web(ACCENT_COLOR));
                    statusLabel.setStyle("-fx-padding: 8; -fx-background-color: #ffebee; -fx-background-radius: 15;");
                    showError("Error occurred during food distribution");
                });
            }
        };
        
        new Thread(distributionTask).start();
    }
    
    private boolean updateFoodDistributionInFirebase(UserData user, String foodType) {
        try {
            // UNIFORM SANITIZATION! (Same as RationAllocation)
            String sanitizedEmail = user.email.replace("@", "at").replace(".", "dot");
            String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            
            // First verify document exists
            String getUrlStr = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID +
                "/databases/(default)/documents/users/" + sanitizedEmail;
            
            URL getUrl = new URL(getUrlStr);
            HttpURLConnection getConn = (HttpURLConnection) getUrl.openConnection();
            getConn.setRequestMethod("GET");
            getConn.setRequestProperty("Content-Type", "application/json");
            
            int getResponseCode = getConn.getResponseCode();
            System.out.println("Scanner GET Response Code: " + getResponseCode);
            
            if (getResponseCode != 200) {
                // Try to find correct document ID
                String correctDocId = findCorrectDocumentId(user.email);
                if (correctDocId != null) {
                    sanitizedEmail = correctDocId;
                } else {
                    System.out.println("Scanner: Could not find document for user: " + user.email);
                    return false;
                }
            }
            
            // PATCH url with updateMask for ALL relevant fields (SAME AS RATION ALLOCATION!)
            String urlStr = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID +
                "/databases/(default)/documents/users/" + sanitizedEmail +
                "?updateMask.fieldPaths=hasRationThisMonth" +
                "&updateMask.fieldPaths=lastAllocationDate" +
                "&updateMask.fieldPaths=lastAllocationMonth" +
                "&updateMask.fieldPaths=rationStatus";
            
            // Add specific fields based on food type
            switch (foodType) {
                case "wheat":
                    urlStr += "&updateMask.fieldPaths=wheatReceived&updateMask.fieldPaths=wheatReceivedDate";
                    break;
                case "rice":
                    urlStr += "&updateMask.fieldPaths=riceReceived&updateMask.fieldPaths=riceReceivedDate";
                    break;
                case "both":
                    urlStr += "&updateMask.fieldPaths=wheatReceived&updateMask.fieldPaths=wheatReceivedDate";
                    urlStr += "&updateMask.fieldPaths=riceReceived&updateMask.fieldPaths=riceReceivedDate";
                    break;
            }
            
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            // SAME FIELDS AS RATION ALLOCATION!
            JSONObject fields = new JSONObject();
            fields.put("hasRationThisMonth", new JSONObject().put("booleanValue", true));
            fields.put("lastAllocationDate", new JSONObject().put("stringValue", currentDate));
            fields.put("lastAllocationMonth", new JSONObject().put("stringValue", currentMonth));
            fields.put("rationStatus", new JSONObject().put("stringValue", "Distributed"));
            
            // Add specific food distribution fields
            switch (foodType) {
                case "wheat":
                    fields.put("wheatReceived", new JSONObject().put("booleanValue", true));
                    fields.put("wheatReceivedDate", new JSONObject().put("stringValue", currentDate));
                    break;
                case "rice":
                    fields.put("riceReceived", new JSONObject().put("booleanValue", true));
                    fields.put("riceReceivedDate", new JSONObject().put("stringValue", currentDate));
                    break;
                case "both":
                    fields.put("wheatReceived", new JSONObject().put("booleanValue", true));
                    fields.put("wheatReceivedDate", new JSONObject().put("stringValue", currentDate));
                    fields.put("riceReceived", new JSONObject().put("booleanValue", true));
                    fields.put("riceReceivedDate", new JSONObject().put("stringValue", currentDate));
                    break;
            }
            
            JSONObject payload = new JSONObject();
            payload.put("fields", fields);
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes());
            }
            
            int responseCode = conn.getResponseCode();
            System.out.println("Scanner PATCH Response Code: " + responseCode);
            
            if (responseCode == 200) {
                System.out.println("Scanner: Successfully updated user document");
                // Create distribution history with unique ID for scanner operations
                createDistributionHistory(user, foodType, currentDate, currentMonth);
                return true;
            } else {
                // Print error for debugging
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    System.out.println("Scanner Firebase error: " + errorResponse.toString());
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Helper method to find the correct document ID (same as RationAllocation)
    private String findCorrectDocumentId(String email) {
        try {
            String urlStr = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + 
                           "/databases/(default)/documents/users";
            
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                
                JSONObject response = new JSONObject(sb.toString());
                if (response.has("documents")) {
                    JSONArray documents = response.getJSONArray("documents");
                    
                    for (int i = 0; i < documents.length(); i++) {
                        JSONObject doc = documents.getJSONObject(i);
                        JSONObject fields = doc.getJSONObject("fields");
                        
                        // Check if this document has the matching email
                        if (fields.has("email")) {
                            String docEmail = fields.getJSONObject("email").getString("stringValue");
                            if (docEmail.equals(email)) {
                                // Extract document ID from the document name
                                String docName = doc.getString("name");
                                String[] parts = docName.split("/");
                                return parts[parts.length - 1]; // Last part is the document ID
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Create distribution history with UNIQUE IDs for scanner operations
    private void createDistributionHistory(UserData user, String foodType, String date, String month) {
        try {
            // Create UNIQUE document ID using timestamp + UUID to avoid overwrites
            String timestamp = String.valueOf(System.currentTimeMillis());
            String uniqueId = user.email.replace("@", "at").replace(".", "dot") + "_" + 
                             month + "scanner_distribution" + timestamp;
            
            String urlStr = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID +
                            "/databases/(default)/documents/distributionHistory?documentId=" + uniqueId;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String items = "";
            switch (foodType) {
                case "wheat":
                    items = WHEAT_ITEM;
                    break;
                case "rice":
                    items = RICE_ITEM;
                    break;
                case "both":
                    items = WHEAT_ITEM + ", " + RICE_ITEM;
                    break;
            }

            JSONObject fields = new JSONObject();
            fields.put("userEmail", new JSONObject().put("stringValue", user.email));
            fields.put("userName", new JSONObject().put("stringValue", user.name));
            fields.put("rationCard", new JSONObject().put("stringValue", user.rationCard));
            fields.put("distributionDate", new JSONObject().put("stringValue", date));
            fields.put("distributionMonth", new JSONObject().put("stringValue", month));
            fields.put("timestamp", new JSONObject().put("timestampValue", java.time.Instant.now().toString()));
            fields.put("distributedBy", new JSONObject().put("stringValue", "Scanner System"));
            fields.put("itemsDistributed", new JSONObject().put("stringValue", items));
            fields.put("distributionType", new JSONObject().put("stringValue", "scanner"));

            JSONObject payload = new JSONObject();
            payload.put("fields", fields);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes());
            }
            
            int responseCode = conn.getResponseCode();
            System.out.println("Scanner: Distribution history created with response code: " + responseCode);
            
        } catch (Exception e) {
            System.out.println("Scanner: Error creating distribution history: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String getStringValue(JSONObject fields, String key) {
        try {
            return fields.getJSONObject(key).getString("stringValue");
        } catch (Exception e) {
            return "";
        }
    }

    private boolean getBooleanValue(JSONObject fields, String key) {
        try {
            return fields.getJSONObject(key).getBoolean("booleanValue");
        } catch (Exception e) {
            return false;
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Scanner Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private Button createCompactButton(String text, String color, int width) {
        Button button = new Button(text);
        button.setPrefWidth(width);
        button.setPrefHeight(35); // Reduced height
        button.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12; " + // Reduced font size
            "-fx-padding: 8 16; " + // Reduced padding
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);"
        );
        
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: derive(" + color + ", -15%); " +
                "-fx-text-fill: white; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 12; " +
                "-fx-padding: 8 16; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 3); " +
                "-fx-scale-y: 1.02; " +
                "-fx-scale-x: 1.02;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 12; " +
                "-fx-padding: 8 16; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2); " +
                "-fx-scale-y: 1.0; " +
                "-fx-scale-x: 1.0;"
            )
        );
        
        return button;
    }
    
    private Button createActionButton(String text, String color, int width) {
        Button button = new Button(text);
        button.setPrefWidth(width);
        button.setPrefHeight(42);
        button.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 13; " +
            "-fx-padding: 10 18; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);"
        );
        
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: derive(" + color + ", -15%); " +
                "-fx-text-fill: white; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 13; " +
                "-fx-padding: 10 18; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 4); " +
                "-fx-scale-y: 1.03; " +
                "-fx-scale-x: 1.03;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 13; " +
                "-fx-padding: 10 18; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3); " +
                "-fx-scale-y: 1.0; " +
                "-fx-scale-x: 1.0;"
            )
        );
        
        return button;
    }
    
    // Inner class to hold user data (MATCHING RATION ALLOCATION STRUCTURE!)
    private static class UserData {
        String name = "";
        String email = "";
        String scheme = "";
        String memberCount = "";
        String phone = "";
        String rationCard = "";
        String lastAllocationDate = "";
        String lastAllocationMonth = "";  // ADDED: Same as RationAllocation
        String wheatReceivedDate = "";
        String riceReceivedDate = "";
        boolean hasRationThisMonth = false;  // CHANGED: Same as RationAllocation (not hasRationToday)
        boolean wheatReceived = false;
        boolean riceReceived = false;
    }
}