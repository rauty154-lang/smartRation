package com.smartration;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

class WelcomePage {

    public void show(Stage stage) {
        // Header section with gradient background matching dashboard
        Label systemTitle = new Label("🍚 Smart Ration System");
        systemTitle.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 28));
        systemTitle.setTextFill(Color.WHITE);
        
        Label subtitle = new Label("Digital Ration Card Management");
        subtitle.setFont(Font.font("Segoe UI", 16));
        subtitle.setTextFill(Color.rgb(200, 200, 255));
        
        VBox headerBox = new VBox(8, systemTitle, subtitle);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(25, 40, 25, 40));
        
        // Create gradient background for header matching dashboard
        LinearGradient headerGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.rgb(106, 90, 205)),
            new Stop(1, Color.rgb(72, 61, 139))
        );
        headerBox.setBackground(new Background(new BackgroundFill(headerGradient, CornerRadii.EMPTY, Insets.EMPTY)));

        // Main welcome content
        Text title = new Text("Welcome to Smart Ration");
        title.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 42));
        title.setFill(Color.rgb(55, 65, 81));

        Label welcomeSubtitle = new Label("Choose your login method to access the system");
        welcomeSubtitle.setFont(Font.font("Segoe UI", 18));
        welcomeSubtitle.setTextFill(Color.rgb(107, 114, 128));

        // Admin login button with modern styling
        Button adminBtn = new Button("👨‍💼 Login as Admin");
        styleModernButton(adminBtn, true);

        // User login button with modern styling
        Button userBtn = new Button("👤 Login as User");
        styleModernButton(userBtn, false);

        // Keep your existing button functionality unchanged
        adminBtn.setOnAction(event -> {
            try {
                
                Adminlogin loginPage=new Adminlogin();
                loginPage.initialize(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        userBtn.setOnAction(event -> {
            try {
                userlogin loginpage=new userlogin();
                loginpage.initialize(stage);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Create cards for buttons
        VBox adminCard = createButtonCard(adminBtn, "Admin Access", "Manage system settings and user accounts");
        VBox userCard = createButtonCard(userBtn, "User Access", "View your ration card details and status");

        HBox buttonCards = new HBox(40, adminCard, userCard);
        buttonCards.setAlignment(Pos.CENTER);

        // Main content layout
        VBox mainContent = new VBox(30);
        mainContent.getChildren().addAll(title, welcomeSubtitle, buttonCards);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(60, 40, 60, 40));

        // Main content area with light background
        VBox contentArea = new VBox(mainContent);
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setBackground(new Background(new BackgroundFill(Color.rgb(249, 250, 251), CornerRadii.EMPTY, Insets.EMPTY)));
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Root container
        VBox root = new VBox();
        root.getChildren().addAll(headerBox, contentArea);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

stage.setTitle("Smart Ration System");
stage.setScene(scene);
stage.setX(screenBounds.getMinX());
stage.setY(screenBounds.getMinY());
stage.setWidth(screenBounds.getWidth());
stage.setHeight(screenBounds.getHeight());
stage.setResizable(false);

        
        // Add fade-in animation for welcome page
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private VBox createButtonCard(Button button, String cardTitle, String cardDescription) {
        Label titleLabel = new Label(cardTitle);
        titleLabel.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.rgb(55, 65, 81));

        Label descLabel = new Label(cardDescription);
        descLabel.setFont(Font.font("Segoe UI", 14));
        descLabel.setTextFill(Color.rgb(107, 114, 128));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(280);

        VBox cardContent = new VBox(15, titleLabel, descLabel, button);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setPadding(new Insets(30, 25, 30, 25));
        cardContent.setPrefWidth(300);
        cardContent.setMinHeight(200);
        
        // Card styling with shadow effect
        cardContent.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);"
        );

        // Add hover effect for cards
        cardContent.setOnMouseEntered(e -> {
            cardContent.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 8);"
            );
        });

        cardContent.setOnMouseExited(e -> {
            cardContent.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);"
            );
        });

        return cardContent;
    }

    private void styleModernButton(Button btn, boolean isAdmin) {
        btn.setPrefHeight(50);
        btn.setPrefWidth(220);
        btn.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 16));
        
        if (isAdmin) {
            // Admin button with purple gradient matching dashboard
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, rgb(106, 90, 205), rgb(72, 61, 139));" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(106, 90, 205, 0.3), 8, 0, 0, 2);"
            );
            
            btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: linear-gradient(to right, rgb(88, 70, 180), rgb(55, 46, 120));" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(106, 90, 205, 0.4), 12, 0, 0, 4);"
            ));
            
            btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: linear-gradient(to right, rgb(106, 90, 205), rgb(72, 61, 139));" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(106, 90, 205, 0.3), 8, 0, 0, 2);"
            ));
        } else {
            // User button with blue gradient
            btn.setStyle(
                "-fx-background-color: linear-gradient(to right, rgb(59, 130, 246), rgb(99, 102, 241));" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 8, 0, 0, 2);"
            );
            
            btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: linear-gradient(to right, rgb(37, 99, 235), rgb(79, 70, 229));" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.4), 12, 0, 0, 4);"
            ));
            
            btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: linear-gradient(to right, rgb(59, 130, 246), rgb(99, 102, 241));" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 8, 0, 0, 2);"
            ));
        }
    }
}