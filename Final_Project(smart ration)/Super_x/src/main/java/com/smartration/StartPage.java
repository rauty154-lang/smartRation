package com.smartration;



import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

// Main Application Class - Start Page
public class StartPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create animated background
        StackPane root = new StackPane();
        
        // Background gradient
        LinearGradient backgroundGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.rgb(67, 56, 202)),
            new Stop(0.5, Color.rgb(99, 102, 241)),
            new Stop(1, Color.rgb(139, 92, 246))
        );
        root.setBackground(new Background(new BackgroundFill(backgroundGradient, CornerRadii.EMPTY, Insets.EMPTY)));
        
        // Create floating ration-related shapes
        createFloatingElements(root);
        
        // Main content
        VBox mainContent = new VBox(30);
        mainContent.setAlignment(Pos.CENTER);
        
        // App logo/icon (represented as styled text since we can't use external images)
        StackPane logoContainer = new StackPane();
        
        // Background circle for logo
        Circle logoBackground = new Circle(80);
        logoBackground.setFill(Color.WHITE);
        logoBackground.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.3)));
        
        // Logo text
        Text logoText = new Text("🍚");
        logoText.setFont(Font.font(60));
        
        logoContainer.getChildren().addAll(logoBackground, logoText);
        
        // Main title with animation
        Text mainTitle = new Text("Smart Ration");
        mainTitle.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 48));
        mainTitle.setFill(Color.WHITE);
        mainTitle.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.5)));
        
        // Subtitle
        Text subtitle = new Text("Digital Ration Management System");
        subtitle.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.NORMAL, 20));
        subtitle.setFill(Color.rgb(220, 220, 255));
        
        // Loading indicator
        Text loadingText = new Text("Loading...");
        loadingText.setFont(Font.font("Segoe UI", 16));
        loadingText.setFill(Color.rgb(200, 200, 255));
        
        // Add pulsing dots animation
        Timeline loadingAnimation = new Timeline(
            new KeyFrame(Duration.seconds(0), e -> loadingText.setText("Loading.")),
            new KeyFrame(Duration.seconds(0.5), e -> loadingText.setText("Loading..")),
            new KeyFrame(Duration.seconds(1), e -> loadingText.setText("Loading...")),
            new KeyFrame(Duration.seconds(1.5), e -> loadingText.setText("Loading"))
        );
        loadingAnimation.setCycleCount(Timeline.INDEFINITE);
        loadingAnimation.play();
        
        mainContent.getChildren().addAll(logoContainer, mainTitle, subtitle, loadingText);
        root.getChildren().add(mainContent);
        
        // Add entrance animations
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), mainContent);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(1), logoContainer);
        scaleIn.setFromX(0.5);
        scaleIn.setFromY(0.5);
        scaleIn.setToX(1);
        scaleIn.setToY(1);
        
        fadeIn.play();
        scaleIn.play();
        
        // Navigate to welcome page after 3 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            loadingAnimation.stop();
            WelcomePage welcomePage = new WelcomePage();
            welcomePage.show(primaryStage);
        });
        delay.play();
        
        // Get the screen bounds dynamically
Rectangle2D screenBounds = Screen.getPrimary().getBounds();
Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

primaryStage.setTitle("Smart Ration System");
primaryStage.setScene(scene);
primaryStage.setX(screenBounds.getMinX());
primaryStage.setY(screenBounds.getMinY());
primaryStage.setWidth(screenBounds.getWidth());
primaryStage.setHeight(screenBounds.getHeight());
primaryStage.setResizable(false); // Prevent user from resizing and changing layout
primaryStage.show();

        
    }
    
    private void createFloatingElements(StackPane root) {
        // Create floating ration-themed elements
        for (int i = 0; i < 15; i++) {
            Rectangle grain = new Rectangle(8 + Math.random() * 6, 8 + Math.random() * 6);
            grain.setFill(Color.rgb(255, 255, 255, 0.1 + Math.random() * 0.2));
            grain.setArcWidth(4);
            grain.setArcHeight(4);
            
            // Random position
            grain.setLayoutX(Math.random() * 950);
            grain.setLayoutY(Math.random() * 650);
            
            root.getChildren().add(grain);
            
            // Floating animation
            Timeline floatAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0), 
                    e -> {
                        grain.setTranslateY(0);
                        grain.setOpacity(0.1 + Math.random() * 0.2);
                    }),
                new KeyFrame(Duration.seconds(3 + Math.random() * 2), 
                    e -> {
                        grain.setTranslateY(-20 - Math.random() * 30);
                        grain.setOpacity(0);
                    })
            );
            floatAnimation.setCycleCount(Timeline.INDEFINITE);
            floatAnimation.play();
        }
        
        // Add some circular elements representing grains
        for (int i = 0; i < 10; i++) {
            Circle circle = new Circle(3 + Math.random() * 4);
            circle.setFill(Color.rgb(255, 255, 255, 0.05 + Math.random() * 0.15));
            
            circle.setLayoutX(Math.random() * 950);
            circle.setLayoutY(Math.random() * 650);
            
            root.getChildren().add(circle);
            
            // Gentle movement animation
            Timeline moveAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> circle.setTranslateX(0)),
                new KeyFrame(Duration.seconds(4 + Math.random() * 3), 
                    e -> circle.setTranslateX(-50 + Math.random() * 100))
            );
            moveAnimation.setAutoReverse(true);
            moveAnimation.setCycleCount(Timeline.INDEFINITE);
            moveAnimation.play();
        }
    }
}

