package GameEngine;
/*package myGameEngine;

import a1.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rml.Angle;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class MoveUpAction extends AbstractInputAction{

	private Camera camera;
	private MyGame game;

	public MoveUpAction(MyGame myGame, Camera c) {
		camera = c;
		game = myGame;
	}
	@Override
	public void performAction(float time, Event e) {
		// TODO Auto-generated method stub
		Vector3 f = camera.getUp(),
		        u = camera.getRt(),
		        n = camera.getFd();
		
		if(camera.getMode() == 'n')
		{
			if(e.getValue() < -0.1)
			{
				Angle rotAmt = Degreef.createFrom(0.2f);
				game.getNode().pitch(rotAmt);
			}
			else{
				Angle rotAmt = Degreef.createFrom(-0.2f);
				game.getNode().pitch(rotAmt);
			}
		}
		else{
			if(e.getValue() < -0.1)
			{
				Angle rotAmt = Degreef.createFrom(10.0f);	
				Vector3 nU = f.rotate(rotAmt, u),
						fN = n.rotate(rotAmt, u);
				camera.setUp((Vector3f) nU);
				camera.setFd((Vector3f) fN);
			}
			else
			{
				Angle rotAmt = Degreef.createFrom(-10.0f);
				Vector3 nU = f.rotate(rotAmt, u),
						fN = n.rotate(rotAmt, u);
				camera.setUp((Vector3f) nU);
				camera.setFd((Vector3f) fN);
			}
		}
		 
	}

}
*/