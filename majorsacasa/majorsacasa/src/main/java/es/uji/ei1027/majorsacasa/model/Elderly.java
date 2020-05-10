package es.uji.ei1027.majorsacasa.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class Elderly {
	private String name;
	private String DNI;
	private String surname;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthDate;
	private String address;
	private String phoneNumber;
	private String bankAccountNumber;
	private String email;
	private String userPwd;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dateCreation;
	private String alergies;
	private String diseases;
	
	public Elderly() {
		
	}

	@Override
	public String toString() {
		return "Elderly [name=" + name + ", DNI=" + DNI + ", surname=" + surname + ", birthDate=" + birthDate
				+ ", address=" + address + ", phoneNumber=" + phoneNumber + ", bankAccountNumber=" + bankAccountNumber
				+ ", email=" + email + ", userPwd=" + userPwd + ", dateCreation=" + dateCreation + ", alergies="
				+ alergies + ", diseases=" + diseases + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDNI() {
		return DNI;
	}

	public void setDNI(String dNI) {
		DNI = dNI;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public String getAlergies() {
		return alergies;
	}

	public void setAlergies(String alergies) {
		this.alergies = alergies;
	}

	public String getDiseases() {
		return diseases;
	}

	public void setDiseases(String diseases) {
		this.diseases = diseases;
	}
}
