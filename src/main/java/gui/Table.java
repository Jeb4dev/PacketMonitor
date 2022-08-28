package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Contain Tableview object that shows packet data
 * */
public class Table extends PacketView{

    static TableView<Packet> table = new TableView<>();

    static ObservableList<Packet> data = FXCollections.observableArrayList();

    /**
     * Creates TableView object containign packet data
     *
     * @return TableView<Packet> object
     * */
    public TableView<Packet> Table() {
        table.setEditable(true);

        // Type
        TableColumn type = new TableColumn<>("Type");
        type.setPrefWidth(50);
        type.setCellValueFactory(new PropertyValueFactory<Packet, String>("type"));


        // Time
        TableColumn time = new TableColumn<>("Time");
        time.setPrefWidth(50);
        time.setCellValueFactory(new PropertyValueFactory<Packet, String>("time"));


        // Source
        TableColumn source = new TableColumn<>("Source");
        source.setPrefWidth(150);
        source.setCellValueFactory(new PropertyValueFactory<Packet, String>("source"));

        // Color cells if they are special
        source.setCellFactory(e -> new TableCell<Packet, String>() {
            @Override
            protected void updateItem(String string, boolean empty) {
                super.updateItem(string, empty);
                if (string != null) {
                    if (string.equals("192.168.1.1")) setStyle("-fx-background-color: #7a0000"); // routter
                    else if (string.equals(NetworkInterfaceName)) setStyle("-fx-background-color: #280000FF"); // your computer ipv4
                    else if (string.equals("2001:14ba:22eb:7300:4c44:c449:b3a0:bc79")) setStyle("-fx-background-color: #280000FF"); // your computer ipv6
                    else if (string.contains("192.168")) setStyle("-fx-background-color: rgb(70,10,0)"); // broadcast
                    else if (string.contains("192.168")) setStyle("-fx-background-color: #4b1212"); // local network
                    else setStyle("");
                }
                else setStyle("");
                setText(string);
            }
        });


        // Destination
        TableColumn destination = new TableColumn<>("Destination");
        destination.setPrefWidth(100);
        destination.setCellValueFactory(new PropertyValueFactory<Packet, String>("destination"));

        // Color cells if they are special
        destination.setCellFactory(e -> new TableCell<Packet, String>() {
            @Override
            protected void updateItem(String string, boolean empty) {
                super.updateItem(string, empty);
                if (string != null) {
                    if (string.equals("192.168.1.1")) setStyle("-fx-background-color: #7a0000"); // routter
                    else if (string.equals(NetworkInterfaceName)) setStyle("-fx-background-color: #280000FF"); // your computer ipv4
                    else if (string.equals("2001:14ba:22eb:7300:4c44:c449:b3a0:bc79")) setStyle("-fx-background-color: #280000FF"); // your computer ipv6
                    else if (string.contains("192.168")) setStyle("-fx-background-color: rgb(70,10,0)"); // broadcast
                    else if (string.contains("192.168")) setStyle("-fx-background-color: #4b1212"); // local network
                    else setStyle("");
                }
                else setStyle("");
                setText(string);
            }
        });


        // Header
        TableColumn header = new TableColumn<>("Header");
        header.setPrefWidth(100);
        header.setCellValueFactory(new PropertyValueFactory<Packet, String>("header"));


        // Payload
        TableColumn payload = new TableColumn<>("Payload");
        payload.setPrefWidth(100);
        payload.setCellValueFactory(new PropertyValueFactory<Packet, String>("payload"));


        // Size / length
        TableColumn size = new TableColumn<>("Size");
        size.setPrefWidth(50);
        size.setCellValueFactory(new PropertyValueFactory<Packet, String>("length"));


        // Hex Stream
        TableColumn hex = new TableColumn<>("Hex stream");
        hex.setPrefWidth(250);
        hex.setCellValueFactory(new PropertyValueFactory<Packet, String>("hex"));


        // Ascii
        TableColumn ascii = new TableColumn<>("Ascii");
        ascii.setPrefWidth(1000);
        ascii.setCellValueFactory(new PropertyValueFactory<Packet, String>("ascii"));

        // Set table row size
        table.setFixedCellSize(25);


        // Add columns to table
        table.getColumns().addAll(type, time, source, destination, size, ascii);

        return table;
    }


    /**
     * Updates the table containing all packet data
     *
     * @param packet new packet to be added to the table
     */
    public static void updateTable(Packet packet) {
        data.add(packet);
        table.setItems(data);

    }

    /**
     * Removes all data from the table
     */
    public static void clearTable() {
        data.clear();
    }
}
