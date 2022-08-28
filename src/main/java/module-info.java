module com.example.networkmonitor {
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires org.pcap4j.core;
    requires java.sql;
    requires java.net.http;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires json.simple;

    exports gui;
    exports Monitor;
}