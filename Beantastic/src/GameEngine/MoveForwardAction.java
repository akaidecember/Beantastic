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
	private boolean a;
	private BeantasticGame game;
	public MoveForwardAction(SceneNode dolphinN, ProtocolClient p, BeantasticGame g, boolean anim) {
		dN = dolphinN;
		protClient = p;
		a = anim;
		game = g;
	}
	@Override
	public void performAction(float a, Event e) {
		if(game.debugCamera == true)
			dN.moveForward(1f);
		else
			dN.moveForward(0.1f);
		game.updateVerticalPosition();
		protClient.sendMoveMessage(dN.getWorldPosition(), "forward");
		game.setWalkTrue(true);
	}
	
}
