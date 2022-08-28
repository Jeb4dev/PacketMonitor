package gui;

import Monitor.MonitorThread;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;

import java.net.UnknownHostException;

import static gui.SavedPacket.drawSavedPacketsToGrid;
import static gui.SavedPacket.removeSavedPackets;


/**
 * Creates Buttons for Monitor View
 *
 * @author Jeb
 *
 * */
public class ToolButtons extends MonitorView{
    static final Color DEFAULT_ICON_COLOR = new Color(1, 1, 1, 1);
    static final Color PLAY_ICON_COLOR = new Color(0, 0.6, 0, 1);
    static final Color PAUSE_ICON_COLOR = new Color(0.6, 0, 0, 1);
    static final Color STOP_ICON_COLOR = new Color(0.6, 0, 0, 1);

    static final String PLAY_ICON = "cil-media-play";

    static final String PAUSE_ICON = "cil-media-pause";

    static final String STOP_ICON = "cil-media-stop";
    static ComboBox<String> nifSelection;

    /**
     * Creates buttons and onAction events
     * <p>  play, pause, stop, load and delete buttons  </p>
     *
     * @return BorderPane containing buttons and actions
     * */
    static BorderPane CreateButtons() {

        // Create PLAY Button
        Button play = new Button("");
        FontIcon play_icon = new FontIcon(PLAY_ICON);
        play.setGraphic(play_icon);
        play.setTooltip(new Tooltip("Start packet capturing"));
        play_icon.setIconColor(DEFAULT_ICON_COLOR);

        // Create PAUSE Button
        Button pause = new Button("");
        FontIcon pause_icon = new FontIcon(PAUSE_ICON);
        pause.setGraphic(pause_icon);
        pause.setTooltip(new Tooltip("Pause packet capturing"));
        pause_icon.setIconColor(DEFAULT_ICON_COLOR);

        // Create STOP Button
        Button stop = new Button("");
        FontIcon stop_icon = new FontIcon(STOP_ICON);
        stop.setGraphic(stop_icon);
        stop.setTooltip(new Tooltip("Reset packet capturing"));
        stop_icon.setIconColor(DEFAULT_ICON_COLOR);


        // PLAY button action
        play.setOnAction(actionEvent -> {
            // When not capturing set capturing true
            if (!capturing) {
                capturing = true;
                window.setTitle("Network Monitor - Capturing...");
                nifSelection.setDisable(true);


                // set button icon colors
                play_icon.setIconColor(PLAY_ICON_COLOR);

                pause_icon.setIconColor(DEFAULT_ICON_COLOR);

                stop_icon.setIconColor(DEFAULT_ICON_COLOR);
            }

            // When capturing thread is not started
            if (!captureThread.get().isAlive()) {
                // Clear old packet data from table
                Table.clearTable();
                isStopped = false;
                // Start packet capturing thread
                captureThread.get().start();
            }

        });

        // PAUSE button action
        pause.setOnAction(actionEvent -> {
            // When capturing set capturing false
            if (capturing) {
                capturing = false;
                window.setTitle("Network Monitor");


                // set button icon colors
                play_icon.setIconColor(DEFAULT_ICON_COLOR);

                pause_icon.setIconColor(PAUSE_ICON_COLOR);

                stop_icon.setIconColor(DEFAULT_ICON_COLOR);
            }

        });

        // STOP button action
        stop.setOnAction(actionEvent -> {
            // When capturing thread is running END it
            if (captureThread.get().isAlive()) {
                capturing = false;
                window.setTitle("Network Monitor");
                nifSelection.setDisable(false);
                isStopped = true;
                // Create new capturing thread for next run
                captureThread.set(new Thread(monitorThread));


                // set button icon colors
                play_icon.setIconColor(DEFAULT_ICON_COLOR);

                pause_icon.setIconColor(DEFAULT_ICON_COLOR);

                stop_icon.setIconColor(STOP_ICON_COLOR);

            }
        });


        // Create "LOAD SAVED" Button & onAction
        Button load_saved = new Button("Load saved");
        load_saved.setTooltip(new Tooltip("Load saved packets"));
        load_saved.setOnAction(e -> {drawSavedPacketsToGrid();});

        // Create "DELETE ALL" Button & onAction
        Button delete_saved = new Button("Delete saved");
        delete_saved.setTooltip(new Tooltip("Remove all saved packets"));
        delete_saved.setOnAction(e -> {removeSavedPackets();});


        // Combo box for interface selection
        ObservableList<String> options = FXCollections.observableArrayList();
        for (PcapNetworkInterface avaibleNetworkInterfase : avaibleNetworkInterfases) {
            options.add(avaibleNetworkInterfase.getDescription());
        }

        nifSelection = new ComboBox<>(options);
        nifSelection.setValue(selectedNif.getDescription());


        nifSelection.setOnAction(e -> {
            String nifSelectionValue = nifSelection.getValue();
            for (PcapNetworkInterface nif : avaibleNetworkInterfases) {
                if (nif.getDescription().equals(nifSelectionValue)) {
                    selectedNif = nif;
                    try {
                        monitorThread = new MonitorThread();
                    } catch (UnknownHostException | PcapNativeException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });


        // Create Panels for Buttons

        // left horizontal box
        HBox tools_left = new HBox(3);
        tools_left.setPadding(new Insets(10));
        tools_left.setAlignment(Pos.BASELINE_LEFT);
        tools_left.setSpacing(5);
        tools_left.getChildren().addAll(play, pause, stop);

        // right horizontal box
        HBox tools_right = new HBox(2);
        tools_right.setStyle("-fx-padding: 10 25");
        tools_right.setAlignment(Pos.BASELINE_LEFT);
        tools_right.setSpacing(5);
        tools_right.getChildren().addAll(load_saved, delete_saved);

        // border panel
        BorderPane tools = new BorderPane();
        tools.setLeft(tools_left);
        tools.setRight(tools_right);
        tools.setCenter(nifSelection);

        return tools;
    }
}
