package GameEngine;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;

public class MoveRightAction extends AbstractInputAction{
	private SceneNode dN;
	public MoveRightAction(SceneNode dolphinN) 
	{
		dN = dolphinN;
	}
	@Override
	public void performAction(float a, Event e) 
	{
		dN.moveRight(0.05f);

	}
}
