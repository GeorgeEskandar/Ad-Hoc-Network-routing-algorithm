import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

public class DSRNode1 {
	public static void main(String[] args) throws SocketException, IOException {

		// array to save sequence of Backward propagation
		String[] path = new String[5];

		System.out.println("Node started..");
		int port = 5005;
		DatagramSocket socket = new DatagramSocket(port);
		socket.setBroadcast(true);
		// InetAddress remoteAddress;

		boolean receivedMessageFlag = false;

		byte[] buffer = null;

		// Initialize packet received from other nodes
		DatagramPacket packet = null;

		while (true) {

			try {
				// receive packets
				buffer = new byte[256];
				packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				byte[] receiveBuffer = packet.getData();
				System.out.println("Buffer length: " + receiveBuffer.length);
				String msg = new String(receiveBuffer);
				System.out.println("Message Received from: " + packet.getAddress().toString());
				System.out.println("Received Message is: " + msg);
				String[] ipArr = msg.trim().split(",");
				System.out.println("====================");
				for(String s : ipArr) {
					System.out.println(s);
				}
				System.out.println("====================");
				
				System.out.println("Flag is: " + ipArr[0]);
				//========================================================================================	
				// check if node receives a route request
				//========================================================================================	
				
				if (ipArr[0].equals("F")) {
					
					// Broadcast message if it was never received before
					if (!receivedMessageFlag) {
						receivedMessageFlag = true;
						// Add my own IP
						msg = msg.trim() + "192.168.210.152" + ",";
						System.out.println("Route discovery message");
						
						// check if I am destination to initiate reply to sender
						if (ipArr[1].equals("192.168.210.152")) {
							System.out.println("Destination reached..replying to sender");
							System.out.println("Message length "+msg.length());
							msg = "R," + msg.substring(18, msg.length()); // destination address field(16 character+ comma=17) is removed									
							System.out.println("Reply message is " + msg);
							ipArr = new String[5];
							ipArr = msg.split(",");
//							//finding previous node to send route reply
							int index;
							for(index =0;index<ipArr.length;index++){
								if(ipArr[index].equals("192.168.210.152")){
									break;
								}
							}
								
							System.out.println("INDEX: "+index);
							// Save path cache
							for (int i = 1; i < ipArr.length; i++) {
								path[i - 1] = ipArr[i];
							}
							//printing path
							System.out.println("Path is ");
							for(int i =0;i<path.length;i++) {
								if(path[i]!=null)
									System.out.print(path[i]+", ");
					}
							System.out.println("Sending Route Reply..\n\n");
							socket.setBroadcast(false);
							// update message and destination IP
							buffer = null;
							buffer = msg.getBytes();
							DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length,
									InetAddress.getByName(ipArr[index - 1]), port);
							socket.send(sendPacket);
							
						} else {
							System.out.println("Continue route discovery" + "\n\n");
							buffer = null;
							buffer = msg.getBytes();
							DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length,
									InetAddress.getByName("192.168.210.255"), port);
							socket.send(sendPacket);
							
						}
					}
				} 
				//========================================================================================
				//if node receives a route reply
				//========================================================================================	
				
				else if (ipArr[0].equals("R")) {

					socket.setBroadcast(false);

					// Save path cache
					for (int i = 1; i < ipArr.length; i++) {
						path[i - 1] = ipArr[i];
					}

					System.out.println("Replying to sender");
					System.out.println("Path is ");
					for(int i =0;i<path.length;i++)
						System.out.print(path[i]+", ");
					int index = 0;
					while (!ipArr[index].equals("192.168.210.152")) {
						index++;
					}
					buffer = null;
					buffer = msg.getBytes();
					
					DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length,
							InetAddress.getByName(ipArr[index - 1]), port);
					socket.send(sendPacket);
				} 
				//========================================================================================	
				//check if a message is received
				//========================================================================================				
				
				else if (ipArr[0].equals("M")) {
					System.out.println("Receiving M Message..");
					int index;
					for(index =0;index<path.length;index++){
						if(path[index].equals("192.168.210.152")){
							break;
						}
					}	
					// check if I am destination to initiate reply to sender
					if(index < path.length -1)
					{
						if (path[index+1]==null ) {
						System.out.println("Message: "+ipArr[1]);
					    } 
					else {
						System.out.println("Forwarding packet to destination..\n\n");
						buffer = null;
						buffer = msg.trim().getBytes();
					    DatagramPacket messagePacket = new DatagramPacket(buffer, buffer.length,
					    InetAddress.getByName(path[index+1]), port);
					    socket.send(messagePacket);
					     }
					}else {
						System.out.println("Message: "+ipArr[1]);
					}
					
					receivedMessageFlag = false;
			    }
				//========================================================================================	
				packet = null;	
				ipArr = null;	
				receiveBuffer = null;
				buffer = null;
		}

			 catch (IOException e) {
				System.out.println(e);
				socket.close();
				break;
			}
		}

		

	}
}