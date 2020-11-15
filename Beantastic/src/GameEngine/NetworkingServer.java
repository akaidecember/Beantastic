package GameEngine;
import java.io.IOException;
import java.net.InetAddress;


import ray.networking.IGameConnection.ProtocolType;

public class NetworkingServer {
	private GameServerUDP thisUDPServer;
	//private GameServerTCP thisTCPServer;
	public NetworkingServer(int serverPort, String protocol)
	{
		try
		{
			thisUDPServer = new GameServerUDP(serverPort, ProtocolType.UDP);
			
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException
	{
		/*if(args.length>1) {
			NetworkingServer app = new NetworkingServer(Integer.parseInt(args[0], args[1]));
		}
		System.out.println("ServerStarted!");
		InetAddress IP = InetAddress.getLocalHost();
		System.out.println("Server IP address: "+IP.getHostAddress());
		int port = Integer.parseInt(JOptionPane.showInputDialog("Please input port: "));
		new GameServerUDP(port, null);
		System.out.println("Server Port Number: " + port);*/
		System.out.println("ServerStarted!");
		InetAddress IP = InetAddress.getLocalHost();
		System.out.println("Server IP address: "+IP.getHostAddress());
		//int port = Integer.parseInt(JOptionPane.showInputDialog("Please input port: "));
		//new GameServerUDP(port);
		//System.out.println("Server Port Number: " + port);
		NetworkingServer app = new NetworkingServer(Integer.parseInt(args[0]), args[1]);
	}
}
