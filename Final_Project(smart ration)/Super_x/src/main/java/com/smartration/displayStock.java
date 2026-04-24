package com.smartration;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

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

public class displayStock {

    Stage quatapStage;
    Scene quataScene;

    static final String Project_id = "new-ration";
    static final String API_key = "AIzaSyCw_hJmevrQsmsQ1Mg0LKBylH7z6zLcsNw";

    public void setQuataScene(Scene quataScene) {
        this.quataScene = quataScene;
    }

    public void setQuatapStage(Stage quatapStage) {
        this.quatapStage = quatapStage;
    }

    public ScrollPane viewQuataScene(Runnable back) {
        // Create header with back button
        VBox headerBox = createHeader(back);

        // Main content
        HBox mainContentBox = createMainContent(back);

        // Main container with simple, clean background
        VBox main = new VBox(25);
        main.getChildren().addAll(headerBox, mainContentBox);
        main.setStyle("-fx-background-color: #f5f6fa;");
        main.setPadding(new Insets(0, 0, 30, 0));

        ScrollPane scrollPane = new ScrollPane(main);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: #f5f6fa;");

        return scrollPane;
    }

    private VBox createHeader(Runnable back) {
        // Create back button for top-left corner
        Button backButton = new Button("Back");
        backButton.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        backButton.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 20; " +
            "-fx-border-color: rgba(255, 255, 255, 0.68); " +
            "-fx-border-radius: 20; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 8 16 8 16;"
        );
        
        backButton.setOnMouseEntered(e -> {
            backButton.setStyle(
                "-fx-background-color: rgba(255,255,255,0.3); " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 20; " +
                "-fx-border-color: rgba(255,255,255,0.5); " +
                "-fx-border-radius: 20; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 8 16 8 16;"
            );
        });

        backButton.setOnMouseExited(e -> {
            backButton.setStyle(
                "-fx-background-color: rgba(255,255,255,0.2); " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 20; " +
                "-fx-border-color: rgba(255,255,255,0.3); " +
                "-fx-border-radius: 20; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 8 16 8 16;"
            );
        });

        backButton.setOnAction(e -> back.run());

        // Create title section
        Text titleText = new Text("Inventory Management");
        titleText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        titleText.setFill(Color.WHITE);

        Text subtitleText = new Text("Digital Ration Card Management");
        subtitleText.setFont(Font.font("Segoe UI", 16));
        subtitleText.setFill(Color.web("rgba(255,255,255,0.8)"));

        VBox titleBox = new VBox(8, titleText, subtitleText);
        titleBox.setAlignment(Pos.CENTER);

        // Header layout with back button and title
        HBox headerContent = new HBox();
        headerContent.setPadding(new Insets(20, 30, 30, 30));
        
        // Left region (empty for balance)
        HBox leftRegion = new HBox();
        leftRegion.setMinWidth(100); // Same min width as right for balance
        
        // Center region for title
        HBox centerRegion = new HBox();
        centerRegion.setAlignment(Pos.CENTER);
        centerRegion.getChildren().add(titleBox);
        
        // Right region for back button
        HBox rightRegion = new HBox();
        rightRegion.setAlignment(Pos.CENTER_RIGHT);
        rightRegion.getChildren().add(backButton);
        
        HBox.setHgrow(leftRegion, Priority.ALWAYS);
        HBox.setHgrow(centerRegion, Priority.ALWAYS);
        HBox.setHgrow(rightRegion, Priority.ALWAYS);
        
        headerContent.getChildren().addAll(leftRegion, centerRegion, rightRegion);
        
        VBox headerBox = new VBox(headerContent);
       headerBox.setStyle(
    "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);"
);

        return headerBox;
    }

    private HBox createMainContent(Runnable back) {
        VBox leftPanel = createViewStockPanel();
        VBox rightPanel = createManageStockPanel(back);

        HBox contentBox = new HBox(25, leftPanel, rightPanel);
        contentBox.setPadding(new Insets(0, 30, 0, 30));
        contentBox.setAlignment(Pos.TOP_CENTER);
        
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        
        return contentBox;
    }

    private VBox createViewStockPanel() {
        // Simple header
        Text headerText = new Text("View Stock");
        headerText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        headerText.setFill(Color.web("#2c3e50"));

        // Stock display
        Label stockDisplayLabel = new Label();
        stockDisplayLabel.setFont(Font.font("Consolas", 13));
        stockDisplayLabel.setWrapText(true);
        stockDisplayLabel.setMaxWidth(Double.MAX_VALUE);
        stockDisplayLabel.setAlignment(Pos.TOP_LEFT);
        stockDisplayLabel.setStyle(
            "-fx-background-color: #ecf0f1; " +
            "-fx-border-color: #bdc3c7; " +
            "-fx-border-radius: 6; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 15; " +
            "-fx-text-fill: #2c3e50;"
        );

        ScrollPane stockScrollPane = new ScrollPane(stockDisplayLabel);
        stockScrollPane.setFitToWidth(true);
        stockScrollPane.setPrefHeight(350);
        stockScrollPane.setMaxHeight(350);
        stockScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        stockScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        stockScrollPane.setStyle("-fx-background-color: transparent;");

        // Simple buttons
        Button viewAllButton = createSimpleButton("View All Stock", "#3498db");
        viewAllButton.setMaxWidth(Double.MAX_VALUE);

        TextField filterMonthField = createSimpleTextField("Enter Month (e.g., July2025)");
        Button filterButton = createSimpleButton("Filter by Month", "#9b59b6");
        filterButton.setMaxWidth(Double.MAX_VALUE);

        // Event handlers (unchanged)
        viewAllButton.setOnAction(e -> {
            String result = readStockInFirestore();
            stockDisplayLabel.setText(result);
        });

        filterButton.setOnAction(e -> {
            String month = filterMonthField.getText().trim();
            if (!month.isEmpty()) {
                String result = readStockForMonth(month);
                stockDisplayLabel.setText(result);
            } else {
                stockDisplayLabel.setText("Please enter a month to filter");
            }
        });

        VBox leftPanel = new VBox(15, 
            headerText, 
            viewAllButton, 
            new Separator(),
            new Label("Filter by Month:") {{ 
                setFont(Font.font("Segoe UI", FontWeight.BOLD, 14)); 
                setTextFill(Color.web("#2c3e50"));
            }},
            filterMonthField, 
            filterButton,
            new Label("Stock Details:") {{ 
                setFont(Font.font("Segoe UI", FontWeight.BOLD, 14)); 
                setTextFill(Color.web("#2c3e50"));
            }},
            stockScrollPane
        );
        
        leftPanel.setPadding(new Insets(25));
        leftPanel.setMaxWidth(450);
        leftPanel.setMinWidth(450);
        leftPanel.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #ecf0f1; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );

        return leftPanel;
    }

    private VBox createManageStockPanel(Runnable back) {
        Text headerText = new Text("Add Stock");
        headerText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        headerText.setFill(Color.web("#2c3e50"));

        // Form fields
        TextField monthField = createSimpleTextField("Month (e.g., July2025)");
        TextField wheatField = createSimpleTextField("Wheat Quantity (kg)");
        TextField riceField = createSimpleTextField("Rice Quantity (kg)");

        // Result display - made bigger
        Label resultDisplayLabel = new Label();
        resultDisplayLabel.setFont(Font.font("Consolas", 13));
        resultDisplayLabel.setWrapText(true);
        resultDisplayLabel.setMaxWidth(Double.MAX_VALUE);
        resultDisplayLabel.setAlignment(Pos.TOP_LEFT);
        resultDisplayLabel.setStyle(
            "-fx-background-color: #ecf0f1; " +
            "-fx-border-color: #bdc3c7; " +
            "-fx-border-radius: 6; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 15; " +
            "-fx-text-fill: #2c3e50;"
        );

        ScrollPane resultScrollPane = new ScrollPane(resultDisplayLabel);
        resultScrollPane.setFitToWidth(true);
        resultScrollPane.setPrefHeight(250);
        resultScrollPane.setMaxHeight(250);
        resultScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        resultScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        resultScrollPane.setStyle("-fx-background-color: transparent;");

        // Action button - only Add Stock remains
        Button addButton = createSimpleButton("Add Stock", "#27ae60");
        addButton.setMaxWidth(Double.MAX_VALUE);

        // Event handler for add button
        addButton.setOnAction(e -> {
            String month = monthField.getText();
            String wheat = wheatField.getText();
            String rice = riceField.getText();
            String result = addStockToFireStore(month, wheat, rice);
            resultDisplayLabel.setText(result);
            updateResultStyle(resultDisplayLabel, result);
        });

        VBox rightPanel = new VBox(15, 
            headerText,
            new Label("Stock Information:") {{ 
                setFont(Font.font("Segoe UI", FontWeight.BOLD, 14)); 
                setTextFill(Color.web("#2c3e50"));
            }},
            monthField, wheatField, riceField,
            addButton,
            new Label("Result:") {{ 
                setFont(Font.font("Segoe UI", FontWeight.BOLD, 14)); 
                setTextFill(Color.web("#2c3e50"));
            }},
            resultScrollPane
        );

        rightPanel.setPadding(new Insets(25));
        rightPanel.setMaxWidth(450);
        rightPanel.setMinWidth(450);
        rightPanel.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #ecf0f1; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );

        return rightPanel;
    }

    private TextField createSimpleTextField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #bdc3c7; " +
            "-fx-border-radius: 4; " +
            "-fx-background-radius: 4; " +
            "-fx-padding: 10; " +
            "-fx-font-size: 14px;"
        );
        
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-border-color: #3498db; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-radius: 4; " +
                    "-fx-background-radius: 4; " +
                    "-fx-padding: 9; " +
                    "-fx-font-size: 14px;"
                );
            } else {
                field.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-border-color: #bdc3c7; " +
                    "-fx-border-radius: 4; " +
                    "-fx-background-radius: 4; " +
                    "-fx-padding: 10; " +
                    "-fx-font-size: 14px;"
                );
            }
        });
        
        return field;
    }

    private Button createSimpleButton(String text, String color) {
        Button button = new Button(text);
        button.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        button.setPadding(new Insets(10, 20, 10, 20));
        button.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 4; " +
            "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: derive(" + color + ", -10%); " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 4; " +
                "-fx-cursor: hand;"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 4; " +
                "-fx-cursor: hand;"
            );
        });

        return button;
    }

    private void updateResultStyle(Label label, String result) {
        if (result.contains("✅")) {
            label.setStyle(
                "-fx-background-color: #d5f4e6; " +
                "-fx-border-color: #27ae60; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6; " +
                "-fx-padding: 15; " +
                "-fx-text-fill: #1e8449;"
            );
        } else if (result.contains("❌") || result.contains("❗")) {
            label.setStyle(
                "-fx-background-color: #fadbd8; " +
                "-fx-border-color: #e74c3c; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6; " +
                "-fx-padding: 15; " +
                "-fx-text-fill: #c0392b;"
            );
        } else {
            label.setStyle(
                "-fx-background-color: #ecf0f1; " +
                "-fx-border-color: #bdc3c7; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6; " +
                "-fx-padding: 15; " +
                "-fx-text-fill: #2c3e50;"
            );
        }
    }

    // Firebase methods - kept addStockToFireStore, readStockInFirestore, and readStockForMonth
    static String addStockToFireStore(String month, String wheatKg, String riceKg) {
        if (month.isEmpty() || wheatKg.isEmpty() || riceKg.isEmpty()) {
            return "❗ Please enter Month, Wheat, and Rice quantity.";
        }

        String endPoint = String.format(
            "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/rationStock?documentId=%s&key=%s",
            Project_id, month, API_key);

        String payload = String.format(
            "{ \"fields\": { \"month\": {\"stringValue\":\"%s\"}, \"wheatKg\": {\"integerValue\":\"%s\"}, \"riceKg\":{\"integerValue\":\"%s\"}}}",
            month, wheatKg, riceKg);

        try {
            URL url = new URL(endPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            InputStream is = conn.getInputStream();
            byte[] responseBytes = is.readAllBytes();
            String response = new String(responseBytes, StandardCharsets.UTF_8);
            is.close();
            conn.disconnect();

            return "✅ Stock Added Successfully!\nMonth: " + month + "\nWheat: " + wheatKg + " kg\nRice: " + riceKg + " kg";
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }

    static String readStockInFirestore() {
        String endpoint = String.format(
            "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/rationStock?key=%s",
            Project_id, API_key);
        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            InputStream is = conn.getInputStream();
            byte[] responseBytes = is.readAllBytes();
            String response = new String(responseBytes, StandardCharsets.UTF_8);
            is.close();
            conn.disconnect();

            JSONObject json = new JSONObject(response);
            JSONArray documents = json.getJSONArray("documents");

            StringBuilder output = new StringBuilder("📦 INVENTORY OVERVIEW\n");
            output.append("═══════════════════════════════════════\n\n");
            
            for (int i = 0; i < documents.length(); i++) {
                JSONObject doc = documents.getJSONObject(i);
                JSONObject fields = doc.getJSONObject("fields");

                if (!fields.has("month") || !fields.has("wheatKg") || !fields.has("riceKg")) continue;

                String month = fields.getJSONObject("month").getString("stringValue");
                String wheat = fields.getJSONObject("wheatKg").getString("integerValue");
                String rice = fields.getJSONObject("riceKg").getString("integerValue");

                output.append(String.format("📅 %s\n   🌾 Wheat: %s kg\n   🍚 Rice: %s kg\n\n", month, wheat, rice));
            }

            output.append("═══════════════════════════════════════");
            return output.toString();
        } catch (Exception e) {
            return "❌ Error loading stock data: " + e.getMessage();
        }
    }

    static String readStockForMonth(String targetMonth) {
        String endpoint = String.format(
            "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/rationStock?key=%s",
            Project_id, API_key);

        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            InputStream is = conn.getInputStream();
            byte[] responseBytes = is.readAllBytes();
            String response = new String(responseBytes, StandardCharsets.UTF_8);
            is.close();
            conn.disconnect();

            JSONObject json = new JSONObject(response);
            JSONArray documents = json.getJSONArray("documents");

            StringBuilder output = new StringBuilder("📊 STOCK DETAILS FOR " + targetMonth.toUpperCase() + "\n");
            output.append("═══════════════════════════════════════\n\n");
            boolean found = false;

            for (int i = 0; i < documents.length(); i++) {
                JSONObject doc = documents.getJSONObject(i);
                JSONObject fields = doc.getJSONObject("fields");

                if (!fields.has("month") || !fields.has("wheatKg") || !fields.has("riceKg")) continue;

                String month = fields.getJSONObject("month").getString("stringValue");
                if (month.equalsIgnoreCase(targetMonth)) {
                    String wheat = fields.getJSONObject("wheatKg").getString("integerValue");
                    String rice = fields.getJSONObject("riceKg").getString("integerValue");
                    output.append(String.format("📅 Month: %s\n🌾 Wheat Stock: %s kg\n🍚 Rice Stock: %s kg\n\n", month, wheat, rice));
                    
                    // Calculate total
                    int totalStock = Integer.parseInt(wheat) + Integer.parseInt(rice);
                    output.append(String.format("📊 Total Stock: %d kg\n", totalStock));
                    found = true;
                }
            }

            output.append("═══════════════════════════════════════");
            return found ? output.toString() : "❌ No stock data found for " + targetMonth;

        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
}