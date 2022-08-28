package gui;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Packet is class where all captured packets are saved as object.
 * Objects can be saved as binary.
 *
 * @version 1.0
 * @author Jeb
 */
public class Packet implements Serializable {
    /**
     * Date / time when packet was captured.
     */
    private final Date time;
    /**
     * Type of packet
     */
    private final String type;
    /**
     * Source IP address
     */
    private final String source;
    /**
     * Destination IP address
     */
    private final String destination;
    /**
     * Packet header
     */
    private final String header;
    /**
     * Packet paylaod
     */
    private final String payload;
    /**
     * Packet lenghth / size
     */
    private final int length;
    /**
     * Packet paylaod hex stream
     */
    private final String hex;
    /**
     * Packet paylaod hex stream translated to ascii
     */
    private final String ascii;
    /**
     * Packet source mac address
     */
    private final String srcMac;
    /**
     * Packet destination mac address
     */
    private final String dstMac;

    /**
     * Generate Packet object for IPv4 or IPv6 packet
     *
     * @param type packet type such as DNS or IPv6
     * @param source source IP address
     * @param destination destination IP address
     * @param header packet header
     * @param payload packet
     * @param length packet length / size
     * @param hex payload hex stream. Can be ""6
     * @param ascii hex stream translated to ascii / readable. Can be ""
     */
    public Packet(String type, String source, String destination, String header, String payload, int length, String hex, String ascii) {
        this.type = type;
        if (source.startsWith("/")) {
            this.source = source.substring(1);
        } else this.source = source;
        if (destination.startsWith("/")) {
            this.destination = destination.substring(1);
        } else this.destination = destination;
        this.header = header;
        this.payload = payload;
        this.length = length;
        this.hex = hex;
        this.ascii = ascii;
        this.srcMac = "";
        this.dstMac = "";
        this.time = new Date();

        Table.updateTable(this);
    }

    /**
     * Generate Packet object for ARP packet
     *
     * @param type packet type such as DNS or IPv6
     * @param source source IP address
     * @param destination destination IP address
     * @param header packet header
     * @param srcMac source MAC address
     * @param dstMac destination MAC address
     * @param length packet length / size
     * @param hex payload hex stream
     * @param raw raw packet
     */
    public Packet(String type, String source, String destination, String header, String srcMac, String dstMac, int length, String hex, String raw) {
        this.type = type;
        if (source.startsWith("/")) {
            this.source = source.substring(1);
        } else this.source = source;
        if (destination.startsWith("/")) {
            this.destination = destination.substring(1);
        } else this.destination = destination;
        this.header = header;
        this.length = length;
        this.srcMac = srcMac;
        this.dstMac = dstMac;
        this.payload = "";
        this.hex = hex;
        this.ascii = raw;
        this.time = new Date();

        Table.updateTable(this);
    }

    /** type of packet such as DNS or IPv6.
     *
     * @return type of packet such as DNS or IPv6.
     */
    public String getType() {
        return type;
    }

    /** packets source IP address
     *
     * @return packets source IP address
     */
    public String getSource() {
        return source;
    }

    /** packets destination IP address.
     *
     * @return packets destination IP address.
     */
    public String getDestination() {
        return destination;
    }

    /** packets header.
     *
     * @return packets header.
     */
    public String getHeader() {
        return header;
    }

    /** packets payload.
     *
     * @return packets payload.
     */
    public String getPayload() {
        return payload;
    }

    /** packets length / size.
     *
     * @return packets length / size.
     */
    public int getLength() {
        return length;
    }

    /** packets payloads hex stream if it exists.
     *
     * @return packets payloads hex stream if it exists.
     */
    public String getHex() {
        return hex;
    }

    /** packets payloads hex steam translated to ascii.
     *
     * @return packets payloads hex steam translated to ascii.
     */
    public String getAscii() {
        return ascii;
    }

    /** packets source MAC address.
     *
     * @return packets source MAC address.
     */
    public String getSrcMac() {
        return srcMac;
    }

    /** packets destination MAC address.
     *
     * @return packets destination MAC address.
     */
    public String getDstMac() {
        return dstMac;
    }

    /** When was packet captured?
     *
     * @return time when packet was captured.
     */
    public String getTime() {
        return time.toString();
    }

    /**
     * Packet object as String
     *
     * @return fields from packet object as String.
     */
    @Override
    public String toString() {
        return "Packet{" +
                "time=" + time +
                ", type='" + type + '\'' +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", header='" + header + '\'' +
                ", payload='" + payload + '\'' +
                ", length=" + length +
                ", hex='" + hex + '\'' +
                ", ascii='" + ascii + '\'' +
                ", srcMac='" + srcMac + '\'' +
                ", dstMac='" + dstMac + '\'' +
                '}';
    }

    /** Does object equal to other object?
     *
     * @param o Packet object
     * @return true or false depending on if the packet is equals to other packet.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Packet packet = (Packet) o;
        return Objects.equals(source, packet.source) && Objects.equals(destination, packet.destination);
    }
}
