package com.smartration;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignupForm {

    private final String FIREBASE_API_KEY = "AIzaSyDWIPwQ5nn65g2f5WKgdQ6LnQsa2rH0NF0";
    private final String PROJECT_ID = "smart-ration-550f9";

    public void initialize(Stage stage) {
        // Header section with gradient background - matching login form
        Label systemTitle = new Label("🍚 Smart Ration System");
        systemTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
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

        // Create form section
        VBox formBox = createFormSection(stage);
        
        // Create scrollable container
        ScrollPane scrollPane = new ScrollPane(formBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        // Main content area with white background
        VBox contentArea = new VBox();
        contentArea.getChildren().add(scrollPane);
        contentArea.setBackground(new Background(new BackgroundFill(Color.rgb(249, 250, 251), CornerRadii.EMPTY, Insets.EMPTY)));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

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
    
    private VBox createFormSection(Stage stage) {
        // Create main container with form title positioned above the card
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40, 30, 40, 30));
        
        // Form title - positioned above the card with BLACK text color
        Label formTitle = new Label("Sign Up Form");
        formTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        formTitle.setTextFill(Color.rgb(0, 0, 0)); // Explicitly set to BLACK using RGB
        formTitle.setAlignment(Pos.CENTER);
        formTitle.setMaxWidth(Double.MAX_VALUE);
        formTitle.setStyle("-fx-text-fill: #000000;"); // Additional CSS styling for black text
        
        // Create card container
        VBox cardContainer = new VBox();
        cardContainer.setMaxWidth(420);
        cardContainer.setMinWidth(420);
        cardContainer.setPadding(new Insets(25));
        cardContainer.setSpacing(18);
        
        // Card styling - white background with shadow
        cardContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 10, 0.2, 0, 2);"
        );
        
        // Create form fields with modern styling
        TextField nameField = createStyledTextField("Name of Family Head", "👤");
        TextField schemeField = createStyledTextField("Scheme Name", "📋");
        TextField memberCountField = createStyledTextField("Family Member Count", "👥");
        TextField phoneField = createStyledTextField("Phone Number", "📞");
        TextField rationCardField = createStyledTextField("Ration Card Number", "🆔");
        TextField emailField = createStyledTextField("Email", "✉️");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleFormField(passwordField, "🔒");
        
        // Error label
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.rgb(239, 68, 68));
        errorLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        errorLabel.setWrapText(true);
        
        // Buttons with modern styling
        Button signupBtn = createPrimaryButton("Sign Up");
        Button backBtn = createSecondaryButton("Back to Login");
        
        // Button actions (keeping original functionality)
        signupBtn.setOnAction(e -> {
            errorLabel.setText("");

            String name = nameField.getText().trim();
            String scheme = schemeField.getText().trim();
            String count = memberCountField.getText().trim();
            String phone = phoneField.getText().trim();
            String ration = rationCardField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (name.isEmpty() || scheme.isEmpty() || count.isEmpty() || phone.isEmpty()
                    || ration.isEmpty() || email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                return;
            }

            String uid = signupFirebase(email, password, errorLabel);
            if (uid != null) {
                storeUser(email, name, scheme, count, phone, ration, email);
                LoggedInUser.setEmail(email);
                try {
                    new UserDashboard().initialize(stage);
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        backBtn.setOnAction(e -> {
            try {
                new userlogin().initialize(stage);
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        HBox buttonBox = new HBox(12, signupBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        
        // Add form fields to card (without the title)
        cardContainer.getChildren().addAll(
            nameField,
            schemeField, 
            memberCountField,
            phoneField,
            rationCardField,
            emailField,
            passwordField,
            buttonBox,
            errorLabel
        );
        
        // Add title and card to main container
        mainContainer.getChildren().addAll(formTitle, cardContainer);
        
        return mainContainer;
    }
    
    private TextField createStyledTextField(String placeholder, String icon) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        styleFormField(field, icon);
        return field;
    }
    
    private void styleFormField(Control field, String icon) {
        field.setMaxWidth(320);
        field.setMinHeight(45);
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
        
        // Focus effects
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
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
    
    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(45);
        btn.setPrefWidth(120);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        
        String defaultStyle = 
            "-fx-background-color: linear-gradient(to right, rgb(59, 130, 246), rgb(99, 102, 241));" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 8, 0, 0, 2);";
            
        String hoverStyle = 
            "-fx-background-color: linear-gradient(to right, rgb(37, 99, 235), rgb(79, 70, 229));" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.4), 12, 0, 0, 4);";
        
        btn.setStyle(defaultStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(defaultStyle));
        
        return btn;
    }
    
    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(45);
        btn.setPrefWidth(120);
        btn.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        
        String defaultStyle = 
            "-fx-background-color: rgb(107, 114, 128);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;";
            
        String hoverStyle = 
            "-fx-background-color: rgb(75, 85, 99);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;";
        
        btn.setStyle(defaultStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(defaultStyle));
        
        return btn;
    }

    // Original Firebase and database methods kept unchanged
    private String signupFirebase(String email, String password, Label errorLabel) {
        try {
            URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + FIREBASE_API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String data = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"returnSecureToken\":true}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes());
            }

            int code = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (code == 200) ? conn.getInputStream() : conn.getErrorStream()));
            StringBuilder sb = new StringBuilder();
            String ln;
            while ((ln = br.readLine()) != null) sb.append(ln);
            JSONObject responseJson = new JSONObject(sb.toString());

            if (code == 200) {
                return responseJson.getString("localId");
            } else {
                if (responseJson.has("error")) {
                    JSONObject errorObj = responseJson.getJSONObject("error");
                    if (errorObj.has("errors")) {
                        String msg = errorObj.getJSONArray("errors").getJSONObject(0).getString("message");
                        errorLabel.setText(firebaseErrorMsg(msg));
                    } else if (errorObj.has("message")) {
                        errorLabel.setText(firebaseErrorMsg(errorObj.getString("message")));
                    } else {
                        errorLabel.setText("Signup failed due to unknown error.");
                    }
                } else {
                    errorLabel.setText("Signup failed. Unexpected response.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Could not sign up. Try again.");
        }
        return null;
    }

    private void storeUser(String email, String name, String scheme, String count, String phone, String ration, String userEmail) {
        try {
            String sanitizedEmail = email.replace("@", "_at_").replace(".", "_dot_");

            String urlStr = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID +
                    "/databases/(default)/documents/users?documentId=" + sanitizedEmail;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject fields = new JSONObject();
            fields.put("name", new JSONObject().put("stringValue", name));
            fields.put("scheme", new JSONObject().put("stringValue", scheme));
            fields.put("memberCount", new JSONObject().put("stringValue", count));
            fields.put("phone", new JSONObject().put("stringValue", phone));
            fields.put("rationCard", new JSONObject().put("stringValue", ration));
            fields.put("email", new JSONObject().put("stringValue", userEmail));
            fields.put("isAdmin", new JSONObject().put("booleanValue", false));

            JSONObject payload = new JSONObject();
            payload.put("fields", fields);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes());
            }

            int responseCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);

            System.out.println("Firestore response (" + responseCode + "): " + sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String firebaseErrorMsg(String msg) {
        return switch (msg) {
            case "EMAIL_EXISTS" -> "Email already registered.";
            case "INVALID_EMAIL" -> "Invalid email format.";
            case "WEAK_PASSWORD : Password should be at least 6 characters" -> "Password too weak.";
            default -> "Signup failed: " + msg;
        };
    }
}