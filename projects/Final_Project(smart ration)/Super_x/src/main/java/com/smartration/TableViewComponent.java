package com.smartration;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TableViewComponent {

    public static class Scheme {
        private String name;
        private String targetGroup;
        private String benefits;

        public Scheme(String name, String targetGroup, String benefits) {
            this.name = name;
            this.targetGroup = targetGroup;
            this.benefits = benefits;
        }

        public String getName() { return name; }
        public String getTargetGroup() { return targetGroup; }
        public String getBenefits() { return benefits; }
    }

    public VBox getSchemeTable() {
        TableView<Scheme> table = new TableView<>();

        TableColumn<Scheme, String> nameCol = new TableColumn<>("Scheme Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(200);

        TableColumn<Scheme, String> targetCol = new TableColumn<>("Target Group");
        targetCol.setCellValueFactory(new PropertyValueFactory<>("targetGroup"));
        targetCol.setMinWidth(200);

        TableColumn<Scheme, String> benefitCol = new TableColumn<>("Benefits");
        benefitCol.setCellValueFactory(new PropertyValueFactory<>("benefits"));
        benefitCol.setMinWidth(600);

        benefitCol.setCellFactory(col -> new TableCell<Scheme, String>() {
            private final Label label = new Label();

            {
                label.setWrapText(true);
                label.setMaxWidth(Double.MAX_VALUE);
                setGraphic(label);
                prefHeightProperty().bind(Bindings.createDoubleBinding(
                        () -> label.getHeight() + 20,
                        label.heightProperty()
                ));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    label.setText(null);
                } else {
                    label.setText(item);
                    label.setPrefWidth(getTableColumn().getWidth() - 20);
                }
            }
        });

        table.getColumns().addAll(nameCol, targetCol, benefitCol);

        // Add data
        table.getItems().addAll(
            new Scheme("Public Distribution System (PDS)", "All ration card holders", "Subsidized food grains including rice, wheat, sugar, and kerosene provided at low cost to ensure food security."),
            new Scheme("Antyodaya Anna Yojana (AAY)", "Poorest families", "35 kg food grains/month at highly subsidized rates (Rice ₹3/kg, Wheat ₹2/kg)."),
            new Scheme("National Food Security Act (NFSA)", "75% rural, 50% urban population", "Entitles 5 kg grains/person/month at subsidized rates: Rice ₹3/kg, Wheat ₹2/kg, Millets ₹1/kg."),
            new Scheme("PM Garib Kalyan Anna Yojana (PMGKAY)", "NFSA beneficiaries", "Additional 5 kg grains/month free during COVID-19 pandemic for support."),
            new Scheme("Annapurna Scheme", "Senior citizens (60+, no pension)", "10 kg food grains/month provided free to ensure nutritional support to senior citizens."),
            new Scheme("One Nation One Ration Card (ONORC)", "Migrant workers", "Allows access to ration from any PDS shop across India using Aadhaar authentication."),
            new Scheme("Shiv Bhojan Thali", "Poor, daily wage workers", "Hot nutritious meals at ₹10 including Rice, dal, sabzi, and roti, served at designated canteens across Maharashtra."),
            new Scheme("MGNREGA", "Rural households", "Provides at least 100 days of wage employment in a financial year to every household whose adult members volunteer to do unskilled manual work."),
            new Scheme("Mid-Day Meal Scheme", "School children", "Provides free lunch to school children on working days to improve nutritional status and encourage school attendance."),
            new Scheme("PM Ujjwala Yojana", "Below Poverty Line families", "Provides LPG connections to women from BPL households to reduce health hazards from traditional fuels."),
            new Scheme("National Nutrition Mission", "Women and children", "Targets malnutrition by promoting nutrition, breastfeeding, and supplementation."),
            new Scheme("Rashtriya Swasthya Bima Yojana", "Unorganized workers", "Health insurance for BPL families up to ₹30,000 per family per year."),
            new Scheme("Janani Suraksha Yojana", "Pregnant women (BPL)", "Cash assistance for institutional delivery to reduce maternal mortality."),
            new Scheme("Indira Gandhi Matritva Sahyog Yojana", "Lactating mothers", "₹6,000 cash support during pregnancy and after childbirth."),
            new Scheme("Sukanya Samriddhi Yojana", "Girl children", "Saving scheme for girl child with tax benefit and high interest."),
            new Scheme("Pradhan Mantri Awas Yojana", "Homeless / BPL families", "Affordable housing for all by 2022 through subsidies."),
            new Scheme("Stand Up India", "SC/ST and women entrepreneurs", "Bank loans between ₹10 lakh–₹1 crore for setting up business."),
            new Scheme("Deendayal Antyodaya Yojana", "Urban/rural poor", "Employment opportunities through skill development."),
            new Scheme("Digital India Ration Card", "All PDS users", "Digitization of ration cards for transparency and portability."),
            new Scheme("Smart Ration Shops", "All citizens", "Biometric-based ration distribution for fraud reduction."),
            new Scheme("Grain ATM", "Pilot cities", "Automated ration grain vending machines using Aadhaar."),
            new Scheme("Food Fortification Scheme", "General public", "Fortification of staple foods to tackle micronutrient deficiency."),
            new Scheme("E-Ration Card Service", "All ration card holders", "Download and access ration card digitally via portal."),
            new Scheme("Ration Card Portability", "Inter-state migrants", "Receive ration from any state where migrated."),
            new Scheme("Bhamashah Yojana", "Women in Rajasthan", "Financial inclusion and delivery of benefits through women’s bank accounts.")
        );

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        VBox.setVgrow(table, Priority.ALWAYS);
        vbox.getChildren().addAll(table);

        return vbox;
    }
}
