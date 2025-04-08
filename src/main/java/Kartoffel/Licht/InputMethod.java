package Kartoffel.Licht;

public interface InputMethod {
	
	public float getXOff();
	public float getYOff();
	public boolean isButtonAPressed();
	public boolean isButtonBPressed();
	public boolean isButtonCPressed();
	public boolean isButtonDPressed();
	public default boolean isPresent() {
		if(getYOff() != 0)
			return true;
		if(getXOff() != 0)
			return true;
		if(isButtonAPressed())
			return true;
		if(isButtonBPressed())
			return true;
		if(isButtonCPressed())
			return true;
		if(isButtonDPressed())
			return true;
		return false;
	}
	

}
