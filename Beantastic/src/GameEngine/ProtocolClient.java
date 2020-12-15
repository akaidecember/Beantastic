package GameEngine;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.Vector;

import Core.BeantasticGame;
import ray.networking.client.GameConnectionClient;
import ray.rml.Matrix3;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class ProtocolClient extends GameConnectionClient{
	private BeantasticGame game;
	private UUID id;
	private Vector<GhostAvatar> ghostAvatars;
	private Vector<GhostNPC> ghostNPCs;
	private NPCcontroller npcc;

	public ProtocolClient(InetAddress remoteAddr, int remotePort, ProtocolType protocolType, BeantasticGame game) throws IOException {
		super(remoteAddr, remotePort, protocolType);
		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostAvatars = new Vector<GhostAvatar>();
		this.ghostNPCs = new Vector<GhostNPC>();
		this.npcc = new NPCcontroller();
	}
	@Override
	protected void processPacket(Object msg){
		String strMessage = (String) msg;
		String[] messageTokens = strMessage.split(",");
		if(messageTokens.length>0) {
			if(messageTokens[0].compareTo("join")==0) {
				if(messageTokens[1].compareTo("success") == 0) {
					 game.setIsConnected(true);
					 sendCreateMessage(game.getPlayerPosition(), game.getPlayerOrientation());
					 System.out.println("Join successfully");
				}
				if(messageTokens[1].compareTo("failure")==0) {
					game.setIsConnected(false);
				}
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
					createGhostAvatarold(ghostID, ghostPosition);
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
					createGhostAvatar(ghostID, ghostPosition);
					System.out.println("ghost avatar created");
					//sendPacket(message);
				}
				catch(IOException e) {
					System.out.println("error creating ghost avatar");
				}
			}
				
			if(messageTokens[0].compareTo("wsds")==0) {
				/*UUID ghostID = UUID.fromString(messageTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(Float.parseFloat(messageTokens[2]), 
						Float.parseFloat(messageTokens[3]), 
						Float.parseFloat(messageTokens[4]));
				createGhostAvatar(ghostID, ghostPosition);*/
			}
			if(messageTokens[0].compareTo("wsds")==0) {
				UUID ghostID = UUID.fromString(messageTokens[1]);
				sendDetailsForMessage(ghostID, game.getPlayerPosition(), game.getPlayerOrientation());
			}
			if(messageTokens[0].compareTo("move")==0) {
				//move
				UUID ghostID = UUID.fromString(messageTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(Float.parseFloat(messageTokens[2]), 
															Float.parseFloat(messageTokens[3]), 
															Float.parseFloat(messageTokens[4]));
				if(messageTokens[5].compareTo("forward")==0) {
					moveNodeF(ghostID, ghostPosition);
				}
				else if(messageTokens[5].compareTo("rRight")==0) {
					rotateNodesR(ghostID, ghostPosition);
				}
				else if(messageTokens[5].compareTo("rLeft")==0) {
					rotateNodesL(ghostID, ghostPosition);
				}
				else if(messageTokens[5].compareTo("vert")==0) {
					vertP(ghostID, ghostPosition);
				}
			}
			
			if(messageTokens[0].compareTo("npc")==0) {
				System.out.println("creating npc");
				int npcID =  Integer.parseInt(messageTokens[1]);
				Vector3 pos = Vector3f.createFrom(
						Float.parseFloat(messageTokens[2]),
						Float.parseFloat(messageTokens[2]),
						Float.parseFloat(messageTokens[2]));
				try {
					createGhostNPC(npcID, pos);
					System.out.println("ghost npc created");
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			
			if(messageTokens[0].compareTo("mnpc")==0)
			{
				int ghostID = Integer.parseInt(messageTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(Float.parseFloat(messageTokens[2]), 
						Float.parseFloat(messageTokens[3]), 
						Float.parseFloat(messageTokens[4]));
				updateGhostNPC(ghostID, ghostPosition);
				System.out.println("npc moving");
				
			}
			if(messageTokens[0].compareTo("npcnear")==0)
			{
				checkForNPC();
			}
			
		}
	}
	private void vertP(UUID ghostID, Vector3 ghostPosition) {
		game.updateVertGhostNew(ghostID, ghostPosition);
	}
	private void rotateNodesL(UUID ghostID, Vector3 ghostPosition) {
		game.rotateLN(ghostID, ghostPosition);
	}
	private void rotateNodesR(UUID ghostID, Vector3 ghostPosition) {
		game.rotateRN(ghostID, ghostPosition);

	}
	private void moveNodeF(UUID ghostID, Vector3 ghostPosition) {
		System.out.println("MOVING FORWARD");
		game.moveNodeF(ghostID, ghostPosition);
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
		GhostAvatar avatar = new GhostAvatar(ghostID, ghostPosition);
		System.out.println(ghostPosition);
		ghostAvatars.addElement(avatar);
		game.addGhostAvatarToGameWorldnew(avatar, ghostPosition);
		//game.addGhostAvatarToGameWorldold(avatar, ghostPosition);

	}
	private void createGhostAvatarold(UUID ghostID, Vector3 ghostPosition) throws IOException {
		GhostAvatar avatar = new GhostAvatar(ghostID, ghostPosition);
		System.out.println(ghostPosition);
		ghostAvatars.addElement(avatar);
		game.addGhostAvatarToGameWorldold(avatar, ghostPosition);
		
	}
	private void removeGhostAvatar(UUID ghostID) {
		GhostAvatar avatar = ghostAvatars.get(0);
		for(int i = 0; i < ghostAvatars.size(); i++)
		{
			if(ghostID == ghostAvatars.get(i).getID())
				avatar = ghostAvatars.get(i);
		}
		game.removeGhostAvatarFromGameWorld(avatar);
	}
	private void sendCreateMessage(Vector3 pos, Matrix3 or) {
		try {
			String msg = new String("create," +id.toString());
			msg += "," + pos.x()+ "," +pos.y()+ ","+pos.z();
			msg += "," + or;
			sendPacket(msg);
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
	public void sendDetailsForMessage(UUID remId, Vector3 pos, Matrix3 or) {
		try {
			String msg = new String("dsfr," + remId.toString());
			msg +=","+pos.x()+","+pos.y()+","+pos.z();
			msg += "," + or;
			sendPacket(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendMoveMessage(Vector3 pos, String s) {
		try {
			String msg = new String("move," + id.toString());
			msg += "," + pos.x() + "," + pos.y() + "," + pos.z();
			msg += "," + s;
			sendPacket(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Vector<GhostAvatar> getCollection(){
		return ghostAvatars;
	}
	private void createGhostNPC(int npcID, Vector3 position) throws IOException{
		GhostNPC newNPC = new GhostNPC(npcID, position);
		ghostNPCs.add(newNPC);
		game.addGhostNPCGameWorld(newNPC, npcID);
	}
	private void updateGhostNPC(int id, Vector3 pos)
	{
		ghostNPCs.get(id).setPosition(pos);
	}
	public void askForNPC()
	{ 
		try
		{ 
			String message = new String("createNPC," + id.toString());
			message += "," + 0 + "," + 0 + "," + 0;
			sendPacket(message);
		}
		catch (IOException e)
		{ 
			e.printStackTrace();
		} 

	 }
	public void askForNPCinfo()
	{ 
		try
		{ 
			String message = new String("needNPC," + id.toString());
			sendPacket(message);
		}
		catch (IOException e)
		{ 
			e.printStackTrace();
		} 

	 }
	private void checkForNPC() {
		
	}
}
