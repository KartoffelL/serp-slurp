package Kartoffel.Licht;

import Kartoffel.Licht.Vulkan.DescriptorPool.DescriptorSet;

public class NumberE extends Entity{
	
	public int val;
	private int aval;
	public float orientation = -0.5f;
	World w;
	public static float WIDTH = 1.0f/16;
	public static float HEIGHT = 1.0f/8;
	public static float[][] Mappings = new float[][] {
			{0, 7*HEIGHT, WIDTH, HEIGHT},
			{WIDTH, 7*HEIGHT, WIDTH, HEIGHT},
			{WIDTH*2, 7*HEIGHT, WIDTH, HEIGHT},
			{WIDTH*3, 7*HEIGHT, WIDTH, HEIGHT},
			{WIDTH*4, 7*HEIGHT, WIDTH, HEIGHT},
			{WIDTH*5, 7*HEIGHT, WIDTH, HEIGHT},
			{WIDTH*6, 7*HEIGHT, WIDTH, HEIGHT},
			{WIDTH*7, 7*HEIGHT, WIDTH, HEIGHT},
			{WIDTH*8, 7*HEIGHT, WIDTH, HEIGHT},
			{WIDTH*9, 7*HEIGHT, WIDTH, HEIGHT}
			
			};
	public DescriptorSet texture;
	
	public NumberE(World w) {
		this.w = w;
		visible = false;
		val = 5037831;
		texture = Main.getSet("ui/symbols.png");
	}
	
	@Override
	public void update(double delta) {
		if(val != aval) {
			aval = val;
			String s = Integer.toString(Math.abs(aval));
			int numChrs = s.length();
			while(numChrs > childs.size()) {
				var e = new Entity();
				e.currentLayer = 4;
				
				e.texture = texture;
				childs.add(e);
				w.addEntity(e);
			}
			while(numChrs < childs.size()) {
				var e = childs.remove(0);
				e.remove();
			}
			float size = this.getW();
			for(int i = 0; i < numChrs; i++) {
				Entity e = childs.get(i);
				e.x = x+numChrs*size*2*orientation+i*size*2;
				e.y = y;
				e.w = size;
				e.h = size;
				int c = s.charAt(i)-48;
				if(c < 0 || c > 9)
					c = 9;
				float[] t = Mappings[c];
				e.tx = t[0];
				e.ty = t[1];
				e.tw = t[2];
				e.th = t[3];
				e.changed = true;
			}
			
		}
	}
	

}
