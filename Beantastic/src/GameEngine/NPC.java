package GameEngine;

import Core.BeantasticGame;
import ray.rml.Vector3;

public class NPC{
	double locX, locY, locZ;
	private BeantasticGame game;
	private double size = 2;

	public double getX()
	{
		return locX;
	}
	public double getY()
	{
		return locY;
	}
	public double getZ()
	{
		return locZ;
	}

	public void updateLocation()
	{
		locX =+ 0.1;
	}
	public void getSmall() {
		game.getSmall(this);
	}
	public void getBig() {
		game.getBig(this);
		
	}
	public double getSize() {
		return size;
	}
}
