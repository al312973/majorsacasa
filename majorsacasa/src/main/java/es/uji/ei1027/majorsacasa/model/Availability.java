package es.uji.ei1027.majorsacasa.model;

import java.util.Date;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;

public class Availability {
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date date;
	private LocalTime begginingHour;
	private LocalTime endingHour;
	private boolean stateAvailable;
	private String elderly_dni;
	private String volunteer_usr;
	
	public Availability() {
		
	}

	@Override
	public String toString() {
		return "Availability [date=" + date + ", begginingHour=" + begginingHour + ", endingHour=" + endingHour
				+ ", stateAvailable=" + stateAvailable + ", elderly_dni=" + elderly_dni + ", volunteer_usr="
				+ volunteer_usr + "]";
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public LocalTime getBegginingHour() {
		return begginingHour;
	}

	public void setBegginingHour(LocalTime begginingHour) {
		this.begginingHour = begginingHour;
	}

	public LocalTime getEndingHour() {
		return endingHour;
	}

	public void setEndingHour(LocalTime endingHour) {
		this.endingHour = endingHour;
	}

	public boolean isStateAvailable() {
		return stateAvailable;
	}

	public void setStateAvailable(boolean stateAvailable) {
		this.stateAvailable = stateAvailable;
	}

	public String getElderly_dni() {
		return elderly_dni;
	}

	public void setElderly_dni(String elderly_dni) {
		this.elderly_dni = elderly_dni;
	}

	public String getVolunteer_usr() {
		return volunteer_usr;
	}

	public void setVolunteer_usr(String volunteer_usr) {
		this.volunteer_usr = volunteer_usr;
	}
	
	
}
