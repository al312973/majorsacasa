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
		jdbcTemplate.update("INSERT INTO CONTRACT VALUES(?,?,?,?,?,?,?,?,?)",
								contract.getNumber(), 
								contract.getDateBeginning(), 
								contract.getDateEnding(),
								contract.getDescription(), 
								contract.getServiceType(), 
								contract.getQuantityServices(), 
								contract.getUnitsOfMeasure(), 
								contract.getPriceUnit(),
								contract.getCompany_cif() 
								);
	}
	
	public void deleteContract(int number){
		jdbcTemplate.update("DELETE FROM PAY WHERE NUMBER = ?", number);
	}
	
	//Obtiene un contrato específico
	public Contract getContract(int number){
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM CONTRACT WHERE NUMBER = ?", new ContractRowMapper(), number);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	//Actualiza un contrato específico
	public void updateContract(Contract contract){
		jdbcTemplate.update("UPDATE CONTRACT SET DATEBEGINNING = ?, DATEENDING = ?, DESCRIPTION = ?, SERVICETYPE = ?,"
							+ " QUANTITYSERVICES = ?, UNITSOFMEASURE = ?, PRICEUNIT = ?, COMPANY_CIF = ? WHERE NUMBER = ?",
								contract.getDateBeginning(), 
								contract.getDateEnding(),
								contract.getDescription(), 
								contract.getServiceType(), 
								contract.getQuantityServices(), 
								contract.getUnitsOfMeasure(), 
								contract.getPriceUnit(), 
								contract.getCompany_cif(),
								contract.getNumber() 
							);
	}
	
	public List<Contract> getContracts(){
		try{
			return jdbcTemplate.query("SELECT * FROM CONTRACT", new ContractRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Contract>();
		}
	}
	
	public List<Contract> getFreeContracts(){
		try{
			return jdbcTemplate.query(" select * from contract where  number not in ( select contract_number from request where contract_number is not null) ", new ContractRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Contract>();
		}		
	}
	
	//Obtiene el número de contratos que hay en la BBDD
	public int getNumberContracts() {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM CONTRACT", Integer.class);
	}
	
}
