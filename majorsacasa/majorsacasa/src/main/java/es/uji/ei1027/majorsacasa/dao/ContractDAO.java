package es.uji.ei1027.majorsacasa.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import es.uji.ei1027.majorsacasa.model.Contract;

@Repository
public class ContractDAO {
	private JdbcTemplate jdbcTemplate;

	@Autowired 
	public void setDataSource(DataSource dataSource){
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void addContract(Contract contract){
		jdbcTemplate.update("INSERT INTO CONTRACT VALUES(?,?,?,?,?,?,?,?,?,?)",
								contract.getNumber(), 
								contract.getDateBeginning(), 
								contract.getDescription(), 
								contract.getServiceType(), 
								contract.getQuantityServices(), 
								contract.getUnitsOfMeasure(), 
								contract.getPriceUnit(), 
								contract.getBeginningHour(), 
								contract.getEndingHour(), 
								contract.getCompany_cif() 
								);
	}
	
	public void deleteContract(int number){
		jdbcTemplate.update("DELETE FROM PAY WHERE NUMBER = ?", number);
	}
	
	public void updateContract(Contract contract){
		jdbcTemplate.update("UPDATE PAY SET DATEBEGINNING = ?, DESCRIPTION = ?, SERVICETYPE = ?,"
							+ " QUANTITYSERVICES = ?, UNITSOFMEASURE = ?, PRICEUNIT = ?, BEGINNINGHOUR = ?,"
							+ " ENDINGHOUR = ?, COMPANY_CIF = ? WHERE NUMBER = ?",
								contract.getDateBeginning(), 
								contract.getDescription(), 
								contract.getServiceType(), 
								contract.getQuantityServices(), 
								contract.getUnitsOfMeasure(), 
								contract.getPriceUnit(), 
								contract.getBeginningHour(), 
								contract.getEndingHour(), 
								contract.getCompany_cif(),
								contract.getNumber() 
							);
	}
	
	public Contract getContract(int number){
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM CONTRACT WHERE NUMBER = ?", new ContractRowMapper(), number);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	public List<Contract> getContracts(){
		try{
			return jdbcTemplate.query("SELECT * FROM CONTRACT", new ContractRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Contract>();
		}
	}
	
}
