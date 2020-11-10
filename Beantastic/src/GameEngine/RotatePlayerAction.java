package GameEngine;

import Core.BeantasticGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Angle;
import ray.rml.Degreef;

public class RotatePlayerAction extends AbstractInputAction {
	
	private SceneNode playerNode;

	public RotatePlayerAction(SceneNode playerNode) {
		
		this.playerNode = playerNode;
		
	}
	
	@Override
	public void performAction(float arg0, Event e) {
		
		if (e.getValue() < -0.7) {
			
			Angle degree = Degreef.createFrom(5.0f);
			playerNode.yaw(degree);
			
		}

		if (e.getValue() > 0.7) {
			
			Angle degree = Degreef.createFrom(-5.0f);
			playerNode.yaw(degree);
			
		}
		
	}

}

