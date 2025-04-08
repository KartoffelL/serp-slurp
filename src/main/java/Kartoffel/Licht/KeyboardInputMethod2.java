package Kartoffel.Licht;

import org.lwjgl.glfw.GLFW;

public class KeyboardInputMethod2 implements InputMethod{
	
	private long glfw;
	
	public KeyboardInputMethod2(long glfwWindow) {
		this.glfw = glfwWindow;
	}

	@Override
	public float getXOff() {
		float x = 0;
		if(GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_LEFT)!=0)
			x-=1;
		if(GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_RIGHT)!=0)
			x+=1;
		return x;
	}

	@Override
	public float getYOff() {
		float x = 0;
		if(GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_DOWN)!=0)
			x-=1;
		if(GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_UP)!=0)
			x+=1;
		return x;
	}

	@Override
	public boolean isButtonAPressed() {
		return GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_KP_0)!=0;
	}

	@Override
	public boolean isButtonBPressed() {
		return GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_KP_1)!=0;
	}

	@Override
	public boolean isButtonCPressed() {
		return GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_KP_2)!=0;
	}

	@Override
	public boolean isButtonDPressed() {
		return GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_KP_3)!=0;
	}

}
