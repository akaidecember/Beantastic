package GameEngine;

import ray.ai.behaviortrees.BTCompositeType;
import ray.ai.behaviortrees.BTSequence;
import ray.ai.behaviortrees.BehaviorTree;

public class NPCcontroller {
	
	private NPC[] NPClist =  new NPC[5];
	private int numNPCs;
	private NPC npc;
	
	BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
	private long thinkStartTime;
	private long tickStartTime;
	private long lastThinkUpdateTime;
	private long lastTickUpdateTime;
	private GameServerUDP thisUDPServer;
	private boolean nearFlag;
	
	public void start()
	{
		thinkStartTime = System.nanoTime();
		tickStartTime = System.nanoTime();
		lastThinkUpdateTime = thinkStartTime;
		lastTickUpdateTime = tickStartTime;
		setupNPCs();
		setupBehaviorTree();
		npcLoop();
	}
	private void npcLoop() {
		while(true) {
			long currentTime = System.nanoTime();
			float elapsedThinkMilliSecs = (currentTime - lastThinkUpdateTime)/(1000000.0f);
			float elapsedTickMilliSecs = (currentTime - lastTickUpdateTime)/(1000000.0f);
			
			if(elapsedTickMilliSecs >= 50.0f)
			{
				lastTickUpdateTime = currentTime;
				npc.updateLocation();
				//thisUDPServer.sendNPCinfo();
			}
			if(elapsedThinkMilliSecs >= 500.0f)
			{
				lastThinkUpdateTime = currentTime;
				bt.update(elapsedThinkMilliSecs);
			}
			Thread.yield();
		}
	}
	private void setupBehaviorTree() {
		bt.insertAtRoot(new BTSequence(10));
		bt.insertAtRoot(new BTSequence(20));
		bt.insert(10, new OneSecPassed(this, npc, false));
		bt.insert(10, new GetSmall(npc));
		bt.insert(20, new AvatarNear(thisUDPServer, this, npc, false));
		bt.insert(20, new GetBig(npc));
		
	}
	public void setupNPCs() 
	{
		npc = new NPC();
		NPClist[0] = npc;
	}

	public void updateNPCs() {
		for(int i = 0; i< numNPCs; i++)
		{
			NPClist[i].updateLocation();
		}
	}
	
	public int getNumOfNPCs() {
		return numNPCs;
	}

	public NPC getNPC(int i) {
		return NPClist[i];
	}
	public boolean getNearFlag() {
		return nearFlag;
	}
	public void setNearFlag(boolean b) {
		nearFlag = b;
		
	}

}
