package Kartoffel.Licht;

import Kartoffel.Licht.Vulkan.DescriptorPool.DescriptorSet;

public class InventoryDisplay extends Entity{
	
	static class progressBar extends Entity {
		float fraction = 1;
		float width = w;
		private float fr = 1;
		@Override
		public void update(double delta) {
			if(fraction != fr) {
				fr = fraction;
				w = width*fr;
				changed = true;
			}
		}
	}
	
	Player p;
	int index;
	progressBar prg;
	Entity adis1;
	Entity adis2;
	Entity adis3;
	Entity adis4;
	NumberE pointsDispl;
	NumberE timepointsDispl;
	DescriptorSet icons;
	
	public InventoryDisplay(int index, Player p, World ww) {
		DescriptorSet t = Main.getSet("white.png");
		icons = Main.getSet("textures/icons.png");
		this.p = p;
		this.index = index;
		visible = false;
		w = 5;
		h = 1;
		x = -Main.WORLD_WIDTH+w;
		y = -Main.WORLD_HEIGHT+h+index*4;
		prg = new progressBar();
		prg.currentLayer = 6;
		prg.x = x;
		prg.y = y;
		prg.w = w;
		prg.width = w;
		prg.texture = t;
		float barHeight = 0.2f;
		prg.h = barHeight;
		Entity bck = new Entity(prg.x, prg.y, prg.w, prg.h);
		bck.r = 0.1f;
		bck.g = 0;
		bck.b = 0;
		bck.a = 1;
		bck.currentLayer = 5;
		bck.texture = t;
		
		final float size = 0.5f;
		final float size2 = 1.1f;
		final float padding = (size2-size)*2;
		final float off = size2-size;
		final float padding2 = 0.0f;
		
		adis1 = new Entity();
//		adis1.g = 0;
		adis1.currentLayer = 7;
		adis1.x = x-w+size+off;
		adis1.y = y+barHeight+padding-0.4f;
		adis1.w = size;
		adis1.h = size;
		adis1.texture = icons;
		adis1.th = 0.25f;
		adis1.ty = 0.75f;
		
		adis2 = new Entity();
//		adis2.g = 0.6f;
		adis2.currentLayer = 7;
		adis2.x = x-w+(size*2+padding)*1+size+off;
		adis2.y = y+barHeight+padding-0.4f;
		adis2.w = size;
		adis2.h = size;
		adis2.texture = icons;
		adis2.th = 0.25f;
		adis2.ty = 0.50f;
		
		adis3 = new Entity();
//		adis3.g = 0.2f;
		adis3.currentLayer = 7;
		adis3.x = x-w+(size*2+padding)*2+size+off;
		adis3.y = y+barHeight+padding-0.4f;
		adis3.w = size;
		adis3.h = size;
		adis3.texture = icons;
		adis3.th = 0.25f;
		adis3.ty = 0.25f;
		
		adis4 = new Entity();
//		adis4.g = 0.8f;
		adis4.currentLayer = 7;
		adis4.x = x-w+(size*2+padding)*3+size+off;
		adis4.y = y+barHeight+padding-0.4f;
		adis4.w = size;
		adis4.h = size;
		adis4.texture = icons;
		adis4.th = 0.25f;
		adis4.ty = 0.0f;
		
		var if1 = new Entity();
//		adis1.g = 0;
		if1.currentLayer = 6;
		if1.x = x-w+size2;
		if1.y = y+barHeight+padding2+size2;
		if1.w = size2;
		if1.h = size2;
		if1.texture = icons;
		if1.th = 0.00f;
		if1.ty = 0.00f;
		if1.texture = Main.getSet("textures/itemframes.png");
		if1.th = 1.0f/3;
		if1.tw = 1.0f/4;
		
		var if2 = new Entity();
//		adis2.g = 0.6f;
		if2.currentLayer = 6;
		if2.x = x-w+(size2*2+padding2)*1+size2;
		if2.y = y+barHeight+padding2+size2;
		if2.w = size2;
		if2.h = size2;
		if2.texture = icons;
		if2.th = 0.00f;
		if2.ty = 0.00f;
		if2.texture = Main.getSet("textures/itemframes.png");
		if2.th = 1.0f/3;
		if2.tw = 1.0f/4;
		
		var if3 = new Entity();
		if3.currentLayer = 6;
		if3.x = x-w+(size2*2+padding2)*2+size2;
		if3.y = y+barHeight+padding2+size2;
		if3.w = size2;
		if3.h = size2;
		if3.texture = icons;
		if3.th = 0.0f;
		if3.ty = 0.0f;
		if3.texture = Main.getSet("textures/itemframes.png");
		if3.th = 1.0f/3;
		if3.tw = 1.0f/4;
		
		var if4 = new Entity();
		if4.currentLayer = 6;
		if4.x = x-w+(size2*2+padding2)*3+size2;
		if4.y = y+barHeight+padding2+size2;
		if4.w = size2;
		if4.h = size2;
		if4.texture = icons;
		if4.th = 0.0f;
		if4.ty = 0.0f;
		if4.texture = Main.getSet("textures/itemframes.png");
		if4.th = 1.0f/3;
		if4.tw = 1.0f/4;
		
		pointsDispl = new NumberE(ww);
		pointsDispl.x = x-w+size;
		pointsDispl.y = y+barHeight+padding*2+size*3;
		pointsDispl.setW(size);
		pointsDispl.orientation = (float) 0;
		timepointsDispl = new NumberE(ww);
		timepointsDispl.x = x-w+size;
		timepointsDispl.y = y+barHeight+padding*3+size*5;
		timepointsDispl.setW(size);
		timepointsDispl.orientation = (float) 0;
		
		childs.add(prg);
		childs.add(bck);
		childs.add(if1);
		childs.add(if2);
		childs.add(if3);
		childs.add(if4);
		childs.add(adis1);
		childs.add(adis2);
		childs.add(adis3);
		childs.add(adis4);
		childs.add(pointsDispl);
		childs.add(timepointsDispl);
	}

	boolean u_a = false;
	boolean u_b = false;
	boolean u_c = false;
	boolean u_d = false;
	
	@Override
	public void update(double delta) {
		prg.fraction = Math.max(p.poitns/Player.MAX_POINTS, 0.001f);
		pointsDispl.val = (int) p.score;
		timepointsDispl.val = (int) p.timescore;
		boolean aa = p.underDashCooldown();
		boolean ab = p.underAbilityBCooldown();
		boolean ac = p.underAbilityCCooldown();
		boolean ad = p.underAbilityDCooldown();
		if(u_a != aa) {
			u_a = aa;
			adis1.a = u_a ? 0.2f : 1;
			adis1.changed = true;
		}
		if(u_b != ab) {
			u_b = ab;
			adis2.a = u_b ? 0.2f : 1;
			adis2.changed = true;
		}
		if(u_c != ac) {
			u_c= ac;
			adis3.a = u_c ? 0.2f : 1;
			adis3.changed = true;
		}
		if(u_d != ad) {
			u_d = ad;
			adis4.a = u_d ? 0.2f : 1;
			adis4.changed = true;
		}
	}
	
	
}
