package Kartoffel.Licht;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import Kartoffel.Licht.Vulkan.DescriptorPool.DescriptorSet;

public class Entity {
	
	public float x = 0.0f, y = 0.0f, w = 1, h = 1, rot = 0;
	public float r = 1, g = 1, b = 1, a = 1;
	public float tx = 0, ty = 0, tw = 1, th = 1;
	int currentLayer = 0;
	private boolean remove = false;
	boolean changed = false;
	boolean sizeChanged = false;
	boolean visible = true;
	DescriptorSet texture;
	List<Entity> childs = new ArrayList<Entity>();
	public boolean despawnable = true;
	public boolean real = false;
	
	public Entity() {}
	public Entity(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	public Entity(int layer, float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.currentLayer = layer;
	}
	
	public Entity setTexture(DescriptorSet set) {
		this.texture = set;
		return this;
	}
	
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public float getW() {
		return w;
	}
	public float getH() {
		return h;
	}
	public int getCurrentLayer() {
		return currentLayer;
	}
	public void setX(float x) {
		this.x = x;
		changed = true;
	}
	public void setY(float y) {
		this.y = y;
		changed = true;
	}
	public void setW(float w) {
		this.w = w;
		changed = true;
		sizeChanged = true;
	}
	public void setH(float h) {
		this.h = h;
		changed = true;
		sizeChanged = true;
	}
	public void setRGBA(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		changed = true;
	}
	public void setCurrentLayer(int currentLayer) {
		this.currentLayer = currentLayer;
	}
	public void remove() {
		this.remove = true;
		childs.forEach(Entity::remove);
	}
	
	public void update(double delta) {
		//Implements something
	}
	
	public boolean isColliding(Entity other) {
		return Math.max(other.x-other.w, this.x-this.w) < Math.min(other.x+other.w, this.x+this.w)
			    && Math.max(this.y-this.h, other.y-other.w) < Math.min(this.y+this.h, other.y+other.h);
	}
	public Entity isColliding(List<?extends Entity> entities) {
		for(int i = 0; i < entities.size(); i++)
			if(isColliding(entities.get(i)))
				return entities.get(i);
		return null;
	}
	public Entity isCollidingRotated(List<?extends Entity> entities) {
		for(int i = 0; i < entities.size(); i++)
			if(isCollidingRotated(entities.get(i)))
				return entities.get(i);
		return null;
	}
	public <T extends Entity> void isColliding(List<T> entities, Consumer<T> consumer) {
		for(int i = 0; i < entities.size(); i++)
			if(isColliding(entities.get(i)))
				consumer.accept(entities.get(i));
	}
	public <T extends Entity> void isCollidingRotated(List<T> entities, Consumer<T> consumer) {
		for(int i = 0; i < entities.size(); i++)
			if(isCollidingRotated(entities.get(i)))
				consumer.accept(entities.get(i));
	}
	public boolean isCollidingWall(float b) {
		return (Math.abs(x)+w > Main.WORLD_WIDTH-b) || (Math.abs(y)+h > Main.WORLD_HEIGHT-b);
	}
	public boolean isOutOfBounds(float b) {
		return (Math.abs(x)-w > Main.WORLD_WIDTH-b) || (Math.abs(y)-h > Main.WORLD_HEIGHT-b);
	}
	public boolean isRemove() {
		return remove;
	}
	public boolean isCollidingRotated(Entity other) {
		return CollisionTools.isCollidingRotated_H(this, other);
	}
	

}
