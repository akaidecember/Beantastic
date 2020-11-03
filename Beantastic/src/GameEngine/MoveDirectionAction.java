package GameEngine;

import Core.BeantasticGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;

public class MoveDirectionAction extends AbstractInputAction {
	
	private Node player;
	private BeantasticGame game;

	public MoveDirectionAction(Node node, BeantasticGame g) {
		
		player = node;
		game = g;
		
	}

	@Override
	public void performAction(float time, Event e) {
		
		// move left
		if (e.getValue() > 0.7f) 
			player.moveLeft(0.05f);
		
		// move right
		if (e.getValue() < -0.7f) 
			player.moveRight(0.05f);

	}

}
