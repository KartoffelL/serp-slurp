package Kartoffel.Licht;

import Kartoffel.Licht.Vulkan.DescriptorPool.DescriptorSet;

public class Platform extends Entity{
	
	double dissapearTimer = -1;
	double appearTimer = 0;
	
	public static double DISSAPEAR_TIME = 1;
	public static double APPEAR_TIME = 0.3;
	public static double SMALL_F = 0.08;
	
	public DescriptorSet ld;
	
	boolean custom = false;
	
	public Platform() {
		real = true;
//		try {
//			setTexture(Main.getSet("white.png"));
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
	}
	
	@Override
	public void update(double delta) {
		if(dissapearTimer >= 0)  {
			dissapearTimer += delta;
			changed = true;
			double c = 1-dissapearTimer/DISSAPEAR_TIME;
			a = (float) (c);
			w -= (float) (delta*SMALL_F);
			h -= (float) (delta*SMALL_F);
			if(dissapearTimer > DISSAPEAR_TIME) {
				remove();
//				Main.midiChannel.noteOn(55, 100);
			}
		}
		if(custom)
			return;
		if(appearTimer >= 0) {
			appearTimer += delta;
			changed = true;
			double c = Math.sqrt(appearTimer/DISSAPEAR_TIME)*.2;
			a = (float) (c);
			if(appearTimer >= APPEAR_TIME) {
				texture = ld;
				a = 1;
				appearTimer = -1;
				currentLayer++;
			}
		}
	}

	public boolean isSturdy() {
		return appearTimer < 0 || custom;
	}
	

}
