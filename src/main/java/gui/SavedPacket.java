package gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.*;

/**
 * Handles saving and reading Packet objects in binary files
 * <p>  Handles saving and removing packets objects     </p>
 *
 * @author Jeb
 * */
public class SavedPacket extends Table{

    /**
     * Reads saved packets from bin file to array list
     * */
    public static void readSavedPacketsFromDisk() {
        try {
            File file = new File("file.bin");
            if (!file.exists()) {file.createNewFile();writeSavedPackets();}
            FileInputStream fis = new FileInputStream("file.bin");
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Read objects
            while (true) {
                try {
                    savedPackets.add((Packet) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    break;
                }
            }
            ois.close();
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Writes saved packets from array list to bin file
     * */
    static void writeSavedPackets() {
        try {
            FileOutputStream fos = new FileOutputStream("file.bin");
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // Write objects to file
            for (Packet old_packet: savedPackets) {
                oos.writeObject(old_packet);
            }

            oos.close();
            fos.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Saves packet to saved packets and rewrites saved to disk
     *
     * @param packet    packet to save
     *
     * */
    public static void savePacket(Packet packet) {
        savedPackets.add(packet);
        writeSavedPackets();
    }

    /**
     * Removes one saved packet
     *
     * @param packet    packet to remove
     *
     * */
    public static void  removeSavedPacket(Packet packet) {
        savedPackets.remove(packet);
        writeSavedPackets();
        window.setScene(PacketStreamView);
        drawSavedPacketsToGrid();
    }


    /**
     * Removes all saved packets
     * <p> Confirm packet removing from user </p>
     *
     * */
    public static void  removeSavedPackets() {
        // Windows alert box
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Hold Up!");
        alert.setHeaderText("This action will delete all saved packets permanently.");
        alert.setContentText("Are you sure about that?");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                // clear saved packets list and write changes
                savedPackets.clear();
                writeSavedPackets();
                drawSavedPacketsToGrid();
            }
        });
    }


    /**
     * Draw saved packets to grid
     * */
    public static void drawSavedPacketsToGrid() {
        clearTable();
        try {
            FileInputStream fis = new FileInputStream("file.bin");
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Read objects
            while (true) {
                try {
                    data.add((Packet) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    break;
                }
            }
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        table.setItems(data);
    }

}
