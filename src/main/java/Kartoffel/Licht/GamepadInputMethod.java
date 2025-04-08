package Kartoffel.Licht;

import org.lwjgl.glfw.GLFW;

public class GamepadInputMethod implements InputMethod{
	
	private long glfw;
	private int num;
	
	public GamepadInputMethod(long glfwWindow, int num) {
		this.glfw = glfwWindow;
		this.num = num;
	}
	public long getGlfw() {
		return glfw;
	}

	@Override
	public float getXOff() {
		float f = GLFW.glfwGetJoystickAxes(num).get(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X);
		if(Math.abs(f) < 0.25)
			f = 0;
		return f;
		
	}

	@Override
	public float getYOff() {
		float f = -GLFW.glfwGetJoystickAxes(num).get(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y);
		if(Math.abs(f) < 0.25)
			f = 0;
		System.out.println("Y : " + f);
		return f;
		
	}

	@Override
	public boolean isButtonAPressed() {
		return GLFW.glfwGetJoystickButtons(num).get(1)==1;
		
	}

	@Override
	public boolean isButtonBPressed() {
		return GLFW.glfwGetJoystickButtons(num).get(0)==1;
		
	}

	@Override
	public boolean isButtonCPressed() {
		return GLFW.glfwGetJoystickButtons(num).get(3)==1;
		
	}

	@Override
	public boolean isButtonDPressed() {
		return GLFW.glfwGetJoystickButtons(num).get(2)==1;
		
	}

	@Override
	public boolean isPresent() {
		return GLFW.glfwJoystickPresent(num);
	}

}
