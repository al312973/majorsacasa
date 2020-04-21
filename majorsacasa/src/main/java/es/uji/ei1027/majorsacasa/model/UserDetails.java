package es.uji.ei1027.majorsacasa.model;

public class UserDetails {
	private String email;
	private String password;
	private String type;
	private boolean pwdCorrect;
	
	
	public UserDetails() {
	}

	public UserDetails(String email, String password, String type, boolean pwdCorrect) {
		this.email = email;
		this.password = password;
		this.type = type;
		this.pwdCorrect = pwdCorrect;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password; 
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isPwdCorrect() {
		return pwdCorrect;
	}

	public void setPwdCorrect(boolean pwdCorrect) {
		this.pwdCorrect = pwdCorrect;
	}
}
