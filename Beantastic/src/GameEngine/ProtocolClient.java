package GameEngine;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.Vector;

import Core.BeantasticGame;
import ray.networking.client.GameConnectionClient;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class ProtocolClient extends GameConnectionClient{
	private BeantasticGame game;
	private UUID id;
	private Vector<GhostAvatar> ghostAvatars;
	public ProtocolClient(InetAddress remoteAddr, int remotePort, ProtocolType protocolType, BeantasticGame game) throws IOException {
		super(remoteAddr, remotePort, protocolType);
		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostAvatars = new Vector<GhostAvatar>();
	}
	@Override
	protected void processPacket(Object msg){
		String strMessage = (String)msg;
		String[] messageTokens = strMessage.split(",");
		if(messageTokens.length>0) {
			if(messageTokens[0].compareTo("Join")==0) {
				if(messageTokens[1].compareTo("success") == 0) {
					 game.setIsConnected(true);
					 sendCreateMessage((Vector3) game.getPlayerPosition());
				}
				if(messageTokens[1].compareTo("success")==0) {
					game.setIsConnected(false);
				}
				if(messageTokens[0].compareTo("bye") == 0){
					UUID ghostID = UUID.fromString(messageTokens[1]);
					removeGhostAvatar(ghostID);
				}
				if((messageTokens[0].compareTo("dsfr")==0)) {
					UUID ghostID = UUID.fromString(messageTokens[1]);
					Vector3 ghostPosition = Vector3f.createFrom(Float.parseFloat(messageTokens[2]), 
											Float.parseFloat(messageTokens[3]), 
											Float.parseFloat(messageTokens[4]));
					try{
						createGhostAvatar(ghostID, ghostPosition);
						System.out.println("ghost avatar created");
						//sendPacket(message);
					}
					catch(IOException e) {
						System.out.println("error creating ghost avatar");
					}
				}
				//oldghost
				if(messageTokens[0].compareTo("create")==0) {
					UUID ghostID = UUID.fromString(messageTokens[1]);
					Vector3 ghostPosition = Vector3f.createFrom(Float.parseFloat(messageTokens[2]), 
											Float.parseFloat(messageTokens[3]), 
											Float.parseFloat(messageTokens[4]));
					try{
						createGhostAvatarold(ghostID, ghostPosition);
						System.out.println("ghost avatar created");
						//sendPacket(message);
					}
					catch(IOException e) {
						System.out.println("error creating ghost avatar");
					}
				}
				
				if(messageTokens[0].compareTo("wsds")==0) {
					UUID ghostID = UUID.fromString(messageTokens[1]);
					sendDetailsForMessage(ghostID, game.getPlayerPosition(), game.getPlayerPosition());
				}
				if(messageTokens[0].compareTo("move")==0) {
					//move
					/*UUID ghostID = UUID.fromString(messageTokens[1]);
					Vector3 ghostPosition = Vector3f.createFrom(Float.parseFloat(messageTokens[2]), 
																Float.parseFloat(messageTokens[3]), 
																Float.parseFloat(messageTokens[4]));*/
					if(messageTokens[5].compareTo("forward")==0) {
						System.out.println("MOVING FORWARD");
					}

				}
			}
		}
		
	}
	private void sendDetailsForMessage(UUID ghostID, Object playerPosition, Object playerPosition2) {
		// TODO Auto-generated method stub
		
	}
	public void sendJoinMessage() {
		try {
			sendPacket(new String("join,"+id.toString()));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	private void createGhostAvatar(UUID ghostID, Vector3 ghostPosition) throws IOException{
		// TODO Auto-generated method stub
		GhostAvatar avatar = new GhostAvatar(ghostID, ghostPosition);
		System.out.println(ghostPosition);
		ghostAvatars.addElement(avatar);
		game.addGhostAvatarToGameWorldnew(avatar, ghostPosition);
		game.addGhostAvatarToGameWorldold(avatar, ghostPosition);

	}
	private void createGhostAvatarold(UUID ghostID, Vector3 ghostPosition) throws IOException {
		GhostAvatar avatar = new GhostAvatar(ghostID, ghostPosition);
		System.out.println(ghostPosition);
		ghostAvatars.addElement(avatar);
		game.addGhostAvatarToGameWorldold(avatar, ghostPosition);
		
	}
	private void removeGhostAvatar(UUID ghostID) {
		// TODO Auto-generated method stub
		GhostAvatar avatar = ghostAvatars.get(0);
		for(int i = 0; i < ghostAvatars.size(); i++)
		{
			if(ghostID == ghostAvatars.get(i).getID())
				avatar = ghostAvatars.get(i);
		}
		game.removeGhostAvatarFromGameWorld(avatar);
	}
	private void sendCreateMessage(Vector3 pos) {
		// TODO Auto-generated method stub
		try {
			String message = new String("create," +id.toString());
			message += "," + pos.x()+ "," +pos.y()+ ","+pos.z();
			sendPacket(message);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	public void sendByeMessage() {
		try
		{
			sendPacket(new String("bye," + id.toString()));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public void sendDetailsForMessage(UUID remId, Vector3 pos) {
		try {
			String message = new String("dsfr," + remId.toString());
			message +=","+pos.x()+","+pos.y()+","+pos.z();
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendMoveMessage(Vector3 pos, String s) {
		try {
			String message = new String("move," + id.toString());
			message += "," + pos.x() + "," + pos.y() + "," + pos.z();
			message += "," + s;
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
