package GameEngine;

import ray.ai.behaviortrees.BTCondition;

public class OneSecPassed extends BTCondition {

	private NPCcontroller npcc;
	private NPC npc;
	private long lastUpdateTime;
	public OneSecPassed(NPCcontroller c, NPC n, boolean b) {
		super(b);
		npcc = c;
		npc = n;
		lastUpdateTime = System.nanoTime();
	}
	@Override
	protected boolean check() {
		float elaspedMilliSecs = (System.nanoTime() - lastUpdateTime)/(1000000.0f);
		if((elaspedMilliSecs >= 1000.0f) && (npc.getSize() == 2.0))
		{
			lastUpdateTime = System.nanoTime();
			npcc.setNearFlag(false);
			return true;
		}
		else {
			return false;
		}
	}

}
