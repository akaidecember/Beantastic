package GameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import Core.BeantasticGame;
import net.java.games.input.Event;

//Class declaration for MoveRightAction
public class MoveRightAction extends AbstractInputAction {

    private Node player;
    private BeantasticGame myGame;

    public MoveRightAction(Node newNode, BeantasticGame g) {
    	
        player = newNode;
        myGame = g;
        
    }

	public void performAction(float time, Event event) {
				
		//Console output
		System.out.println("Right Action riding the player");
		player.moveRight(0.15f);
	
	}
    
}