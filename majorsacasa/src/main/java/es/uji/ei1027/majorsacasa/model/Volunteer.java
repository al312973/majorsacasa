package es.uji.ei1027.majorsacasa.model;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class Volunteer {
	private String name;
	private String phoneNumber;
	private String email;
	private String usr;
	private String pwd;
	private String hobbies;
	@DateTimeFormat(pattern = "yyyy-mm-dd")
	private Date applicationDate;
	private Date acceptationDate;
	private boolean accepted;
	private Date birthDate;
	
	public Volunteer() {
		
	}

	@Override
	public String toString() {
		return "Volunteer [name=" + name + ", phoneNumber=" + phoneNumber + ", email=" + email + ", usr=" + usr
				+ ", pwd=" + pwd + ", hobbies=" + hobbies + ", applicationDate=" + applicationDate
				+ ", acceptationDate=" + acceptationDate + ", accepted=" + accepted + ", birthDate=" + birthDate + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getUsr() {
		return usr;
	}

	public void setUsr(String usr) {
		this.usr = usr;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getHobbies() {
		return hobbies;
	}

	public void setHobbies(String hobbies) {
		this.hobbies = hobbies;
	}

	public Date getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}

	public Date getAcceptationDate() {
		return acceptationDate;
	}

	public void setAcceptationDate(Date acceptationDate) {
		this.acceptationDate = acceptationDate;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	
}
