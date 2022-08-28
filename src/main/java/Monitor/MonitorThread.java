package Monitor;

import gui.Main;
import org.pcap4j.core.*;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.packet.*;
import org.pcap4j.util.MacAddress;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static gui.Main.selectedNif;

/**
 * Monitor Thread is thread that listens network traffic and
 * handles captured packets and makes them objects of Packet class.
 *
 * @version 1.0
 * @author Jeb
 * @author <a href="https://github.com/kaitoy/pcap4j">Pcap4J</a>
 */
public class MonitorThread implements Runnable {
    private static PcapNetworkInterface nif;
    private final int SNAPLEN;
    private final int READ_TIMEOUT;
    private final int COUNT;


    /** Create MonitorThread object.
     *
      * @throws PcapNativeException Exception
     * @throws UnknownHostException Exception
     */
    public MonitorThread() throws PcapNativeException, UnknownHostException {

        nif = selectedNif;
        SNAPLEN = 65536;
        READ_TIMEOUT = 10;
        COUNT = 100000;
    }

    /**
     * Runs on thread start.
     */
    @Override
    public void run() {
        while (!Main.isStopped) {
            if (Main.capturing) {
                final PcapHandle handle;
                try {
                    handle = nif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);

                    int num = 0;
                    // or just capture everything but only handle when capturing is on
                    while (Main.capturing) {
                        org.pcap4j.packet.Packet packet = handle.getNextPacket();
                        if (packet != null) {
                            handlePacket(packet);
                            num++;
                            if (num >= COUNT) {
                                break;
                            }
                        }
                    }

//                    PcapStat ps = handle.getStats();
//                    System.out.println("ps_recv: " + ps.getNumPacketsReceived());
//                    System.out.println("ps_drop: " + ps.getNumPacketsDropped());
//                    System.out.println("ps_ifdrop: " + ps.getNumPacketsDroppedByIf());

                    handle.close();
                } catch (PcapNativeException | NotOpenException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {Thread.sleep(500);}
                catch (InterruptedException e) {throw new RuntimeException(e);}
            }
        }
//        System.out.println("Thread DEAD");
    }

    /**
     * Classifies captured packets and saves them as Packet objects.
     * Define types of packets such as DNS or IPv6.
     *
     * @param packet captured packet
     */
    private void handlePacket(org.pcap4j.packet.Packet packet) {
        String hexStream  = "";
        String ascii = "";

        // IPv4
        if (packet.contains(IpV4Packet.class)) {
            IpV4Packet ipv4 = packet.get(IpV4Packet.class);
            Inet4Address srcAddr = ipv4.getHeader().getSrcAddr();
            Inet4Address dstAddr = ipv4.getHeader().getDstAddr();


            String payload = packet.getPayload().toString();
            if (payload.contains("Hex stream: ")) {
                String[] parts = payload.split("Hex stream: ");
                hexStream = parts[1];
                ascii = HexToAscii(parts[1]);
            }

            String type  = "IPv4";
            if (ipv4.getPayload().contains(DnsPacket.class))  { // You could add more types
                type  = "DNS";
                String[] parts = payload.split("QNAME: ");
                ascii = parts[1].split("\n")[0];
            }

            // Create new Packet object
            new gui.Packet(type, srcAddr.toString(), dstAddr.toString(), packet.getHeader().toString(), packet.getPayload().toString(), packet.length(), hexStream, ascii);


        }
        // IPv6
        else if (packet.contains(IpV6Packet.class)) {
            IpV6Packet ipv6 = packet.get(IpV6Packet.class);
            Inet6Address srcAddr = ipv6.getHeader().getSrcAddr();
            Inet6Address dstAddr = ipv6.getHeader().getDstAddr();

            String payload = packet.getPayload().toString();
            if (payload.contains("Hex stream: ")) {
                String[] parts = payload.split("Hex stream: ");
                hexStream = parts[1];
                ascii = HexToAscii(parts[1]);
            }

            String type  = "IPv6";
            if (ipv6.getPayload().contains(DnsPacket.class))  { // You could add more types
                type  = "DNS";
                String[] parts = payload.split("QNAME: ");
                ascii = parts[1].split("\n")[0];
            }

            // Create new Packet object
            new gui.Packet(type, srcAddr.toString(), dstAddr.toString(), packet.getHeader().toString(), packet.getPayload().toString(), packet.length(), hexStream, ascii);
        }

        // ARP
        else if (packet.contains(ArpPacket.class)) {
            ArpPacket arp = packet.get(ArpPacket.class);
            InetAddress srcAddr = arp.getHeader().getSrcProtocolAddr();
            InetAddress dstAddr = arp.getHeader().getDstProtocolAddr();
            MacAddress srcMac = arp.getHeader().getSrcHardwareAddr();
            MacAddress dstMac = arp.getHeader().getDstHardwareAddr();


            // Create new Packet object
            new gui.Packet("ARP", srcAddr.toString(), dstAddr.toString(), packet.getHeader().toString(), srcMac.toString(), dstMac.toString(), packet.length(), arp.toHexString(), arp.toString());
        }

    }

    /**
     * Translates hex decimals to ascii strings
     *
     * @param hexString Sting of hex decimal values of packet payload
     * @return ascii form
     */
    public static String HexToAscii(String hexString) {
        StringBuilder str = new StringBuilder();

        try
        {
            for (int i = 0; i < hexString.length() - 1; i += 3)
            {
                String s = hexString.substring(i, (i + 2));
                int decimal = Integer.parseInt(s, 16);
                str.append((char) decimal);
            }
        }
        catch (Exception e)
        {
            System.out.println("ERROR - " + e);
        }
        return str.toString();
    }
}
