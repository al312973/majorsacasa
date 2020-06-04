package es.uji.ei1027.majorsacasa.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import es.uji.ei1027.majorsacasa.model.Elderly;

@Repository
public class ElderlyDAO {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired 
	public void setDataSource(DataSource dataSource){
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	//Añade una nueva persona mayor en la base de datos
	public void addElderly(Elderly elderly){
		jdbcTemplate.update("INSERT INTO ELDERLY VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",
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
								elderly.getDiseases());
	}
	
	//Obtiene los datos de una persona mayor a partir de su DNI
	public Elderly getElderlyByDNI(String DNI){
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM ELDERLY WHERE DNI = ?", new ElderlyRowMapper(), DNI);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	//Obtiene los datos de una persona mayor a partir de su email
	public Elderly getElderlyByEmail(String Email){
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM ELDERLY WHERE EMAIL = ?", new ElderlyRowMapper(), Email);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	//Obtiene el listado de todas las personas mayores registradas en el sistema
	public List<Elderly> getElderlies(){
		try{
			return jdbcTemplate.query("SELECT * FROM ELDERLY", new ElderlyRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Elderly>();
		}
		
	}

	//Actualiza los datos de una persona mayor específica
	public void updateElderly(Elderly elderly){
		jdbcTemplate.update("UPDATE ELDERLY SET NAME = ?, SURNAME = ?, BIRTHDATE = ?, ADDRESS = ?, PHONENUMBER = ?, BANKACCOUNTNUMBER = ?,"
							+ " EMAIL = ?, USERPWD = ?, ALERGIES = ?, DISEASES = ? "
							+ "WHERE DNI = ?",
							elderly.getName(),
							elderly.getSurname(),
							elderly.getBirthDate(),
							elderly.getAddress(),
							elderly.getPhoneNumber(),
							elderly.getBankAccountNumber(),
							elderly.getEmail(),
							elderly.getUserPwd(),
							elderly.getAlergies(),
							elderly.getDiseases(),
							elderly.getDNI()
							);
	}
	
	//Elimina una persona mayor de la base de datos
	public void deleteElderly(String DNI){
		jdbcTemplate.update("DELETE FROM ELDERLY WHERE DNI = ?", DNI);
	}
		
}