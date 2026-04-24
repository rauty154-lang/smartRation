package com.smartration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Adduserpage {

    Scene UseraddScene;
    Stage UseraddStage;

    private final static String FIREBASE_API_KEY = "AIzaSyDWIPwQ5nn65g2f5WKgdQ6LnQsa2rH0NF0";
    private final static String PROJECT_ID = "smart-ration-550f9";

    public void setUseraddScene(Scene useraddScene) {
        UseraddScene = useraddScene;
    }

    public void setUseraddStage(Stage useraddStage) {
        UseraddStage = useraddStage;
    }

    public VBox UserAddMethode(Runnable back) {
        // Create header section matching dashboard style
        VBox headerSection = createHeaderSection(back);
        
        // Create main content area
        VBox mainContent = createMainContent();
        
        // Main container
        VBox container = new VBox();
        container.getChildren().addAll(headerSection, mainContent);
        container.setStyle("-fx-background-color: #f5f7fa;");
        
        return container;
    }

    private VBox createHeaderSection(Runnable back) {
        // Header with gradient background matching dashboard
        HBox header = new HBox();
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: linear-gradient(to right, #667eea 0%, #764ba2 100%);");
        
        // Title section
        VBox titleSection = new VBox(5);
        
        // Main title
        Text mainTitle = new Text("👤 User Management");
        mainTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        mainTitle.setStyle("-fx-fill: white;");
        
        // Subtitle
        Text subtitle = new Text("Add user information");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setStyle("-fx-fill: #e8eaf6;");
        
        titleSection.getChildren().addAll(mainTitle, subtitle);
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Back button with modern styling
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
        
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg) {
                back.run();
            }
        });
        
        header.getChildren().addAll(titleSection, spacer, backButton);
        
        VBox headerContainer = new VBox(header);
        return headerContainer;
    }

    private VBox createMainContent() {
        // Main content container with responsive design
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.TOP_CENTER);
        
        // Create scrollable container for cards
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        
        // Cards container with responsive layout
        VBox cardsContainer = new VBox(20);
        cardsContainer.setAlignment(Pos.CENTER);
        cardsContainer.setPadding(new Insets(10));
        
        // Add User Card
        VBox addUserCard = createAddUserCard();
        addUserCard.setMaxWidth(600); // Reasonable max width
        addUserCard.setPrefWidth(Region.USE_COMPUTED_SIZE);
        
        cardsContainer.getChildren().add(addUserCard);
        
        scrollPane.setContent(cardsContainer);
        mainContainer.getChildren().add(scrollPane);
        
        return mainContainer;
    }

    private VBox createAddUserCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        
        // Card header
        HBox cardHeader = new HBox(10);
        cardHeader.setAlignment(Pos.CENTER_LEFT);
        
        Text cardIcon = new Text("➕");
        cardIcon.setFont(Font.font(20));
        
        Text cardTitle = new Text("Add New User");
        cardTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        cardTitle.setStyle("-fx-fill: #2c3e50;");
        
        cardHeader.getChildren().addAll(cardIcon, cardTitle);
        
        // Create form in a grid-like layout for better space utilization
        VBox formContainer = new VBox(12);
        
        // Row 1: Name and Scheme
        HBox row1 = new HBox(12);
        TextField HeadField = createCompactTextField("Family Head Name", "👤");
        TextField SchemeField = createCompactTextField("Scheme Name", "📋");
        HBox.setHgrow(HeadField, Priority.ALWAYS);
        HBox.setHgrow(SchemeField, Priority.ALWAYS);
        row1.getChildren().addAll(HeadField, SchemeField);
        
        // Row 2: Member Count and Phone
        HBox row2 = new HBox(12);
        TextField MemberCountField = createCompactTextField("Member Count", "👥");
        TextField PhoneNoField = createCompactTextField("Phone Number", "📞");
        HBox.setHgrow(MemberCountField, Priority.ALWAYS);
        HBox.setHgrow(PhoneNoField, Priority.ALWAYS);
        row2.getChildren().addAll(MemberCountField, PhoneNoField);
        
        // Row 3: Card Number and Email
        HBox row3 = new HBox(12);
        TextField cardNoField = createCompactTextField("Ration Card No.", "🆔");
        TextField EmailField = createCompactTextField("Email Address", "✉");
        HBox.setHgrow(cardNoField, Priority.ALWAYS);
        HBox.setHgrow(EmailField, Priority.ALWAYS);
        row3.getChildren().addAll(cardNoField, EmailField);
        
        // Row 4: Password (full width)
        TextField passField = createCompactTextField("Password", "🔒");
        
        formContainer.getChildren().addAll(row1, row2, row3, passField);
        
        // Error label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11px;");
        errorLabel.setWrapText(true);
        
        // Add button
        Button addButton = new Button("Add User");
        styleCompactButton(addButton, "#27ae60", "#2ecc71");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setPrefHeight(40);
        
        addButton.setOnAction(e -> {
            String name = HeadField.getText().trim();
            String scheme = SchemeField.getText().trim();
            String count = MemberCountField.getText().trim();
            String phone = PhoneNoField.getText().trim();
            String ration = cardNoField.getText().trim();
            String email = EmailField.getText().trim();
            String password = passField.getText().trim();

            String uid = signupFirebase(email, password, errorLabel);
            if (uid != null) {
                storeUser(name, scheme, count, phone, ration, email);
                LoggedInUser.setEmail(email);
                
                // Clear fields after successful addition
                HeadField.clear();
                SchemeField.clear();
                MemberCountField.clear();
                PhoneNoField.clear();
                cardNoField.clear();
                EmailField.clear();
                passField.clear();
                
                errorLabel.setText("✓ User added successfully!");
                errorLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px;");
            }
        });
        
        card.getChildren().addAll(cardHeader, formContainer, errorLabel, addButton);
        return card;
    }

    private TextField createCompactTextField(String placeholder, String icon) {
        TextField field = new TextField();
        field.setPromptText(icon + " " + placeholder);
        field.setFont(Font.font("Segoe UI", 13));
        field.setPrefHeight(36);
        field.setStyle("-fx-background-color: #f8f9fa; " +
                        "-fx-border-color: #e9ecef; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 6; " +
                        "-fx-background-radius: 6; " +
                        "-fx-padding: 8 12; " +
                        "-fx-font-size: 13px;");
        
        field.setOnMouseEntered(e -> 
            field.setStyle("-fx-background-color: #ffffff; " +
                            "-fx-border-color: #667eea; " +
                            "-fx-border-width: 1.5; " +
                            "-fx-border-radius: 6; " +
                            "-fx-background-radius: 6; " +
                            "-fx-padding: 7.5 11.5; " +
                            "-fx-font-size: 13px;"));
        
        field.setOnMouseExited(e -> 
            field.setStyle("-fx-background-color: #f8f9fa; " +
                            "-fx-border-color: #e9ecef; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 6; " +
                            "-fx-background-radius: 6; " +
                            "-fx-padding: 8 12; " +
                            "-fx-font-size: 13px;"));
        
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle("-fx-background-color: #ffffff; " +
                                "-fx-border-color: #667eea; " +
                                "-fx-border-width: 2; " +
                                "-fx-border-radius: 6; " +
                                "-fx-background-radius: 6; " +
                                "-fx-padding: 7 11; " +
                                "-fx-font-size: 13px;");
            } else {
                field.setStyle("-fx-background-color: #f8f9fa; " +
                                "-fx-border-color: #e9ecef; " +
                                "-fx-border-width: 1; " +
                                "-fx-border-radius: 6; " +
                                "-fx-background-radius: 6; " +
                                "-fx-padding: 8 12; " +
                                "-fx-font-size: 13px;");
            }
        });
        
        return field;
    }

    private void styleCompactButton(Button button, String normalColor, String hoverColor) {
        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        button.setStyle("-fx-background-color: " + normalColor + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-padding: 10 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-weight: bold;");
        
        button.setOnMouseEntered(e -> 
            button.setStyle("-fx-background-color: " + hoverColor + "; " +
                            "-fx-text-fill: white; " +
                            "-fx-border-radius: 8; " +
                            "-fx-background-radius: 8; " +
                            "-fx-padding: 10 20; " +
                            "-fx-cursor: hand; " +
                            "-fx-font-weight: bold; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);"));
        
        button.setOnMouseExited(e -> 
            button.setStyle("-fx-background-color: " + normalColor + "; " +
                            "-fx-text-fill: white; " +
                            "-fx-border-radius: 8; " +
                            "-fx-background-radius: 8; " +
                            "-fx-padding: 10 20; " +
                            "-fx-cursor: hand; " +
                            "-fx-font-weight: bold;"));
    }

    // Keep all original methods unchanged
    private String storeUser(String name, String scheme, String count, String phone, String ration, String email) {
        try {
            String sanitizedEmail = email.replace("@", "at").replace(".", "dot");

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
            fields.put("email", new JSONObject().put("stringValue", email));
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
        return email;
    }

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

    private String firebaseErrorMsg(String msg) {
        switch (msg) {
            case "EMAIL_EXISTS":
                return "Email already exists. Please use a different email.";
            case "INVALID_EMAIL":
                return "Invalid email format. Please enter a valid email.";
            case "WEAK_PASSWORD":
                return "Password should be at least 6 characters.";
            case "MISSING_PASSWORD":
                return "Password is required.";
            case "MISSING_EMAIL":
                return "Email is required.";
            default:
                return "Error: " + msg;
        }
    }
}