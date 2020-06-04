package es.uji.ei1027.majorsacasa.model;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class Contract {
	private int number;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dateBeginning;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dateEnding;
	private String description;
	private int serviceType;
	private int quantityServices;
	private String unitsOfMeasure;
	private float priceUnit;
	private String company_cif;
	
	public Contract() {
		
	}

	@Override
	public String toString() {
		return "Contract [number=" + number + ", dateBeginning=" + dateBeginning + ", dateEnding=" + dateEnding + ", description=" + description
				+ ", serviceType=" + serviceType + ", quantityServices=" + quantityServices + ", unitsOfMeasure="
				+ unitsOfMeasure + ", priceUnit=" + priceUnit + ", company_cif=" + company_cif + "]";
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Date getDateBeginning() {
		return dateBeginning;
	}

	public void setDateBeginning(Date dateBeginning) {
		this.dateBeginning = dateBeginning;
	}
	
	public Date getDateEnding(){
		return dateEnding;
	}
	
	public void setDateEnding(Date dateEnding){
		this.dateEnding = dateEnding;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

	public int getQuantityServices() {
		return quantityServices;
	}

	public void setQuantityServices(int quantityServices) {
		this.quantityServices = quantityServices;
	}

	public String getUnitsOfMeasure() {
		return unitsOfMeasure;
	}

	public void setUnitsOfMeasure(String unitsOfMeasure) {
		this.unitsOfMeasure = unitsOfMeasure;
	}

	public float getPriceUnit() {
		return priceUnit;
	}

	public void setPriceUnit(float priceUnit) {
		this.priceUnit = priceUnit;
	}

	public String getCompany_cif() {
		return company_cif;
	}

	public void setCompany_cif(String company_cif) {
		this.company_cif = company_cif;
	}
	
	
}
