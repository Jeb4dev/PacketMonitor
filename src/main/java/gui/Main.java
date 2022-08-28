package gui;

import Monitor.MonitorThread;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static gui.SavedPacket.readSavedPacketsFromDisk;

/**
 * Main application
 */
public class Main extends Application {
    /**
     * List of saved packets
     */
    static ArrayList<Packet> savedPackets = new ArrayList<>();

    /**
     * Network interface that Monitor Thread listens
     */
    public static String NetworkInterfaceName;

    public static List<PcapNetworkInterface> avaibleNetworkInterfases;

    public static PcapNetworkInterface selectedNif;

    /**
     * is MonitorThread capturing packets?
     */
    public static boolean capturing = false;
    /**
     * is packet capturing stopped?
     */
    public static boolean isStopped = false;

    // Threads
    /**
     * Packet capturing thread
     */
    public static Runnable monitorThread;
    public static AtomicReference<Thread> captureThread;

    // Stages
    /**
     * Main stage
     */
    public static Stage window;

    // Scenes
    /**
     * Scene that has all captured packets in table
     */
    public static Scene PacketStreamView;
    /**
     * Scene that shows extended information of a packet
     */
    public static Scene PacketDataView;

    // Views
    static MonitorView monitorView;
    /**
     * Stores users screen dimensions
     */
    static Rectangle2D screenSize; // Screen size

    @Override
    public void start(Stage stage) throws UnknownHostException, PcapNativeException {
        window = stage;

        readSavedPacketsFromDisk();

        avaibleNetworkInterfases = Pcaps.findAllDevs();
        if (avaibleNetworkInterfases.isEmpty()) {
            System.out.println("ERROR: No network interfaces were found");
            return;
        }
        selectedNif = avaibleNetworkInterfases.get(0);


        // get local ip address
        NetworkInterfaceName = InetAddress.getLocalHost().toString().split("/")[1];

        // Setup monitoring thread, thread starts on play button click
        monitorThread = new MonitorThread();
        captureThread = new AtomicReference<>(new Thread(monitorThread));

        // Define and draw first scene
        monitorView = new MonitorView();
        monitorView.Draw();

        // Get screen size
        screenSize = Screen.getPrimary().getBounds();


        // Define window variables
        window.setTitle("Network Monitor");
        window.getIcons().add(new Image("file:icon.png"));
        window.setWidth(1280);
        window.setHeight(720);
        window.show();

    }

    /**
     * Main method that runs firs.
     *
     * @param args not used I guess
     */
    public static void main(String[] args) {
        // Launch javaFX gui
        launch();
    }
}