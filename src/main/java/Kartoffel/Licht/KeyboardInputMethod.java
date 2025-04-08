package Kartoffel.Licht;

import org.lwjgl.glfw.GLFW;

public class KeyboardInputMethod implements InputMethod{
	
	private long glfw;
	
	public KeyboardInputMethod(long glfwWindow) {
		this.glfw = glfwWindow;
	}

	@Override
	public float getXOff() {
		float x = 0;
		if(GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_A)!=0)
			x-=1;
		if(GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_D)!=0)
			x+=1;
		return x;
	}

	@Override
	public float getYOff() {
		float x = 0;
		if(GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_S)!=0)
			x-=1;
		if(GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_W)!=0)
			x+=1;
		return x;
	}

	@Override
	public boolean isButtonAPressed() {
		return GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_E)!=0;
	}

	@Override
	public boolean isButtonBPressed() {
		return GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_Q)!=0;
	}

	@Override
	public boolean isButtonCPressed() {
		return GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_R)!=0;
	}

	@Override
	public boolean isButtonDPressed() {
		return GLFW.glfwGetKey(glfw, GLFW.GLFW_KEY_C)!=0;
	}

}
