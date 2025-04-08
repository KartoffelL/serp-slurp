package Kartoffel.Licht;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Kartoffel.Licht.Vulkan.DescriptorPool.DescriptorSet;

public class PlatformSpawner extends Entity{

	public static int SPAWN_DELAY = (int) (Main.MUSIC_BASE_MILLIS*0.05);
	public static float DESPAWN_CHANCE = 1;
	public static float SEPERATION = 1;
	public static int M_PLATFORM_SIZE = 3;
	public static int PLATFORM_SPAWN_BORDER = 1;
	
	
	private List<Platform> platforms = new ArrayList<Platform>();
	private long t = 0;
	private DescriptorSet[] textures;
	private DescriptorSet[] texturesL;
	private static HashMap<int[], float[]> platformUV = new HashMap<int[], float[]>();
	private static float[] fallback;
	private static int[] fallbackc;
	public int MOLD_SPAWN_CHANCE = 1;
	public int phase = 0;
	static {
		final float width = 1.0f/8;
		final float height = 1.0f/32;
		platformUV.put(fallbackc = new int[] {1, 1}, fallback = new float[] {0, 0, width, height});
		
		platformUV.put(new int[] {2, 1}, new float[] {width, 0, width*2, height});
		
		platformUV.put(new int[] {1, 2}, new float[] {0*width, 1*height, 1*width, 2*height});
		platformUV.put(new int[] {3, 1}, new float[] {3*width, 0*height, 3*width, 1*height});
		platformUV.put(new int[] {1, 3}, new float[] {0*width, 3*height, 1*width, 3*height});
		platformUV.put(new int[] {2, 2}, new float[] {1*width, 1*height, 2*width, 2*height});
		platformUV.put(new int[] {2, 3}, new float[] {6*width, 0*height, 2*width, 3*height});
		platformUV.put(new int[] {3, 1}, new float[] {3*width, 0*height, 3*width, 1*height});
		platformUV.put(new int[] {3, 2}, new float[] {3*width, 1*height, 3*width, 2*height});
		platformUV.put(new int[] {3, 3}, new float[] {1*width, 3*height, 3*width, 3*height});
		platformUV.put(new int[] {4, 3}, new float[] {4*width, 3*height, 4*width, 3*height});
		platformUV.put(new int[] {4, 1}, new float[] {3*width, 6*height, 4*width, 1*height});
		platformUV.put(new int[] {3, 4}, new float[] {0*width, 6*height, 3*width, 4*height});
		platformUV.put(new int[] {1, 4}, new float[] {7*width, 6*height, 1*width, 4*height});
		platformUV.put(new int[] {4, 2}, new float[] {3*width, 7*height, 4*width, 2*height});
		platformUV.put(new int[] {2, 4}, new float[] {0*width, 10*height, 2*width, 4*height});
		platformUV.put(new int[] {4, 4}, new float[] {3*width, 10*height, 4*width, 4*height});
		platformUV.put(new int[] {6, 1}, new float[] {0*width, 23*height, 6*width, 1*height});
		platformUV.put(new int[] {5, 2}, new float[] {3*width, 21*height, 5*width, 2*height});
		platformUV.put(new int[] {8, 8}, new float[] {0*width, 24*height, 8*width, 8*height});
		
	}
	
	World world;
	public PlatformSpawner(World w) throws IOException {
		this.world = w;
		Platform pf = new Platform();
		pf.x = 0;
		pf.y = 0;
		pf.w = 5+Main.RANDOM.nextInt(5); //Min size
		pf.h = 5+Main.RANDOM.nextInt(5);
		platforms.add(pf);
		world.addEntityR(pf);
		visible = false;
		textures = new DescriptorSet[3];
		textures[0] = Main.getSet("textures/ts1a.png");
		textures[1] = Main.getSet("textures/ts2a.png");
		textures[2] = Main.getSet("textures/ts3a.png");
		texturesL = new DescriptorSet[3];
		texturesL[0] = Main.getSet("textures/ts1b.png");
		texturesL[1] = Main.getSet("textures/ts2b.png");
		texturesL[2] = Main.getSet("textures/ts3b.png");
		applyTexture(pf);
	}
	
	public void addBigPlatform() {
		Platform pf = new Platform();
		pf.x = 0;
		pf.y = 0;
		int k = Main.RANDOM.nextInt(5);
		pf.w = 5+k; //Min size
		pf.h = 5+k;
		pf.texture = Main.getSet("textures/entite.png");
		pf.tw = 2*PlayerListDisplay.width;
		pf.th = 2*PlayerListDisplay.height;
		pf.tx = 2*(1+Main.RANDOM.nextInt(3))*PlayerListDisplay.width;
		pf.ty = 5*PlayerListDisplay.height;
		pf.custom = true;
		
		platforms.add(pf);
		world.addEntity(pf);
	}
	
	private float b_sm = 0;
	private float[] b_v;
	private int[] b_c;
	public void applyTexture(Platform pf) {
		pf.texture = texturesL[phase];
		pf.ld = textures[phase];
		var a = platformUV.get(new int[] {(int)pf.w, (int)pf.h});
		if(a == null) {
			b_sm = 0;
			b_v = null;
			b_c = null;
			platformUV.forEach((c, d)->{
				if(c[0] <= pf.w && c[1] <= pf.h && (c[0]*c[1] > b_sm)) {
					b_sm = c[0]*c[1];
					b_v = d;
					b_c = c;
				}
			});
			if(b_v == null) {
				b_v = fallback;
				b_c = fallbackc;
				System.err.println("Fallback!");
			}
			pf.tx = b_v[0];
			pf.ty = b_v[1];
			pf.tw = b_v[2];
			pf.th = b_v[3];
			if(Main.RANDOM.nextBoolean()) {
				pf.tx += pf.tw;
				pf.tw *= -1;
			}
			if(Main.RANDOM.nextBoolean()) {
				pf.ty += pf.th;
				pf.th *= -1;
			}
			pf.w = b_c[0];
			pf.h = b_c[1];
		} else {
			pf.tx = a[0];
			pf.ty = a[1];
			pf.tw = a[2];
			pf.th = a[3];
		}
	}
	
	public boolean spawnPlattform() {
		for(int asd = 0; asd < 25; asd++){ //10 tries
			float seedX = Main.RANDOM.nextFloat(-Main.WORLD_WIDTH+1, Main.WORLD_WIDTH-1);
			float seedY = Main.RANDOM.nextFloat(-Main.WORLD_HEIGHT+1, Main.WORLD_HEIGHT-1);
			Platform pf = new Platform();
			pf.x = seedX;
			pf.y = seedY;
			pf.w = 1+Main.RANDOM.nextInt(M_PLATFORM_SIZE); //Min size
			pf.h = 1+Main.RANDOM.nextInt(M_PLATFORM_SIZE);
//			pf.r = Main.RANDOM.nextFloat();
			if(isCollidingPlatform(pf, SEPERATION+1)) //No spawn
				continue;
			platforms.add(pf);
			world.addEntity(pf);
			a:
			for(int i = 0; i < 5; i++) {
				if(Main.RANDOM.nextBoolean()) {
					pf.w++;
					if(isCollidingPlatform(pf, SEPERATION)) {
						pf.w--;
						break a;
					}
				}else {
					pf.h++;
					if(isCollidingPlatform(pf, SEPERATION)) {
						pf.h--;
						break a;
					}
				}
			}
			applyTexture(pf);
			int r = Main.RANDOM.nextInt(2)+2;
			for(int i = 0; i < r; i++) {
				float xx = Main.RANDOM.nextFloat(pf.w*2)-pf.w;
				float yy = Main.RANDOM.nextFloat(pf.h*2)-pf.h;
				Orb o = new Orb(this, Main.players);
				o.x = xx+pf.x;
				o.y = yy+pf.y;
				world.addEntity(o);
			}
			if(Main.RANDOM.nextInt(MOLD_SPAWN_CHANCE) == 0) {
				float xx = Main.RANDOM.nextFloat(pf.w*2)-pf.w;
				float yy = Main.RANDOM.nextFloat(pf.h*2)-pf.h;
				Schimmel o = new Schimmel(this, Main.players);
				o.x = xx+pf.x;
				o.y = yy+pf.y;
				world.addEntity(o);
			}
//			Main.midiChannel.noteOn(50+Main.RANDOM.nextInt(5)*2, 100);
			return true;
		}
		return false;
	}
	
	public boolean isCollidingPlatform(Entity e, float b) {
		if(e.isCollidingWall(b))
			return true;
		e.w += b;
		e.h += b;
		for(Platform p : platforms) {
			if(p == e)
				continue;
			if(!p.isSturdy() && !(e instanceof Platform))
				continue;
			if(p.isColliding(e)) {
				e.w -= b;
				e.h -= b;
				return true;
			}
		}
		e.w -= b;
		e.h -= b;
		return false;
	}
	
	@Override
	public void update(double delta) {
		if(t < System.currentTimeMillis()-SPAWN_DELAY) {
			boolean r = spawnPlattform();
			if(!r) { //Delete a platform
				if(Main.RANDOM.nextFloat() < DESPAWN_CHANCE)
					for(int i = 0; i < 10; i++) {
						int asd = Main.RANDOM.nextInt(platforms.size());
						if(platforms.get(asd).dissapearTimer >=0)
							continue;
						platforms.get(asd).dissapearTimer = 0;
						break;
					}
//				Main.midiChannel.noteOn(40+Main.RANDOM.nextInt(5)*2, 100);
			}
			
			t = System.currentTimeMillis();
		}
		platforms.removeIf(Platform::isRemove);
		
	}
	
}
