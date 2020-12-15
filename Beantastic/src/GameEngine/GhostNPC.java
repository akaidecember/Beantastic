package GameEngine;
import java.util.UUID;

import ray.rage.scene.Entity;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
public class GhostNPC {
	private int id;
	private SceneNode node;
	private Entity entity;
	
	public GhostNPC(int id, Vector3 position) {
		this.id = id;
	}
	public void setNode(SceneNode ghostN) {
		this.node = ghostN;
	}
	public void setEntity(Entity ghostE) {
		this.entity = ghostE;
	}
	/*public void setPosition(float f, float g, float h) {
		this.node.setLocalPosition(f, g, h);
	}*/
	public void setPosition(Vector3 position) {
		this.node.setLocalPosition(position);
	}
	public Vector3 getPosition(Vector3 position) {
		return node.getLocalPosition();
	}
}
