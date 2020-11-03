package GameEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.game.*;
import Core.BeantasticGame;
import net.java.games.input.Event;

public class QuitGameAction extends AbstractInputAction {

    private BeantasticGame game;

    public QuitGameAction(BeantasticGame g) {
    	
        game = g;
        
    }

    public void performAction(float time, Event event) {
    	
        System.out.println("shutdown");
        game.setState(Game.State.STOPPING);

    }

}
