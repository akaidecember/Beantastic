package GameEngine;

import Core.BeantasticGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;

public class MoveBackwardAction extends AbstractInputAction{
	private SceneNode dN;
	private BeantasticGame game;
	public MoveBackwardAction(SceneNode dolphinN, BeantasticGame game) {
		dN = dolphinN;
		this.game = game;
	}
	@Override
	public void performAction(float a, Event e) {
		dN.moveBackward(0.1f);
		game.updateVerticalPosition();
	}
}
