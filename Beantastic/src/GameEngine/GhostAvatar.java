package GameEngine;

import java.util.UUID;

import ray.rage.scene.Entity;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

public class GhostAvatar {
	private UUID id;
	private SceneNode node;
	private Entity entity;
	public GhostAvatar(UUID ghostID, Vector3 ghostPosition) {
		// TODO Auto-generated constructor stub
		this.id = ghostID;
	}
	public void setNode(SceneNode ghostN) {
		this.node = ghostN;
	}
	public void setEntity(Entity ghostE) {
		this.entity = ghostE;
	}
	public void setPosition(float f, float g, float h) {
		this.node.setLocalPosition(f,g,h);
	}
	public Vector3 getLocalPosition() {
		return this.node.getLocalPosition();
	}
	public void updatePosition(Vector3 ghostPosition) {
		this.node.setLocalPosition(ghostPosition.x(), ghostPosition.y(), ghostPosition.z());
	}
	public SceneNode getGhostN() {
		return node;
	}
	public UUID getID() {
		// TODO Auto-generated method stub
		return id;
	}

}
