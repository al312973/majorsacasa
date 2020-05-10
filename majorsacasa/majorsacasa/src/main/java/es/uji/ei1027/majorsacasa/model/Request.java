package es.uji.ei1027.majorsacasa.model;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class Request {
	private int number;
	private int serviceType;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date creationDate;
	private int state;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date approvedDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date rejectedDate;
	private String comments;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endDate;
	private boolean finished;
	private String elderly_dni;
	private Integer contract_number;
	private String userCAS;
	
	public Request() {
		
	}
	
	@Override
	public String toString() {
		return "Request [number=" + number + ", serviceType=" + serviceType + ", creationDate=" + creationDate
				+ ", state=" + state + ", approvedDate=" + approvedDate + ", rejectedDate=" + rejectedDate
				+ ", comments=" + comments + ", endDate=" + endDate + ", finished=" + finished + ", elderly_dni="
				+ elderly_dni + ", contract_number=" + contract_number + ", userCAS=" + userCAS + "]";
	}

	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public int getServiceType() {
		return serviceType;
	}
	
	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public int getState() {
		return state;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public Date getApprovedDate() {
		return approvedDate;
	}
	
	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}
	
	public Date getRejectedDate() {
		return rejectedDate;
	}
	
	public void setRejectedDate(Date rejectedDate) {
		this.rejectedDate = rejectedDate;
	}
	
	public String getComments() {
		return comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public String getElderly_dni() {
		return elderly_dni;
	}
	
	public void setElderly_dni(String elderly_dni) {
		this.elderly_dni = elderly_dni;
	}
	
	public Integer getContract_number() {
		return contract_number;
	}
	
	public void setContract_number(Integer contract_number) {
		this.contract_number = contract_number;
	}

	public String getUserCAS() {
		return userCAS;
	}

	public void setUserCAS(String userCAS) {
		this.userCAS = userCAS;
	}
}
