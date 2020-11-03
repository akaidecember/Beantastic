package GameEngine;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rage.game.*;
import ray.rml.*;
import Core.BeantasticGame;
import net.java.games.input.Event;

//Class declaration for MoveBackwardsAction
public class MoveBackwardsAction extends AbstractInputAction {

    private Node player;
    private BeantasticGame myGame;

    public MoveBackwardsAction(Node newNode, BeantasticGame g) {
    	
    	player = newNode;
        myGame = g;
        
    }

    public void performAction(float time, Event event) {
        
        //Console output
        System.out.println("Backwards Action riding the player");
        player.moveForward(-0.15f);

        
    }

}