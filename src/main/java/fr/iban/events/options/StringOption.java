package fr.iban.events.options;

public class StringOption extends Option{

	private String stringValue;
	
	
	public StringOption(String name, String defaultValue) {
		super(name);
		stringValue = defaultValue;
	}
	
	public StringOption(String name) {
		super(name);
		stringValue = "nd";
	}
	
	
	public String getStringValue() {
		return stringValue;
	}
	
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

}
