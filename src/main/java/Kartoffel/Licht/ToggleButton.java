package Kartoffel.Licht;

import java.util.function.BiConsumer;

public class ToggleButton extends Button{
	
	private BiConsumer<Integer, ToggleButton> action;
	private int maxStages;
	private int current = 0;
	public ToggleButton(BiConsumer<Integer, ToggleButton> action, int maxStages) {
		super(null);
		this.action = action;
		this.maxStages = maxStages;
	}
	
	public void toggleClick() {
		current = (current+1)%maxStages;
		action.accept(current, this);
	}
	@Override
	public void click() {
		toggleClick();
	}

}
