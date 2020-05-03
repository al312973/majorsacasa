package es.uji.ei1027.majorsacasa.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.uji.ei1027.majorsacasa.model.Availability;

@Repository
public class AvailabilityDAO {
	private JdbcTemplate jdbcTemplate;

	@Autowired 
	public void setDataSource(DataSource dataSource){
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void addAvailability(Availability availability){
		jdbcTemplate.update("INSERT INTO AVAILABILITY VALUES(?,?,?,?,?,?)",
								availability.getDate(), 
								availability.getBeginningHour(), 
								availability.getEndingHour(), 
								availability.isStateAvailable(), 
								availability.getElderly_dni(), 
								availability.getVolunteer_usr()
								);
	}
	
	public void deleteAvailability(Availability availability){
		jdbcTemplate.update("DELETE FROM AVAILABILITY WHERE DATE = ? AND BEGINNINGHOUR = ? AND VOLUNTEER_USR = ?",
								availability.getDate(), 
								availability.getBeginningHour(), 
								availability.getVolunteer_usr()
								);
	}
	
	public void updateAvailability(Availability lastAvailability, Availability currentAvailability){
		jdbcTemplate.update("UPDATE AVAILABILITY SET DATE = ?, BEGINNINGHOUR = ?, ENDINGHOUR = ?"
							+ " WHERE DATE = ? AND BEGINNINGHOUR = ? AND VOLUNTEER_USR = ?", 
								currentAvailability.getDate(), 
								currentAvailability.getBeginningHour(),
								currentAvailability.getEndingHour(),
								lastAvailability.getDate(), 
								lastAvailability.getBeginningHour(),
								lastAvailability.getVolunteer_usr()
							);
	}
	
	public Availability getAvailability(Date date, LocalTime beginningHour, String volunteer_usr){
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM AVAILABILITY WHERE DATE = ? AND BEGINNINGHOUR = ? AND VOLUNTEER_USR = ?", 
					new AvailabilityRowMapper(),
					date, 
					beginningHour, 
					volunteer_usr 
					);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	public List<Availability> getFreeAvailability(){
		try{
			return jdbcTemplate.query("SELECT * FROM AVAILABILITY WHERE ELDERLY_DNI IS NULL", new AvailabilityRowMapper());
		}catch(EmptyResultDataAccessException e){
			return new ArrayList<Availability>();
		}
	}
	
	public List<Availability> getAvailabilitiesFromElderly(String elderly_dni){
		try{
			return jdbcTemplate.query("SELECT * FROM AVAILABILITY WHERE ELDERLY_DNI = ?", new AvailabilityRowMapper(), elderly_dni);
		}catch(EmptyResultDataAccessException e){
			return new ArrayList<Availability>();
		}
	}
	
	public List<Availability> getAvailabilitiesFromVolunteer(String volunteer){
		try{
			return jdbcTemplate.query("SELECT * FROM AVAILABILITY WHERE VOLUNTEER_USR = ?", 
					new AvailabilityRowMapper(), volunteer);
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Availability>();
		}
	}
}

