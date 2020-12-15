package GameEngine;

import Core.BeantasticGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Angle;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class RotateRightAction extends AbstractInputAction {
	private SceneNode pN;
	private Camera camera;
	private ProtocolClient protClient;

	public RotateRightAction(SceneNode playerN, ProtocolClient p,Camera c) {
		pN = playerN;
		camera = c;
		protClient = p;

	}
	@Override
	public void performAction(float a, Event e) {
		protClient.sendMoveMessage(pN.getLocalPosition(), "rRight");
		Angle rotAmt = Degreef.createFrom(-5.0f);
		pN.yaw(rotAmt);

	}
}
