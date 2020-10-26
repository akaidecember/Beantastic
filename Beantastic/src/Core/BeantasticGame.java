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
import net.java.games.input.Controller;

public class BeantasticGame extends VariableFrameRateGame{
	
	//Variables for the class Game-------------------------------------------------------------------
	
	//Private variables
	GL4RenderSystem rs;
	float elapsTime = 0.0f;
	
	//Public variables


	
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
		
		
	}
	
	//Function to setup the Cameras of the game
	@Override
	protected void setupCameras(SceneManager arg0, RenderWindow arg1) {
		// TODO Auto-generated method stub
		
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
		
		
		
	}
	
	
}
