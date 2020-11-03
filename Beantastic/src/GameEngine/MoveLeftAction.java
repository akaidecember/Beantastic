package GameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import Core.BeantasticGame;
import net.java.games.input.Event;

//Class declaration for MoveLeftAction
public class MoveLeftAction extends AbstractInputAction {

    private Node player;
    private BeantasticGame myGame;

    public MoveLeftAction(Node newNode, BeantasticGame g) {
    	
        player = newNode;
        myGame = g;
        
    }

	public void performAction(float time, Event e) {
		
		//Console output
		System.out.println("Left Action riding the player");
		player.moveLeft(0.15f);
			
	}
    
}