package com.smartration;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class userdemo {
    
    private final static String PROJECT_ID = "smart-ration-550f9";
    Scene usermanagementScene, useradd;
    Stage usermanagementStage;

    public void setUsermanagementScene(Scene usermanagementScene) {
        this.usermanagementScene = usermanagementScene;
    }
    
    public void setUsermanagementStage(Stage usermanagementStage) {
        this.usermanagementStage = usermanagementStage;
    }

    public VBox UserManagementScene(Runnable back) {
        // Main container with gradient background
        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background: linear-gradient(to bottom, #667eea, #764ba2); -fx-padding: 0;");
        
        // Header section
        VBox headerSection = createHeaderSection(back);
        
        // Content area with white background and rounded corners
        VBox contentArea = new VBox(20);
        contentArea.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 20 20 0 0; " +
            "-fx-padding: 30 40 30 40; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, -5);"
        );
        contentArea.setAlignment(Pos.TOP_CENTER);
        contentArea.setMaxWidth(Double.MAX_VALUE);
        
        // Search and actions section
        VBox searchSection = createSearchSection();
        
        // User display area
        VBox userDisplayContainer = createUserDisplayContainer();
        ScrollPane scrollPane = new ScrollPane(userDisplayContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setMaxHeight(Region.USE_COMPUTED_SIZE);
        scrollPane.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-background: transparent; " +
            "-fx-border-color: transparent;"
        );
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        // Snackbar for notifications
        Text snackBar = new Text("");
        snackBar.setStyle(
            "-fx-fill: #4CAF50; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold;"
        );
        
        setupUserManagementLogic(searchSection, userDisplayContainer, snackBar);
        
        // Add components to content area (removed stats cards)
        contentArea.getChildren().addAll(searchSection, scrollPane, snackBar);
        
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        mainContainer.getChildren().addAll(headerSection, contentArea);
        
        return mainContainer;
    }

    private VBox createHeaderSection(Runnable back) {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 40, 20, 40));
        header.setStyle("-fx-background-color: transparent;");
        
        // Back button with modern styling
        Button backButton = new Button("Back");
        backButton.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 8 16; " +
            "-fx-border-color: rgba(255,255,255,0.3); " +
            "-fx-border-radius: 20;"
        );
        backButton.setOnMouseEntered(e -> 
            backButton.setStyle(backButton.getStyle() + "-fx-background-color: rgba(255,255,255,0.3);")
        );
        backButton.setOnMouseExited(e -> 
            backButton.setStyle(backButton.getStyle().replace("-fx-background-color: rgba(255,255,255,0.3);", 
                                                            "-fx-background-color: rgba(255,255,255,0.2);"))
        );
        backButton.setOnAction(e -> back.run());
        
        // Title
        Text title = new Text("User Management");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setFill(Color.WHITE);
        
        Text subtitle = new Text("Manage all system users and their permissions");
        subtitle.setFont(Font.font("System", 16));
        subtitle.setFill(Color.rgb(255, 255, 255, 0.8));
        
    HBox titleContainer = new HBox();
titleContainer.setAlignment(Pos.CENTER_LEFT);
titleContainer.setMaxWidth(Double.MAX_VALUE);

Region spacer = new Region();
HBox.setHgrow(spacer, Priority.ALWAYS);

titleContainer.getChildren().addAll(new VBox(5, title, subtitle), spacer, backButton);

        header.getChildren().add(titleContainer);
        return header;
    }

    private VBox createSearchSection() {
        VBox searchSection = new VBox(20);
        searchSection.setAlignment(Pos.TOP_LEFT);
        searchSection.setMaxWidth(Double.MAX_VALUE);
        
        // Action buttons
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER_LEFT);
        
        Button addUserBtn = createModernButton("+ Add New User", "#4CAF50", true);
        Button refreshBtn = createModernButton("🔄 Refresh", "#2196F3", false);
        
        actionButtons.getChildren().addAll(addUserBtn, refreshBtn);
        
        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search users by name or email...");
        searchField.setStyle(
            "-fx-background-color: #f5f5f5; " +
            "-fx-background-radius: 25; " +
            "-fx-padding: 12 20; " +
            "-fx-font-size: 14px; " +
            "-fx-border-color: transparent; " +
            "-fx-focus-color: #667eea; " +
            "-fx-faint-focus-color: transparent;"
        );
        searchField.setPrefHeight(45);
        searchField.setMaxWidth(Double.MAX_VALUE);
        
        searchSection.getChildren().addAll(actionButtons, searchField);
        searchSection.setUserData(searchField); // Store reference for later use
        
        return searchSection;
    }

    private Button createModernButton(String text, String color, boolean primary) {
        Button button = new Button(text);
        if (primary) {
            button.setStyle(
                "-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 25; " +
                "-fx-padding: 12 24; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"
            );
        } else {
            button.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: " + color + "; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 25; " +
                "-fx-padding: 12 24; " +
                "-fx-border-color: " + color + "; " +
                "-fx-border-radius: 25; " +
                "-fx-cursor: hand;"
            );
        }
        
        button.setOnMouseEntered(e -> {
            if (primary) {
                button.setStyle(button.getStyle() + "-fx-opacity: 0.9;");
            } else {
                button.setStyle(button.getStyle().replace("-fx-background-color: transparent;", 
                                                         "-fx-background-color: " + color + "; -fx-text-fill: white;"));
            }
        });
        
        button.setOnMouseExited(e -> {
            if (primary) {
                button.setStyle(button.getStyle().replace("-fx-opacity: 0.9;", ""));
            } else {
                button.setStyle(button.getStyle().replace("-fx-background-color: " + color + "; -fx-text-fill: white;", 
                                                         "-fx-background-color: transparent; -fx-text-fill: " + color + ";"));
            }
        });
        
        return button;
    }

    private VBox createUserDisplayContainer() {
        VBox container = new VBox(15);
        container.setStyle("-fx-padding: 0;");
        container.setAlignment(Pos.TOP_LEFT);
        container.setMaxWidth(Double.MAX_VALUE);
        
        // Header for user list
        Text listHeader = new Text("All Users");
        listHeader.setFont(Font.font("System", FontWeight.BOLD, 18));
        listHeader.setFill(Color.rgb(60, 60, 60));
        
        container.getChildren().add(listHeader);
        return container;
    }

    private void setupUserManagementLogic(VBox searchSection, VBox userDisplay, Text snackBar) {
        // Get references to UI components
        HBox actionButtons = (HBox) searchSection.getChildren().get(0);
        Button addUserBtn = (Button) actionButtons.getChildren().get(0);
        Button refreshBtn = (Button) actionButtons.getChildren().get(1);
        TextField searchField = (TextField) searchSection.getUserData();
        
        // Add User Button Action
        addUserBtn.setOnAction(e -> {
            initilizadduserpage();
            usermanagementStage.setScene(useradd);
        });
        
        // Refresh Button Action - FIXED VERSION (removed stats update)
        refreshBtn.setOnAction(e -> {
            // Clear existing users (keep header)
            userDisplay.getChildren().subList(1, userDisplay.getChildren().size()).clear();
            snackBar.setText("");
            
            JSONArray documents = readUsersFromFirestore();
            if (documents != null) {
                for (int i = 0; i < documents.length(); i++) {
                    try {
                        JSONObject doc = documents.getJSONObject(i);
                        String docId = doc.getString("name").split("/")[doc.getString("name").split("/").length - 1];

                        if (doc.has("fields")) {
                            JSONObject fields = doc.getJSONObject("fields");
                            
                            // Safe extraction with null checks and default values
                            String email = "No email";
                            String name = "Unknown User";
                            
                            // Check if email field exists and extract safely
                            if (fields.has("email")) {
                                JSONObject emailObj = fields.getJSONObject("email");
                                if (emailObj.has("stringValue")) {
                                    email = emailObj.getString("stringValue");
                                }
                            }
                            
                            // Check if name field exists and extract safely
                            if (fields.has("name")) {
                                JSONObject nameObj = fields.getJSONObject("name");
                                if (nameObj.has("stringValue")) {
                                    name = nameObj.getString("stringValue");
                                }
                            }

                            VBox userCard = createUserCard(name, email, docId, userDisplay, snackBar);
                            userDisplay.getChildren().add(userCard);
                        }
                    } catch (Exception ex) {
                        System.out.println("Error processing user document " + i + ": " + ex.getMessage());
                        ex.printStackTrace();
                        // Continue processing other users even if one fails
                        continue;
                    }
                }
                
                if (documents.length() == 0) {
                    VBox emptyState = createEmptyState();
                    userDisplay.getChildren().add(emptyState);
                }
            } else {
                VBox errorState = createErrorState();
                userDisplay.getChildren().add(errorState);
            }
        });
        
        // Search functionality
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String search = newVal.toLowerCase();
            userDisplay.getChildren().subList(1, userDisplay.getChildren().size()).forEach(node -> {
                String userData = (String) node.getUserData();
                if (userData != null) {
                    node.setVisible(userData.contains(search));
                    node.setManaged(userData.contains(search));
                }
            });
        });
    }

    private VBox createUserCard(String name, String email, String docId, VBox userDisplay, Text snackBar) {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-padding: 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); " +
            "-fx-border-color: #f0f0f0; " +
            "-fx-border-radius: 12;"
        );
        card.setUserData((name + " " + email).toLowerCase());
        card.setMaxWidth(Double.MAX_VALUE);
        
        HBox cardContent = new HBox(15);
        cardContent.setAlignment(Pos.CENTER_LEFT);
        cardContent.setMaxWidth(Double.MAX_VALUE);
        
        // User avatar placeholder
        VBox avatar = new VBox();
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-background-radius: 25; " +
            "-fx-min-width: 50; " +
            "-fx-min-height: 50; " +
            "-fx-max-width: 50; " +
            "-fx-max-height: 50;"
        );
        Text avatarText = new Text(name.length() > 0 ? name.substring(0, 1).toUpperCase() : "?");
        avatarText.setFont(Font.font("System", FontWeight.BOLD, 20));
        avatarText.setFill(Color.WHITE);
        avatar.getChildren().add(avatarText);
        
        // User info
        VBox userInfo = new VBox(5);
        Text nameText = new Text(name);
        nameText.setFont(Font.font("System", FontWeight.BOLD, 16));
        nameText.setFill(Color.rgb(60, 60, 60));
        
        Text emailText = new Text(email);
        emailText.setFont(Font.font("System", 14));
        emailText.setFill(Color.rgb(120, 120, 120));
        
        userInfo.getChildren().addAll(nameText, emailText);
        
        // Actions
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle(
            "-fx-background-color: #ff4757; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 8 16; " +
            "-fx-cursor: hand;"
        );
        
        deleteBtn.setOnMouseEntered(e -> 
            deleteBtn.setStyle(deleteBtn.getStyle() + "-fx-background-color: #ff3742;")
        );
        deleteBtn.setOnMouseExited(e -> 
            deleteBtn.setStyle(deleteBtn.getStyle().replace("-fx-background-color: #ff3742;", "-fx-background-color: #ff4757;"))
        );
        
        deleteBtn.setOnAction(ev -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete User");
            confirm.setContentText("Are you sure you want to delete user: " + name + "?");
            
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean success = deleteUserFromFirestore(docId);
                    if (success) {
                        userDisplay.getChildren().remove(card);
                        showSnackbar(snackBar, "✅ User deleted: " + name);
                    } else {
                        showSnackbar(snackBar, "❌ Failed to delete user");
                    }
                }
            });
        });
        
        cardContent.getChildren().addAll(avatar, userInfo, spacer, deleteBtn);
        card.getChildren().add(cardContent);
        
        return card;
    }

    private VBox createEmptyState() {
        VBox emptyState = new VBox(15);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setStyle("-fx-padding: 40;");
        
        Text emptyIcon = new Text("👥");
        emptyIcon.setFont(Font.font(48));
        
        Text emptyText = new Text("No users found");
        emptyText.setFont(Font.font("System", FontWeight.BOLD, 18));
        emptyText.setFill(Color.rgb(150, 150, 150));
        
        Text emptySubtext = new Text("Click 'Add New User' to get started");
        emptySubtext.setFont(Font.font("System", 14));
        emptySubtext.setFill(Color.rgb(180, 180, 180));
        
        emptyState.getChildren().addAll(emptyIcon, emptyText, emptySubtext);
        return emptyState;
    }

    private VBox createErrorState() {
        VBox errorState = new VBox(15);
        errorState.setAlignment(Pos.CENTER);
        errorState.setStyle("-fx-padding: 40;");
        
        Text errorIcon = new Text("⚠️");
        errorIcon.setFont(Font.font(48));
        
        Text errorText = new Text("Unable to load users");
        errorText.setFont(Font.font("System", FontWeight.BOLD, 18));
        errorText.setFill(Color.rgb(255, 87, 87));
        
        Text errorSubtext = new Text("Please check your connection and try again");
        errorSubtext.setFont(Font.font("System", 14));
        errorSubtext.setFill(Color.rgb(180, 180, 180));
        
        errorState.getChildren().addAll(errorIcon, errorText, errorSubtext);
        return errorState;
    }

    private void showSnackbar(Text snackbar, String message) {
        snackbar.setText(message);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(() -> snackbar.setText(""));
        }).start();
    }

    private static JSONArray readUsersFromFirestore() {
        String endpoint = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + "/databases/(default)/documents/users";
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            if (conn.getResponseCode() == 200) {
                String response = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                JSONObject json = new JSONObject(response);
                return json.optJSONArray("documents");
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error reading: " + e.getMessage());
            return null;
        }
    }

    private static boolean deleteUserFromFirestore(String documentId) {
        String endpoint = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + "/databases/(default)/documents/users/" + documentId;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json");
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            System.out.println("Delete error: " + e.getMessage());
            return false;
        }
    }

    private void initilizadduserpage(){
        Adduserpage pg = new Adduserpage();
        pg.setUseraddStage(usermanagementStage);
        useradd = new Scene(pg.UserAddMethode(this::HandleBackButton), 1400, 900);
        pg.setUseraddScene(useradd);
    }

    private void HandleBackButton(){
        usermanagementStage.setScene(usermanagementScene);
    }
}