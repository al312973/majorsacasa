package es.uji.ei1027.majorsacasa.model;

public class SocialWorker {
	private String name;
	private String userCAS;
	private String pwd;
	private String phoneNumber;
	private String email;
	
	public SocialWorker() {
		
	}

	@Override
	public String toString() {
		return "SocialWorker [name=" + name + ", userCAS=" + userCAS + ", pwd=" + pwd + ", phoneNumber=" + phoneNumber
				+ ", email=" + email + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserCAS() {
		return userCAS;
	}

	public void setUserCAS(String userCAS) {
		this.userCAS = userCAS;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
