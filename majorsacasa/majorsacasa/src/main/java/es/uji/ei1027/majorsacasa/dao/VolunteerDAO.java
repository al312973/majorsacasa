package es.uji.ei1027.majorsacasa.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.uji.ei1027.majorsacasa.model.Volunteer;

@Repository
public class VolunteerDAO {
	private JdbcTemplate jdbcTemplate;

	@Autowired 
	public void setDataSource(DataSource dataSource){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
	}
	
	//Añade un nuevo voluntario a la BBDD
	public void addVolunteer(Volunteer volunteer) {
		jdbcTemplate.update("INSERT INTO VOLUNTEER VALUES(?,?,?,?,?,?,?,?,?,?,?)",
							volunteer.getName(),
							volunteer.getPhoneNumber(),
							volunteer.getEmail(),
							volunteer.getUsr(),
							volunteer.getPwd(),
							volunteer.getHobbies(),
							volunteer.getApplicationDate(),
							volunteer.getAcceptationDate(),
							volunteer.isAccepted(),
							volunteer.getBirthDate(),
							volunteer.getEndDate()
							);
			}
	
	//Obtiene un voluntario a partir de su nombre de usuario
	public Volunteer getVolunteerByUsr(String Usr) {
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM VOLUNTEER WHERE USR = ?", new VolunteerRowMapper(), Usr);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	//Obtiene un voluntario a partir de su email
	public Volunteer getVolunteerByEmail(String email) {
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM VOLUNTEER WHERE EMAIL = ?", new VolunteerRowMapper(), email);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	//Lista todos los voluntarios no aceptados de la BBDD
	public List<Volunteer> getNotAcceptedVolunteers(){
		try{
			return jdbcTemplate.query("SELECT * FROM VOLUNTEER WHERE ACCEPTED = FALSE AND ENDDATE IS NULL", new VolunteerRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Volunteer>();
		}
		
	}
		
	//Actualiza la información de un voluntario
	public void updateVolunteer(Volunteer volunteer){
		jdbcTemplate.update("UPDATE VOLUNTEER SET NAME = ?, PHONENUMBER = ?, EMAIL = ?, USR = ?, PWD = ?, HOBBIES = ?,"
							+" BIRTHDATE = ? WHERE USR = ?",
							volunteer.getName(),
							volunteer.getPhoneNumber(),
							volunteer.getEmail(),
							volunteer.getUsr(),
							volunteer.getPwd(),
							volunteer.getHobbies(),
							volunteer.getBirthDate(),
							volunteer.getUsr()
							);
	}
	
	//Establece una fecha a partir de la cual el voluntario ya no prestará servicios
	public void setVolunteerEndDate(Date endDate, String volunteerUsr){
		jdbcTemplate.update("UPDATE VOLUNTEER SET ENDDATE = ? WHERE USR = ?",
							endDate,
							volunteerUsr
							);
	}
	
	//Establece un voluntario como aceptado
	public void acceptVolunteer(String usr){
		jdbcTemplate.update("UPDATE VOLUNTEER SET ACCEPTATIONDATE = ?, ACCEPTED = TRUE WHERE USR = ?", new java.sql.Date(new Date().getTime()) , usr);
	}
	//Borra un voluntario de la BBDD
	public void deleteVolunteer(String Usr) {
		jdbcTemplate.update("UPDATE VOLUNTEER SET ENDDATE = ? WHERE USR = ?", new java.sql.Date(new Date().getTime()), Usr);
	}

}
