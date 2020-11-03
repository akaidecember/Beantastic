package Core;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import ray.rage.*;
import ray.rage.game.*;
import ray.rage.game.Game;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.scene.controllers.*;
import ray.rml.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;

import ray.rage.rendersystem.states.*;
import ray.rage.asset.texture.*;
import ray.input.*;
import ray.input.action.*;

import ray.rage.rendersystem.shader.*;
import ray.rage.util.*;
import GameEngine.*;
import myGameEngine.MoveBackwardsAction;
import myGameEngine.MoveDownwardsAction;
import myGameEngine.MoveForwardsAction;
import myGameEngine.MoveLeftAction;
import myGameEngine.MoveRightAction;
import myGameEngine.MoveUpwardsAction;
import myGameEngine.PanLeftRightAction;
import myGameEngine.PitchUpDownAction;
import myGameEngine.RideToggleAction;
import myGameEngine.RotateCameraDownAction;
import myGameEngine.RotateCameraLeftAction;
import myGameEngine.RotateCameraRightAction;
import myGameEngine.RotateCameraUpAction;
import myGameEngine.cameraXAxisAction;
import myGameEngine.cameraYAxisAction;
import net.java.games.input.Controller;

public class BeantasticGame extends VariableFrameRateGame{
	
	//Variables for the class Game-------------------------------------------------------------------
	
	//Private variables-----
	GL4RenderSystem rs;
	float elapsTime = 0.0f;
	private InputManager im;
    private Action quitGameAction, moveForwardAction, moveBackwardAction, moveLeftAction, moveRightAction, moveUpAction, moveDownAction, rotateRightAction, rotateLeftAction, rotateUpAction, rotateDownAction, cameraY, cameraX, cameraPitch, cameraPan;
    private SceneNode cameraNode;
    
	//Public variables------
    public Camera camera;
    public SceneNode playerNode;
	
	
	//Functions for the Game-------------------------------------------------------------------------
	
	//Constructor for the class Game
	public BeantasticGame() {
		
		super();
		
	}
	
    //Main function for the program/game
    public static void main(String[] args) {
    	
        Game game = new BeantasticGame();
        
        try {
        	
            game.startup();
            game.run();
            
        } catch (Exception e) {
        	
            e.printStackTrace(System.err);
            
        } finally {
        	
            game.shutdown();
            game.exit();
            
        }
        
    }
    
    //Function to setup the window of the game
	@Override
	protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) {
		
		rs.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
		
	}
	
	//Function to setup the Cameras of the game
	@Override
	protected void setupCameras(SceneManager sm, RenderWindow rw) {
		// TODO Auto-generated method stub
        SceneNode rootNode = sm.getRootSceneNode();
        camera = sm.createCamera("MainCamera", Projection.PERSPECTIVE);
        rw.getViewport(0).setCamera(camera);
		camera.setRt((Vector3f)Vector3f.createFrom(1.0f, 0.0f, 0.0f));
		camera.setUp((Vector3f)Vector3f.createFrom(0.0f, 1.0f, 0.0f));
		camera.setFd((Vector3f)Vector3f.createFrom(0.0f, 0.0f, -1.0f));
        camera.setPo((Vector3f)Vector3f.createFrom(0.0f, 0.0f, 0.0f));
        cameraNode = rootNode.createChildSceneNode(camera.getName() + "Node");
        cameraNode.attachObject(camera);
        
	}

	//Function to setup the gameScene
	@Override
	protected void setupScene(Engine arg0, SceneManager arg1) throws IOException {
		// TODO Auto-generated method stub
		
	}

	//Function to update the game variables and data
	@Override
	protected void update(Engine arg0) {
		// TODO Auto-generated method stub
		
	}
	
	//Function to initialize the game inputs
	protected void setupInputs() {
		
        im = new GenericInputManager();
        //Get the list of all the input devices available
        ArrayList<Controller> controllers = im.getControllers();
        
        moveForwardAction = new MoveForwardsAction(camera, this);						//camera forward
        moveBackwardAction = new MoveBackwardsAction(camera, this);						//camera backward
        moveLeftAction = new MoveLeftAction(camera, this);								//camera left
        moveRightAction = new MoveRightAction(camera, this);							//camera right
        moveUpAction = new MoveUpwardsAction(camera, this);								//camera upwards
        moveDownAction = new MoveDownwardsAction(camera, this);							//camera downwards
        rotateRightAction = new RotateCameraRightAction(this);							//Pan right
        rotateLeftAction = new RotateCameraLeftAction(this);							//Pan left
        rotateUpAction = new RotateCameraUpAction(this);								//Pitch up
        rotateDownAction = new RotateCameraDownAction(this);							//Pitch Down
        cameraY = new cameraYAxisAction(camera, this);									//Camera Y axis for XB1 controller
        cameraX = new cameraXAxisAction(camera, this);									//Camera X axis for XB1 controller
        cameraPitch = new PitchUpDownAction(this);										//Camera pitch up and down action for XB1 controller 
        cameraPan = new PanLeftRightAction(this);										//Camera pan left and right action for XB1 controller

        //Error checking to check if the controllers are connected or not (ensuring the game does not crash)
        for (Controller c : controllers) {
        	
        	//If the controller type is keyboard, then use the keyboard controls, otherwise use the gamepad controls
            if (c.getType() == Controller.Type.KEYBOARD)
                keyboardControls(c);													//Call the keyboard =Control function to handle the keyboard inputs
            else if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK)
                gamepadControls(c);														//Call the gamepad input to control the XB1 inputs
            
        }
		
	}
	
    //Function to handle the gamepad controlls
    void gamepadControls(Controller gpName) {
    	
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Y, cameraY, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);  
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.X, cameraX, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RX, cameraPan, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RY, cameraPitch, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._4, moveUpAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);  
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._5, moveDownAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);  

    }

    //Function to handle the keyboard controls
    void keyboardControls(Controller kbName) {
		
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, moveLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, moveRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.W, moveForwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.S, moveBackwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.LEFT, rotateLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.RIGHT, rotateRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.DOWN, rotateUpAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.UP, rotateDownAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.Q, quitGameAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        
    }
	
	
}
