package teammates.e2e.pageobjects;

public class AddCookieParameter {
	public String name;
	public String value;
	public boolean isSecure;
	public boolean isHttpOnly;

	public AddCookieParameter(String name, String value, boolean isSecure, boolean isHttpOnly) {
		this.name = name;
		this.value = value;
		this.isSecure = isSecure;
		this.isHttpOnly = isHttpOnly;
	}
}