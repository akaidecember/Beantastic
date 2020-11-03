package GameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import Core.BeantasticGame;
import net.java.games.input.Event;

//Class declaration for MoveForwardsAction 
public class MoveForwardsAction extends AbstractInputAction {

    private Node player;
    private BeantasticGame myGame;

    public MoveForwardsAction(Node newNode, BeantasticGame g) {
    	
        player = newNode;
        myGame = g;
        
    }

    public void performAction(float time, Event event) {
       	
        System.out.println("Forward Action riding the player");
        player.moveForward(0.15f);

        
    }
    
}