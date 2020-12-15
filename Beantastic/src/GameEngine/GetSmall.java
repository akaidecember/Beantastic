package GameEngine;

import ray.ai.behaviortrees.BTAction;
import ray.ai.behaviortrees.BTStatus;

public class GetSmall extends BTAction {

	private NPC npc;
	public GetSmall(NPC n) {
		npc = n;
	}

	@Override
	protected BTStatus update(float elaspedTime) {
		npc.getSmall();
		return BTStatus.BH_SUCCESS;
	}

}
