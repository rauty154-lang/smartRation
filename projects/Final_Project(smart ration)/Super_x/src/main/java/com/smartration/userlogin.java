package com.smartration;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class userlogin {

    public void initialize(Stage stage) {
        // Header section with gradient background
        Label systemTitle = new Label("🍚 Smart Ration System");
        systemTitle.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 24));
        systemTitle.setTextFill(Color.WHITE);
        
        Label subtitle = new Label("Digital Ration Card Management");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.rgb(200, 200, 255));
        
        VBox headerBox = new VBox(5, systemTitle, subtitle);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(20, 40, 20, 40));
        
        // Create gradient background for header
        LinearGradient headerGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.rgb(106, 90, 205)),
            new Stop(1, Color.rgb(72, 61, 139))
        );
        headerBox.setBackground(new Background(new BackgroundFill(headerGradient, CornerRadii.EMPTY, Insets.EMPTY)));

        // Main content area
        Label title = new Label("User Login");
        title.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 32));
        title.setTextFill(Color.rgb(55, 65, 81));
        
        // Email field with modern styling
        TextField emailField = new TextField();
        emailField.setPromptText("User Email");
        styleModernField(emailField);

        // Password field with modern styling
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleModernField(passwordField);

        Label forgotPasswordLink = new Label("Forgot Password?");
forgotPasswordLink.setTextFill(Color.rgb(59, 130, 246));
forgotPasswordLink.setUnderline(true);
forgotPasswordLink.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.MEDIUM, 13));
forgotPasswordLink.setStyle("-fx-cursor: hand;");
forgotPasswordLink.setAlignment(Pos.CENTER_RIGHT);
forgotPasswordLink.setMaxWidth(Double.MAX_VALUE);

// Add hover effects for forgot password link
forgotPasswordLink.setOnMouseEntered(e -> {
    forgotPasswordLink.setTextFill(Color.rgb(37, 99, 235));
    forgotPasswordLink.setStyle("-fx-cursor: hand; -fx-underline: true;");
});

forgotPasswordLink.setOnMouseExited(e -> {
    forgotPasswordLink.setTextFill(Color.rgb(59, 130, 246));
    forgotPasswordLink.setStyle("-fx-cursor: hand;");
});
// Add action for forgot password link
forgotPasswordLink.setOnMouseClicked(e -> {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Forgot Password");
    alert.setHeaderText(null);
    alert.setContentText("Please contact support to reset your password.");
    alert.showAndWait();
});


        // Status label
        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Segoe UI", 14));

        // Login button with modern styling
        Button loginBtn = new Button("Login");
        styleModernButton(loginBtn, true);
        
        // Keep your existing login functionality unchanged
        // Replace your existing login button action with this:
loginBtn.setOnAction(e -> {
    String email = emailField.getText().trim();
    String password = passwordField.getText().trim();
    
    // Basic validation
    if (email.isEmpty() || password.isEmpty()) {
        statusLabel.setTextFill(Color.rgb(239, 68, 68));
        statusLabel.setText("✗ Please enter both email and password");
        return;
    }
    
    // Disable button and show loading
    loginBtn.setDisable(true);
    loginBtn.setText("Logging in...");
    statusLabel.setText("Authenticating...");
    statusLabel.setTextFill(Color.rgb(107, 114, 128));
    
    try {
        if (signInWithEmailAndPassword(email, password)) {
            statusLabel.setTextFill(Color.rgb(34, 197, 94));
            statusLabel.setText("✓ Login successful");
            
            try {
                new UserDashboard().initialize(stage); 
            } catch (Exception dashboardEx) {
                // Handle dashboard initialization error gracefully
                statusLabel.setTextFill(Color.rgb(239, 68, 68));
                String errorMsg = dashboardEx.getMessage();
                if (errorMsg != null && errorMsg.contains("fetch")) {
                    statusLabel.setText("Please Enter Valid Credentials");
                } else {
                    statusLabel.setText("✗ Unable to load dashboard. Please try again.");
                }
                System.out.println("Dashboard error: " + errorMsg);
            }
        } else {
            statusLabel.setTextFill(Color.rgb(239, 68, 68));
            statusLabel.setText("✗ Invalid email or password");
        }
    } catch (Exception loginEx) {
        // Handle any other login-related errors
        statusLabel.setTextFill(Color.rgb(239, 68, 68));
        statusLabel.setText("✗ Login failed. Please check your connection.");
        System.out.println("Login error: " + loginEx.getMessage());
    } finally {
        // Re-enable button
        loginBtn.setDisable(false);
        loginBtn.setText("Login");
    }
});

        // Signup section
        Label signupLabel = new Label("Don't have an account?");
        signupLabel.setTextFill(Color.rgb(107, 114, 128));
        signupLabel.setFont(Font.font("Segoe UI", 14));

        Label signupLink = new Label("Sign up");
        signupLink.setTextFill(Color.rgb(59, 130, 246));
        signupLink.setUnderline(true);
        signupLink.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));
        signupLink.setStyle("-fx-cursor: hand;");

        // Keep your existing signup functionality unchanged
        signupLink.setOnMouseClicked((MouseEvent e) -> {
            try {
                new SignupForm().initialize(stage);
            
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox signupBox = new HBox(5, signupLabel, signupLink);
        signupBox.setAlignment(Pos.CENTER);

        // Back button with modern styling
        Button backBtn = new Button("Back");
        styleModernButton(backBtn, false);
        
        // Keep your existing back button functionality unchanged
        backBtn.setOnAction(e -> {
            try {
               WelcomePage page = new WelcomePage(); 
               page.show(stage);
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Login form container
        VBox loginForm = new VBox(20);
        loginForm.getChildren().addAll(title, emailField, passwordField, forgotPasswordLink, loginBtn, statusLabel);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setPadding(new Insets(40, 40, 20, 40));
        loginForm.setMaxWidth(400);
        loginForm.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        // Bottom section
        VBox bottomSection = new VBox(15, signupBox, backBtn);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(20));

        // Main content area
        VBox contentArea = new VBox(30, loginForm, bottomSection);
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setPadding(new Insets(40));
        contentArea.setBackground(new Background(new BackgroundFill(Color.rgb(249, 250, 251), CornerRadii.EMPTY, Insets.EMPTY)));

        // Root container
        VBox root = new VBox();
        root.getChildren().addAll(headerBox, contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

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

private boolean signInWithEmailAndPassword(String email, String password) {
    try {
        URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyDWIPwQ5nn65g2f5WKgdQ6LnQsa2rH0NF0");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String payload = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", email, password);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes());
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            LoggedInUser.setEmail(email);
            return true;
        } else {
            // Handle specific error cases
            if (responseCode == 400) {
                // Firebase returns 400 for invalid credentials, user not found, etc.
                System.out.println("Authentication failed: Invalid credentials or user not found");
            } else {
                System.out.println("Authentication failed with response code: " + responseCode);
            }
            return false;
        }
    } catch (Exception e) {
        // Handle network errors, connection timeouts, etc.
        System.out.println("Network error during login: " + e.getMessage());
        return false;
    }
}

    // Modern field styling to match dashboard theme
    private void styleModernField(TextField field) {
        field.setMaxWidth(320);
        field.setPrefHeight(45);
        field.setStyle(
            "-fx-background-color: rgb(249, 250, 251);" +
            "-fx-border-color: rgb(209, 213, 219);" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 12px 16px;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Segoe UI';"
        );
        
        field.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                field.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: rgb(59, 130, 246);" +
                    "-fx-border-width: 2px;" +
                    "-fx-border-radius: 8px;" +
                    "-fx-background-radius: 8px;" +
                    "-fx-padding: 11px 15px;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-family: 'Segoe UI';"
                );
            } else {
                field.setStyle(
                    "-fx-background-color: rgb(249, 250, 251);" +
                    "-fx-border-color: rgb(209, 213, 219);" +
                    "-fx-border-width: 1px;" +
                    "-fx-border-radius: 8px;" +
                    "-fx-background-radius: 8px;" +
                    "-fx-padding: 12px 16px;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-family: 'Segoe UI';"
                );
            }
        });
    }

    // Modern button styling to match dashboard theme
    private void styleModernButton(Button btn, boolean isPrimary) {
        btn.setPrefHeight(45);
        btn.setPrefWidth(isPrimary ? 320 : 120);
        btn.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));
        
        if (isPrimary) {
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, rgb(59, 130, 246), rgb(99, 102, 241));" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 8, 0, 0, 2);"
            );
            
            btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: linear-gradient(to right, rgb(37, 99, 235), rgb(79, 70, 229));" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.4), 12, 0, 0, 4);"
            ));
            
            btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: linear-gradient(to right, rgb(59, 130, 246), rgb(99, 102, 241));" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 8, 0, 0, 2);"
            ));
        } else {
            btn.setStyle(
                "-fx-background-color: rgb(107, 114, 128);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6px;" +
                "-fx-cursor: hand;"
            );
            
            btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: rgb(75, 85, 99);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6px;" +
                "-fx-cursor: hand;"
            ));
            
            btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: rgb(107, 114, 128);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6px;" +
                "-fx-cursor: hand;"
            ));
        }
    }
}