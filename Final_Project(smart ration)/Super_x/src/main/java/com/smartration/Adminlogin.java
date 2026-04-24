package com.smartration;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class Adminlogin {

    private final String adminEmail = "umesh@gmail.com"; 

    public void initialize(Stage stage) {
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8fafc;");
        
        // Top header bar matching dashboard style
        HBox header = new HBox();
        header.setPrefHeight(70);
        header.setMinHeight(70);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 30, 0, 30));
        header.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);"
        );

        // Header content
        HBox headerLeft = new HBox(15);
        headerLeft.setAlignment(Pos.CENTER_LEFT);

        Label headerIcon = new Label("🏪");
        headerIcon.setFont(Font.font(26));

        VBox headerTextBox = new VBox(2);
        headerTextBox.setAlignment(Pos.CENTER_LEFT);
        
        Label headerTitle = new Label("Smart Ration System");
        headerTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        headerTitle.setTextFill(Color.WHITE);

        Label headerSubtitle = new Label("Digital Ration Card Management");
        headerSubtitle.setFont(Font.font("Segoe UI", 15));
        headerSubtitle.setTextFill(Color.rgb(255, 255, 255, 0.9));
        
        headerTextBox.getChildren().addAll(headerTitle, headerSubtitle);
        headerLeft.getChildren().addAll(headerIcon, headerTextBox);

        // Header right side - admin indicator
        HBox headerRight = new HBox(10);
        headerRight.setAlignment(Pos.CENTER_RIGHT);

        Label adminBadge = new Label("👨‍💼 Admin Portal");
        adminBadge.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 15));
        adminBadge.setTextFill(Color.WHITE);
        adminBadge.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2); " +
            "-fx-background-radius: 25; " +
            "-fx-padding: 10 20;"
        );

        headerRight.getChildren().add(adminBadge);
        
        // Create spacer region
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox headerContent = new HBox();
        headerContent.getChildren().addAll(headerLeft, spacer, headerRight);
        header.getChildren().add(headerContent);

        // Create ScrollPane for main content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: #f8fafc; -fx-background: #f8fafc;");

        // Main content area inside scroll pane
        VBox mainContent = new VBox();
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setMinHeight(Region.USE_COMPUTED_SIZE);
        mainContent.setPadding(new Insets(30, 40, 30, 40));
        mainContent.setStyle("-fx-background-color: #f8fafc;");

        // Login container with proper spacing
        VBox loginContainer = new VBox(25);
        loginContainer.setAlignment(Pos.CENTER);
        loginContainer.setMaxWidth(500);

        // Welcome section with better spacing
        VBox welcomeSection = new VBox(12);
        welcomeSection.setAlignment(Pos.CENTER);

        Label welcomeTitle = new Label("Welcome Back!");
        welcomeTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 42));
        welcomeTitle.setTextFill(Color.rgb(51, 65, 85));

        Label welcomeSubtitle = new Label("Sign in to access the admin dashboard");
        welcomeSubtitle.setFont(Font.font("Segoe UI", 18));
        welcomeSubtitle.setTextFill(Color.rgb(100, 116, 139));
        welcomeSubtitle.setWrapText(true);

        // Emoji wave with better positioning
        Label waveEmoji = new Label("👋");
        waveEmoji.setFont(Font.font(36));

        welcomeSection.getChildren().addAll(welcomeTitle, welcomeSubtitle, waveEmoji);

        // Login form card - improved dimensions and styling
        VBox loginCard = new VBox(20);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(35, 40, 35, 40));
        loginCard.setPrefWidth(450);
        loginCard.setMaxWidth(450);
        loginCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 16; " +
            "-fx-border-color: #e2e8f0; " +
            "-fx-border-radius: 16; " +
            "-fx-border-width: 1; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 25, 0, 0, 8);"
        );

        // Form title with better spacing
        Label formTitle = new Label("Administrator Access");
        formTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        formTitle.setTextFill(Color.rgb(51, 65, 85));

        // Email field group with improved layout
        VBox emailGroup = new VBox(8);
        emailGroup.setAlignment(Pos.CENTER_LEFT);
        emailGroup.setPrefWidth(370);

        Label emailLabel = new Label("Email Address");
        emailLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 15));
        emailLabel.setTextFill(Color.rgb(51, 65, 85));

        TextField emailField = new TextField();
        emailField.setPromptText("Enter admin email");
        styleDashboardFormField(emailField);

        emailGroup.getChildren().addAll(emailLabel, emailField);

        // Password field group with improved layout
        VBox passwordGroup = new VBox(8);
        passwordGroup.setAlignment(Pos.CENTER_LEFT);
        passwordGroup.setPrefWidth(370);

        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 15));
        passwordLabel.setTextFill(Color.rgb(51, 65, 85));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        styleDashboardFormField(passwordField);

        passwordGroup.getChildren().addAll(passwordLabel, passwordField);
        // Add this after the passwordGroup and before the loginBtn in your Adminlogin.java

// Forgot password section with proper dashboard styling
HBox forgotPasswordSection = new HBox();
forgotPasswordSection.setAlignment(Pos.CENTER_RIGHT);
forgotPasswordSection.setPrefWidth(370);
forgotPasswordSection.setPadding(new Insets(5, 0, 0, 0));

Label forgotPasswordLink = new Label("Forgot Password?");
forgotPasswordLink.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
forgotPasswordLink.setTextFill(Color.rgb(102, 126, 234)); // Matching your dashboard theme
forgotPasswordLink.setUnderline(true);
forgotPasswordLink.setStyle("-fx-cursor: hand;");

// Add hover effects matching your dashboard theme
forgotPasswordLink.setOnMouseEntered(e -> {
    forgotPasswordLink.setTextFill(Color.rgb(90, 103, 216));
    forgotPasswordLink.setStyle("-fx-cursor: hand; -fx-underline: true;");
});

forgotPasswordLink.setOnMouseExited(e -> {
    forgotPasswordLink.setTextFill(Color.rgb(102, 126, 234));
    forgotPasswordLink.setStyle("-fx-cursor: hand;");
});

// Add click handler (currently just shows a message)
forgotPasswordLink.setOnMouseClicked(e -> {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Forgot Password");
    alert.setHeaderText(null);
    alert.setContentText("Please contact support to reset your password.");
    alert.showAndWait();
});

forgotPasswordSection.getChildren().add(forgotPasswordLink);

        // Login button with improved styling
        Button loginBtn = new Button("Sign in to Dashboard");
        styleDashboardPrimaryButton(loginBtn);

        // Status label with better positioning
        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 15));
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setPrefWidth(370);
        statusLabel.setWrapText(true);

        // Back button with improved spacing
        Button backBtn = new Button("Back");
        styleDashboardSecondaryButton(backBtn);

        loginCard.getChildren().addAll(formTitle, emailGroup, passwordGroup, forgotPasswordSection, loginBtn, statusLabel, backBtn);

        loginContainer.getChildren().addAll(welcomeSection, loginCard);
        
        // Add container to main content with centering
        mainContent.getChildren().add(loginContainer);
        VBox.setVgrow(loginContainer, Priority.NEVER);
        
        // Set content to scroll pane
        scrollPane.setContent(mainContent);

        // Event handlers (keeping original functionality)
        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            String idToken = signInWithEmailAndPassword(email, password);
            if (idToken != null) {
                if (email.equals(adminEmail)) {
                    statusLabel.setTextFill(Color.rgb(34, 197, 94));
                    statusLabel.setText("✓ Login successful");

                    try {
                        AdminDashboard dashboard = new AdminDashboard();  
                        dashboard.initialize(stage);  
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    statusLabel.setTextFill(Color.rgb(239, 68, 68));
                    statusLabel.setText("✗ Access Denied: Not an Admin");
                }
            } else {
                statusLabel.setTextFill(Color.rgb(239, 68, 68));
                statusLabel.setText("✗ Invalid credentials");
            }
        });

        backBtn.setOnAction(e -> {
            try {
           WelcomePage page = new WelcomePage(); 
               page.show(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Assemble layout
        root.setTop(header);
        root.setCenter(scrollPane);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

stage.setTitle("Smart Ration System");
stage.setScene(scene);
stage.setX(screenBounds.getMinX());
stage.setY(screenBounds.getMinY());
stage.setWidth(screenBounds.getWidth());
stage.setHeight(screenBounds.getHeight());
stage.setResizable(false);

    }

    private String signInWithEmailAndPassword(String email, String password) {
        try {
            URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyCw_hJmevrQsmsQ1Mg0LKBylH7z6zLcsNw");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String payload = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                email, password
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes());
            }

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                String responseBody = response.toString();
                int tokenStart = responseBody.indexOf("\"idToken\":\"") + 11;
                int tokenEnd = responseBody.indexOf("\"", tokenStart);
                return responseBody.substring(tokenStart, tokenEnd); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void styleDashboardFormField(TextField field) {
        field.setPrefHeight(50);
        field.setPrefWidth(370);
        field.setMaxWidth(370);
        field.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #d1d5db; " +
            "-fx-border-radius: 10; " +
            "-fx-border-width: 1; " +
            "-fx-padding: 15 18; " +
            "-fx-font-size: 15px; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-text-fill: #374151; " +
            "-fx-prompt-text-fill: #9ca3af;"
        );
        
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: #667eea; " +
                    "-fx-border-radius: 10; " +
                    "-fx-border-width: 2; " +
                    "-fx-padding: 15 18; " +
                    "-fx-font-size: 15px; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-text-fill: #374151; " +
                    "-fx-prompt-text-fill: #9ca3af; " +
                    "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.25), 12, 0, 0, 0);"
                );
            } else {
                field.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: #d1d5db; " +
                    "-fx-border-radius: 10; " +
                    "-fx-border-width: 1; " +
                    "-fx-padding: 15 18; " +
                    "-fx-font-size: 15px; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-text-fill: #374151; " +
                    "-fx-prompt-text-fill: #9ca3af;"
                );
            }
        });
    }

    private void styleDashboardPrimaryButton(Button btn) {
        btn.setPrefHeight(52);
        btn.setPrefWidth(370);
        btn.setMaxWidth(370);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        btn.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-border-width: 0; " +
            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 15, 0, 0, 5);"
        );
        
        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #5a67d8, #6b46c1); " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand; " +
                "-fx-border-width: 0; " +
                "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 20, 0, 0, 8);"
            );
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand; " +
                "-fx-border-width: 0; " +
                "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 15, 0, 0, 5);"
            );
        });
    }

    private void styleDashboardSecondaryButton(Button btn) {
        btn.setPrefHeight(46);
        btn.setPrefWidth(320);
        btn.setMaxWidth(320);
        btn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 15));
        btn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #667eea; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-border-width: 0;"
        );
        
        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                "-fx-background-color: #f1f5f9; " +
                "-fx-text-fill: #5a67d8; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand; " +
                "-fx-border-width: 0;"
            );
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: #667eea; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand; " +
                "-fx-border-width: 0;"
            );
        });
    }
}