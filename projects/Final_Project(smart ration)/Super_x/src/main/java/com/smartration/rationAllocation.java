package com.smartration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class rationAllocation {

    Scene rationAllocationScene;
    Stage rationAllocationStage;
    
     private final static String FIREBASE_API_KEY = "AIzaSyDWIPwQ5nn65g2f5WKgdQ6LnQsa2rH0NF0";
    private final static String PROJECT_ID = "smart-ration-550f9";
    
    private VBox userListContainer;
    private Label statusLabel;
    private List<UserData> usersList = new ArrayList<>();

    public void setRationAllocationScene(Scene rationAllocationScene) {
        this.rationAllocationScene = rationAllocationScene;
    }

    public void setRationAllocationStage(Stage rationAllocationStage) {
        this.rationAllocationStage = rationAllocationStage;
    }

    public VBox RationAllocationMethod(Runnable back) {
        // Create header section
        VBox headerSection = createHeaderSection(back);
        
        // Create main content area
        VBox mainContent = createMainContent();
        
        // Main container
        VBox container = new VBox();
        container.getChildren().addAll(headerSection, mainContent);
        container.setStyle("-fx-background-color: #f5f7fa;");
        
        // Load users when page is created
        loadUsersFromFirebase();
        
        return container;
    }

    private VBox createHeaderSection(Runnable back) {
        // Header with gradient background
        HBox header = new HBox();
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: linear-gradient(to right, #667eea 0%, #764ba2 100%);");
        
        // Title section
        VBox titleSection = new VBox(5);
        
        // Main title
        Text mainTitle = new Text("📦 Monthly Ration Allocation");
        mainTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        mainTitle.setStyle("-fx-fill: white;");
        
        // Subtitle with current month
        String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        Text subtitle = new Text("Distribute monthly rations - " + currentMonth);
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setStyle("-fx-fill: #e8eaf6;");
        
        titleSection.getChildren().addAll(mainTitle, subtitle);
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Back button
        Button backButton = new Button("Back");
        backButton.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); " +
                            "-fx-text-fill: white; " +
                            "-fx-border-radius: 20; " +
                            "-fx-background-radius: 20; " +
                            "-fx-padding: 10 20; " +
                            "-fx-cursor: hand;");
        
        backButton.setOnMouseEntered(e -> 
            backButton.setStyle("-fx-background-color: rgba(255,255,255,0.3); " +
                                "-fx-text-fill: white; " +
                                "-fx-border-radius: 20; " +
                                "-fx-background-radius: 20; " +
                                "-fx-padding: 10 20; " +
                                "-fx-cursor: hand;"));
        
        backButton.setOnMouseExited(e -> 
            backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); " +
                                "-fx-text-fill: white; " +
                                "-fx-border-radius: 20; " +
                                "-fx-background-radius: 20; " +
                                "-fx-padding: 10 20; " +
                                "-fx-cursor: hand;"));
        
        backButton.setOnAction(e -> back.run());
        
        // Refresh button
        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        refreshButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); " +
                               "-fx-text-fill: white; " +
                               "-fx-border-radius: 20; " +
                               "-fx-background-radius: 20; " +
                               "-fx-padding: 10 20; " +
                               "-fx-cursor: hand; " +
                               "-fx-margin-right: 10;");
        
        refreshButton.setOnMouseEntered(e -> 
            refreshButton.setStyle("-fx-background-color: rgba(255,255,255,0.3); " +
                                   "-fx-text-fill: white; " +
                                   "-fx-border-radius: 20; " +
                                   "-fx-background-radius: 20; " +
                                   "-fx-padding: 10 20; " +
                                   "-fx-cursor: hand; " +
                                   "-fx-margin-right: 10;"));
        
        refreshButton.setOnMouseExited(e -> 
            refreshButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); " +
                                   "-fx-text-fill: white; " +
                                   "-fx-border-radius: 20; " +
                                   "-fx-background-radius: 20; " +
                                   "-fx-padding: 10 20; " +
                                   "-fx-cursor: hand; " +
                                   "-fx-margin-right: 10;"));
        
        refreshButton.setOnAction(e -> loadUsersFromFirebase());
        
        HBox buttonContainer = new HBox(10);
        buttonContainer.getChildren().addAll(refreshButton, backButton);
        
        header.getChildren().addAll(titleSection, spacer, buttonContainer);
        
        VBox headerContainer = new VBox(header);
        return headerContainer;
    }

    private VBox createMainContent() {
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.TOP_CENTER);
        
        // Status label
        statusLabel = new Label("Loading users...");
        statusLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
        statusLabel.setAlignment(Pos.CENTER);
        
        // Search box
        HBox searchBox = createSearchBox();
        
        // Users list container
        userListContainer = new VBox(15);
        userListContainer.setAlignment(Pos.TOP_CENTER);
        
        // Scroll pane for users list
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(userListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.setPrefHeight(500);
        
        mainContainer.getChildren().addAll(statusLabel, searchBox, scrollPane);
        
        return mainContainer;
    }
    
    private HBox createSearchBox() {
        HBox searchContainer = new HBox(10);
        searchContainer.setAlignment(Pos.CENTER);
        searchContainer.setPadding(new Insets(10));
        searchContainer.setMaxWidth(600);
        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search users by name, email, or ration card...");
        searchField.setFont(Font.font("Segoe UI", 14));
        searchField.setPrefHeight(40);
        searchField.setStyle("-fx-background-color: white; " +
                             "-fx-border-color: #ddd; " +
                             "-fx-border-radius: 20; " +
                             "-fx-background-radius: 20; " +
                             "-fx-padding: 10 15;");
        
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filterUsers(newText.toLowerCase().trim());
        });
        
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchContainer.getChildren().add(searchField);
        
        return searchContainer;
    }

    private void filterUsers(String searchText) {
        userListContainer.getChildren().clear();
        
        if (searchText.isEmpty()) {
            displayAllUsers();
        } else {
            for (UserData user : usersList) {
                if (user.name.toLowerCase().contains(searchText) ||
                    user.email.toLowerCase().contains(searchText) ||
                    user.rationCard.toLowerCase().contains(searchText)) {
                    userListContainer.getChildren().add(createUserCard(user));
                }
            }
        }
    }

    private void displayAllUsers() {
        userListContainer.getChildren().clear();
        for (UserData user : usersList) {
            userListContainer.getChildren().add(createUserCard(user));
        }
    }

    private VBox createUserCard(UserData user) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; " +
                      "-fx-background-radius: 12; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        card.setMaxWidth(800);
        
        // User header
        HBox userHeader = new HBox(15);
        userHeader.setAlignment(Pos.CENTER_LEFT);
        
        // User avatar
        Label avatar = new Label("👤");
        avatar.setFont(Font.font(24));
        avatar.setStyle("-fx-background-color: #667eea; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 25; " +
                        "-fx-min-width: 50; " +
                        "-fx-min-height: 50; " +
                        "-fx-alignment: center;");
        
        // User details
        VBox userDetails = new VBox(5);
        
        Text userName = new Text(user.name);
        userName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        userName.setStyle("-fx-fill: #2c3e50;");
        
        Text userInfo = new Text("📧 " + user.email + " | 🆔 " + user.rationCard);
        userInfo.setFont(Font.font("Segoe UI", 13));
        userInfo.setStyle("-fx-fill: #666;");
        
        userDetails.getChildren().addAll(userName, userInfo);
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Monthly allocation status
        Label statusBadge = new Label();
        if (user.hasRationThisMonth) {
            statusBadge.setText("✅ Allocated This Month");
            statusBadge.setStyle("-fx-background-color: #d4edda; " +
                                 "-fx-text-fill: #155724; " +
                                 "-fx-padding: 5 10; " +
                                 "-fx-background-radius: 15; " +
                                 "-fx-font-size: 11px; " +
                                 "-fx-font-weight: bold;");
            
            // Show allocation date
            if (user.lastAllocationDate != null && !user.lastAllocationDate.isEmpty()) {
                VBox statusContainer = new VBox(2);
                statusContainer.setAlignment(Pos.CENTER_RIGHT);
                
                Label dateLabel = new Label("📅 " + user.lastAllocationDate);
                dateLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 10px;");
                
                statusContainer.getChildren().addAll(statusBadge, dateLabel);
                userHeader.getChildren().addAll(avatar, userDetails, spacer, statusContainer);
            } else {
                userHeader.getChildren().addAll(avatar, userDetails, spacer, statusBadge);
            }
        } else {
            statusBadge.setText("⏳ Pending This Month");
            statusBadge.setStyle("-fx-background-color: #fff3cd; " +
                                 "-fx-text-fill: #856404; " +
                                 "-fx-padding: 5 10; " +
                                 "-fx-background-radius: 15; " +
                                 "-fx-font-size: 11px; " +
                                 "-fx-font-weight: bold;");
            userHeader.getChildren().addAll(avatar, userDetails, spacer, statusBadge);
        }
        
        // User additional info
        HBox userInfoRow = new HBox(20);
        userInfoRow.setAlignment(Pos.CENTER_LEFT);
        
        Label schemeLabel = new Label("📋 " + user.scheme);
        schemeLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        
        Label membersLabel = new Label("👥 " + user.memberCount + " members");
        membersLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        
        Label phoneLabel = new Label("📞 " + user.phone);
        phoneLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        
        userInfoRow.getChildren().addAll(schemeLabel, membersLabel, phoneLabel);
        
        // Action buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        
        Button allocateButton = new Button();
        if (user.hasRationThisMonth) {
            allocateButton.setText("Already Allocated This Month");
            allocateButton.setDisable(true);
            allocateButton.setStyle("-fx-background-color: #6c757d; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-border-radius: 8; " +
                                    "-fx-background-radius: 8; " +
                                    "-fx-padding: 8 16; " +
                                    "-fx-font-weight: bold;");
        } else {
            allocateButton.setText("📦 Allocate Monthly Ration");
            allocateButton.setStyle("-fx-background-color: #28a745; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-border-radius: 8; " +
                                    "-fx-background-radius: 8; " +
                                    "-fx-padding: 8 16; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-font-weight: bold;");
            
            allocateButton.setOnMouseEntered(e -> 
                allocateButton.setStyle("-fx-background-color: #218838; " +
                                        "-fx-text-fill: white; " +
                                        "-fx-border-radius: 8; " +
                                        "-fx-background-radius: 8; " +
                                        "-fx-padding: 8 16; " +
                                        "-fx-cursor: hand; " +
                                        "-fx-font-weight: bold; " +
                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2);"));
            
            allocateButton.setOnMouseExited(e -> 
                allocateButton.setStyle("-fx-background-color: #28a745; " +
                                        "-fx-text-fill: white; " +
                                        "-fx-border-radius: 8; " +
                                        "-fx-background-radius: 8; " +
                                        "-fx-padding: 8 16; " +
                                        "-fx-cursor: hand; " +
                                        "-fx-font-weight: bold;"));
            
            allocateButton.setOnAction(e -> allocateRationToUser(user));
        }
        
        Button viewHistoryButton = new Button("📊 View History");
        viewHistoryButton.setStyle("-fx-background-color: #007bff; " +
                                   "-fx-text-fill: white; " +
                                   "-fx-border-radius: 8; " +
                                   "-fx-background-radius: 8; " +
                                   "-fx-padding: 8 16; " +
                                   "-fx-cursor: hand; " +
                                   "-fx-font-weight: bold;");
        
        viewHistoryButton.setOnMouseEntered(e -> 
            viewHistoryButton.setStyle("-fx-background-color: #0056b3; " +
                                       "-fx-text-fill: white; " +
                                       "-fx-border-radius: 8; " +
                                       "-fx-background-radius: 8; " +
                                       "-fx-padding: 8 16; " +
                                       "-fx-cursor: hand; " +
                                       "-fx-font-weight: bold; " +
                                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2);"));
        
        viewHistoryButton.setOnMouseExited(e -> 
            viewHistoryButton.setStyle("-fx-background-color: #007bff; " +
                                       "-fx-text-fill: white; " +
                                       "-fx-border-radius: 8; " +
                                       "-fx-background-radius: 8; " +
                                       "-fx-padding: 8 16; " +
                                       "-fx-cursor: hand; " +
                                       "-fx-font-weight: bold;"));
        
        viewHistoryButton.setOnAction(e -> viewUserHistory(user));
        
        actionButtons.getChildren().addAll(allocateButton, viewHistoryButton);
        
        card.getChildren().addAll(userHeader, userInfoRow, actionButtons);
        
        return card;
    }

    private void allocateRationToUser(UserData user) {
        // Show confirmation dialog with monthly allocation details
        String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Monthly Ration Allocation");
        confirmationAlert.setHeaderText("Allocate monthly ration to " + user.name + "?");
        confirmationAlert.setContentText("This will allocate the full monthly ration for " + currentMonth + 
                                       " and mark as distributed.\n\nMonthly ration items:\n🌾 Wheat: 5kg\n🍚 Rice: 3kg\n\n" +
                                       "Note: This can only be done once per month per user.");
        
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response.getButtonData().isDefaultButton()) {
                // User clicked OK, proceed with allocation
                performRationAllocation(user);
            }
        });
    }

    private void performRationAllocation(UserData user) {
        // Create background task for allocation
        Task<Boolean> allocationTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return allocateRationInFirebase(user);
            }
        };
        
        allocationTask.setOnSucceeded(e -> {
            if (allocationTask.getValue()) {
                // Update user status locally
                user.hasRationThisMonth = true;
                user.lastAllocationDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                user.lastAllocationMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
                user.wheatReceived = true;
                user.riceReceived = true;
                user.wheatReceivedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                user.riceReceivedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                
                // Refresh the display
                displayAllUsers();
                
                // Show success message
                String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                         "Monthly ration allocated successfully to " + user.name + " for " + currentMonth + 
                         "\n\nItems distributed:\n🌾 Wheat: 5kg\n🍚 Rice: 3kg");
                
                statusLabel.setText("✅ Monthly ration allocated to " + user.name + " at " + 
                                  java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", 
                         "Failed to allocate monthly ration. Please try again.");
            }
        });
        
        allocationTask.setOnFailed(e -> {
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "An error occurred while allocating monthly ration.");
        });
        
        // Run task in background
        new Thread(allocationTask).start();
        
        // Update status
        statusLabel.setText("⏳ Allocating monthly ration to " + user.name + "...");
    }

    private boolean allocateRationInFirebase(UserData user) {
        try {
            // UNIFORM SANITIZATION!
            String sanitizedEmail = user.email.replace("@", "at").replace(".", "dot");
            String currentDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
            String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

            // First, verify the document exists by doing a GET request
            String getUrlStr = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID +
                "/databases/(default)/documents/users/" + sanitizedEmail;
            
            URL getUrl = new URL(getUrlStr);
            HttpURLConnection getConn = (HttpURLConnection) getUrl.openConnection();
            getConn.setRequestMethod("GET");
            getConn.setRequestProperty("Content-Type", "application/json");
            
            int getResponseCode = getConn.getResponseCode();
            System.out.println("GET Response Code: " + getResponseCode);
            
            if (getResponseCode != 200) {
                System.out.println("User document not found with ID: " + sanitizedEmail);
                System.out.println("Original email: " + user.email);
                
                // Try to find the correct document ID by searching
                String correctDocId = findCorrectDocumentId(user.email);
                if (correctDocId != null) {
                    sanitizedEmail = correctDocId;
                    System.out.println("Found correct document ID: " + sanitizedEmail);
                } else {
                    System.out.println("Could not find document for user: " + user.email);
                    return false;
                }
            }

            // Now proceed with the update using POST + X-HTTP-Method-Override
            // Build the URL with updateMask parameters
            String updateMask = "hasRationThisMonth,lastAllocationDate,lastAllocationMonth,rationStatus,wheatReceived,wheatReceivedDate,riceReceived,riceReceivedDate";
            String patchUrlStr = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID +
                "/databases/(default)/documents/users/" + sanitizedEmail + 
                "?updateMask.fieldPaths=" + updateMask.replace(",", "&updateMask.fieldPaths=");
                
            URL patchUrl = new URL(patchUrlStr);
            HttpURLConnection patchConn = (HttpURLConnection) patchUrl.openConnection();

            // Use POST with X-HTTP-Method-Override header for PATCH
            patchConn.setRequestMethod("POST");
            patchConn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            patchConn.setRequestProperty("Content-Type", "application/json");
            patchConn.setDoOutput(true);

            // Create the update payload
            JSONObject fields = new JSONObject();
            fields.put("hasRationThisMonth", new JSONObject().put("booleanValue", true));
            fields.put("lastAllocationDate", new JSONObject().put("stringValue", currentDate));
            fields.put("lastAllocationMonth", new JSONObject().put("stringValue", currentMonth));
            fields.put("rationStatus", new JSONObject().put("stringValue", "Distributed"));
            fields.put("wheatReceived", new JSONObject().put("booleanValue", true));
            fields.put("wheatReceivedDate", new JSONObject().put("stringValue", currentDate));
            fields.put("riceReceived", new JSONObject().put("booleanValue", true));
            fields.put("riceReceivedDate", new JSONObject().put("stringValue", currentDate));

            JSONObject payload = new JSONObject();
            payload.put("fields", fields);

            System.out.println("Request URL: " + patchUrlStr);
            System.out.println("Payload: " + payload.toString());

            try (OutputStream os = patchConn.getOutputStream()) {
                os.write(payload.toString().getBytes());
            }

            int patchResponseCode = patchConn.getResponseCode();
            System.out.println("PATCH Response Code: " + patchResponseCode);
            
            if (patchResponseCode == 200) {
                System.out.println("Successfully updated user document");
                // FIXED: Create both history records with unique IDs
                createDistributionHistory(user, currentDate, currentMonth, "admin_allocation");
                return true;
            } else {
                // Print error for debugging
                System.out.println("PATCH failed with response code: " + patchResponseCode);
                InputStream err = patchConn.getErrorStream();
                if (err != null) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(err));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) sb.append(line);
                    System.out.println("PATCH Firestore error: " + sb.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to find the correct document ID
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

    // FIXED: Unified distribution history creation with unique IDs
    private void createDistributionHistory(UserData user, String date, String month, String distributionSource) {
        try {
            // Create UNIQUE document ID using timestamp + UUID to avoid overwrites
            String timestamp = String.valueOf(System.currentTimeMillis());
            String uniqueId = user.email.replace("@", "at").replace(".", "dot") + "_" + 
                             month + "" + distributionSource + "" + timestamp;
            
            String urlStr = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID +
                            "/databases/(default)/documents/distributionHistory?documentId=" + uniqueId;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject fields = new JSONObject();
            fields.put("userEmail", new JSONObject().put("stringValue", user.email));
            fields.put("userName", new JSONObject().put("stringValue", user.name));
            fields.put("rationCard", new JSONObject().put("stringValue", user.rationCard));
            fields.put("distributionDate", new JSONObject().put("stringValue", date));
            fields.put("distributionMonth", new JSONObject().put("stringValue", month));
            fields.put("timestamp", new JSONObject().put("timestampValue", java.time.Instant.now().toString()));
            fields.put("distributedBy", new JSONObject().put("stringValue", "Admin - " + LoggedInUser.getEmail()));
            fields.put("itemsDistributed", new JSONObject().put("stringValue", "🌾 Wheat: 5kg, 🍚 Rice: 3kg"));
            fields.put("distributionType", new JSONObject().put("stringValue", distributionSource));

            JSONObject payload = new JSONObject();
            payload.put("fields", fields);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes());
            }
            
            int responseCode = conn.getResponseCode();
            System.out.println("Distribution history created with response code: " + responseCode);
            
        } catch (Exception e) {
            System.out.println("Error creating distribution history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewUserHistory(UserData user) {
        // This would open a new window/dialog showing allocation history
        Alert historyAlert = new Alert(Alert.AlertType.INFORMATION);
        historyAlert.setTitle("Monthly Allocation History");
        historyAlert.setHeaderText("Monthly Ration History for " + user.name);
        
        String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        String historyText = "Current Month: " + currentMonth + "\n";
        historyText += "Last allocation: " + 
                           (user.lastAllocationDate != null ? user.lastAllocationDate : "Never") + "\n\n";
        
        if (user.hasRationThisMonth) {
            historyText += "Current Status: ✅ Allocated for " + currentMonth + "\n";
            historyText += "🌾 Wheat: " + (user.wheatReceived ? "✅ Received (5kg)" : "❌ Not received") + "\n";
            historyText += "🍚 Rice: " + (user.riceReceived ? "✅ Received (3kg)" : "❌ Not received") + "\n";
        } else {
            historyText += "Current Status: ⏳ Pending allocation for " + currentMonth + "\n";
        }
        
        historyText += "\nNote: Rations are allocated once per month.\n";
        historyText += "Detailed monthly history will be available in a future update.";
        
        historyAlert.setContentText(historyText);
        historyAlert.showAndWait();
    }

    private void loadUsersFromFirebase() {
        statusLabel.setText("🔄 Loading users...");
        
        Task<List<UserData>> loadTask = new Task<List<UserData>>() {
            @Override
            protected List<UserData> call() throws Exception {
                return fetchUsersFromFirebase();
            }
        };
        
        loadTask.setOnSucceeded(e -> {
            usersList = loadTask.getValue();
            Platform.runLater(() -> {
                if (usersList.isEmpty()) {
                    statusLabel.setText("No users found. Add users first.");
                } else {
                    String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                    long allocatedThisMonth = usersList.stream().mapToLong(u -> u.hasRationThisMonth ? 1 : 0).sum();
                    statusLabel.setText("📊 Total users: " + usersList.size() + 
                                      " | 🟢 Allocated for " + currentMonth + ": " + allocatedThisMonth);
                }
                displayAllUsers();
            });
        });
        
        loadTask.setOnFailed(e -> {
            statusLabel.setText("❌ Failed to load users. Check your connection.");
        });
        
        new Thread(loadTask).start();
    }

    private List<UserData> fetchUsersFromFirebase() {
        List<UserData> users = new ArrayList<>();
        String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
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
                        
                        UserData user = new UserData();
                        user.name = getStringValue(fields, "name");
                        user.email = getStringValue(fields, "email");
                        user.scheme = getStringValue(fields, "scheme");
                        user.memberCount = getStringValue(fields, "memberCount");
                        user.phone = getStringValue(fields, "phone");
                        user.rationCard = getStringValue(fields, "rationCard");
                        user.lastAllocationDate = getStringValue(fields, "lastAllocationDate");
                        user.lastAllocationMonth = getStringValue(fields, "lastAllocationMonth");
                        user.wheatReceivedDate = getStringValue(fields, "wheatReceivedDate");
                        user.riceReceivedDate = getStringValue(fields, "riceReceivedDate");
                        
                        // Check if ration was allocated THIS MONTH (not today)
                        user.hasRationThisMonth = getBooleanValue(fields, "hasRationThisMonth") && 
                                                currentMonth.equals(user.lastAllocationMonth);
                        
                        // Check if wheat and rice were received this month
                        user.wheatReceived = getBooleanValue(fields, "wheatReceived") &&
                                           currentMonth.equals(getMonthFromDate(user.wheatReceivedDate));
                        user.riceReceived = getBooleanValue(fields, "riceReceived") &&
                                          currentMonth.equals(getMonthFromDate(user.riceReceivedDate));
                        
                        // Don't add admin users to the list
                        if (!getBooleanValue(fields, "isAdmin")) {
                            users.add(user);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return users;
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    // Inner class to hold user data
    private static class UserData {
        String name = "";
        String email = "";
        String scheme = "";
        String memberCount = "";
        String phone = "";
        String rationCard = "";
        String lastAllocationDate = "";
        String lastAllocationMonth = "";  // New field to track allocation month
        String wheatReceivedDate = "";
        String riceReceivedDate = "";
        boolean hasRationThisMonth = false;  // Changed from hasRationToday
        boolean wheatReceived = false;
        boolean riceReceived = false;
    }
}