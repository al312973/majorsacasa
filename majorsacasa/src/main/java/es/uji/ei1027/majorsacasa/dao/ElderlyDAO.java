package es.uji.ei1027.majorsacasa.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import es.uji.ei1027.majorsacasa.model.Elderly;

@Repository
public class ElderlyDAO {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired 
	public void setDataSource(DataSource dataSource){
		jdbcTemplate  =new JdbcTemplate(dataSource);
	}
	
	public void addElderly(Elderly elderly){
		jdbcTemplate.update("INSERT INTO ELDERLY VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)",
								elderly.getName(), 
								elderly.getDNI(),
								elderly.getSurname(),
								elderly.getBirthDate(),
								elderly.getAddress(),
								elderly.getPhoneNumber(),
								elderly.getBankAccountNumber(),
								elderly.getEmail(),
								elderly.getUserPwd(),
								elderly.getDateCreation(),
								elderly.getAlergies(),
								elderly.getDiseases(),
								elderly.getSocialWorker_userCAS());
	}
	
	public void deleteElderly(String DNI){
		jdbcTemplate.update("DELETE FROM ELDERLY WHERE DNI = ?", DNI);
	}
	
	public void updateElderly(Elderly elderly){
		jdbcTemplate.update("UPDATE ELDERLY SET NAME = ?, SURNAME = ?, BIRTHDATE = ?, ADDRESS = ?, PHONENUMBER = ?, BANKACCOUNTNUMBER = ?,"
							+ " EMAIL = ?, USERPWD = ?, DATECREATION = ?, ALERGIES = ?, DISEASES = ?, SOCIALWORKER_USERCAS = ? "
							+ "WHERE DNI = ?",
							elderly.getName(),
							elderly.getSurname(),
							elderly.getBirthDate(),
							elderly.getAddress(),
							elderly.getPhoneNumber(),
							elderly.getBankAccountNumber(),
							elderly.getEmail(),
							elderly.getUserPwd(),
							elderly.getDateCreation(),
							elderly.getAlergies(),
							elderly.getDiseases(),
							elderly.getSocialWorker_userCAS(),
							elderly.getDNI()
							);
	}
	
	public Elderly getElderly(String DNI){
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM ELDERLY WHERE DNI = ?", new ElderlyRowMapper(), DNI);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	public List<Elderly> getElderlies(){
		try{
			return jdbcTemplate.query("SELECT * FROM ELDERLY", new ElderlyRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Elderly>();
		}
		
	}
	
}