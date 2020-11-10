package GameEngine;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;

public class MoveBackwardAction extends AbstractInputAction{
	private SceneNode dN;
	public MoveBackwardAction(SceneNode dolphinN) {
		dN = dolphinN;
	}
	@Override
	public void performAction(float a, Event e) {
		dN.moveBackward(0.05f);
	}
}
