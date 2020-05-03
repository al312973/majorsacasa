package es.uji.ei1027.majorsacasa.model;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class Invoice {
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;
	private int number;
	private float amount;
	private String concept;
	private String elderly_dni;
	
	public Invoice() {
		
	}

	@Override
	public String toString() {
		return "Invoice [date=" + date + ", number=" + number + ", amount=" + amount + ", concept=" + concept
				+ ", elderly_dni=" + elderly_dni + "]";
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getElderly_dni() {
		return elderly_dni;
	}

	public void setElderly_dni(String elderly_dni) {
		this.elderly_dni = elderly_dni;
	}
	
	
}
