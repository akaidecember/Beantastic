package GameEngine;

import Core.BeantasticGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class MoveCameraAction extends AbstractInputAction {
	
	private BeantasticGame game;

	public MoveCameraAction(BeantasticGame g) {
		
		game = g;
		
	}

	@Override
	public void performAction(float time, Event e) {
		
		if (e.getValue() == 0.25) 
			if (game.getCameraElevationAngle() < 60) 
				game.setCameraElevationAngle(game.getCameraElevationAngle() + 5.0f);

		if (e.getValue() == 0.75) 
			if (game.getCameraElevationAngle() > -30) 
				game.setCameraElevationAngle(game.getCameraElevationAngle() - 5.0f);

		if (e.getValue() == 1.0) 
			game.setCameraAzimuthAngle(game.getCameraAzimuthAngle() - 5.0f);

		if (e.getValue() == 0.5) 
				game.setCameraAzimuthAngle(game.getCameraAzimuthAngle() + 5.0f);

	}
		
}
		

