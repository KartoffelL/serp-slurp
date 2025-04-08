package Kartoffel.Licht;

import java.util.ArrayList;
import java.util.List;

public class UIManager extends Entity{
	
	public static int FOCUS_DELAY = 200;
	
	long glfwHandle;
	List<InputMethod> methods = new ArrayList<>();
	List<Button> buttons = new ArrayList<>();
	Button focus = null;
	public long p = 0;
	boolean c = false;
	
	public UIManager(Button firstElement, long glfw) {
		this.glfwHandle = glfw;
		focus = firstElement;
		buttons.add(firstElement);
		visible = false;
		firstElement.changeFocus(false, true);
	}
	public void addButton(Button b) {
		buttons.add(b);
		b.changeFocus(false, false);
	}
	public void s(float xoff, float yoff) {
		if(buttons.size() == 0) //Nothing to switch to
			return;
		if(focus == null)
			focus = buttons.get(0);
		float bd = 0;
		Button b = null;
		for(Button a : buttons) {
			if(!a.selectable) continue;
			float dx = a.x-focus.x;
			float dy = a.y-focus.y;
			float d = (float) Math.sqrt(dx*dx+dy*dy);
			float d2 = (float) Math.sqrt(xoff*xoff+yoff*yoff);
			float dot = (dx*xoff+dy*yoff)/d/d2/d;
			if(dot > bd) {
				bd = dot;
				b = a;
			}
		}
		if(b == null)
			return;
		if(focus != b) {
			focus.changeFocus(false, false);
			b.changeFocus(false, true);
			SoundUtils.playButtonSound();
		}
		focus = b;
		
	}
	@Override
	public void update(double delta) {
		float xoff = 0;
		float yoff = 0;
		boolean click = false;
		for(InputMethod m : methods) {
			xoff += m.getXOff();
			yoff += m.getYOff();
			click |= m.isButtonAPressed();
		}
		if((xoff != 0 || yoff != 0) && p < System.currentTimeMillis()-FOCUS_DELAY) {
			
			s(xoff, -yoff);
			
			p = System.currentTimeMillis();
		}
		if(click) {
			if(!c) {
				c = true;
				if(focus != null) {
					focus.changeFocus(true, true);
					focus.click();
				}
			}
		} else {
			if(c)
				if(focus != null)
					focus.changeFocus(false, true);
			c = false;
		}
	}
	
	
	

}
