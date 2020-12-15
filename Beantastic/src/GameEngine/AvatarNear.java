package GameEngine;

import ray.ai.behaviortrees.BTCondition;

public class AvatarNear extends BTCondition {

	private GameServerUDP server;
	private NPCcontroller npcc;
	private NPC npc;
	public AvatarNear(GameServerUDP thisUDPServer, NPCcontroller npCcontroller, NPC n, boolean b) {
		super(b);
		server = thisUDPServer;
		npcc = npCcontroller;
		npc = n;
		
	}

	@Override
	protected boolean check() {
		server.sendCheckForAvatarNear();
		return npcc.getNearFlag();
	}


}
