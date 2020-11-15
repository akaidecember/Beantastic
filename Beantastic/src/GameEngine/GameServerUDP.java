package GameEngine;

import java.io.IOException;
import java.util.UUID;
import java.net.InetAddress;

import ray.networking.server.GameConnectionServer;
import ray.networking.server.IClientInfo;

public class GameServerUDP extends GameConnectionServer<UUID>{
	

	public GameServerUDP(int localPort, ProtocolType protocolType) throws IOException {
		super(localPort, ProtocolType.UDP);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void processPacket(Object o, InetAddress senderIP, int sndPort) {
		String message = (String) o;
		String[] msgTokens = message.split(",");
		if(msgTokens.length>0) {
			if(msgTokens[0].compareTo("join")==0) {
				try {
					IClientInfo ci;
					ci = getServerSocket().createClientInfo(senderIP, sndPort);
					UUID clientID = UUID.fromString(msgTokens[1]);
					addClient(ci, clientID);
					sendJoinedMessage(clientID, true);
					System.out.print("JOINED");
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(msgTokens[0].compareTo("bye") == 0)
			{
				UUID clientID = UUID.fromString(msgTokens[1]);
				sendByeMessages(clientID);
				removeClient(clientID);
			}
			if(msgTokens[0].compareTo("create")==0) {
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] pos = { msgTokens[2], msgTokens[3], msgTokens[4]};
				sendCreateMessages(clientID, pos);
				sendWantsDetailsMessages(clientID);
				removeClient(clientID);
				System.out.println("CREATE");
			}
			if(msgTokens[0].compareTo("dsfr")==0) {
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
				sendDetailsMessage(clientID, pos);
			}
			if(msgTokens[0].compareTo("move")==0) {
				
			}
		}
		
	}
	private void sendJoinedMessage(UUID clientID, boolean success){
		try {
			String message = new String("join,");
			if(success) {
				message+="success";
				sendPacket(message, clientID);
			}
				
			else {
				message+="failure";
				sendPacket(message, clientID);
			}
				
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	private void sendCreateMessages(UUID clientID, String[] pos) {
		try {
			String message = new String("create," + clientID.toString());
			message += "," + pos[0];
			message += "," + pos[1];
			message += "," + pos[2];
			forwardPacketToAll(message, clientID);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	public void sendDetailsMessage(UUID senderID, String[] position) {
		try {
			String message = new String("dsfr," + senderID);
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			sendPacket(message, senderID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendWantsDetailsMessages(UUID clientID){  
		try {
			String message = new String("wsds," + clientID.toString());
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendMoveMessages(UUID clientID, String[] position, String s){
		try {
			String message = new String("move," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			message += "," + s;
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendByeMessages(UUID clientID){
		try {
			String message = new String("bye," + clientID.toString());
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// java -Dsun.java2d.d3d=false -Dsun.java2d.uiScale=1 NetworkingServer/ip address 9000 UDP
}
