package GameEngine;

import Core.BeantasticGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class MoveLeftAction extends AbstractInputAction{
	private SceneNode dN;
	public MoveLeftAction(SceneNode dolphinN) 
	{
		dN = dolphinN;
	}
	@Override
	public void performAction(float arg0, Event arg1) 
	{
		dN.moveLeft(0.05f);
	}
}
