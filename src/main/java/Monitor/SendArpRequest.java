/**
 * Send ARP request to local network device to find out its MAC Address
 *
 * @source https://github.com/kaitoy/pcap4j/blob/v1/pcap4j-sample/src/main/java/org/pcap4j/sample/SendArpRequest.java
 */
package Monitor;

import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.*;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.ArpHardwareType;
import org.pcap4j.packet.namednumber.ArpOperation;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static gui.Main.NetworkInterfaceName;

/**
 * Is used to send ARP request for local network devices to get their MAC address.
 */
public class SendArpRequest {

    private static final String COUNT_KEY = SendArpRequest.class.getName() + ".count";
    private static final int COUNT = Integer.getInteger(COUNT_KEY, 1);

    private static final String READ_TIMEOUT_KEY = SendArpRequest.class.getName() + ".readTimeout";
    private static final int READ_TIMEOUT = Integer.getInteger(READ_TIMEOUT_KEY, 10); // [ms]

    private static final String SNAPLEN_KEY = SendArpRequest.class.getName() + ".snaplen";
    private static final int SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]

    private static final MacAddress SRC_MAC_ADDR = MacAddress.getByName("fe:00:01:02:03:04");
    private final String DST_IP_ADDR;
    private static MacAddress resolvedAddr;


    /**
     * Sends the ARP request to defined IP address.
     *
     * @param dts_ip_addr destination IP address
     */
    public SendArpRequest(String dts_ip_addr) {
        this.DST_IP_ADDR = dts_ip_addr;
        try {
            getMacAddress();
        } catch (UnknownHostException | PcapNativeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @return resolved mac address in string format.
     */
    public String getMac() {
        if (resolvedAddr == null) return "null";
        return resolvedAddr.toString();
    }

    public void getMacAddress() throws PcapNativeException, UnknownHostException {
        String strSrcIpAddress = "192.0.2.100"; // for InetAddress.getByName()

        PcapNetworkInterface nif;
        InetAddress addr = InetAddress.getByName(NetworkInterfaceName);
        nif = Pcaps.getDevByAddress(addr);

        PcapHandle handle = nif.openLive(SNAPLEN, PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
        PcapHandle sendHandle = nif.openLive(SNAPLEN, PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
        ExecutorService pool = Executors.newSingleThreadExecutor();


        try {
            handle.setFilter("arp and src host " + this.DST_IP_ADDR + " and dst host " + strSrcIpAddress + " and ether dst " + Pcaps.toBpfString(SRC_MAC_ADDR), BpfCompileMode.OPTIMIZE);

            PacketListener listener = packet -> {
                if (packet.contains(ArpPacket.class)) {
                    ArpPacket arp = packet.get(ArpPacket.class);
                    if (arp.getHeader().getOperation().equals(ArpOperation.REPLY)) {
                        SendArpRequest.resolvedAddr = arp.getHeader().getSrcHardwareAddr();
                    }
                }
            };


            Task t = new Task(handle, listener);
            pool.execute(t);


            ArpPacket.Builder arpBuilder = new ArpPacket.Builder();
            try {
                arpBuilder.hardwareType(ArpHardwareType.ETHERNET).protocolType(EtherType.IPV4).hardwareAddrLength((byte) MacAddress.SIZE_IN_BYTES).protocolAddrLength((byte) ByteArrays.INET4_ADDRESS_SIZE_IN_BYTES).operation(ArpOperation.REQUEST).srcHardwareAddr(SRC_MAC_ADDR).srcProtocolAddr(InetAddress.getByName(strSrcIpAddress)).dstHardwareAddr(MacAddress.ETHER_BROADCAST_ADDRESS).dstProtocolAddr(InetAddress.getByName(this.DST_IP_ADDR));

            } catch (UnknownHostException e) {
                throw new IllegalArgumentException(e);
            }

            EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
            etherBuilder.dstAddr(MacAddress.ETHER_BROADCAST_ADDRESS).srcAddr(SRC_MAC_ADDR).type(EtherType.ARP).payloadBuilder(arpBuilder).paddingAtBuild(true);

            for (int i = 0; i < COUNT; i++) {
                Packet p = etherBuilder.build();
                try {
                    sendHandle.sendPacket(p);

                } catch (NotOpenException e) {
                    throw new RuntimeException(e);
                }
                try {

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (NotOpenException e) {
            throw new RuntimeException(e);
        } finally {
            if (handle.isOpen() && resolvedAddr != null) {
                handle.close();
            }
            if (sendHandle.isOpen()) {
                sendHandle.close();
            }
            if (!pool.isShutdown()) {
                pool.shutdown();
            }
        }
    }

    private static class Task implements Runnable {

        private PcapHandle handle;
        private PacketListener listener;

        public Task(PcapHandle handle, PacketListener listener) {
            this.handle = handle;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                handle.loop(COUNT, listener);
            } catch (PcapNativeException | InterruptedException | NotOpenException e) {
                e.printStackTrace();
            }
        }
    }
}