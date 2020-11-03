package Core;

import net.java.games.input.Event;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class Camera3pController {
	
	private Camera camera;
	private SceneNode cameraNode, playerNode;
	private float cameraAz, cameraElevation, radius;
	private Vector3 playerPosition, worldVector;

	public Camera3pController(Camera newCamera, SceneNode newCameraNode, SceneNode newPlayerNode, String newInputDeviceName, InputManager im) {
		// TODO Auto-generated constructor stub
				
		//Initializing all the local variables
		camera = newCamera;
		cameraNode = newCameraNode;
		playerNode = newPlayerNode;
		cameraAz = 0.0f;
		cameraElevation = 20.0f;
		radius = 2.0f;
		worldVector = Vector3f.createFrom(0.0f, 1.0f, 0.0f);
		setUpInput(im, newInputDeviceName);
		
	}

	private void setUpInput(InputManager im, String inputDevice) {
		// TODO Auto-generated method stub
		
		Action orbitAction = new OrbitAroundAction();
		im.associateAction(inputDevice, net.java.games.input.Component.Identifier.Axis.RX, orbitAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
	}
	
	public void updateCameraPosition() {
		
		//Set the camera to rotate around the target and setting the angle of the altitude
		double angle1 = Math.toRadians(cameraAz), angle2 = Math.toRadians(this.getCameraElevationAngle());					
		double x,y,z;
		
		//Setting the coordinates for the camera node local position
		x = radius * Math.cos(angle2) * Math.sin(angle1);
		y = radius * Math.sin(angle2);
		z = radius * Math.cos(angle2) * Math.cos(angle1);
		cameraNode.setLocalPosition(Vector3f.createFrom((float)x, (float)y, (float)z).add(playerNode.getWorldPosition()));
		cameraNode.lookAt(playerNode, worldVector);;
		
	}

	//Getter and setter functions for the class-------------------------------------------------------------------
	
	//Function to get the camera elevation angle
	public float getCameraElevationAngle() {
	
		return this.cameraElevation;
		
	}
	
	//Function to get the radius
	public float getRadius() {
		
		return this.radius;
		
	}
	
	//Function to get the azimuth angle 
	public float getAzimuth() {
		
		return this.cameraAz;
		
	}
	
	//Function to set the new elevation angle
	public void setCameraElevationAngle(float newValue) {
		
		this.cameraElevation = newValue;
		
	}
	
	//Function to set the new azimuth
	public void setAzimuth(float newValue) {
		
		this.cameraAz = newValue;
		
	}
	
	//Function to set the radius
	public void setRadius(float newValue) {
		
		this.radius = newValue;
		
	}
	
	//Set the rotate action
	public void setRotateAction(float degree) {
		
		cameraAz += degree;
		cameraAz = cameraAz % 360;
		updateCameraPosition();
		
	}
	
	private class OrbitAroundAction extends AbstractInputAction{

		@Override
		public void performAction(float time, Event e) {
			// TODO Auto-generated method stub
			
			float degree;
			
			if(e.getValue() < -0.2)
				degree = -0.2f;
			else {
				
				if(e.getValue() > 0.2)
					degree = 0.2f;
				else
					degree = 0.0f;
				
			}
			
			setRotateAction(degree);
			
		}
		
	}

}
