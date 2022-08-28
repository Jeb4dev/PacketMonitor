# Packet Manager
For capturing and analyzing network traffic and packets

## About The Project

### Why was this program created?
This program was written for university java programming class practice project.
Instructions for the project was to create program with multiple classes and to create graphical interface using JavaFX library.

### What can it do?
This program captures your devices network traffic and lists all the packets to a table. 
You can inspect specific packet closer and see more precise information about the packet itself and the source and 
destination of it, including geolocation, device vendor and organization.

### Where to use it?
You can use the program for learning about how the internet works and how devices communicate with each other. 
You could also detect malicious traffic on your network.
Using this application effectively requires prior understanding of internet, like what is IP or MAC address, how about header or DNS.

### What I learned on creating this?
This was my first Java program, and I had no prior experience working with Java.
The project took me around 60 hours of work including planing, coding, testing and documenting.

I learned a lot about Java and got to a lot more familiar with java syntax. 
I found javadoc very interesting and helpful tool, with mostly prior experience of python, I had not seen something like this. 
Definitely makes documenting faster - you really don't need to make it individually, but it comes with the code.
I learned how to use javafx and how to style the view using in-line styling and css file.
I learned how to use external packets and how to send http request. I kinda learned how to use Maven and pom.xml. 
Feels like requirements.txt in python, but I need to read more about it.

After this project I also understand python and javascript better. 
The way javascript always gets children's when modifying DOM of website makes a lot more sense to me now. 
Also got to understand more about python classes, class methods, static functions and private variables.

I also got to understand how the internet works a lot better. 
Most of the internet traffic is encrypted and man in the middle cannot really see your password - 
what VPN companies tell you they can - but what they can see is your dns request, weirdly enough they are not encrypted.

### Build With

- [JavaFX] - Java library for graphical user interface
- [Ikonli] - Java library for icons
- [Pcap4J] - Java library for capturing packets

## Getting Started

### Prerequisites
You will need these to run the application:
- [Java] 8 or greater
- [Npcap] or other packet capture library

### Running .jar
1. Download the PacketManager.Jar
2. Save it to your disk
3. Open that folder in your CMD (`cd "path.to.Monitor.jar"`)
4. Execute: `"path.to.java.exe" -jar Monitor.jar`

### Running source code
To install the packet manager to your local Maven repository, simply execute:

```bash
git clone --recursive https://github.com/JesperKauppinen/packet-monitor
cd packet-monitor
mvn install
```

## Contributing
Want to contribute? Great. You can for example:
- Report a bug
- Propose a new feature
- Submit fix to existing problem
- Submit new feature

### Submit changes
1. Fork the repository.
2. Create new branch from master.
3. Write changes and commit them to the new branch you created.
4. Issue a pull request to this original repository from your new fork.


## License
Distributed under the Apache License 2.0. See [LICENSE](LICENSE) for more information.

## Acknowledgments
- [Wireshark] - Packet manager
- [WireScope] - Packet manager written in java


[npcap]: <https://npcap.com/#download>
[java]: <https://www.java.com/en/download/>
[javafx]: <https://openjfx.io/>
[Ikonli]: <https://kordamp.org/ikonli/>
[pcap4j]: <https://www.pcap4j.org/>

[Wireshark]: <https://www.wireshark.org/>
[WireScope]: <https://github.com/dxk3355/WireScope>
