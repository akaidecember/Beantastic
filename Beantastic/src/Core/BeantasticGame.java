package Core;

import static ray.rage.scene.SkeletalEntity.EndType.LOOP;

import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.*;
import java.util.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
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
import ray.networking.IGameConnection.ProtocolType;
import ray.physics.PhysicsObject;
import ray.physics.PhysicsEngine;
import ray.physics.PhysicsEngineFactory;
import ray.rage.rendersystem.shader.*;
import ray.rage.util.*;
import GameEngine.*;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import ray.audio.*;
import com.jogamp.openal.ALFactory;

//Class declaration for BeantasticGame
public class BeantasticGame extends VariableFrameRateGame {

	GL4RenderSystem rs;
	float elapsTime = 0.0f;
	String elapsTimeStr, inputName, hud;
    int elapsTimeSec, counter = 0;
    
    //Private variables for the class BeantasticGame-----------------------------------------------------------------------------------------------------------------
    private InputManager im;
    private SceneManager sm;
    private Action moveForwardAction, moveBackwardAction, moveLeftAction, moveRightAction, moveCameraAction, moveDirectionAction, moveUpDownAction, rotateRightA, rotateLeftA, colorA, rotateAction, rotatePlayerAction;
    public SceneNode cameraNode;
	private SceneNode gameWorldObjectsNode;
	private SceneNode playerObjectNode, manualObjectsNode, shipObjectNode, oreObjectNode, crystalObjectNode;
    private Camera3pController playerController;	
    private static final String SKYBOX_NAME = "SkyBox";
    private boolean skyBoxVisible = true;
    
	//server variables---
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isClientConnected;
	private Vector<UUID> gameObjectsToRemove;
    
    //Physics variables
	private SceneNode ball1Node, ball2Node, groundNode;
    private SceneNode cameraPositionNode;
    private final static String GROUND_E = "Ground";
    private final static String GROUND_N = "GroundNode";
    private PhysicsEngine physicsEng; 
    private PhysicsObject ball1PhysObj, ball2PhysObj, gndPlaneP;
    
    //Animation variables
    private boolean running = false;
    private boolean walkB, idleB;														//Animation
	
    //Sound variables
    private IAudioManager audioManager;  
    private Sound stepSound, bgSound, sparkSound;
	
    //Public variables for the class BeantasticGame------------------------------------------------------------------------------------------------------------------
    public Camera camera;
    public SceneNode playerNode, shipNode, oreNode, crystalNode;										

    //Protected Variables--------------------------------------------------------------------------------------------------------------------------------------------
    
    //script variables----
    protected ScriptEngine jsEngine;
    protected File scriptFile;
    
    //Constructor for the class BeantasticGame
    public BeantasticGame(String serverAddr, int sPort) {
    	
        super();
        this.serverAddress = serverAddr;
        this.serverPort = sPort;
        this.serverProtocol = ProtocolType.UDP;
        isClientConnected = false;
        
        walkB=false;
        idleB=false;
        
    }
    
    //Main function for the game
    public static void main(String[] args) {
    	
        Game game = new BeantasticGame(args[0], Integer.parseInt(args[1]));
        
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
    
    //Game implementation starts here------------------------------------------------------------------------------------------------------------------------------------------
    
	//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
	//Code for setting up the windows, cameras, scenes, objects, textures for the game-----------------------------------------------------------------------------------------
	//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
    
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
    
	private void setupOrbitCameras(Engine eng, SceneManager sm) {
		
		String msName = im.getMouseName();
		playerController = new Camera3pController(camera, cameraNode, playerNode, msName, im, this);
		
	}
	
    @Override
    protected void setupScene(Engine eng, SceneManager sm) throws IOException {
    	//physics demonstration
    	SceneNode rootNode = sm.getRootSceneNode();
    	
    	Entity ball1Entity = sm.createEntity("ball1", "dolphinHighPoly.obj"); 
    	ball1Node = rootNode.createChildSceneNode("Ball1Node");
    	ball1Node.attachObject(ball1Entity);
    	ball1Node.setLocalPosition(0, 2, -2);
    	ball1Node.setLocalScale(1f, 1f, 1f);

    	Entity groundEntity = sm.createEntity(GROUND_E, "cube.obj");
    	groundNode = rootNode.createChildSceneNode(GROUND_N);
    	groundNode.attachObject(groundEntity);
    	groundNode.setLocalPosition(0, -2, -2);

    	im = new GenericInputManager();		
    	setupNetworking();
    	//Initializing the input manager
    	getInput();																									//Determine the type of input device
        gameWorldObjectsNode = sm.getRootSceneNode().createChildSceneNode("GameWorldObjectsNode");			        //Initializing the gameWorldObjects Scene Node
        manualObjectsNode = gameWorldObjectsNode.createChildSceneNode("ManualObjectsNode");							//Initializing the manualObjects scene node 
        
		//Creating the player node to add in the game, upgrade from last only entity approach
		playerObjectNode = gameWorldObjectsNode.createChildSceneNode("PlayerNode");
        //Creating a player
        //Entity playerEntity = sm.createEntity("myPlayer", "astro.obj");
        //playerEntity.setPrimitive(Primitive.TRIANGLES);
		SkeletalEntity playerEntity = sm.createSkeletalEntity("myPlayer", "astroRig.rkm", "astro.rks");
        playerNode = playerObjectNode.createChildSceneNode(playerEntity.getName() + "Node");
        playerNode.attachObject(playerEntity);
        playerNode.setLocalScale(.06f, .06f, .06f);
        //player texture
        TextureManager tmd1 = eng.getTextureManager();
        Texture assetd1 = tmd1.getAssetByPath("astroTex.png");
        RenderSystem rsd1 = sm.getRenderSystem();
        TextureState stated1 =  (TextureState) rsd1.createRenderState(RenderState.Type.TEXTURE);
        stated1.setTexture(assetd1);
        playerEntity.setRenderState(stated1);
        playerNode.yaw(Degreef.createFrom(180.0f));
        
        //animations----
        playerEntity.loadAnimation("walk", "walk2.rka");
        //Idle is not used currently
        //playerEntity.loadAnimation("idle", "idle.rka");
        
        //spaceship----
		shipObjectNode = (SceneNode) gameWorldObjectsNode.createChildNode("shipNode");
        Entity shipEntity = sm.createEntity("myShip", "spaceship.obj");
        shipEntity.setPrimitive(Primitive.TRIANGLES);
        shipNode = shipObjectNode.createChildSceneNode(shipEntity.getName() + "Node");
        shipNode.attachObject(shipEntity);
        shipNode.setLocalPosition(0, 0, 0);
        
        TextureManager shipTM = eng.getTextureManager();
        //change cube.png is spaceship texture
        Texture shipA = shipTM.getAssetByPath("cube.png");
        RenderSystem shipR = sm.getRenderSystem();
        TextureState shipS = (TextureState) shipR.createRenderState(RenderState.Type.TEXTURE);
        shipS.setTexture(shipA);
        shipEntity.setRenderState(shipS);
        
        //ores----
		oreObjectNode = (SceneNode) gameWorldObjectsNode.createChildNode("oreNode");
        Entity oreEntity = sm.createEntity("myOre", "ore.obj");
        oreEntity.setPrimitive(Primitive.TRIANGLES);
        oreNode = oreObjectNode.createChildSceneNode(oreEntity.getName() + "Node");
        oreNode.attachObject(oreEntity);
        oreNode.setLocalPosition(-5, 0, -5);
        oreNode.setLocalScale(0.05f, 0.05f, 0.05f);
        RotationController rcOre = new RotationController(Vector3f.createUnitVectorY(), .1f);						//Rotation for the ore model in the game
        rcOre.addNode(oreNode);
        sm.addController(rcOre);
        
        //crystals----
		crystalObjectNode = (SceneNode) gameWorldObjectsNode.createChildNode("crystalNode");
        Entity crystalEntity = sm.createEntity("myCrystal", "crystal.obj");
        crystalEntity.setPrimitive(Primitive.TRIANGLES);
        crystalNode = crystalObjectNode.createChildSceneNode(crystalEntity.getName() + "Node");
        crystalNode.attachObject(crystalEntity);
        crystalNode.setLocalPosition(-3, 0, -3);
        crystalNode.setLocalScale(0.1f, 0.1f, 0.1f);
        
        // Set up Lights----
        sm.getAmbientLight().setIntensity(new Color(.3f, .3f, .3f));
		Light plight = sm.createLight("testLamp1", Light.Type.POINT);
		plight.setAmbient(new Color(.2f, .2f, .2f));
        plight.setDiffuse(new Color(.9f, .9f, .9f));
		plight.setSpecular(new Color(.9f, 1.0f, .05f));
        plight.setRange(1.75f);
        
		SceneNode plightNode = sm.getRootSceneNode().createChildSceneNode("plightNode");
        plightNode.attachObject(plight);
        playerEntity.getParentSceneNode().attachObject(plight);
        
        //Setting up sky box   
        Configuration conf = eng.getConfiguration();
		TextureManager tm= getEngine().getTextureManager();
		tm.setBaseDirectoryPath(conf.valueOf("assets.skyboxes.path"));
		Texture front = tm.getAssetByPath("front.png");
		Texture back = tm.getAssetByPath("back.png");
		Texture left = tm.getAssetByPath("left.png");
		Texture right = tm.getAssetByPath("right.png");
		Texture top = tm.getAssetByPath("top.png");
		Texture bottom = tm.getAssetByPath("bot.png");
		tm.setBaseDirectoryPath(conf.valueOf("assets.textures.path"));
		
		AffineTransform xform = new AffineTransform();        
		xform.translate(0, front.getImage().getHeight());       
		xform.scale(1d, -1d);
		
		front.transform(xform);
		back.transform(xform);
		left.transform(xform);
		right.transform(xform);
		top.transform(xform);
		bottom.transform(xform);

		SkyBox sb = sm.createSkyBox(SKYBOX_NAME);        
		sb.setTexture(front, SkyBox.Face.FRONT);        
		sb.setTexture(back, SkyBox.Face.BACK);        
		sb.setTexture(left, SkyBox.Face.LEFT);        
		sb.setTexture(right, SkyBox.Face.RIGHT);        
		sb.setTexture(top, SkyBox.Face.TOP);        
		sb.setTexture(bottom, SkyBox.Face.BOTTOM);        
		sm.setActiveSkyBox(sb);
		
		//Terrain
		Tessellation tessE = sm.createTessellation("tessE", 6);
		tessE.setSubdivisions(8f);
		SceneNode tessN = (SceneNode) sm.getRootSceneNode().createChildNode("TessN");
		tessN.attachObject(tessE);	
		tessN.scale(15, 31, 15);
		tessN.setLocalPosition(-1, -1, -5);
		tessE.setHeightMap(this.getEngine(), "tn.png");
		tessE.setTexture(this.getEngine(), "moon.jpeg");
		
		//TESTING professor's script
		//Prepare script engine
		ScriptEngineManager factory = new ScriptEngineManager();
		java.util.List<ScriptEngineFactory> list = factory.getEngineFactories();
		jsEngine = factory.getEngineByName("js");
		
		//pressing SPACE light CHANGES TEST
		scriptFile = new File("UpdateLightColor.js");
		
		this.runScript(scriptFile);
		im = new GenericInputManager();
		String kbName = im.getKeyboardName();
		//colorAction = new ColorAction(sm);
		
		initPhysicsSystem();
    	createRagePhysicsWorld();
		setupOrbitCameras(eng,sm);
        setupInputs(sm);																								//Calling the function to setup the inputs
        initAudio(sm);
        
    }
	
	//*****end of setting up the windows, cameras, scenes, objects, textures for the game*****
	
    
    //0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
	//Physics method for the game----------------------------------------------------------------------------------------------------------------------------------------------
	//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
    
    private void initPhysicsSystem() {
		// TODO Auto-generated method stub
		String engine = "ray.physics.JBullet.JBulletPhysicsEngine";
		float[] gravity = {0, -3f, 0};
		physicsEng = PhysicsEngineFactory.createPhysicsEngine(engine);
		physicsEng.initSystem();
		physicsEng.setGravity(gravity);
	}
    
    private void createRagePhysicsWorld() {
		// TODO Auto-generated method stub
		 float mass = 1.0f;
		 float up[] = {0,1,0};
		 double[] temptf; 
		 
		 temptf = toDoubleArray(ball1Node.getLocalTransform().toFloatArray());
		 ball1PhysObj = physicsEng.addSphereObject(physicsEng.nextUID(),mass, temptf, 2.0f);
		 
		 ball1PhysObj.setBounciness(1f);
		 ball1Node.setPhysicsObject(ball1PhysObj);
		 
		 //PLAYER NOT WORKING
		 /*temptf = toDoubleArray(playerNode.getLocalTransform().toFloatArray());
		 ball2PhysObj = physicsEng.addSphereObject(physicsEng.nextUID(),mass, temptf, 2.0f);
		 
		 //ball2PhysObj.setBounciness(.01f);
		 //ball2PhysObj.setFriction(1f);
		 playerNode.setPhysicsObject(ball2PhysObj);
		 */
		 temptf = toDoubleArray(groundNode.getLocalTransform().toFloatArray());
		 gndPlaneP = physicsEng.addStaticPlaneObject(physicsEng.nextUID(),temptf, up, 0.0f);
		 gndPlaneP.setBounciness(1f);
		 groundNode.scale(3f, .05f, 3f);
		 groundNode.setLocalPosition(0, -7, -2);
		 groundNode.setPhysicsObject(gndPlaneP);
		
	}
    
    private double[] toDoubleArray(float[] arr) {
		// TODO Auto-generated method stub
    	
		if (arr == null) 
			 return null;
		
		int n = arr.length;
		double[] ret = new double[n];
		
		for (int i = 0; i < n; i++) 
			ret[i] = (double)arr[i];
	    
		return ret;
		
	}
    
	private float[] toFloatArray(double[] arr) {
		// TODO Auto-generated method stub
		
		if (arr == null) 
			 return null;
		
		int n = arr.length;
		float[] ret = new float[n];
		
		for (int i = 0; i < n; i++)  
			ret[i] = (float)arr[i];
	    
		return ret;
		
	}
	
	//start physics by pressing space
	public void keyPressed(KeyEvent e){
		
    	switch (e.getKeyCode()){   
    	
    		case KeyEvent.VK_SPACE:System.out.println("Starting Physics!");
    			running = true;
    			break;
    			
    	} 
    	
    	super.keyPressed(e);
    	
    }
	
    //**********Physics methods END**********
	
	//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
	//Input handling for gamepad and keyboard----------------------------------------------------------------------------------------------------------------------------------
	//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000

    //Function to get the type of controller for the game
	private void getInput() {
		// TODO Auto-generated method stub
    	ArrayList<Controller> controllers = im.getControllers();						//Get the list of all the input devices available
    	
        //Error checking to check if the controllers are connected or not (ensuring the game does not crash)
        for (Controller c : controllers) 
        	inputName = c.getName();
        
	}

	//Function to setup inputs for various actions
    protected void setupInputs(SceneManager sm){
    	
    	ArrayList<Controller> controllers = im.getControllers();						//Get the list of all the input devices available
    	
    	//Initialization action gamepad
    	//"MoveForward-sAction.java " is not used
    	moveForwardAction = new MoveForwardAction(playerNode, protClient, this, true);  
        moveBackwardAction = new MoveBackwardAction(playerNode);						
        moveLeftAction = new MoveLeftAction(playerNode);								
        moveRightAction = new MoveRightAction(playerNode);	
        rotateRightA = new RotateRightAction(playerNode, camera);
        rotateLeftA = new RotateLeftAction(playerNode, camera);
        rotateAction = new RotateAction(this);
        moveDirectionAction = new MoveDirectionAction(playerNode, this);
        moveUpDownAction = new MoveUpDownAction(playerNode, this);
        rotatePlayerAction = new RotatePlayerAction(playerNode);

        //Error checking to check if the controllers are connected or not (ensuring the game does not crash)
        for (Controller c : controllers) {
        	
        	//If the controller type is keyboard, then use the keyboard controls, otherwise use the gamepad controls
            if (c.getType() == Controller.Type.KEYBOARD)
                keyboardControls(c);													//Call the keyboard Control function to handle the keyboard inputs
            else if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK)
                gamepadControls(c);														//Call the gamepad input to control the XB1 inputs
            
        }
        
    }
    
    //Function to handle the gamepad controlls
    void gamepadControls(Controller gpName) {
    	
    	//im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.POV, moveCameraAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);  
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.X, rotatePlayerAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);  
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Y, moveUpDownAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RX, rotateAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RY, rotateAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);	

    }

    //Function to handle the keyboard controls
    void keyboardControls(Controller kbName) {
   
        //im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, moveLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        //im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, moveRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.W, moveForwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.S, moveBackwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, rotateLeftA, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, rotateRightA, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        
    }
    
	//*****end of input handling*****
    
	//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
	//Networking part of the game----------------------------------------------------------------------------------------------------------------------------------------------
	//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
    
    //Function to setup networking
    private void setupNetworking() {
    	
    	gameObjectsToRemove = new Vector<UUID>();
    	isClientConnected = false;
    	
    	try {   
    		protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
    	}   
    	catch (UnknownHostException e) { 
    		e.printStackTrace();
    	}   
    	catch (IOException e) { 
    		e.printStackTrace();
    	} 
    	if (protClient == null){   
    		System.out.println("missing protocol host"); 
    	} 
    	else{ 
    		// ask client protocol to send initial join message/ /to server, with a unique identifier for this 
    		protClient.sendJoinMessage();
    	}
    	
    }
    
    //Function to process networking
    protected void processNetworking(float elapsTime) {
    	
    	if(protClient != null) 
    		protClient.processPackets();
    	
    	//Remove ghost avatars for players who have left the game
    	Iterator<UUID> it = gameObjectsToRemove.iterator();
    	
    	while(it.hasNext()) 
    		sm.destroySceneNode(it.next().toString());
    	
    	gameObjectsToRemove.clear();
    	
    }

    //Function to check if the client is connected or not
	public void setIsConnected(boolean b) {
		// TODO Auto-generated method stub
		this.isClientConnected = b;
		
	}

	//Function to add the ghost avatar of the other player
	public void addGhostAvatarToGameWorldnew(GhostAvatar avatar, Vector3 ghostPosition) throws IOException{
		// TODO Auto-generated method stub
		
		if(avatar!=null) {
			
			Entity ghostE = sm.createEntity("ghostN", "dolphinHighPoly.obj");
			ghostE.setPrimitive(Primitive.TRIANGLES);
			SceneNode ghostN = getEngine().getSceneManager().getRootSceneNode().createChildSceneNode(avatar.getID().toString());
			ghostN.attachObject(ghostE);
			ghostN.setLocalPosition(ghostPosition);
			avatar.setNode(ghostN);
			avatar.setEntity(ghostE);
			avatar.setPosition(ghostPosition.x(), ghostPosition.y(), ghostPosition.z());
			
		}
		
	}
	
	public void addGhostAvatarToGameWorldold(GhostAvatar avatar, Vector3 ghostPosition) throws IOException{
		// TODO Auto-generated method stub
		
		if(avatar!=null) {
			
			Entity ghostE = sm.createEntity("ghost0", "dolphinHighPoly.obj");
			ghostE.setPrimitive(Primitive.TRIANGLES);
			SceneNode ghostN = getEngine().getSceneManager().getRootSceneNode().createChildSceneNode(avatar.getID().toString());
			ghostN.attachObject(ghostE);
			ghostN.setLocalPosition(ghostPosition);
			avatar.setNode(ghostN);
			avatar.setEntity(ghostE);
			avatar.setPosition(ghostPosition.x(), ghostPosition.y(), ghostPosition.z());
			
		}
		
	}
	
	//Function to remove Ghost avatar
	public void removeGhostAvatarFromGameWorld(GhostAvatar avatar) {
		// TODO Auto-generated method stub
		
		if(avatar!=null)
			gameObjectsToRemove.add((UUID)avatar.getID());
		
	}
	
	//Function to communicate
	private class SendCloseConnectionPacketAction extends AbstractInputAction{
		
		// for leaving the game... need to attach to an input device
    	@Override
    	public void performAction(float arg0, net.java.games.input.Event arg1){
    	 
    		if(protClient != null && isClientConnected == true) 
    			protClient.sendByeMessage();
    		
    	} 
    	
	}
	
	//*****end of networking functionalities*****
	
	//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
	//Getter and setter functions----------------------------------------------------------------------------------------------------------------------------------------------
	//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
	
	//Function to return the camera Elevation
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
	 
    //Function to return the position of the player
    public Vector3 getPlayerPosition() {
    	
    	SceneNode playerN = sm.getSceneNode("playerNode");
    	return playerN.getWorldPosition();
    	
    }
    
	//*****end of getter & setter methods*****
    
	//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
	//Update function for the game---------------------------------------------------------------------------------------------------------------------------------------------
	//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
    
    //Function to keep updating all the gameWorld variables and states after each iteration
    public void setWalkTrue(boolean animW)
    {
    	walkB = animW;
    }
    public void setWalkFalse()
    {
    	walkB = false;
    }
    public void setIdleTrue(boolean animI)
    {
    	idleB = animI;
    }
    public void setIdleFalse()
    {
    	idleB = false;
    }
    
    private void doTheWalk() {
    	
    	SkeletalEntity playerEntity = (SkeletalEntity) getEngine().getSceneManager().getEntity("myPlayer");
    	playerEntity.stopAnimation();
    	playerEntity.playAnimation("walk", 0.75f, LOOP, 0);
    	
    }
    
    private void Idle() {
    	
    	SkeletalEntity playerEntity = (SkeletalEntity) getEngine().getSceneManager().getEntity("myPlayer");
    	playerEntity.stopAnimation();
    	playerEntity.playAnimation("idle", .9f, LOOP, 0);
    	

    }
    
    private void doTheStop() {
    	
    	SkeletalEntity playerEntity = (SkeletalEntity) getEngine().getSceneManager().getEntity("myPlayer");
    	playerEntity.stopAnimation();
    	
    }
    
    @Override
    protected void update(Engine engine) {
    	
		// build and set HUD
		rs = (GL4RenderSystem) engine.getRenderSystem();
		elapsTime += engine.getElapsedTimeMillis();
		elapsTimeSec = Math.round(elapsTime/1000.0f);
		elapsTimeStr = Integer.toString(elapsTimeSec);
		im.update(elapsTime);	
		playerController.updateCameraPosition();
		processNetworking(elapsTime);
		//physics
		if(running) {
			Matrix4 mat;
			physicsEng.update(elapsTime);
			for(SceneNode s: engine.getSceneManager().getSceneNodes()){
				if(s.getPhysicsObject()!=null) {
					mat = Matrix4f.createFrom(toFloatArray(s.getPhysicsObject().getTransform()));
					s.setLocalPosition(mat.value(0, 3), mat.value(1, 3), mat.value(2, 3));
				}
			}
		}
		//animations
		SkeletalEntity playerEntity = (SkeletalEntity) engine.getSceneManager().getEntity("myPlayer");
    	playerEntity.update();
		if(!walkB){
    		doTheWalk();
        }
        else{
        	setWalkFalse();
        }
		
		//Updating the sound variables with each gameEngine cycle
		SceneManager sm = engine.getSceneManager();
		SceneNode playerNode = sm.getSceneNode("myPlayerNode"), shipNode = sm.getSceneNode("myShipNode");		
		//stepSound.setLocation(tempNode.getWorldPosition());
		bgSound.setLocation(playerNode.getWorldPosition());
		sparkSound.setLocation(shipNode.getWorldPosition());
		setEarParameters(sm);
		
		//Update the input manager with elapsed time
		im.update(elapsTime);	
		
	}
    
	//*****end of update function*****
        
	//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
	//Scripting Functionalities for the game-----------------------------------------------------------------------------------------------------------------------------------
	//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
    
    //Function to run the script file
    private void runScript(File scriptFile) {
    	
    	 try{ 
    		 FileReader fileReader = new FileReader(scriptFile);      
    		 jsEngine.eval(fileReader);      
    		 fileReader.close();   
    	 }
    	 catch (FileNotFoundException e1){ 
    		 System.out.println(scriptFile + " not found " + e1); 
    	 }
    	 catch (IOException e2){ 
    		 System.out.println(scriptFile + " not found " + e2); 
    	 }
    	 catch (ScriptException e3){ 
    		 System.out.println(scriptFile + " not found " + e3); 
    	 }
    	 catch (NullPointerException e4){ 
    		 System.out.println(scriptFile + " not found " + e4); 
    	 }
    	 
    }
    
	//*****end of scripting Functionalities*****

	//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
	//Sound functionalities for the game---------------------------------------------------------------------------------------------------------------------------------------
	//0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000

    //Function to initialize the audio
    public void initAudio(SceneManager sm) {
    	
    	AudioResource  resource1, resource2, resource3;
    	audioManager = AudioManagerFactory.createAudioManager("ray.audio.joal.JOALAudioManager");
    	
    	if(!audioManager.initialize()) {

    		System.out.println("Audio manager failed to initialize");
    		return;
    				
    	}
    	
    	//Setting the resources with .wav files for the game
    	//resource1 =  audioManager.createAudioResource("Sounds/step.wav", AudioResourceType.AUDIO_SAMPLE);
    	resource2 = audioManager.createAudioResource("Sounds/Background.wav", AudioResourceType.AUDIO_SAMPLE);
    	resource3 = audioManager.createAudioResource("Sounds/sparks.wav", AudioResourceType.AUDIO_SAMPLE);

    	
    	//Setting attributes for the sound
		/*
		 * stepSound = new Sound(resource1, SoundType.SOUND_EFFECT, 100, true);
		 * stepSound.initialize(audioManager); 
		 * stepSound.setMaxDistance(10.0f);
		 * stepSound.setMinDistance(0.5f); 
		 * stepSound.setRollOff(5.0f);
		 */
    	
    	bgSound = new Sound(resource2, SoundType.SOUND_MUSIC, 100, true);
    	bgSound.initialize(audioManager);
    	
		sparkSound = new Sound(resource3, SoundType.SOUND_EFFECT, 100, true);
		sparkSound.initialize(audioManager); 
		sparkSound.setMaxDistance(2.0f);
		sparkSound.setMinDistance(0.5f); 
		sparkSound.setRollOff(1.2f);
    	
    	//Attaching the sounds to the player
    	SceneNode playerNode = sm.getSceneNode("myPlayerNode");
    	//stepSound.setLocation(playerNode.getWorldPosition());
    	bgSound.setLocation(playerNode.getWorldPosition());
    	
    	//Attaching the sounds to the space ship
    	SceneNode spaceShip = sm.getSceneNode("myShipNode");
    	sparkSound.setLocation(spaceShip.getWorldPosition());
    	
    	//Setting the ear parameters for the player
    	setEarParameters(sm);
    	
    	//Playing the sounds
    	//stepSound.play();
    	bgSound.play();
    	
    }
    
    //Function to set the ear parameters for the player
    public void setEarParameters(SceneManager sm) {
    	
    	SceneNode playerNode = sm.getSceneNode("myPlayerNode");
    	Vector3 avDir = playerNode.getWorldForwardAxis();
    	audioManager.getEar().setLocation(playerNode.getWorldPosition());
    	audioManager.getEar().setOrientation(avDir, Vector3f.createFrom(0,1,0));
    	
    }
    
    
	//*****end of sound Functionalities*****
}

