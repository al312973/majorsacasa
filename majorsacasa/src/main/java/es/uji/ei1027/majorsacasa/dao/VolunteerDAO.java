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
import es.uji.ei1027.majorsacasa.model.Volunteer;

@Repository
public class VolunteerDAO {
	private JdbcTemplate jdbcTemplate;

	@Autowired 
	public void setDataSource(DataSource dataSource){
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void addVolunteer(Volunteer volunteer) {
		jdbcTemplate.update("INSERT INTO VOLUNTEER VALUES(?,?,?,?,?,?,?,?,?)",
							volunteer.getName(),
							volunteer.getPhoneNumber(),
							volunteer.getEmail(),
							volunteer.getUsr(),
							volunteer.getPwd(),
							volunteer.getHobbies(),
							volunteer.getApplicationDate(),
							volunteer.getAcceptationDate(),
							//volunteer.getAccepted(),
							volunteer.getBirthDate()
							);
			}
	
	public void deleteVoulunteer(String Usr) {
		jdbcTemplate.update("DELETE FROM VOLUNTEER WHERE USR = ?", Usr);
	}
	
	public void updateVolunteer(Volunteer volunteer){
		jdbcTemplate.update("UPDATE ELDERLY SET NAME = ?, PHONENUMBER = ?, EMAIL = ?, USR = ?, PWD = ?, HOBBIES = ?,"
							+ " APPLICATIONDATE = ?, ACCEPTATIONDATE = ?, BIRTHDATE = ? WHERE USR = ?",
							volunteer.getName(),
							volunteer.getPhoneNumber(),
							volunteer.getEmail(),
							volunteer.getPwd(),
							volunteer.getHobbies(),
							volunteer.getApplicationDate(),
							volunteer.getAcceptationDate(),
							//volunteer.getAccepted(),
							volunteer.getBirthDate(),
							volunteer.getUsr()

							);
			
	}
	
	public Volunteer getVolunteer(String Usr) {
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM VOLUNTEER WHERE USR = ?", new VolunteerRowMapper(), Usr);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	public List<Volunteer> getVolunteers(){
		try{
			return jdbcTemplate.query("SELECT * FROM VOLUNTEER", new VolunteerRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Volunteer>();
		}
		
	}
}
