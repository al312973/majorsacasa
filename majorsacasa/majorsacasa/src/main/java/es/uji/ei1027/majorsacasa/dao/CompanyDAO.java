package es.uji.ei1027.majorsacasa.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import es.uji.ei1027.majorsacasa.model.Company;

@Repository
public class CompanyDAO {
	private JdbcTemplate jdbcTemplate;

	@Autowired 
	public void setDataSource(DataSource dataSource){
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	//Añade una empresa nueva a la BBDD
	public void addCompany(Company company){
		jdbcTemplate.update("INSERT INTO COMPANY VALUES(?,?,?,?,?,?,?,?)",
								company.getName(), 
								company.getCIF(), 
								company.getAddress(), 
								company.getContactPersonName(), 
								company.getContactPersonPhoneNumber(), 
								company.getContactPersonEmail(), 
								company.getServiceType(),
								company.getPwd()
								);
	}
	
	//Obtiene una empresa concreta a partir de su CIF
	public Company getCompanyByCIF(String CIF){
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM COMPANY WHERE CIF = ?", new CompanyRowMapper(), CIF);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	//Obtiene una empresa concreta a partir de su CIF
	public Company getCompanyByName(String name){
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM COMPANY WHERE name = ?", new CompanyRowMapper(), name);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	//Obtiene un listado con todas las empresas registradas en el sistema
	public List<Company> getCompanies(){
		try{
			return jdbcTemplate.query("SELECT * FROM COMPANY", new CompanyRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Company>();
		}
	}
		
	//Actualiza los datos de una empresa concreta
	public void updateCompany(Company company){
		jdbcTemplate.update("UPDATE PAY SET NAME = ?, ADDRESS = ?, CONTACTPERSONNAME = ?,"
							+ " CONTACTPERSONPHONENUMBER = ?, PERSONEMAIL = ?, SERVICETYPE = ?, PWD = ? WHERE CIF = ?",
								company.getName(), 
								company.getAddress(), 
								company.getContactPersonName(), 
								company.getContactPersonPhoneNumber(), 
								company.getContactPersonEmail(), 
								company.getServiceType(),
								company.getPwd(),
								company.getCIF()

							);
	}
	
	//Elimina una empresa de la BBDD
	public void deleteCompany(String CIF){
		jdbcTemplate.update("DELETE FROM COMPANY WHERE CIF = ?", CIF);
	}
	
}
