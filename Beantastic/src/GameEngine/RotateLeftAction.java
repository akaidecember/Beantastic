package GameEngine;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Angle;
import ray.rml.Degreef;

public class RotateLeftAction extends AbstractInputAction{
	private SceneNode pN;
	private Camera camera;
	private ProtocolClient protClient;
	public RotateLeftAction(SceneNode playerN, ProtocolClient p,Camera c) {
		pN = playerN;
		camera = c;
		protClient = p;
	}
	@Override
	public void performAction(float a, Event e) {
		protClient.sendMoveMessage(pN.getLocalPosition(), "rLeft");
		Angle rotAmt1 = Degreef.createFrom(5.0f);
		pN.yaw(rotAmt1);


	}
}
