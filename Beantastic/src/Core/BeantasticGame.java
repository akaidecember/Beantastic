package Core;

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
import ray.rage.rendersystem.shader.*;
import ray.rage.util.*;
import GameEngine.*;
import net.java.games.input.Controller;
import net.java.games.input.Event;

//Class declaration for BeantasticGame
public class BeantasticGame extends VariableFrameRateGame {

	GL4RenderSystem rs;
	float elapsTime = 0.0f;
	String elapsTimeStr, inputName, hud;
    int elapsTimeSec, counter = 0;
    
    //Private variables for the class BeantasticGame-----------------------------------------------------------------------
    private InputManager im;
    private Action moveForwardAction, moveBackwardAction, moveLeftAction, moveRightAction, moveCameraAction, moveDirectionAction, moveUpDownAction, rotateRightA, rotateLeftA, colorA;
    public SceneNode cameraNode;
	private SceneNode gameWorldObjectsNode;
	private SceneNode playerObjectNode, manualObjectsNode;
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
    
    //Public variables for the class BeantasticGame------------------------------------------------------------------------
    public Camera camera;
    public SceneNode playerNode;										
    private SceneManager sm;
    
    //script variables
    protected ScriptEngine jsEngine;
    //public ColorAction colorAction;
    protected File scriptFile;
    
    //Constructor for the class BeantasticGame
    public BeantasticGame(String serverAddr, int sPort) {
    	
        super();
        this.serverAddress = serverAddr;
        this.serverPort = sPort;
        this.serverProtocol = ProtocolType.UDP;
        isClientConnected=false;
        
    }
    
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
    //Color action class TEST----------
    
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
    
    protected void processNetworking(float elapsTime) {
    	if(protClient != null) {
    		protClient.processPackets();
    	}
    	//Remove ghost avatars for players who have left the game
    	Iterator<UUID> it = gameObjectsToRemove.iterator();
    	while(it.hasNext()) {
    		sm.destroySceneNode(it.next().toString());
    	}
    	gameObjectsToRemove.clear();
    }
    public Vector3 getPlayerPosition() {
    	SceneNode playerN = sm.getSceneNode("playerNode");
    	return playerN.getWorldPosition();
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
	private void setupOrbitCameras(Engine eng, SceneManager sm) {
		
		SceneNode playerN = sm.getSceneNode("PlayerNode");
		SceneNode cameraN = sm.getSceneNode("MainCameraNode");
		Camera camera = sm.getCamera("MainCamera");
		String msName = im.getMouseName();
		playerController = new Camera3pController(camera, cameraN, playerN, msName, im);
	}
    @Override
    protected void setupScene(Engine eng, SceneManager sm) throws IOException {
    	
    	im = new GenericInputManager();		
    	setupNetworking();
    	//Initializing the input manager
    	getInput();																									//Determine the type of input device
        gameWorldObjectsNode = sm.getRootSceneNode().createChildSceneNode("GameWorldObjectsNode");			        //Initializing the gameWorldObjects Scene Node
        manualObjectsNode = gameWorldObjectsNode.createChildSceneNode("ManualObjectsNode");							//Initializing the manualObjects scene node 
        
        //Make two triangles for the floor/ground of the game------------------------------------------------------------------------------------------------------
        //----Triangle 1 of 2----
        ManualObject triangle1 = sm.createManualObject("triangle1");	
        SceneNode triangle1Node = manualObjectsNode.createChildSceneNode("Triangle1Node");		
        triangle1Node.scale(1000.0f, 0.05f, 1000.0f);
        triangle1Node.moveDown(2.0f);
        ManualObjectSection triangle1Sec = triangle1.createManualSection("triangle1Sec");
        triangle1.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        
        //Setting the coordinates
		float[] vertices1 = new float[] { 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // front top
				1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, -1.0f, 1.0f, 						// front bottom
				0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 						// right top
				0.0f, 0.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, 						// right bottom
				0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 						// left top
				0.0f, 0.0f, -1.0f, 0.0f, -1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 						// left bottom
				0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 							// top
				0.0f, -1.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 1.0f 						// bottom
		};

		float[] texture1 = new float[] { 0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 					// front top
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 											// front bottom
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 											// right top
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 											// right bottom
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 											// left top
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 											// left bottom
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 											// top
				1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f 												// bottom

		}; 										

		float[] normals1 = new float[] { 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 	// front top
				0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 							// front bottom
				0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 						// right top
				0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 						// right bottom
				-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 						// left top
				-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 						// left bottom
				0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 							// top
				0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f 						// bottom

		};

		int[] indices1 = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 };
		
		FloatBuffer verticalBuffer1 = BufferUtil.directFloatBuffer(vertices1);
		FloatBuffer normalBuffer1 = BufferUtil.directFloatBuffer(normals1);
		FloatBuffer textureBuffer1 = BufferUtil.directFloatBuffer(texture1);
		IntBuffer indicesBuffer1 = BufferUtil.directIntBuffer(indices1);
		triangle1Sec.setVertexBuffer(verticalBuffer1);
		triangle1Sec.setNormalsBuffer(normalBuffer1);
		triangle1Sec.setTextureCoordsBuffer(textureBuffer1);
		triangle1Sec.setIndexBuffer(indicesBuffer1);
		Texture textureGround1 = eng.getTextureManager().getAssetByPath("red.jpeg");				//Texture file for the ground
		TextureState textureGroundState1 = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		textureGroundState1.setTexture(textureGround1);
		FrontFaceState faceState1 = (FrontFaceState)sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);
		triangle1.setDataSource(DataSource.INDEX_BUFFER);
		triangle1.setRenderState(textureGroundState1);
		triangle1.setRenderState(faceState1);
		triangle1Node.attachObject(triangle1);
		
		//----Triangle 2 of 2----
        ManualObject triangle2 = sm.createManualObject("triangle2");
        SceneNode triangle2Node = manualObjectsNode.createChildSceneNode("Triangle2Node");
        triangle2Node.yaw(Degreef.createFrom(180.0f));
        triangle2Node.scale(1000.0f, 0.05f, 1000.0f);
        triangle2Node.moveDown(2.0f);
        ManualObjectSection triangle2Sec = triangle1.createManualSection("triangle2Sec");
        triangle2.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        
        //Setting the coordinates
		float[] vertices2 = new float[] { 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // front top
				1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, -1.0f, 1.0f, 						// front bottom
				0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 						// right top
				0.0f, 0.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, -1.0f, 						// right bottom
				0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 						// left top
				0.0f, 0.0f, -1.0f, 0.0f, -1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 						// left bottom
				0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 							// top
				0.0f, -1.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 1.0f 						// bottom
		};

		float[] texture2 = new float[] { 0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 					// front top
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 											// front bottom
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 											// right top
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f, 											// right bottom
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 											// left top
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 											// left bottom
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 											// top
				1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f }; 											// bottom

		float[] normals2 = new float[] { 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 	// front top
				0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 							// front bottom
				0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 						// right top
				0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f, -0.5f, 						// right bottom
				-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 						// left top
				-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 						// left bottom
				0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 							// top
				0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f 						// bottom

		};

		int[] indices2 = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 };
		
		FloatBuffer verticalBuffer2 = BufferUtil.directFloatBuffer(vertices2);
		FloatBuffer normalBuffer2 = BufferUtil.directFloatBuffer(normals2);
		FloatBuffer textureBuffer2 = BufferUtil.directFloatBuffer(texture2);
		IntBuffer indicesBuffer2 = BufferUtil.directIntBuffer(indices2);
		triangle2Sec.setVertexBuffer(verticalBuffer2);
		triangle2Sec.setNormalsBuffer(normalBuffer2);
		triangle2Sec.setTextureCoordsBuffer(textureBuffer2);
		triangle2Sec.setIndexBuffer(indicesBuffer2);
		Texture textureGround2 = eng.getTextureManager().getAssetByPath("red.jpeg");				//Texture file for the ground
		TextureState textureGroundState2 = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		textureGroundState2.setTexture(textureGround2);
		FrontFaceState faceState2 = (FrontFaceState)sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);
		triangle2.setDataSource(DataSource.INDEX_BUFFER);
		triangle2.setRenderState(textureGroundState2);
		triangle2.setRenderState(faceState2);
		triangle2Node.attachObject(triangle2);		
		//---------------------------------------------------------------------------------------------------------------------------------------------------
		
		//Creating the player node to add in the game, upgrade from last only entity approach
		playerObjectNode = gameWorldObjectsNode.createChildSceneNode("PlayerNode");
		
        //Creating a player
        Entity playerEntity = sm.createEntity("myPlayer", "dolphinHighPoly.obj");
        playerEntity.setPrimitive(Primitive.TRIANGLES);
        playerNode = playerObjectNode.createChildSceneNode(playerEntity.getName() + "Node");
        playerNode.attachObject(playerEntity);
        //player texture
        TextureManager tmd1 = eng.getTextureManager();
        Texture assetd1 = tmd1.getAssetByPath("Dolphin_HighPolyUV.png");
        RenderSystem rsd1 = sm.getRenderSystem();
        TextureState stated1 =  (TextureState) rsd1.createRenderState(RenderState.Type.TEXTURE);
        stated1.setTexture(assetd1);
        playerEntity.setRenderState(stated1);
        
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
      
		/*
		 * StretchController sc = new StretchController(); sc.addNode(playerNode);
		 * sm.addController(sc);
		 */
        
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
				
		tessN.scale(10, 20, 10);
		tessN.setLocalPosition(-1, -1, -5);
		tessE.setHeightMap(this.getEngine(), "terrain2.png");
		tessE.setTexture(this.getEngine(), "tm.png");
		
		/*
        Configuration conf = eng.getConfiguration();        
        TextureManager tm = getEngine().getTextureManager();        
        tm.setBaseDirectoryPath(conf.valueOf("assets.skyboxes.path"));        
        Texture front = tm.getAssetByPath("front.jpeg");        
        Texture back = tm.getAssetByPath("back.jpeg");        
        Texture left = tm.getAssetByPath("left.jpeg");        
        Texture right = tm.getAssetByPath("right.jpeg");        
        Texture top = tm.getAssetByPath("top.jpeg");        
        Texture bottom = tm.getAssetByPath("bottom.jpeg");        
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
        */
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
		
		
		setupOrbitCameras(eng,sm);
        setupInputs(sm);																								//Calling the function to setup the inputs

       
    }
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

    	//Initialization action keyboard
    	//moveCameraAction = new MoveCameraAction(this);
    	//moveDirectionAction = new MoveDirectionAction(playerNode, this);
    	//moveUpDownAction = new MoveUpDownAction(playerNode, this);
    	//rotateAction = new RotateAction(this);
    	//Initialization action gamepad
    	Camera camera = sm.getCamera("MainCamera");
    	moveForwardAction = new MoveForwardAction(playerNode, protClient);						//camera forward
        moveBackwardAction = new MoveBackwardAction(playerNode);						//camera backward
        moveLeftAction = new MoveLeftAction(playerNode);								//camera left
        moveRightAction = new MoveRightAction(playerNode);	
        rotateRightA = new RotateRightAction(playerNode, camera);
        rotateLeftA = new RotateLeftAction(playerNode, camera);
        //colorA = new ColorAction(sm);
        //camera right
        //rotatePlayerLeftAction = new RotateLeftAction(playerNode, this);				    //Rotate the player left
        //rotatePlayerRightAction = new RotateRightAction(playerNode, this);			//Rotate the player right

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
    	
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.POV, moveCameraAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);  
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.X, moveDirectionAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);  
    	im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Y, moveUpDownAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
    	//im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RX, rotateAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

    }

    //Function to handle the keyboard controls
    void keyboardControls(Controller kbName) {
   
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, moveLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, moveRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.W, moveForwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.S, moveBackwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.Q, rotateLeftA, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.E, rotateRightA, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        //im.associateAction(kbName,net.java.games.input.Component.Identifier.Key.SPACE, colorAction,InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
    }

    @Override
    protected void update(Engine engine) {
    	
		// build and set HUD
		rs = (GL4RenderSystem) engine.getRenderSystem();
		elapsTime += engine.getElapsedTimeMillis();
		elapsTimeSec = Math.round(elapsTime/1000.0f);
		elapsTimeStr = Integer.toString(elapsTimeSec);
		//hud = "Time = " + elapsTimeStr ;
		//rs.setHUD(hud, 15, 15);
		im.update(elapsTime);	
		playerController.updateCameraPosition();
		processNetworking(elapsTime);
		
		//im.update(elapsTime);																			//Error here, don't forget to include
		
	}

	public void setIsConnected(boolean b) {
		// TODO Auto-generated method stub
		this.isClientConnected = b;
	}

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
	public void removeGhostAvatarFromGameWorld(GhostAvatar avatar) {
		// TODO Auto-generated method stub
		if(avatar!=null)
			gameObjectsToRemove.add((UUID)avatar.getID());
	}
	
	private class SendCloseConnectionPacketAction extends AbstractInputAction
    { // for leaving the game... need to attach to an input device
    	@Override
    	public void performAction(float arg0, net.java.games.input.Event arg1)
    	{ 
    		if(protClient != null && isClientConnected == true)
    		{ 
    			protClient.sendByeMessage();
    		}
    	} 
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

