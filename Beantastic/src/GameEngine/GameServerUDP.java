package GameEngine;

import java.io.IOException;
import java.util.UUID;
import java.net.InetAddress;

import ray.networking.server.GameConnectionServer;
import ray.networking.server.IClientInfo;

public class GameServerUDP extends GameConnectionServer<UUID>{
	
	public GameServerUDP(int localPort, ProtocolType protocolType) throws IOException {
		super(localPort, ProtocolType.UDP);
	}
	@Override
	public void processPacket(Object o, InetAddress senderIP, int sndPort) {
		String message = (String) o;
		String[] msgTokens = message.split(",");
		
		System.out.println(o);
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
			if(msgTokens[0].compareTo("create")==0) {
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] pos = { msgTokens[2], msgTokens[3], msgTokens[4]};
				sendCreateMessages(clientID, pos);
				sendWantsDetailsMessages(clientID);
				//removeClient(clientID);
				System.out.println("CREATE");
			}
			if(msgTokens[0].compareTo("bye") == 0)
			{
				UUID clientID = UUID.fromString(msgTokens[1]);
				sendByeMessages(clientID);
				removeClient(clientID);
				System.out.println("BYE");
			}
			if(msgTokens[0].compareTo("dsfr")==0) {
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
				sendDetailsMessage(clientID, pos);
			}
			if(msgTokens[0].compareTo("move")==0) {
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] position = {msgTokens[2], msgTokens[3], msgTokens[4]};
				String command = msgTokens[5];
				sendMoveMessages(clientID, position, command);
			}
			
			// NPC
			if(msgTokens[0].compareTo("createNPC")==0) {
				int npcID = (int)Math.random();
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
				sendCreateNPC(clientID, npcID, pos);
				
			}
			if(msgTokens[0].compareTo("needNPC")==0) {
				//sendCheckForAvatarNear();
			}
			if(msgTokens[0].compareTo("collide")==0) {
				
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
	// NPC
	public void sendNPCinfo(NPCcontroller npcCtrl) {
		for(int i =0; i<npcCtrl.getNumOfNPCs(); i++)
		{
			try
			{
				String message = new String("mnpc," + Integer.toString(i));
				message += "," + (npcCtrl.getNPC(i)).getX();
				message += "," + (npcCtrl.getNPC(i)).getY();
				message += "," + (npcCtrl.getNPC(i)).getZ();
				sendPacketToAll(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void sendCreateNPC(UUID clientID, int id, String[] position) {
		try {
			String message = new String("npc," + id);
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			sendPacket(message, clientID);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	public void sendCheckForAvatarNear() {
		//npc.start();
		//AvatarNear = new AvatarNear(this, npc, false);
		try {
			String message = new String("npcnear");
			sendPacketToAll(message);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
