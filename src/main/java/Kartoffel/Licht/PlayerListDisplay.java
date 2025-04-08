package Kartoffel.Licht;

import java.util.HashMap;
import java.util.List;

import Kartoffel.Licht.Vulkan.DescriptorPool.DescriptorSet;

public class PlayerListDisplay extends Entity {

	
	List<InputMethod> users;
	World ww;
	int size = 0;
	HashMap<InputMethod, Entity> ee = new HashMap<InputMethod, Entity>();
	HashMap<InputMethod, Integer> skinIndex = new HashMap<InputMethod, Integer>();
	
	public DescriptorSet ds;
	final static int SKIN_COUNT = 4;
	final static float width = 1.0f/16;
	final static float height = 1.0f/8;
	public PlayerListDisplay(List<InputMethod> users, World ww) {
		this.ww = ww;
		visible = false;
		ds = Main.getSet("textures/entite.png");
		this.users = users;
	}
	long t = 0;
	double time = 0;
	int anim = 0;
	@Override
	public void update(double delta) {
		time += delta;
		if(childs.size() != users.size()) {
			for(InputMethod m : users) {
				if(!ee.containsKey(m)) {
					Entity displ = new Entity();
					skinIndex.put(m, 1);
					ee.put(m, displ);
					displ.setTexture(ds);
					displ.tw = width;
					displ.th = height;
					displ.ty = height*(1+skinIndex.get(m));
					displ.x = x;
					displ.y = y+size*h*2;
					displ.currentLayer = 8;
					childs.add(displ);
					ww.addEntity(displ);
					size++;
				}
			}
			
		}
		if(t < System.currentTimeMillis()-500)
			for(InputMethod m : users) {
				if(m.isButtonBPressed()) {
					skinIndex.put(m, (skinIndex.get(m)+1)%SKIN_COUNT);
					ee.get(m).ty = height*(1+skinIndex.get(m));
					ee.get(m).changed = true;
					t = System.currentTimeMillis();
				}
				if(time > 0.2) {
					ee.get(m).tx = Main.RANDOM.nextInt(3)*width;
					ee.get(m).changed = true;
					time = 0;
				}
			}
	}
	
}
