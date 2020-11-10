package GameEngine;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Angle;
import ray.rml.Degreef;

public class RotateLeftAction extends AbstractInputAction{
	private SceneNode dN;
	private Camera camera;
	public RotateLeftAction(SceneNode dolphinN, Camera c) {
		dN = dolphinN;
		camera = c;
	}
	@Override
	public void performAction(float a, Event e) {
		Angle rotAmt1 = Degreef.createFrom(5.0f);
		dN.yaw(rotAmt1);

	}
}
