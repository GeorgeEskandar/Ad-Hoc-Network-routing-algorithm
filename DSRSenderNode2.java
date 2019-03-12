import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

public class DSRSenderNode2 {
	public static void main(String[] args) throws SocketException, IOException {

		System.out.println("Node started");
		int port = 5005;
		DatagramSocket socket = new DatagramSocket(port);
		socket.setBroadcast(true);
		InetAddress remoteAddress;

		// RREQ message and adding Source IP
		String destination = "192.168.210.175,";
		String RREQ = "F," + destination+ "192.168.210.177,";
		System.out.println("Initial Message " + RREQ);
		byte[] buffer = RREQ.getBytes();
		String[] path = new String[5];


		// initiates flooding
		System.out.println("initiating flooding");

		DatagramPacket floodingPacket = new DatagramPacket(buffer, buffer.length,
				InetAddress.getByName("192.168.210.255"), port);
		socket.send(floodingPacket);

		// Initialize packet received from other nodes
		buffer = null;
		DatagramPacket packet = null;;
		

		while (true) {

			try {
				// receive packets
				buffer = new byte[256];
				packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				byte[] receivedBuffer = packet.getData();
				String msg = new String(receivedBuffer);
				msg = msg+",";
				String[] ipArr = msg.split(",");
				System.out.println("====================");
				for(String s : ipArr) {
					System.out.println(s);
				}
				System.out.println("====================");

				
				if (ipArr[0].equals("R")) {
					System.out.println("Received Route Reply ");
					for (int i = 1; i < ipArr.length; i++) {
						path[i - 1] = ipArr[i];
					}
					//print path found by route discovery
					System.out.println("Path is ");
					for(int i =0;i<path.length;i++) {
						if(path[i]!=null)
							System.out.println(path[i]);
					}
					//Communicating with destination
					System.out.println("Sending Message..");
					socket.setBroadcast(false);
					msg = "M," + "Hello!";
					System.out.print("Sending: " + msg);
					System.out.println(" to " + path[1]);
					System.out.println("Message length is " + msg.length());
					byte[] messageBuffer= msg.trim().getBytes();
					System.out.println("Buffer length: " + messageBuffer.length);				
					DatagramPacket messagePacket = new DatagramPacket(messageBuffer, messageBuffer.length,
							InetAddress.getByName(path[1]), port);	
					socket.send(messagePacket);
															
				}

			} catch (IOException e) {
				System.out.println(e);
				break;
			}
			buffer=null;
			packet=null;
		}

		socket.close();

	}
}