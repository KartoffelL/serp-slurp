package Kartoffel.Licht;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.vulkan.VK13;

import Kartoffel.Licht.AGGraphics.AGDrawer;
import Kartoffel.Licht.AGGraphics.AGShader;
import Kartoffel.Licht.Vulkan.Buffer;
import Kartoffel.Licht.Vulkan.VulkanBufferUtils;
import Kartoffel.Licht.Vulkan.VulkanFreeable;

public class World {
	
	private HashMap<String, List<Entity>[]> levels = new HashMap<String, List<Entity>[]>();
	private List<Entity>[] layers;
	private String CurrentLevel = "";
	private String CurrentLevelac = "";
	private int layerCount = 0;
	private HashMap<Entity, Buffer> buffers = new HashMap<>();
	private Buffer index;
	private List<Buffer> spareBuffers = new ArrayList<Buffer>();
	private AGDrawer drawer;
	private VulkanBufferUtils utils;
	
	private List<Entity> pendingAdds = new ArrayList<Entity>();
	private List<Entity> pendingAdds2 = new ArrayList<Entity>();
	
	public static int ENTITY_BUFFER_SIZE = (2*4+2*4+4*4)*4;
	boolean runtime = false;
	boolean wantsReset = false;
	double worldMoveDX = 0;
	double worldMoveDY = 0;
	
	public int getAllocatedBuffers() {
		return buffers.size()+spareBuffers.size();
	}
	public int getSpareBufferSize() {
		return spareBuffers.size();
	}
	
	@SuppressWarnings("unchecked")
	public World(VulkanBufferUtils utils, AGDrawer drawer, int numLayers) {
		this.layerCount = numLayers;
		this.layers = new List[numLayers];
		for(int i = 0; i < numLayers; i++)
			this.layers[i] = new ArrayList<Entity>();
		this.drawer = drawer;
		this.utils = utils;
		index = utils.generateIndexBuffer(6*2, false);
		utils.putBuffer(index, new short[] {0, 1, 2, 2, 3, 0}, 0, 0, 6);
	}
	public void loadLevel(String name) {
		CurrentLevel = name;
	}
	@SuppressWarnings("unchecked")
	public void switchLevelNOW(String name) {
		processLayers();
		CurrentLevel = name;
		levels.put(CurrentLevelac, layers);
		layers = levels.get(CurrentLevel);
		if(layers == null) {
			this.layers = new List[layerCount];
			for(int i = 0; i < layerCount; i++)
				this.layers[i] = new ArrayList<Entity>();
		}
		CurrentLevelac = CurrentLevel;
	}
	public void addEntity(Entity e) {
		if(!runtime)
			throw new RuntimeException("Can't add runtime objects pre-runtime!");
		e.changed = true;
		if(CurrentLevel.contentEquals(CurrentLevelac))
			pendingAdds2.add(e); //Adds the entities directly after the tick of all entities
		else
			pendingAdds.add(e);//Adds the entities directly after the world has changed
	}
	public void setRuntime() {
		this.runtime = true;
	}
	public void setMoveWorld(double dx, double dy) {
		worldMoveDX = dx;
		worldMoveDY = dy;
	}
	public void addEntityR(Entity e) {
		if(runtime)
			throw new RuntimeException("Can't add pre-runtime objects at runtime!");
		e.changed = true;
		layers[e.currentLayer].add(e);
	}
	void processLayer(int layer) { //Currently commented out because of "java.lang.IllegalArgumentException: Comparison method violates its general contract!" when running the sort function
//		layers[layer].sort((a, b)->{
//			return a.w*a.h-b.w*b.h > 0 ? -1 : 1;
//		});
	}
	void processLayers() {
		for(int i = 0; i < layerCount; i++) {
			layers[i].sort((a, b)->{
				return a.w*a.h-b.w*b.h > 0 ? -1 : 1;
			});
		}
	}
//	public void removeEntity(Entity e) {
//		layers[e.currentLayer].remove(e);
//		spareBuffers.add(buffers.remove(e));
//	}
//	public void moveEntity(Entity e, int newLayer) {
//		layers[newLayer].add(layers[e.currentLayer].remove(layers[e.currentLayer].indexOf(e)));
//	}
	
	Buffer getBuffer(Entity e) {
		if(buffers.containsKey(e))
			return buffers.get(e);
		if(spareBuffers.size() > 0) {
			var b = spareBuffers.remove(0);
			buffers.put(e, b);
			return b;
		}
		Buffer b = utils.generateBuffer(ENTITY_BUFFER_SIZE, VK13.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, false, false, false, 0, true, true, true);
		updateBuffer(b, e);
		buffers.put(e, b);
		return b;
	}
	
	void updateBuffer(Buffer b, Entity e) {
		float sn = (float) Math.sin(e.rot);
		float cs = org.joml.Math.cosFromSin(sn, e.rot);
		float w = (float) (e.w);
		float h = (float) (e.h);
		float w1 = w;
		float h1 = h;
		float w2 = w;
		float h2 = -h;
		float w3 = -w;
		float h3 = -h;
		float w4 = -w;
		float h4 = h;
		float w12 = e.x+w1*cs-h1*sn;
		float h12 = e.y+h1*cs+w1*sn;
		float w22 = e.x+w2*cs-h2*sn;
		float h22 = e.y+h2*cs+w2*sn;
		float w32 = e.x+w3*cs-h3*sn;
		float h32 = e.y+h3*cs+w3*sn;
		float w42 = e.x+w4*cs-h4*sn;
		float h42 = e.y+h4*cs+w4*sn;
		utils.putBufferDirect(b, new float[] {
//				w12*Main.GLOBAL_ROTATIONcs-h12*Main.GLOBAL_ROTATIONsn, h12*Main.GLOBAL_ROTATIONcs+w12*Main.GLOBAL_ROTATIONsn, e.tx+e.tw, e.ty+e.th, e.r, e.g, e.b, e.a,
//				w22*Main.GLOBAL_ROTATIONcs-h22*Main.GLOBAL_ROTATIONsn, h22*Main.GLOBAL_ROTATIONcs+w22*Main.GLOBAL_ROTATIONsn, e.tx+e.tw, e.ty, e.r, e.g, e.b, e.a,
//				w32*Main.GLOBAL_ROTATIONcs-h32*Main.GLOBAL_ROTATIONsn, h32*Main.GLOBAL_ROTATIONcs+w32*Main.GLOBAL_ROTATIONsn, e.tx, e.ty, e.r, e.g, e.b, e.a,
//				w42*Main.GLOBAL_ROTATIONcs-h42*Main.GLOBAL_ROTATIONsn, h42*Main.GLOBAL_ROTATIONcs+w42*Main.GLOBAL_ROTATIONsn, e.tx, e.ty+e.th, e.r, e.g, e.b, e.a,
				w12, h12, e.tx+e.tw, e.ty+e.th, e.r, e.g, e.b, e.a,
				w22, h22, e.tx+e.tw, e.ty, e.r, e.g, e.b, e.a,
				w32, h32, e.tx, e.ty, e.r, e.g, e.b, e.a,
				w42, h42, e.tx, e.ty+e.th, e.r, e.g, e.b, e.a,
				}, 0, 0, ENTITY_BUFFER_SIZE/4);
	}
	
	Buffer getUpdatedBuffer(Entity e) {
		var b = getBuffer(e);
		if(e.changed) {
			updateBuffer(b, e);
			e.changed = false;
		}
		return b;
	}
	long t = -1;
	public void rerecordDrawBuffer(AGShader shad) {
		float[] rotc = new float[] {Main.GLOBAL_ROTATIONcs, Main.GLOBAL_ROTATIONsn};
		final float[] IDENTITY = new float[] {1, 0};
		if(!CurrentLevelac.contentEquals(CurrentLevel)) { //Switch
			switchLevelNOW(CurrentLevel);
		}
		pendingAdds.forEach((e)->{
			layers[e.currentLayer].add(e);
			processLayer(e.currentLayer);
			e.childs.forEach((e2)->{
				layers[e2.currentLayer].add(e2);
				processLayer(e2.currentLayer);
			});
		});
		pendingAdds.clear();
		double delta = 0;
		if(t == -1) {
			delta = -1;
			t = System.nanoTime();
		} else {
			delta = (System.nanoTime()-t)/1000000000.0;
			t = System.nanoTime();
		}
		drawer.bindIndexBuffer16(index);
		for(int i = 0; i < layerCount; i++) {
			List<Entity> entities = layers[i];
			boolean s = false;
			for(Entity e : entities) {
				if(e.real)
					if(worldMoveDX != 0 || worldMoveDY != 0) {
						e.x += worldMoveDX*delta;
						e.y += worldMoveDY*delta;
						e.changed = true;
					}
				if(delta != -1)
					e.update(delta);
				if(e.visible) {
					Buffer b = getUpdatedBuffer(e);
					drawer.bindBuffers(b);
					if(e.texture != null)
						drawer.bindDescriptorSets(shad, e.texture, Main.MAINSHADER_UNIFORMS);
					VK13.vkCmdPushConstants(drawer.getCommandBuffer(), shad.getPipeline().getLayout().getAddress(), VK13.VK_SHADER_STAGE_VERTEX_BIT, 0, e.real ? rotc : IDENTITY);
					drawer.drawIndexed(6, 1);
					s |= e.sizeChanged;
				}
				if(e.despawnable) {
					if(e.isOutOfBounds(0))
						e.remove();
				}
			}
			entities.removeIf((e)->{
				if(e.isRemove()) {
					spareBuffers.add(buffers.remove(e));
					return true;
				}
				return false;
			});
			if(s) processLayer(i);
		}
		pendingAdds2.forEach((e)->{
			layers[e.currentLayer].add(e);
			e.childs.forEach((e2)->{
				layers[e2.currentLayer].add(e2);
				processLayer(e2.currentLayer);
			});
			processLayer(e.currentLayer);
		});
		pendingAdds2.clear();
//		worldMoveDX = 0;
//		worldMoveDY = 0;
	}
	
	
	public int getLayerCount() {
		return layerCount;
	}
	
	public void free() {
		drawer.join();
		index.free();
		spareBuffers.forEach(VulkanFreeable::free);
		buffers.forEach((_, b)->b.free());
	}
	@SuppressWarnings("unchecked")
	private void resetNOW() {
		levels.clear();
		layers = levels.get(CurrentLevel);
		if(layers == null) {
			this.layers = new List[layerCount];
			for(int i = 0; i < layerCount; i++)
				this.layers[i] = new ArrayList<Entity>();
		}
		buffers.forEach((_, b)->b.free());
		buffers.clear();
		spareBuffers.forEach(VulkanFreeable::free);
		spareBuffers.clear();
		pendingAdds.clear();
		pendingAdds2.clear();
		runtime = false;
		wantsReset = false;
		CurrentLevel = "";
		CurrentLevelac = "";
	}
	public void reset() {
		wantsReset = true;
		resetNOW();
	}
	

}
