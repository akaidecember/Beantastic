/*package GameEngine;
import javax.script.Invocable;
import javax.script.ScriptException;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Light;
import ray.rage.scene.SceneManager;

public class ColorAction extends AbstractInputAction{
    	private SceneManager sm;
		public ColorAction(SceneManager sm2) {
			// TODO Auto-generated constructor stub
			sm2 = sm;
		}
		@Override
		public void performAction(float time, Event e) {
			// TODO Auto-generated method stub
			Invocable invocableEngine = (Invocable) jsEngine ;
			Light lgt = sm.getLight("testLamp1");
			try{   
				invocableEngine.invokeFunction("updateAmbientColor", lgt); 
			}
			catch (ScriptException e1)  {   
				System.out.println("ScriptException in " + scriptFile + e1); 
		    }
			catch (NoSuchMethodException e2){   
				System.out.println("No such method in " + scriptFile + e2); 
			}
			catch (NullPointerException e3){ 
				System.out.println ("Null ptr exception reading " + scriptFile + e3); 
				}
		}
    	
    }*/