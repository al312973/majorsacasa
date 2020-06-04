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
	
	//Añade un nuevo contrato de una empresa
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
	
	//Obtiene un contrato específico
	public Contract getContract(int number){
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM CONTRACT WHERE NUMBER = ?", new ContractRowMapper(), number);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	//Obtiene todos los contratos de servicios de comida no finalizados
	public List<Contract> getFoodContracts(){
		try{
			return jdbcTemplate.query("SELECT * FROM CONTRACT WHERE SERVICETYPE=0 AND "
					+ "(DATEENDING >= NOW() OR DATEENDING IS NULL)", new ContractRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Contract>();
		}
	}
	
	//Obtiene todos los contratos sanitarios no finalizados
	public List<Contract> getHealthContracts(){
		try{
			return jdbcTemplate.query("SELECT * FROM CONTRACT WHERE SERVICETYPE=1 AND "
					+ "(DATEENDING >= NOW() OR DATEENDING IS NULL)", new ContractRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Contract>();
		}
	}
	
	//Obtiene todos los contratos de limpieza no finalizados
	public List<Contract> getCleaningContracts(){
		try{
			return jdbcTemplate.query("SELECT * FROM CONTRACT WHERE SERVICETYPE=2 AND"
					+ "(DATEENDING >= NOW() OR DATEENDING IS NULL)", new ContractRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Contract>();
		}
	}
	
	//Obtiene todos los contratos no asignados a alguna solicitud
	public List<Contract> getFreeContracts(){
		try{
			return jdbcTemplate.query(" select * from contract where number not in ( select contract_number from request where contract_number is not null) ", new ContractRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Contract>();
		}		
	}
	
	//Obtiene el número de contratos que hay en la BBDD
	public int getNumberContracts() {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM CONTRACT", Integer.class);
	}

	//Actualiza los datos de un contrato específico
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
	
	//Elimina un contrato concreto
	public void deleteContract(int number){
		jdbcTemplate.update("DELETE FROM CONTRACT WHERE NUMBER = ?", number);
	}

}
