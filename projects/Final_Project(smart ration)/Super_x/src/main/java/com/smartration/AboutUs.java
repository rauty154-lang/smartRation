package com.smartration;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;

public class AboutUs  {
    private Stage primaryStage;
    private Scene previousScene;

    // Modern color palette
    private static final Color PRIMARY_BLUE = Color.web("#2563EB");
    private static final Color SECONDARY_BLUE = Color.web("#1E40AF");
    private static final Color ACCENT_BLUE = Color.web("#3B82F6");
    private static final Color LIGHT_BLUE = Color.web("#EFF6FF");
    private static final Color CARD_BG = Color.web("#FFFFFF");
    private static final Color TEXT_PRIMARY = Color.web("#1F2937");
    private static final Color TEXT_SECONDARY = Color.web("#6B7280");
    private static final Color BORDER_COLOR = Color.web("#E5E7EB");
    private static final Color SUCCESS_GREEN = Color.web("#10B981");
    private static final Color WARNING_ORANGE = Color.web("#F59E0B");
    private static final Color ERROR_RED = Color.web("#EF4444");

    public AboutUs(Stage stage, Scene previousScene) {
        this.primaryStage = stage;
        this.previousScene = previousScene;
    }

    public Parent getContent() {
        // Back Button
        Button backButton = new Button("← Back");
        backButton.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        backButton.setTextFill(Color.WHITE);
        backButton.setStyle("-fx-background-color: #2563EB; -fx-background-radius: 8;");
        backButton.setPadding(new Insets(8, 16, 8, 16));

        // Optional: Add hover effect
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #1E40AF; -fx-background-radius: 8;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: #2563EB; -fx-background-radius: 8;"));

        // Add action to go back
    backButton.setOnAction(e -> {
    if (previousScene != null) {
        primaryStage.setScene(previousScene);
    } else {
        System.err.println("Previous scene is null: cannot navigate back.");
    }
});



        VBox mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(0, 40, 60, 40));
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPrefSize(1050, 600);

        LinearGradient blueGradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#DBEAFE")),  // Dark blue
            new Stop(1, Color.web("#BFDBFE"))   // Light blue
        );
        mainContainer.setBackground(new Background(new BackgroundFill(blueGradient, CornerRadii.EMPTY, Insets.EMPTY)));

        // Add subtle background pattern
        addBackgroundPattern(mainContainer);

        // Removed backButton at top:
        // mainContainer.getChildren().add(0, backButton); // (Commented out)

        // Header Section
        VBox headerSection = createModernHeader();
        mainContainer.getChildren().add(headerSection);

        // Core2Web Section
        VBox core2WebSection = createModernSection("🎯", "Powered by Core2Web", PRIMARY_BLUE);
        addModernHoverEffect(core2WebSection);
        mainContainer.getChildren().add(core2WebSection);

        // Super X Section
        VBox superXSection = createModernSection("🚀", "Super X – Innovation Platform", SUCCESS_GREEN);
        addModernHoverEffect(superXSection);
        mainContainer.getChildren().add(superXSection);

        // Smart Ration Project Section
        VBox smartRationSection = createModernSection("💡", "Smart Ration Project", WARNING_ORANGE);
        addModernHoverEffect(smartRationSection);
        mainContainer.getChildren().add(smartRationSection);

        // Team Section
        VBox teamSection = createModernSection("👥", "Team CompileStorm", ERROR_RED);
        addModernHoverEffect(teamSection);
        mainContainer.getChildren().add(teamSection);

        // Values Section
        VBox valuesSection = createModernSection("💎", "Our Core Values", ACCENT_BLUE);
        addModernHoverEffect(valuesSection);
        mainContainer.getChildren().add(valuesSection);

        // ADD Back button at the END, wrapped in HBox for horizontal centering
        HBox backBtnBox = new HBox(backButton);
        backBtnBox.setAlignment(Pos.CENTER);
        VBox.setMargin(backBtnBox, new Insets(20, 0, 0, 0));  // Spacing above the button
        mainContainer.getChildren().add(backBtnBox);

        // Create scroll pane
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: white;");
       Platform.runLater(() -> scrollPane.setVvalue(0));



        // Add entrance animations
        addEntranceAnimations(mainContainer);

        return scrollPane;
    }

    // All your other methods exactly as you provided, unchanged:

    private void addBackgroundPattern(VBox container) {
        for (int i = 0; i < 5; i++) {
            Circle decorCircle = new Circle(2);
            decorCircle.setFill(Color.web("#F3F4F6"));
            decorCircle.setTranslateX(100 + i * 200);
            decorCircle.setTranslateY(50 + i * 150);

            TranslateTransition float1 = new TranslateTransition(Duration.seconds(4 + i), decorCircle);
            float1.setByY(-10);
            float1.setAutoReverse(true);
            float1.setCycleCount(TranslateTransition.INDEFINITE);
            float1.play();

            container.getChildren().add(decorCircle);
        }
    }

    private VBox createModernHeader() {
        VBox header = new VBox(25);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20, 20, 60, 20));

        Label titleLabel = new Label("Smart Ration");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 56));
        titleLabel.setTextFill(TEXT_PRIMARY);

        Label subtitleLabel = new Label("About Our Innovation");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        subtitleLabel.setTextFill(TEXT_SECONDARY);

        Rectangle decorLine = new Rectangle(100, 4);
        LinearGradient lineGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, PRIMARY_BLUE),
                new Stop(0.5, ACCENT_BLUE),
                new Stop(1, SUCCESS_GREEN)
        );
        decorLine.setFill(lineGradient);
        decorLine.setArcWidth(4);
        decorLine.setArcHeight(4);

        Text descriptionText = new Text("Revolutionizing Public Distribution Through Technology");
        descriptionText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        descriptionText.setFill(TEXT_SECONDARY);
        descriptionText.setTextAlignment(TextAlignment.CENTER);
        descriptionText.setWrappingWidth(600);

        header.getChildren().addAll(titleLabel, decorLine, subtitleLabel, descriptionText);
        return header;
    }

    private VBox createModernSection(String icon, String title, Color accentColor) {
        VBox section = new VBox(30);
        section.setPadding(new Insets(50));
        section.setAlignment(Pos.TOP_CENTER);
        section.setMaxWidth(1000);

        Rectangle cardBg = new Rectangle();
        cardBg.widthProperty().bind(section.widthProperty());
        cardBg.heightProperty().bind(section.heightProperty());
        cardBg.setFill(CARD_BG);
        cardBg.setArcWidth(20);
        cardBg.setArcHeight(20);
        cardBg.setStroke(BORDER_COLOR);
        cardBg.setStrokeWidth(1);

        DropShadow modernShadow = new DropShadow();
        modernShadow.setColor(Color.web("#000000", 0.08));
        modernShadow.setRadius(25);
        modernShadow.setOffsetY(5);
        modernShadow.setSpread(0.1);
        cardBg.setEffect(modernShadow);

        StackPane iconContainer = createModernIcon(icon, accentColor);

        VBox headerContainer = new VBox(20);
        headerContainer.setAlignment(Pos.CENTER);

        if (title.contains("Core2Web")) {
            URL imageUrl = getClass().getResource("/sir.jpg");
            if (imageUrl != null) {
                Image logoImage = new Image(imageUrl.toExternalForm());
                ImageView logoView = new ImageView(logoImage);
                logoView.setFitWidth(250);
                logoView.setPreserveRatio(true);
                logoView.setSmooth(true);
                logoView.setCache(true);
                headerContainer.getChildren().add(logoView);
            } else {
                System.err.println("Image resource '/sir.jpg' not found! Check your resource path.");
            }
        }

        headerContainer.getChildren().add(iconContainer);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(TEXT_PRIMARY);
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.TOP_CENTER);

        if (title.contains("Core2Web")) {
            contentBox.getChildren().addAll(createCore2WebContent());
        } else if (title.contains("Super X")) {
            contentBox.getChildren().addAll(createSuperXContent());
        } else if (title.contains("Smart Ration Project")) {
            contentBox.getChildren().addAll(createSmartRationContent());
        } else if (title.contains("Team")) {
            contentBox.getChildren().addAll(createTeamContent());
        } else if (title.contains("Values")) {
            contentBox.getChildren().addAll(createValuesContent());
        }

        section.getChildren().addAll(headerContainer, titleLabel, contentBox);
        section.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

        return section;
    }

    private StackPane createModernIcon(String icon, Color accentColor) {
        StackPane iconContainer = new StackPane();

        Circle outerCircle = new Circle(50);
        LinearGradient iconGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, accentColor),
                new Stop(1, accentColor.deriveColor(0, 1, 0.8, 1))
        );
        outerCircle.setFill(iconGradient);

        Circle innerCircle = new Circle(45);
        innerCircle.setFill(Color.web("#FFFFFF", 0.2));

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 28));
        iconLabel.setTextFill(Color.WHITE);

        DropShadow iconShadow = new DropShadow();
        iconShadow.setColor(accentColor.deriveColor(0, 1, 1, 0.4));
        iconShadow.setRadius(20);
        iconShadow.setSpread(0.2);
        outerCircle.setEffect(iconShadow);

        iconContainer.getChildren().addAll(outerCircle, innerCircle, iconLabel);
        return iconContainer;
    }

    private VBox[] createCore2WebContent() {
        VBox content1 = createCore2WebContentBox(
                "This project is proudly developed under the guidance of ", "Shashi Bagal Sir", " at " +
                        "Core2Web, a place where coding is not just taught — it's understood deeply. " +
                        "Known for his unique teaching style, Sir makes even the toughest concepts feel simple and meaningful."
        );

        VBox content2 = createCore2WebContentBox(
                "At Core2Web, we follow the approach: \"", "Know the code till the core", "\" — " +
                        "and that's exactly what shaped this smart solution. This approach has empowered us to " +
                        "build not just functional code, but meaningful solutions that address real-world problems."
        );

        return new VBox[]{content1, content2};
    }

    private VBox createCore2WebContentBox(String beforeBold, String boldText, String afterBold) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);

        TextFlow textFlow = new TextFlow();
        textFlow.setTextAlignment(TextAlignment.JUSTIFY);
        textFlow.setMaxWidth(800);

        Text beforeText = new Text(beforeBold);
        beforeText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        beforeText.setFill(TEXT_PRIMARY);

        Text bold = new Text(boldText);
        bold.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        bold.setFill(Color.web("#111111"));

        Text afterText = new Text(afterBold);
        afterText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        afterText.setFill(TEXT_PRIMARY);

        textFlow.getChildren().addAll(beforeText, bold, afterText);
        textFlow.setLineSpacing(2);

        box.getChildren().add(textFlow);
        return box;
    }

    private VBox[] createSuperXContent() {
        VBox content1 = createContentBox(
                "Super X is not just a project presentation platform — it's a launchpad for creativity, " +
                        "innovation, and bold ideas. It gives students the perfect opportunity to express their " +
                        "technical skills and present solutions that can make a real difference."
        );

        VBox content2 = createContentBox(
                "Our journey through Super X led us to create the Smart Ration project — a digital solution " +
                        "aimed at transforming public distribution. Throughout this journey, we were guided by mentors " +
                        "who provided constant support, insights, and technical guidance."
        );

        return new VBox[]{content1, content2};
    }

    private VBox[] createSmartRationContent() {
        VBox description = createContentBox(
                "Smart Ration is an innovative distribution system that aims to simplify and digitize " +
                        "the ration distribution system by offering a transparent, efficient, and user-friendly " +
                        "platform for both citizens and administrators."
        );

        VBox techStack = createModernTechStack();
        VBox features = createModernFeatures();

        return new VBox[]{description, techStack, features};
    }

    private VBox[] createTeamContent() {
        VBox description = createContentBox(
                "Team CompileStorm — a passionate group of budding developers dedicated to building " +
                        "smart, real-world solutions. Together, we combined creativity, logic, and teamwork to " +
                        "bring Smart Ration to life with a mission to make technology more impactful for society."
        );

        VBox teamGrid = createModernTeamGrid();

        return new VBox[]{description, teamGrid};
    }

    private VBox[] createValuesContent() {
        VBox valuesGrid = createModernValuesGrid();
        return new VBox[]{valuesGrid};
    }

    private VBox createContentBox(String content) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);

        Text contentText = new Text(content);
        contentText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        contentText.setFill(TEXT_PRIMARY);
        contentText.setTextAlignment(TextAlignment.JUSTIFY);
        contentText.setWrappingWidth(800);
        contentText.setLineSpacing(2);

        box.getChildren().add(contentText);
        return box;
    }

    private VBox createModernTechStack() {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);

        Label techTitle = new Label("Technologies Used");
        techTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        techTitle.setTextFill(TEXT_PRIMARY);

        FlowPane techFlow = new FlowPane();
        techFlow.setHgap(15);
        techFlow.setVgap(15);
        techFlow.setAlignment(Pos.CENTER);

        String[] technologies = {"Java", "JavaFX", "Firebase", "Firestore", "REST APIs"};
        Color[] techColors = {PRIMARY_BLUE, SUCCESS_GREEN, WARNING_ORANGE, ERROR_RED, ACCENT_BLUE};

        for (int i = 0; i < technologies.length; i++) {
            StackPane techBadge = createModernTechBadge(technologies[i], techColors[i % techColors.length]);
            techFlow.getChildren().add(techBadge);
        }

        container.getChildren().addAll(techTitle, techFlow);
        return container;
    }

    private StackPane createModernTechBadge(String tech, Color color) {
        StackPane badge = new StackPane();

        Rectangle bg = new Rectangle(120, 35);
        bg.setFill(color.deriveColor(0, 0.3, 0.95, 1));
        bg.setStroke(color);
        bg.setStrokeWidth(1.5);
        bg.setArcWidth(20);
        bg.setArcHeight(20);

        Label label = new Label(tech);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        label.setTextFill(color);

        badge.getChildren().addAll(bg, label);

        badge.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), badge);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
            bg.setFill(color.deriveColor(0, 0.5, 0.9, 1));
        });

        badge.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), badge);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
            bg.setFill(color.deriveColor(0, 0.3, 0.95, 1));
        });

        return badge;
    }

    private VBox createModernFeatures() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);

        Label featuresTitle = new Label("Key Features");
        featuresTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        featuresTitle.setTextFill(TEXT_PRIMARY);

        GridPane featuresGrid = new GridPane();
        featuresGrid.setHgap(25);
        featuresGrid.setVgap(25);
        featuresGrid.setAlignment(Pos.CENTER);

        String[][] features = {
                {"🔐", "Secure Authentication", "Email and password-based user authentication"},
                {"📊", "Real-time Management", "Live stock management and inventory tracking"},
                {"🌐", "Cloud Integration", "Firebase and Firestore for seamless data management"},
                {"🔄", "API Integration", "REST APIs for efficient data communication"}
        };

        for (int i = 0; i < features.length; i++) {
            VBox featureBox = createModernFeatureCard(features[i][0], features[i][1], features[i][2]);
            featuresGrid.add(featureBox, i % 2, i / 2);
        }

        container.getChildren().addAll(featuresTitle, featuresGrid);
        return container;
    }

    private VBox createModernFeatureCard(String icon, String title, String description) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(350);
        Rectangle bg = new Rectangle(350, 140);
        bg.setFill(Color.web("#F9FAFB"));
        bg.setStroke(BORDER_COLOR);
        bg.setStrokeWidth(1);
        bg.setArcWidth(15);
        bg.setArcHeight(15);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 24));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(TEXT_PRIMARY);

        Text descText = new Text(description);
        descText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        descText.setFill(TEXT_PRIMARY);
        descText.setWrappingWidth(300);
        descText.setTextAlignment(TextAlignment.CENTER);

        VBox content = new VBox(8);
        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(iconLabel, titleLabel, descText);

        StackPane container = new StackPane();
        container.getChildren().addAll(bg, content);

        card.getChildren().add(container);
        return card;
    }

    private VBox createModernTeamGrid() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);

        Label teamTitle = new Label("Our Team Members");
        teamTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        teamTitle.setTextFill(TEXT_PRIMARY);

        GridPane teamGrid = new GridPane();
        teamGrid.setHgap(20);
        teamGrid.setVgap(20);
        teamGrid.setAlignment(Pos.CENTER);

        String[] teamMembers = {"Prerana Nevase", "Vaishnavi Wale", "Yash Raut", "Anuj Kale"};
        Color[] memberColors = {PRIMARY_BLUE, SUCCESS_GREEN, WARNING_ORANGE, ERROR_RED};

        for (int i = 0; i < teamMembers.length; i++) {
            VBox memberCard = createModernMemberCard(teamMembers[i], memberColors[i]);
            teamGrid.add(memberCard, i % 2, i / 2);
        }

        container.getChildren().addAll(teamTitle, teamGrid);
        return container;
    }

    private VBox createModernMemberCard(String name, Color accentColor) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(220);

        Rectangle bg = new Rectangle(220, 100);
        bg.setFill(CARD_BG);
        bg.setStroke(accentColor.deriveColor(0, 0.5, 1, 0.3));
        bg.setStrokeWidth(2);
        bg.setArcWidth(15);
        bg.setArcHeight(15);

        Rectangle accentBar = new Rectangle(220, 4);
        accentBar.setFill(accentColor);

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setTextFill(TEXT_PRIMARY);

        Label roleLabel = new Label("Developer");
        roleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        roleLabel.setTextFill(TEXT_PRIMARY);

        VBox content = new VBox(5);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(nameLabel, roleLabel);

        StackPane container = new StackPane();
        container.setAlignment(Pos.TOP_CENTER);
        container.getChildren().addAll(bg, accentBar, content);

        card.getChildren().add(container);

        card.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });

        card.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });

        return card;
    }

    private VBox createModernValuesGrid() {
        VBox container = new VBox(25);
        container.setAlignment(Pos.CENTER);

        Label valuesTitle = new Label("What We Stand For");
        valuesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        valuesTitle.setTextFill(TEXT_PRIMARY);

        HBox valuesRow = new HBox(30);
        valuesRow.setAlignment(Pos.CENTER);

        String[][] values = {
                {"💪", "Be a Force for Good", "Making the world a better place through positive actions, innovation and diversity."},
                {"🤝", "Be a Good Human", "Value good communication, be open, honest and constructive with yourself and others."},
                {"⭐", "Empower Others", "Empowering others to achieve their goals, both globally and within our community."}
        };

        Color[] valueColors = {SUCCESS_GREEN, PRIMARY_BLUE, WARNING_ORANGE};

        for (int i = 0; i < values.length; i++) {
            VBox valueCard = createModernValueCard(values[i][0], values[i][1], values[i][2], valueColors[i]);
            valuesRow.getChildren().add(valueCard);
        }

        container.getChildren().addAll(valuesTitle, valuesRow);
        return container;
    }

    private VBox createModernValueCard(String icon, String title, String description, Color accentColor) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(280);

        Rectangle bg = new Rectangle(280, 200);
        bg.setFill(CARD_BG);
        bg.setStroke(BORDER_COLOR);
        bg.setStrokeWidth(1);
        bg.setArcWidth(15);
        bg.setArcHeight(15);

        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.web("#000000", 0.05));
        cardShadow.setRadius(15);
        cardShadow.setOffsetY(3);
        bg.setEffect(cardShadow);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 28));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(accentColor);
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        Text descText = new Text(description);
        descText.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        descText.setFill(TEXT_PRIMARY);
        descText.setWrappingWidth(220);
        descText.setTextAlignment(TextAlignment.CENTER);
        descText.setLineSpacing(2);

        VBox content = new VBox(12);
        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(iconLabel, titleLabel, descText);

        StackPane container = new StackPane();
        container.getChildren().addAll(bg, content);

        card.getChildren().add(container);
        return card;
    }

    private void addModernHoverEffect(VBox section) {
        section.setOnMouseEntered(e -> {
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), section);
            scaleIn.setToX(1.02);
            scaleIn.setToY(1.02);
            scaleIn.play();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), section);
            fadeIn.setToValue(0.95);
            fadeIn.play();
        });

        section.setOnMouseExited(e -> {
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(300), section);
            scaleOut.setToX(1.0);
            scaleOut.setToY(1.0);
            scaleOut.play();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), section);
            fadeOut.setToValue(1.0);
            fadeOut.play();
        });
    }

    private void addEntranceAnimations(VBox container) {
        for (int i = 0; i < container.getChildren().size(); i++) {
            FadeTransition fade = new FadeTransition(Duration.millis(600), container.getChildren().get(i));
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setDelay(Duration.millis(i * 200));

            TranslateTransition translate = new TranslateTransition(Duration.millis(600), container.getChildren().get(i));
            translate.setFromY(50);
            translate.setToY(0);
            translate.setDelay(Duration.millis(i * 200));

            fade.play();
            translate.play();
        }
    }
}