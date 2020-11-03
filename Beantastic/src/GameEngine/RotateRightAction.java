package GameEngine;

import Core.BeantasticGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;
import ray.rage.scene.SceneNode;
import ray.rml.Angle;
import ray.rml.Degreef;

public class RotateRightAction extends AbstractInputAction {
	
	private BeantasticGame game;
	private Node player;

	public RotateRightAction(Node newNode, BeantasticGame g) {
		
		player = newNode;
		game = g;
		
	}

	@Override
	public void performAction(float arg0, Event arg1) {
		
		Angle degree = Degreef.createFrom(-5.0f);
		game.cameraNode.yaw(degree);
		game.setCameraAzimuthAngle(game.getCameraAzimuthAngle() - 5.0f);
		
	}
	
}
