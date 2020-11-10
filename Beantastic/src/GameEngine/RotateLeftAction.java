package GameEngine;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Angle;
import ray.rml.Degreef;

public class RotateLeftAction extends AbstractInputAction{
	
	private SceneNode node;
	private Camera camera;
	
	public RotateLeftAction(SceneNode newNode, Camera c) {
		
		node = newNode;
		camera = c;
		
	}
	
	@Override
	public void performAction(float a, Event e) {
		
		Angle rotAmt1 = Degreef.createFrom(5.0f);
		node.yaw(rotAmt1);
		

	}
}
