package GameEngine;

import Core.BeantasticGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;

public class MoveUpDownAction extends AbstractInputAction {
	
	private Node player;
	private BeantasticGame game;

	public MoveUpDownAction(Node n, BeantasticGame g) {
		
		player = n;
		game = g;
		
	}

	@Override
	public void performAction(float time, Event e) {
		
		// move forward
		if (e.getValue() < -0.7f) 
			player.moveForward(0.05f);
			
		// move backward
		if (e.getValue() > 0.7f) 
			player.moveBackward(0.05f);
		
	}
	
}
