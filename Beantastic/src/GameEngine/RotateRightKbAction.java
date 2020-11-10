package GameEngine;

import Core.BeantasticGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Angle;
import ray.rml.Degreef;

public class RotateRightKbAction extends AbstractInputAction {
	
	private BeantasticGame game;

	public RotateRightKbAction(BeantasticGame g) {
		
		game = g;
		
	}
	
	@Override
	public void performAction(float arg0, Event e) {
		
		SceneNode camera = game.getEngine().getSceneManager().getSceneNode("MainCameraNode");
		Angle degree = Degreef.createFrom(-5.0f);
		camera.yaw(degree);
		game.setCameraAzimuthAngle(game.getCameraAzimuthAngle() - 5.0f);
		
	}

}
