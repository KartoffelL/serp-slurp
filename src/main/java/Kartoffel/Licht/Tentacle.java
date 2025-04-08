package Kartoffel.Licht;

import java.util.List;

public class Tentacle extends Entity{
	
	List<Player> players;
	
	public static float IMPACT_TIME = 2;
	
	public static float TENTACLE_DAMAGE = 15;
	
	float tis;
	float mulX = Main.WORLD_WIDTH*20;
	float mulY = 1;
	
	public Tentacle(List<Player> player) {
		this.players = player;
		setTexture(Main.getSet("white.png"));
		this.r = 1;
		this.g = 0;
		this.b = 0.5f;
		this.a = 0;
		this.currentLayer = 1;
		this.real = true;
	}
	
	@Override
	public void update(double delta) {
		tis += delta;
		float d = (float) Math.max(Math.pow(tis/IMPACT_TIME, 20), 0.01);
		float d2 = (float) Math.sqrt(tis/IMPACT_TIME)+0.1f;
		this.a = d;
		this.w = mulX*d2;
		this.h = mulY*d2;
		changed = true;
		if(tis >= IMPACT_TIME) {
			isCollidingRotated(players, (a)->{
				a.damage(TENTACLE_DAMAGE);
			});
//			Main.midiChannel.noteOn(8, 100);
			remove();
		}
	}

}
