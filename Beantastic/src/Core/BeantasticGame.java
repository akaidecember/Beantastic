package Core;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import ray.rage.*;
import ray.rage.game.*;
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
import net.java.games.input.Controller;

//Class declaration for BeantasticGame
public class BeantasticGame extends VariableFrameRateGame {

	GL4RenderSystem rs;
	float elapsTime = 0.0f;
	String elapsTimeStr, inputName, hud;
    int elapsTimeSec, counter = 0;
    
    //Private variables for the class BeantasticGame-----------------------------------------------------------------------
    private InputManager im;
    private Action moveForwardAction, moveBackwardAction, moveLeftAction, moveRightAction, moveCameraAction, moveDirectionAction, moveUpDownAction, rotateAction, rotatePlayerLeftAction, rotatePlayerRightAction;
    private SceneNode cameraNode, gameWorldObjectsNode, playerObjectNode;
    private Camera3pController playerController;	
    
    //Public variables for the class BeantasticGame------------------------------------------------------------------------
    public Camera camera;
    public SceneNode playerNode;										
   
    //Constructor for the class BeantasticGame
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
    
	@Override
	protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) {
		
		rs.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
		
	}

    @Override
    protected void setupCameras(SceneManager sm, RenderWindow rw) {
    	
    	SceneNode rootNode = sm.getRootSceneNode();
    	
    	camera = sm.createCamera("MainCamera", Projection.PERSPECTIVE);
    	rw.getViewport(0).setCamera(camera);
    	cameraNode = rootNode.createChildSceneNode("MainCameraNode");
    	cameraNode.attachObject(camera);
    	camera.setMode('n');
    	camera.getFrustum().setFarClipDistance(1000.0f);
    	
    }
	
    @Override
    protected void setupScene(Engine eng, SceneManager sm) throws IOException {
    	
    	im = new GenericInputManager();																				//Initializing the input manager
    	
        setupInputs(sm);																								//Calling the function to setup the inputs

        gameWorldObjectsNode = sm.getRootSceneNode().createChildSceneNode("GameWorldObjectsNode");			        //Initializing the gameWorldObjects Scene Node
        
		
		//Creating the player node to add in the game, upgrade from last only entity approach
		playerObjectNode = gameWorldObjectsNode.createChildSceneNode("PlayerNode");
		
        //Creating a player
        Entity playerEntity = sm.createEntity("myPlayer", "bean.obj");
        playerEntity.setPrimitive(Primitive.TRIANGLES);
        playerNode = playerObjectNode.createChildSceneNode(playerEntity.getName() + "Node");
        playerNode.attachObject(playerEntity);
                
        //Setting up the orbit controllers for the player
        playerController = new Camera3pController(camera, cameraNode, playerNode, inputName, im);														
        playerNode.yaw(Degreef.createFrom(180.0f));
        
        // Set up Lights
        sm.getAmbientLight().setIntensity(new Color(.3f, .3f, .3f));
		Light plight = sm.createLight("testLamp1", Light.Type.POINT);
		plight.setAmbient(new Color(.1f, .1f, .1f));
        plight.setDiffuse(new Color(.7f, .7f, .7f));
		plight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
        plight.setRange(5f);
        
		SceneNode plightNode = sm.getRootSceneNode().createChildSceneNode("plightNode");
        plightNode.attachObject(plight);
      
        StretchController sc = new StretchController();
        sc.addNode(playerNode);
        sm.addController(sc);
       
    }


	//Function to setup inputs for various actions
    protected void setupInputs(SceneManager sm){ 

    	ArrayList<Controller> controllers = im.getControllers();						//Get the list of all the input devices available
    	
    	//Initialization action keyboard
    	moveCameraAction = new MoveCameraAction(this);
    	moveDirectionAction = new MoveDirectionAction(playerNode, this);
    	moveUpDownAction = new MoveUpDownAction(playerNode, this);
    	rotateAction = new RotateAction(this);
    	//Initialization action gamepad
        moveForwardAction = new MoveForwardsAction(playerNode, this);						//camera forward
        moveBackwardAction = new MoveBackwardsAction(playerNode, this);						//camera backward
        moveLeftAction = new MoveLeftAction(playerNode, this);								//camera left
        moveRightAction = new MoveRightAction(playerNode, this);								//camera right
        rotatePlayerLeftAction = new RotateLeftAction(playerNode, this);				//Rotate the dolphin left
        rotatePlayerRightAction = new RotateRightAction(playerNode, this);			//Rotate the dolphin right

        //Error checking to check if the controllers are connected or not (ensuring the game does not crash)
        for (Controller c : controllers) {
        	
        	inputName = c.getName();
        	//If the controller type is keyboard, then use the keyboard controls, otherwise use the gamepad controls
            if (c.getType() == Controller.Type.KEYBOARD)
                keyboardControls(c);													//Call the keyboard Control function to handle the keyboard inputs
            else if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK)
                gamepadControls(c);														//Call the gamepad input to control the XB1 inputs
            
        }
        
    }
    
    //Function to handle the gamepad controlls
    void gamepadControls(Controller gpName) {
    	
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.POV, moveCameraAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);  
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.X, moveDirectionAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);  
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Y, moveUpDownAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RX, rotateAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

    }

    //Function to handle the keyboard controls
    void keyboardControls(Controller kbName) {
   
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, moveLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, moveRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.W, moveForwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.S, moveBackwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.Q, rotatePlayerLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.E, rotatePlayerRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        
    }

    @Override
    protected void update(Engine engine) {
    	
		// build and set HUD
		rs = (GL4RenderSystem) engine.getRenderSystem();
		elapsTime += engine.getElapsedTimeMillis();
		elapsTimeSec = Math.round(elapsTime/1000.0f);
		elapsTimeStr = Integer.toString(elapsTimeSec);
		hud = "Time = " + elapsTimeStr ;
		rs.setHUD(hud, 15, 15);
		
		im.update(elapsTime);																			//Error here, don't forget to include
		
	}
    
	//Getter and setter functions
    
	public void setCameraElevationAngle(float newAngle) {
		
		this.playerController.setCameraElevationAngle(newAngle);
		
	}

	public float getCameraElevationAngle() {
		
		return this.playerController.getCameraElevationAngle();
		
	}

	public void setRadius(float r) {
		
		this.playerController.setRadius(r);
		
	}

	public float getRadius() {
		
		return this.playerController.getRadius();
		
	}

	public void setCameraAzimuthAngle(float newAngle) {
		
		this.playerController.setAzimuth(newAngle);
		
	}

	public float getCameraAzimuthAngle() {
		
		return this.playerController.getAzimuth();
		
	}

}



