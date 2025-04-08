package Kartoffel.Licht;

import java.util.List;

public class Schimmel extends Entity{
	
	public static final float ORB_SIZE = 0.8f;
	public static final float ORB_REGEN = 5;
	
	public Schimmel(PlatformSpawner ps, List<Player> player) {
		this.real = true;
		currentLayer = 1;
		r = 1;
		g = 1;
		b = 0;
		a = 1;
		tw = 1.0f/8;
		th = 1;
		tx = Main.RANDOM.nextInt(8)/8.0f;
		this.ps = ps;
		t = Main.RANDOM.nextInt(50000);
		this.players = player;
		setTexture(Main.getSet("textures/mold.png")); //textures/orb.png
	}
	
	
	double t = 0;
	float d = 0;
	float airTime;
	PlatformSpawner ps;
	boolean pl = false;
	float dis = 1;
	List<Player> players;
	
	@Override
	public void update(double delta) {
		t += delta;
		float a = (int) (Math.sin(t)*5);
		if(a != d || !pl) {
			d = a;
			w = ORB_SIZE+ORB_SIZE*a/5*.2f*dis;
			h = ORB_SIZE+ORB_SIZE*a/5*.2f*dis;
//			rot = (float) t;
			changed = true;
			pl = ps.isCollidingPlatform(this, 0);
		}
		if(!pl) {
			airTime += delta;
			dis = (float) (1-Math.pow(airTime/Player.MAX_AIR_TIME, 2));
			this.a = dis;
			changed = true;
		}
		if(airTime > Player.MAX_AIR_TIME) {
			remove();
		}
		var p = (Player) isColliding(players);
		if(p != null) {
			if(!p.isDead()) {
				remove();
				p.damage(ORB_REGEN);
//				Main.midiChannel.noteOn(100+Main.RANDOM.nextInt(5)*2, 100);
			}
		}
		
	}

}
