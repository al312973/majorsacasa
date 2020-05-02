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
	
	public void addCompany(Company company){
		jdbcTemplate.update("INSERT INTO COMPANY VALUES(?,?,?,?,?,?,?)",
								company.getName(), 
								company.getCIF(), 
								company.getAddress(), 
								company.getContactPersonName(), 
								company.getContactPersonPhoneNumber(), 
								company.getContactPersonEmail(), 
								company.getServiceType()
								);
	}
	
	public void deleteCompany(String CIF){
		jdbcTemplate.update("DELETE FROM COMPANY WHERE CIF = ?", CIF);
	}
	
	public void updateCompany(Company company){
		jdbcTemplate.update("UPDATE PAY SET NAME = ?, ADDRESS = ?, CONTACTPERSONNAME = ?,"
							+ " CONTACTPERSONPHONENUMBER = ?, PERSONEMAIL = ?, SERVICETYPE = ? WHERE CIF = ?",
								company.getName(), 
								company.getAddress(), 
								company.getContactPersonName(), 
								company.getContactPersonPhoneNumber(), 
								company.getContactPersonEmail(), 
								company.getServiceType(),
								company.getCIF()

							);
	}
	
	public Company getCompany(String CIF){
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM COMPANY WHERE CIF = ?", new CompanyRowMapper(), CIF);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	public List<Company> getCompanys(){
		try{
			return jdbcTemplate.query("SELECT * FROM COMPANY", new CompanyRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Company>();
		}
	}
	
}
