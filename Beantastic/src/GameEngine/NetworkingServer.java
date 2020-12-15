package GameEngine;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.JOptionPane;

import ray.networking.IGameConnection.ProtocolType;

public class NetworkingServer {
	
	private GameServerUDP thisUDPServer;
	private NPCcontroller npcCtrl;
	private long startTime;
	private long lastUpdateTime;
	
	public NetworkingServer(int serverPort, String protocol)
	{
		try
		{
			startTime = System.nanoTime();
			lastUpdateTime = startTime;
			npcCtrl = new NPCcontroller();
			
			thisUDPServer = new GameServerUDP(serverPort, ProtocolType.UDP);
			
			npcCtrl.setupNPCs();
			
			npcLoop();
		
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	private void npcLoop() {
		while(true) {
			long frameStartTime = System.nanoTime();
			float elapMilSecs = (frameStartTime - lastUpdateTime)/(10000000.0f);
			if(elapMilSecs >= 50.0f)
			{
				lastUpdateTime = frameStartTime;
				npcCtrl.updateNPCs();
				thisUDPServer.sendNPCinfo(npcCtrl);
			}
			Thread.yield();
		}
	}
	public static void main(String[] args) throws IOException
	{
		System.out.println("ServerStarted!");
		InetAddress IP = InetAddress.getLocalHost();
		System.out.println("Server IP address: "+IP.getHostAddress());
		NetworkingServer app = new NetworkingServer(Integer.parseInt(args[0]), args[1]);
	}
}
