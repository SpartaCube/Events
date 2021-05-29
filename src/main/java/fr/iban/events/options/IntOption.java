package fr.iban.events.options;

public class IntOption extends Option {
	
	private int intValue;
	
	public IntOption(String name, int defaultVal) {
		super(name);
		this.intValue = defaultVal;
	}
	
	public int getIntValue() {
		return intValue;
	}
	
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
}
