package GameEngine;
import GameEngine.MoveForwardAction;
import Core.BeantasticGame;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class MoveForwardAction extends AbstractInputAction {

	private SceneNode dN;
	private ProtocolClient protClient;
	public MoveForwardAction(SceneNode dolphinN, ProtocolClient p) {
		dN = dolphinN;
		protClient = p;
	}
	@Override
	public void performAction(float a, Event e) {
		dN.moveForward(0.05f);
		protClient.sendMoveMessage(dN.getWorldPosition(), "forward");
	}
	
}
