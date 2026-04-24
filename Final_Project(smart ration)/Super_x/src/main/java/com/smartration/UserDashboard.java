package com.smartration;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class UserDashboard {
    private final String PROJECT_ID = "new-ration";

    private BorderPane mainRoot;
    private String userName;
    private String userScheme;
    private String userCount;
    private String userPhone;
    private String userRation;
    private String userEmail;
    private String userRationStatus;
    private JSONArray userRationHistory;

   public void initialize(Stage stage) throws Exception {
    String email = LoggedInUser.getEmail();
    if (email == null || email.isEmpty()) {
        throw new Exception("Email not found. Please login again.");
    }

    String sanitizedEmail = email.replace("@", "_at_").replace(".", "_dot_");
    JSONObject userData = fetchUserData(sanitizedEmail);
    if (userData == null) {
        throw new Exception("Unable to fetch user data. Please check your connection and try again.");
    }

    // Extract user data
    userName = getField(userData, "name");
    userScheme = getField(userData, "scheme");
    userCount = getField(userData, "memberCount");
    userPhone = getField(userData, "phone");
    userRation = getField(userData, "rationCard");
    userEmail = getField(userData, "email");
    userRationStatus = getField(userData, "rationStatus");
    userRationHistory = getHistory(userData, "rationHistory");

    // Create the modern UI
    mainRoot = createModernLayout(userName, userScheme, userCount, userPhone, userRation, userEmail, userRationStatus, userRationHistory, stage);
    Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    Scene scene = new Scene(mainRoot, screenBounds.getWidth(), screenBounds.getHeight());

    stage.setTitle("Smart Ration System");
    stage.setScene(scene);
    stage.setX(screenBounds.getMinX());
    stage.setY(screenBounds.getMinY());
    stage.setWidth(screenBounds.getWidth());
    stage.setHeight(screenBounds.getHeight());
    stage.setResizable(false);
    
    // Add entrance animation
    addEntranceAnimation(mainRoot);
}

    private BorderPane createModernLayout(String name, String scheme, String count, String phone,
                                          String ration, String email, String rationStatus, JSONArray rationHistory, Stage stage) {
        BorderPane root = new BorderPane();

        // Create modern header
        HBox header = createModernHeader(name);

        // Create sidebar - pass stage and root (sidebar will use mainRoot reference)
        VBox sidebar = createModernSidebar(stage);

        // Create main content
        ScrollPane mainScrollPane = createMainContent(name, scheme, count, phone, ration, email, rationStatus, rationHistory);

        root.setTop(header);
        root.setLeft(sidebar);
        root.setCenter(mainScrollPane);

        return root;
    }

    private HBox createModernHeader(String userName) {
        HBox header = new HBox();
        header.setPrefHeight(80);
        header.setMinHeight(80);
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
                "-fx-background-color: linear-gradient(to right, #667eea 0%, #764ba2 100%);" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);"
        );

        // App title and subtitle
        VBox titleSection = new VBox(5);
        Label appTitle = new Label("🌾 Smart Ration System");
        appTitle.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;"
        );

        Label subtitle = new Label("Digital Ration Card Management");
        subtitle.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.8);" +
                        "-fx-font-size: 14px;"
        );

        titleSection.getChildren().addAll(appTitle, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // User profile section
        HBox userSection = createUserProfileSection(userName);

        header.getChildren().addAll(titleSection, spacer, userSection);
        return header;
    }

    private HBox createUserProfileSection(String userName) {
        HBox userSection = new HBox(15);
        userSection.setAlignment(Pos.CENTER_RIGHT);

        // User avatar
        Circle avatar = new Circle(25);
        avatar.setFill(Color.WHITE);
        avatar.setStroke(Color.valueOf("#667eea"));
        avatar.setStrokeWidth(2);

        Label avatarIcon = new Label("👤");
        avatarIcon.setStyle("-fx-font-size: 20px;");

        StackPane avatarStack = new StackPane(avatar, avatarIcon);

        // User info
        VBox userInfo = new VBox(2);
        Label userNameLabel = new Label("Welcome, " + userName);
        userNameLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;"
        );

        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a"));
        Label timeLabel = new Label(currentTime);
        timeLabel.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.8);" +
                        "-fx-font-size: 12px;"
        );

        userInfo.getChildren().addAll(userNameLabel, timeLabel);
        userSection.getChildren().addAll(avatarStack, userInfo);

        return userSection;
    }

    private VBox createModernSidebar(Stage stage) {
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(300);
        sidebar.setPadding(new Insets(25, 20, 25, 20));
        sidebar.setStyle(
                "-fx-background-color: #2c3e50;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 3, 0);"
        );

        // Nav items including new options "Scheme Details" and "About Us"
       String[] navItems = {
    "🏠 Dashboard",
    "📜 History",
    "📝 Scheme Details",
    "📞 Contact Support"
};

        Label[] navLabels = new Label[navItems.length];

        for (int i = 0; i < navItems.length; i++) {
            boolean active = (i == 0); // First item active by default
            navLabels[i] = createNavItem(navItems[i], active);
            sidebar.getChildren().add(navLabels[i]);
        }

        // Add event handlers for nav click
        for (Label navLabel : navLabels) {
            navLabel.setOnMouseClicked(event -> {
                // Clear active styles first
                for (Label lbl : navLabels) {
                    lbl.setStyle(
                            "-fx-text-fill: rgba(255,255,255,0.8);" +
                                    "-fx-font-size: 14px;" +
                                    "-fx-background-radius: 8;" +
                                    "-fx-cursor: hand;"
                    );
                }
                // Set active style for clicked item
                navLabel.setStyle(
                        "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-color: rgba(255,255,255,0.15);" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-border-color: rgba(255,255,255,0.3);" +
                                "-fx-border-width: 1;" +
                                "-fx-border-radius: 8;"
                );

                String navText = navLabel.getText();

                // Execute navigation
               switch(navText) {
    case "🏠 Dashboard" ->
        refreshUserDataAndRedraw("dashboard");
    case "📜 History" ->
        refreshUserDataAndRedraw("history");
    case "📝 Scheme Details" ->{
    TableViewComponent schemeComponent = new TableViewComponent();
    VBox schemeTable = schemeComponent.getSchemeTable();
    mainRoot.setCenter(schemeTable);
      
                
    }   
       
  
    case "📞 Contact Support" ->
        mainRoot.setCenter(createContactSupportPage());
}


            });
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Logout section
        Label logoutItem = createNavItem("🚪 Logout", false);
        logoutItem.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            alert.getButtonTypes().setAll(yesButton, noButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == yesButton) {
                WelcomePage wp = new WelcomePage();
                wp.show(stage);
            }
        });

        sidebar.getChildren().addAll(spacer, logoutItem);
        return sidebar;
    }

    private Label createNavItem(String text, boolean active) {
        Label item = new Label(text);
        item.setPrefWidth(210);
        item.setPadding(new Insets(12, 15, 12, 15));
        item.setAlignment(Pos.CENTER_LEFT);

        if (active) {
            item.setStyle(
                    "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-background-color: rgba(255,255,255,0.15);" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;" +
                            "-fx-border-color: rgba(255,255,255,0.3);" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 8;"
            );
        } else {
            item.setStyle(
                    "-fx-text-fill: rgba(255,255,255,0.8);" +
                            "-fx-font-size: 14px;" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;"
            );
        }

        // Hover effects
        String originalStyle = item.getStyle();
        item.setOnMouseEntered(e -> {
            if (!active) {
                item.setStyle(originalStyle + "-fx-background-color: rgba(255,255,255,0.1);");
            }
        });
        item.setOnMouseExited(e -> {
            if (!active) {
                item.setStyle(originalStyle);
            }
        });

        return item;
    }

    // --- Main Content Pages for Navigation ---

    private ScrollPane createRationDetailsPage() {
        VBox detailsPage = new VBox(20);
        detailsPage.setPadding(new Insets(30));
        detailsPage.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("📋 Ration Card Details");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // Example content (You can enrich with detailed info)
        Label info = new Label(
                "Family Head: " + (userName != null ? userName : "N/A") + "\n" +
                        "Scheme: " + (userScheme != null ? userScheme : "N/A") + "\n" +
                        "Member Count: " + (userCount != null ? userCount : "N/A") + "\n" +
                        "Phone: " + (userPhone != null ? userPhone : "N/A") + "\n" +
                        "Ration Card No: " + (userRation != null ? userRation : "N/A") + "\n" +
                        "Email: " + (userEmail != null ? userEmail : "N/A")
        );
        info.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");

        detailsPage.getChildren().addAll(title, info);

        ScrollPane scrollPane = new ScrollPane(detailsPage);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private ScrollPane createContactSupportPage() {
    VBox chatLayout = new VBox(10);
    chatLayout.setPadding(new Insets(20));
    chatLayout.setStyle("-fx-background-color: #f8f9fa;");

    Label title = new Label("📞 Contact Support - Chatbot");
    title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

    TextArea chatArea = new TextArea();
    chatArea.setEditable(false);
    chatArea.setWrapText(true);
    chatArea.setPrefHeight(400);

    TextField userInput = new TextField();
    userInput.setPromptText("Type your message here...");

    Button sendBtn = new Button("Send");
    sendBtn.setDefaultButton(true);

    HBox inputBox = new HBox(10, userInput, sendBtn);
    inputBox.setAlignment(Pos.CENTER);

    chatArea.appendText("Bot: Hello! How can I help you today?\n");

    sendBtn.setOnAction(e -> {
        String input = userInput.getText().trim();
        if (!input.isEmpty()) {
            chatArea.appendText("You: " + input + "\n");

            String response = generateBotResponse(input);
            chatArea.appendText("Bot: " + response + "\n");

            userInput.clear();
            chatArea.positionCaret(chatArea.getLength());
        }
    });

    chatLayout.getChildren().addAll(title, chatArea, inputBox);

    ScrollPane scrollPane = new ScrollPane(chatLayout);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    return scrollPane;
}

private String generateBotResponse(String userMessage) {
    userMessage = userMessage.toLowerCase();

    if (userMessage.contains("hello") || userMessage.contains("hi")) {
        return "Hello! How can I assist you?";
    } else if (userMessage.contains("hours")) {
        return "Our support team is available Mon-Fri 9AM to 6PM.";
    } else if (userMessage.contains("contact")) {
        return "You can contact us at support@smartration.com or call +1-800-123-4567.";
    } else if (userMessage.contains("thanks") || userMessage.contains("thank you")) {
        return "You're welcome! Let me know if you have any other questions.";
    } else {
        return "Sorry, I didn't understand that. Could you please rephrase?";
    }
}


// === BEGIN ADDED METHOD
private void refreshUserDataAndRedraw(String showWhich) {
    String email = LoggedInUser.getEmail();
    if (email != null) {
        String sanitizedEmail = email.replace("@", "_at_").replace(".", "_dot_");
        JSONObject userData = fetchUserData(sanitizedEmail);
        if (userData != null) {
            userName = getField(userData, "name");
            userScheme = getField(userData, "scheme");
            userCount = getField(userData, "memberCount");
            userPhone = getField(userData, "phone");
            userRation = getField(userData, "rationCard");
            userEmail = getField(userData, "email");
            userRationStatus = getField(userData, "rationStatus");
            userRationHistory = getHistory(userData, "rationHistory");
        }
    }
    // Redraw
    switch (showWhich) {
        case "dashboard" ->
            mainRoot.setCenter(createMainContent(userName, userScheme, userCount, userPhone, userRation, userEmail, userRationStatus, userRationHistory));
        case "status" ->
            mainRoot.setCenter(createStatusPage());
        case "history" ->
            mainRoot.setCenter(createHistoryPage());
    }
}
// === END ADDED METHOD

    private ScrollPane createStatusPage() {
        VBox statusPage = new VBox(20);
        statusPage.setPadding(new Insets(30));
        statusPage.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("📊 Current Status");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        Label statusLabel = new Label(userRationStatus != null ? userRationStatus : "N/A");
        statusLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: " + getStatusColorForDisplay(userRationStatus) + ";");

        statusPage.getChildren().addAll(title, statusLabel);

        ScrollPane scrollPane = new ScrollPane(statusPage);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private ScrollPane createHistoryPage() {
        VBox historyPage = new VBox(20);
        historyPage.setPadding(new Insets(30));
        historyPage.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("📜 Ration History");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        VBox historyContent = createHistorySection(userRationHistory);

        historyPage.getChildren().addAll(title, historyContent);

        ScrollPane scrollPane = new ScrollPane(historyPage);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private ScrollPane createAboutUsPage() {
        VBox aboutPage = new VBox(20);
        aboutPage.setPadding(new Insets(30));
        aboutPage.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("ℹ️ About Us");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        Label aboutInfo = new Label(
                "Smart Ration System\n" +
                        "Version: 1.0\n" +
                        "Developed by Your Company\n\n" +
                        "This app helps users manage their ration cards easily and securely.\n\n" +
                        "Contact support: support@smartration.com"
        );
        aboutInfo.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");

        aboutPage.getChildren().addAll(title, aboutInfo);

        ScrollPane scrollPane = new ScrollPane(aboutPage);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

   

    private String getStatusColorForDisplay(String status) {
        if (status == null) return "#95a5a6";
        switch (status.toLowerCase()) {
            case "active", "available" -> {
                return "#27ae60";
            }
            case "pending" -> {
                return "#f39c12";
            }
            case "expired", "unavailable" -> {
                return "#e74c3c";
            }
            default -> {
                return "#3498db";
            }
        }
    }

    // The existing methods you originally had below –
    // unchanged and preserved as-is

    private ScrollPane createMainContent(String name, String scheme, String count, String phone,
                                         String ration, String email, String rationStatus, JSONArray rationHistory) {
        VBox content = new VBox(25);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f8f9fa;");

        VBox welcomeSection = createWelcomeSection(name);
        HBox statsRow = createStatsCards(scheme, count, rationStatus);
        HBox mainCardsRow = createMainCards(name, scheme, count, phone, ration, email, rationStatus);
        VBox historySection = createHistorySection(rationHistory);

        content.getChildren().addAll(welcomeSection, statsRow, mainCardsRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        return scrollPane;
    }

    private VBox createWelcomeSection(String name) {
        VBox welcomeSection = new VBox(10);

        Label welcomeTitle = new Label("Welcome , " + name + "! 👋");
        welcomeTitle.setStyle(
                "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-wrap-text: true;"
        );

        Label welcomeSubtitle = new Label("Here's your digital ration card dashboard with all your details and recent activity.");
        welcomeSubtitle.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #7f8c8d;" +
                        "-fx-wrap-text: true;"
        );

        welcomeSection.getChildren().addAll(welcomeTitle, welcomeSubtitle);
        return welcomeSection;
    }

    private HBox createStatsCards(String scheme, String count, String rationStatus) {
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER);
        statsRow.setSpacing(20);

        VBox schemeCard = createStatCard("📋", "Scheme Type", scheme, "#3498db");
        VBox membersCard = createStatCard("👥", "Family Members", count, "#2ecc71");
        VBox statusCard = createStatCard("✅", "Ration Status", rationStatus, "#e74c3c");

        for (VBox card : new VBox[]{schemeCard, membersCard, statusCard}) {
            HBox.setHgrow(card, Priority.ALWAYS);
            card.setMaxWidth(Double.MAX_VALUE);
            addCardHoverEffect(card);
        }

        statsRow.getChildren().addAll(schemeCard, membersCard, statusCard);
        return statsRow;
    }

    private VBox createStatCard(String icon, String title, String value, String color) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(25));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-padding: 10;" +
                        "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 12;"
        );

        topRow.getChildren().add(iconLabel);

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-text-fill: #7f8c8d;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: normal;" +
                        "-fx-wrap-text: true;"
        );

        Label valueLabel = new Label(value != null ? value : "N/A");
        valueLabel.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-wrap-text: true;"
        );

        card.getChildren().addAll(topRow, titleLabel, valueLabel);
        return card;
    }

    private HBox createMainCards(String name, String scheme, String count, String phone,
                                 String ration, String email, String rationStatus) {
        HBox mainCardsRow = new HBox(20);

        VBox rationCard = createRationCardDetails(name, scheme, count, phone, ration, email);
        VBox statusCard = createStatusCard(rationStatus);

        HBox.setHgrow(rationCard, Priority.ALWAYS);
        HBox.setHgrow(statusCard, Priority.ALWAYS);
        rationCard.setMaxWidth(Double.MAX_VALUE);
        statusCard.setMaxWidth(Double.MAX_VALUE);

        mainCardsRow.getChildren().addAll(rationCard, statusCard);
        return mainCardsRow;
    }

    private VBox createRationCardDetails(String name, String scheme, String count, String phone, String ration, String email) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );

        Label cardTitle = new Label("📄 Ration Card Details");
        cardTitle.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2c3e50;"
        );

        VBox detailsList = new VBox(12);
        String[][] details = {
                {"👤 Family Head", name},
                {"📋 Scheme Name", scheme},
                {"👥 Member Count", count},
                {"📞 Phone Number", phone},
                {"🆔 Ration Card No", ration},
                {"📧 Email", email}
        };

        for (String[] detail : details) {
            HBox detailRow = createDetailRow(detail[0], detail[1]);
            detailsList.getChildren().add(detailRow);
        }

        card.getChildren().addAll(cardTitle, detailsList);
        addCardHoverEffect(card);
        return card;
    }

    private HBox createDetailRow(String label, String value) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle(
                "-fx-background-color: #f8f9fa;" +
                        "-fx-background-radius: 8;"
        );

        Label keyLabel = new Label(label);
        keyLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-min-width: 140px;"
        );

        Label valueLabel = new Label(value != null ? value : "N/A");
        valueLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #34495e;" +
                        "-fx-wrap-text: true;"
        );

        row.getChildren().addAll(keyLabel, valueLabel);
        return row;
    }

    private VBox createStatusCard(String rationStatus) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );

        Label cardTitle = new Label("📊 Current Status");
        cardTitle.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2c3e50;"
        );

        HBox statusIndicator = new HBox(15);
        statusIndicator.setAlignment(Pos.CENTER_LEFT);
        statusIndicator.setPadding(new Insets(20));

        String statusColor = getStatusColor(rationStatus);
        statusIndicator.setStyle(
                "-fx-background-color: " + statusColor + ";" +
                        "-fx-background-radius: 12;"
        );

        Circle statusDot = new Circle(8);
        statusDot.setFill(Color.WHITE);

        Label statusLabel = new Label(rationStatus != null ? rationStatus : "Unknown");
        statusLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-wrap-text: true;"
        );

        statusIndicator.getChildren().addAll(statusDot, statusLabel);

        Label lastUpdated = new Label("Last updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        lastUpdated.setStyle(
                "-fx-text-fill: #7f8c8d;" +
                        "-fx-font-size: 12px;"
        );

        card.getChildren().addAll(cardTitle, statusIndicator, lastUpdated);
        addCardHoverEffect(card);
        return card;
    }

    private String getStatusColor(String status) {
        if (status == null) return "#95a5a6";
        switch (status.toLowerCase()) {
            case "active", "available" -> {
                return "#27ae60";
            }
            case "pending" -> {
                return "#f39c12";
            }
            case "expired", "unavailable" -> {
                return "#e74c3c";
            }
            default -> {
                return "#3498db";
            }
        }
    }

    private VBox createHistorySection(JSONArray rationHistory) {
        VBox historySection = new VBox(20);
        historySection.setPadding(new Insets(25));
        historySection.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );

        Label historyTitle = new Label("📜 Ration History");
        historyTitle.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2c3e50;"
        );

        VBox historyList = new VBox(10);

        if (rationHistory != null && rationHistory.length() > 0) {
            for (int i = 0; i < rationHistory.length(); i++) {
                try {
                    String entry = rationHistory.getJSONObject(i).getString("stringValue");
                    HBox historyItem = createHistoryItem(entry, i + 1);
                    historyList.getChildren().add(historyItem);
                } catch (Exception e) {
                    // Skip invalid entries
                }
            }
        } else {
            Label noHistoryLabel = new Label("📝 No ration history available yet.");
            noHistoryLabel.setStyle(
                    "-fx-text-fill: #7f8c8d;" +
                            "-fx-font-size: 16px;" +
                            "-fx-padding: 20;"
            );
            historyList.getChildren().add(noHistoryLabel);
        }

        historySection.getChildren().addAll(historyTitle, historyList);
        addCardHoverEffect(historySection);
        return historySection;
    }

    private HBox createHistoryItem(String entry, int index) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12, 15, 12, 15));
        item.setStyle(
                "-fx-background-color: #f8f9fa;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        Circle indexCircle = new Circle(12);
        indexCircle.setFill(Color.valueOf("#667eea"));

        Label indexLabel = new Label(String.valueOf(index));
        indexLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;"
        );

        StackPane indexStack = new StackPane(indexCircle, indexLabel);

        Label entryLabel = new Label(entry);
        entryLabel.setStyle(
                "-fx-text-fill: #2c3e50;" +
                        "-fx-font-size: 14px;" +
                        "-fx-wrap-text: true;"
        );

        item.getChildren().addAll(indexStack, entryLabel);

        // Hover effect
        String originalStyle = item.getStyle();
        item.setOnMouseEntered(e -> item.setStyle(originalStyle.replace("#f8f9fa", "#e9ecef")));
        item.setOnMouseExited(e -> item.setStyle(originalStyle));

        return item;
    }

    private void addCardHoverEffect(VBox card) {
        String originalStyle = card.getStyle();
        card.setOnMouseEntered(e -> {
            card.setStyle(originalStyle.replace(
                    "dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2)",
                    "dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0, 0, 4)"
            ));

            ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
            st.setToX(1.02);
            st.setToY(1.02);
            st.play();
        });

        card.setOnMouseExited(e -> {
            card.setStyle(originalStyle);

            ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    private void addEntranceAnimation(BorderPane root) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), root);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    private JSONObject fetchUserData(String documentId) {
        try {
            String url = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID +
                    "/databases/(default)/documents/users/" + documentId;

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) json.append(line);
            reader.close();

            JSONObject response = new JSONObject(json.toString());
            return response.getJSONObject("fields");
        } catch (Exception e) {
            return null;
        }
    }

    private String getField(JSONObject obj, String key) {
        try {
            return obj.getJSONObject(key).getString("stringValue");
        } catch (Exception e) {
            return "N/A";
        }
    }

    private JSONArray getHistory(JSONObject obj, String key) {
        try {
            JSONObject arrayValue = obj.getJSONObject(key).getJSONObject("arrayValue");
            return arrayValue.optJSONArray("values");
        } catch (Exception e) {
            return null;
        }
    }

   
}
