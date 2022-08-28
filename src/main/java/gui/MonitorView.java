package gui;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * Packet Stream Scene
 *
 * @author Jeb
 * */
public class MonitorView extends Main {

    /**
     * Draw Packet Stream scene
     * */
    public void Draw() {
        // Define Main Panel
        BorderPane mainPanel = new BorderPane();
        mainPanel.setStyle("-fx-background-color: #2b2a33");

        // Table View
        Table table = new Table(); // Create table view
        mainPanel.setCenter(table.Table()); // Add table view to main panel


        PacketView packetView = new PacketView();

        // Open extended info scene by double-click on cell
        Table.table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Packet selectedPacket = Table.table.getSelectionModel().getSelectedItem();
                if (selectedPacket != null) packetView.draw(selectedPacket);
            }
        });

        // Top Buttons
        mainPanel.setTop(ToolButtons.CreateButtons());


        // Create scene
        PacketStreamView = new Scene(mainPanel);
        // Import stylesheets
        PacketStreamView.getStylesheets().add("static/css/main.css");
        // Set Scene
        window.setScene(PacketStreamView);
    }
}
