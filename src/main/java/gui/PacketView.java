package gui;


import Monitor.SendArpRequest;
import Monitor.SendHttpPacket;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kordamp.ikonli.javafx.FontIcon;


import static gui.SavedPacket.*;


/**
 * Extended Packet Information Scene
 *
 * @author Jeb
 * */
public class PacketView extends Main {
    Packet packet;

    /*  Initialize Threads that are used to get packet information
        such as IP geolocation by http request. The request will take
        some time, so use threads and update it when received.
    */
    private Thread GetSrcMac;
    private Thread GetDstMac;
    private Thread GetSrcVendor;
    private Thread GetDstVendor;
    private Thread GetSrcGeo;
    private Thread GetDstGeo;

    // Text areas
    private TextArea SourceDataArea;
    private TextArea DestinationDataArea;
    private TextArea GeneralInfoArea;
    private VBox PayloadDataArea;

    // Save MAC addresses
    private String srcMAC;
    private String dstMAC;

    /**
     * Draw Extended Packet Information Scene
     * TODO needs cleaning
     * */

    BorderPane topPane() {

        // top tools
        BorderPane top = new BorderPane();
        top.setStyle("-fx-background-color: #2b2a33");
        top.setPadding(new Insets(10));

        // Create Save Button
        Button save = new Button("");
        FontIcon save_icon = new FontIcon("cil-save");
        save.setGraphic(save_icon);
        save_icon.setIconColor(new Color(0.7, 0.1, 0.1, 1));
        save_icon.setIconSize(20);
        save.setTooltip(new Tooltip("Save packet for later"));
        save.setId("save-btn");
        save.setOnAction(e -> {
            // save packet info
            savePacket(packet);
        });

        // Create Delete Button
        Button delete = new Button("");
        FontIcon delete_icon = new FontIcon("cil-trash");
        delete.setGraphic(delete_icon);
        delete_icon.setIconColor(new Color(0.7, 0.1, 0.1, 1));
        delete_icon.setIconSize(20);
        delete.setTooltip(new Tooltip("Delete saved packet"));
        delete.setOnAction(e -> {
            removeSavedPacket(packet);
        });
        HBox tools = new HBox(save, delete);
        tools.setSpacing(10);
        top.setLeft(tools);



        // Create Exit Button
        Button X = new Button("");
        FontIcon exit_icon = new FontIcon("cil-account-logout");
        X.setGraphic(exit_icon);
        exit_icon.setIconColor(new Color(0.7, 0.1, 0.1, 1));
        exit_icon.setIconSize(20);
        X.setTooltip(new Tooltip("Back to packet list"));
        X.setOnAction(e -> {
            window.setScene(PacketStreamView);
        });
        top.setRight(X);

        // tittle label
        Label lbl = new Label("Extended packet information");
        lbl.setStyle("-fx-text-fill: white");
        top.setCenter(lbl);

        return top;
    }
    public void draw(Packet packet) {
        this.packet = packet;

        // Layout 1 - children are laid out in vertical column
        BorderPane mainPanel = new BorderPane();

        mainPanel.setTop(topPane());

        // Center
        BorderPane center = new BorderPane();
        mainPanel.setCenter(center);


        VBox src = new VBox();
        src.setStyle("-fx-background-color: #2b2b2b");
        src.setPrefHeight(screenSize.getHeight() / 2);
        VBox grl = new VBox();
        grl.setStyle("-fx-background-color: #2b2b2b");
        grl.setPrefHeight(screenSize.getHeight() / 2);
        VBox dst = new VBox();
        dst.setStyle("-fx-background-color: #2b2b2b");
        dst.setPrefHeight(screenSize.getHeight() / 2);
        VBox payload = new VBox();
        payload.setStyle("-fx-background-color: #2b2b2b");
        payload.setPrefHeight(screenSize.getHeight());


        //      General      //
        BorderPane grl_header = new BorderPane();
        HeaderLabel grl_header_label = new HeaderLabel("General Info");
        grl_header.setCenter(grl_header_label);

        // Source text area
        GeneralInfoArea = new TextArea();
        fillGeneralData();

        grl.getChildren().addAll(grl_header, GeneralInfoArea);


        //      Source      //
        BorderPane src_header = new BorderPane();
        HeaderLabel src_header_label = new HeaderLabel("Source");
        src_header.setCenter(src_header_label);

        // Source text area
        SourceDataArea = new TextArea();
        fillSourceData();

        src.getChildren().addAll(src_header, SourceDataArea);


        //      Destination      //
        BorderPane dst_header = new BorderPane();
        HeaderLabel dst_header_label = new HeaderLabel("Destination");
        dst_header.setCenter(dst_header_label);

        // Destination text area
        DestinationDataArea = new TextArea();
        fillDestinationData();

        dst.getChildren().addAll(dst_header, DestinationDataArea);


        //      PAYLOAD      //
        BorderPane payload_header = new BorderPane();
        HeaderLabel pay_header_label = new HeaderLabel("Payload");
        payload_header.setCenter(pay_header_label);


        // Payload text area
        PayloadDataArea = new VBox();
        fillPayloadData();

        payload.getChildren().addAll(payload_header, PayloadDataArea);


        FlowGridPane flowGridPane = new FlowGridPane(2, 1);
        flowGridPane.setStyle("-fx-background-color: #222222");

        VBox srcDstPane = new VBox();
        srcDstPane.setStyle("-fx-background-color: #222222");

        srcDstPane.getChildren().addAll(grl, src, dst);
        flowGridPane.getChildren().addAll(srcDstPane, payload);

        flowGridPane.setAlignment(Pos.CENTER);

        center.setCenter(flowGridPane);

        // Create scene
        PacketDataView = new Scene(mainPanel);
        // Import stylesheets
        PacketDataView.getStylesheets().add("static/css/main.css");
        // Set Scene
        window.setScene(PacketDataView);

        if (packet.getType().equals("ARP")) {
            srcMAC = packet.getSrcMac();
            GetSrcVendor.start();
            dstMAC = packet.getDstMac();
            GetDstVendor.start();
        } else {
            GetSrcMac.start();

        }
    }

    /**
     * Demo using style class on graphical element
     * Works like Label, but assigns style class that can be used in css file.
     * */
    public static class HeaderLabel extends Label {
        HeaderLabel(String text) {
            this.setText(text);
            this.getStyleClass().add("HeaderLabel");
        }
    }

    /**
     * Checks if IP address is local IPv4 address
     *
     * @param ip IP address
     * @return boolean, true if ip is local ip
     * */
    public static boolean isLocalAddress(String ip) {
        // Check for IPv4
        if (ip.startsWith("10")) return true; // 10.0.0.0 – 10.255.255.255
        if (ip.startsWith("172")) { // 172.16.0.0 – 172.31.255.255
            int sub_ip = Integer.parseInt(ip.substring(4, 6));
            if (sub_ip >= 16 && sub_ip <= 31) return true;
        }
        if (ip.startsWith("192")) return true; // 192.168.0.0 – 192.168.255.255

        // Check for IPv6
        // Cannot understand how IPv6 addresses work

        return false;
    }

    void fillGeneralData() {
        GeneralInfoArea.setPrefWidth(screenSize.getWidth() / 2);

        // Time
        GeneralInfoArea.appendText("Time: \t\t" + packet.getTime());

        // Length
        GeneralInfoArea.appendText("\nLength: \t\t" + packet.getLength());
    }

    void fillSourceData() {
        SourceDataArea.setPrefWidth(screenSize.getWidth() / 2);
        boolean isLocal = isLocalAddress(packet.getSource());

        // IP
        if (packet.getType().equals("ARP")) {
            SourceDataArea.appendText("Protocol: \t\t\t" + "Address Resolution Protocol (ARP)");
            SourceDataArea.appendText("\nIP Address: \t\t" + packet.getSource());
            SourceDataArea.appendText("\nMac Address: \t\t" + packet.getSrcMac());
            SourceDataArea.appendText("\nVendor: \t\t\t" + "figuring Vendor");

        } else {
            String ip = packet.getSource();
            SourceDataArea.appendText("IP Address: \t\t" + ip);

            // Local network
            SourceDataArea.appendText("\nLocal network: \t" + isLocal);

            // MAC Address
            SourceDataArea.appendText("\n\nMAC Address: \t\t" + "figuring MAC");
            GetSrcMac = new Thread(() -> {
                if (isLocal) {
                    SendArpRequest ARP = new SendArpRequest(ip);
                    srcMAC = ARP.getMac();
                    String sourceData = SourceDataArea.getText();

                    if (srcMAC.equals("null")) {

                        sourceData = sourceData.replace("figuring MAC", "failed");
                        sourceData = sourceData.replace("figuring Vendor", "-");
                        sourceData = sourceData.replace("figuring hostname", "-");

                        SourceDataArea.setText(sourceData);


                    } else {

                        sourceData = sourceData.replace("figuring MAC", srcMAC);
                        SourceDataArea.setText(sourceData);
                        // Start thread for getting vendor
                        GetSrcVendor.start();
                    }
                } else {
                    String sourceData = SourceDataArea.getText();

                    sourceData = sourceData.replace("figuring MAC", "-");
                    sourceData = sourceData.replace("figuring Vendor", "-");
                    SourceDataArea.setText(sourceData);

                    GetSrcGeo.start();
                }

                GetDstMac.start();
            });

            // Vendor
            SourceDataArea.appendText("\nMAC Vendor: \t\t" + (isLocal ? "figuring Vendor" : "-"));

            // Host Name
            SourceDataArea.appendText("\nHost Name: \t\t" + (isLocal ? "figuring hostname" : "-"));

            // Geolocation
            SourceDataArea.appendText("\n\nIP Geolocation: \t" + (isLocal ? "Local Network" : "figuring Geolocation"));
            GetSrcGeo = new Thread(() -> {
                String response = SendHttpPacket.get("http://ip-api.com/json/" + ip);
                Object obj;
                JSONObject jo;
                try {
                    obj = new JSONParser().parse(response);
                    jo = (JSONObject) obj;
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                String city = (String) jo.get("city");
                String country = (String) jo.get("country");
                String org = (String) jo.get("org");
                String status = (String) jo.get("status");
                String Geolocation;
                if (status.equals("success")) {
                    Geolocation = city + ", " + country;
                } else {
                    Geolocation = "failed";
                    org = "failed";
                }

                String sourceData = SourceDataArea.getText();
                sourceData = sourceData.replace("figuring Geolocation", Geolocation);
                sourceData = sourceData.replace("figuring org", org);
                SourceDataArea.setText(sourceData);

            });

            // organization
            SourceDataArea.appendText("\nOrganization: \t\t" + (isLocal ? "-" : "figuring org"));
        }
        GetSrcVendor = new Thread(() -> {
            String Vendor = "";
            Vendor = SendHttpPacket.getVendor("https://api.macvendors.com/" + srcMAC);

            String sourceData = SourceDataArea.getText();
            sourceData = sourceData.replace("figuring Vendor", Vendor);
            SourceDataArea.setText(sourceData);

        });
    }

    void fillDestinationData() {

        DestinationDataArea.setPrefWidth(screenSize.getWidth() / 2);
        boolean isLocal = isLocalAddress(packet.getDestination());

        // IP

        if (packet.getType().equals("ARP")) {
            DestinationDataArea.appendText("Protocol: \t\t\t" + "Address Resolution Protocol (ARP)");
            DestinationDataArea.appendText("\nIP Address: \t\t" + packet.getDestination());
            DestinationDataArea.appendText("\nMac Address: \t\t" + packet.getDstMac());
            DestinationDataArea.appendText("\nVendor: \t\t\t" + "figuring Vendor");
        } else {
            String ip = packet.getDestination();
            DestinationDataArea.appendText("IP Address: \t\t" + ip);

            // Local network
            DestinationDataArea.appendText("\nLocal network: \t" + isLocal);

            // MAC Address
            DestinationDataArea.appendText("\n\nMAC Address: \t\t" + "figuring MAC");
            GetDstMac = new Thread(() -> {
                if (isLocal) {
                    SendArpRequest ARP = new SendArpRequest(ip);
                    dstMAC = ARP.getMac();
                    String destinationData = DestinationDataArea.getText();

                    if (dstMAC.equals("null")) {

                        destinationData = destinationData.replace("figuring MAC", "failed");
                        destinationData = destinationData.replace("figuring Vendor", "-");
                        destinationData = destinationData.replace("figuring hostname", "-");
                        DestinationDataArea.setText(destinationData);
                    } else {

                        destinationData = destinationData.replace("figuring MAC", dstMAC);
                        DestinationDataArea.setText(destinationData);
                        // Start thread for getting vendor
                        GetDstVendor.start();
                    }
                } else {
                    String destinationData = DestinationDataArea.getText();

                    destinationData = destinationData.replace("figuring MAC", "-");
                    destinationData = destinationData.replace("figuring Vendor", "-");
                    DestinationDataArea.setText(destinationData);

                    GetDstGeo.start();
                }


            });

            // Vendor
            DestinationDataArea.appendText("\nMAC Vendor: \t\t" + (isLocal ? "figuring Vendor" : "-"));

            // Host Name
            DestinationDataArea.appendText("\nHost Name: \t\t" + (isLocal ? "figuring hostname" : "-"));

            // Geolocation
            DestinationDataArea.appendText("\n\nIP Geolocation: \t" + (isLocal ? "Local Network" : "figuring Geolocation"));
            GetDstGeo = new Thread(() -> {
                String response = SendHttpPacket.get("http://ip-api.com/json/" + ip);
                Object obj;
                JSONObject jo;
                try {
                    obj = new JSONParser().parse(response);
                    jo = (JSONObject) obj;
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                String city = (String) jo.get("city");
                String country = (String) jo.get("country");
                String org = (String) jo.get("org");
                String status = (String) jo.get("status");
                String Geolocation;
                if (status.equals("success")) {
                    Geolocation = city + ", " + country;
                } else {
                    Geolocation = "failed";
                    org = "failed";
                }

                String DestinationData = DestinationDataArea.getText();
                DestinationData = DestinationData.replace("figuring Geolocation", Geolocation);
                DestinationData = DestinationData.replace("figuring org", org);
                DestinationDataArea.setText(DestinationData);

            });

            // organization
            DestinationDataArea.appendText("\nOrganization: \t\t" + (isLocal ? "-" : "figuring org"));
        }
        GetDstVendor = new Thread(() -> {
            String Vendor = "";
            Vendor = SendHttpPacket.getVendor("https://api.macvendors.com/" + dstMAC);

            String destinationData = DestinationDataArea.getText();
            destinationData = destinationData.replace("figuring Vendor", Vendor);
            DestinationDataArea.setText(destinationData);

        });
    }

    void fillPayloadData() {
        PayloadDataArea.setPrefWidth(screenSize.getWidth() / 2);

        // payload ascii
        Label lbl_ascii = new Label("ASCII");
        lbl_ascii.getStyleClass().add("data");
        TextArea data_ascii = new TextArea();
        data_ascii.setText(packet.getAscii());
        data_ascii.getStyleClass().add("data");
        PayloadDataArea.getChildren().addAll(lbl_ascii, data_ascii);

        // payload hex stream
        Label lbl_hex = new Label("Hex Stream");
        lbl_hex.getStyleClass().add("data");
        TextArea data_hex = new TextArea();
        data_hex.setText(packet.getHex());
        data_hex.getStyleClass().add("data");
        PayloadDataArea.getChildren().addAll(lbl_hex, data_hex);

        // payload hex stream
        Label lbl_header = new Label("Header");
        lbl_header.getStyleClass().add("data");
        TextArea data_header = new TextArea();
        data_header.setText(packet.getHeader());
        data_header.getStyleClass().add("data");
        PayloadDataArea.getChildren().addAll(lbl_header, data_header);

        // packet data
        Label lbl_payload = new Label("Packet");
        lbl_payload.getStyleClass().add("data");
        TextArea data_payload = new TextArea();
        data_payload.setText(packet.getPayload());
        data_payload.getStyleClass().add("data");
        PayloadDataArea.getChildren().addAll(lbl_payload, data_payload);

        PayloadDataArea.setStyle("-fx-padding: 0 0 20px 0");

    }
}
