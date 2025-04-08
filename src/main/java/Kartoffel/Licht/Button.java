package Kartoffel.Licht;

public class Button extends Entity{
	
	private Runnable action;
	private float Nr = 0.8f, Ng = 0.8f, Nb = 0.8f, Na = 1, Nr2 = 1.1f, Ng2 = 1.1f, Nb2 = 1.1f, Na2 = 1, Nrc = 0.2f, Ngc = 0.2f, Nbc = 0.6f, Nac = 1;
	boolean selectable = true;
	public Button(Runnable action) {
		this.action = action;
	}
	
	public void changeFocus(boolean click, boolean focus) {
		if(click) {
			this.r = Nrc;
			this.g = Ngc;
			this.b = Nbc;
			this.a = Nac;
//			Main.midiChannel.noteOn(110, 100);
		} else if(focus) {
			this.r = Nr2;
			this.g = Ng2;
			this.b = Nb2;
			this.a = Na2;
//			Main.midiChannel.noteOn(100, 100);
		} else {
			this.r = Nr;
			this.g = Ng;
			this.b = Nb;
			this.a = Na;
		}
		changed = true;
		
	}
	
	@Override
	public void update(double delta) {
		
	}

	public void click() {
		if(action != null)
			action.run();
	}

}
