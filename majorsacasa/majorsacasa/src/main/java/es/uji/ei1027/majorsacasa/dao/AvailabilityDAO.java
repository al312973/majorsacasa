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
	
	//Añade una nueva disponibilidad
	public void addAvailability(Availability availability){
		jdbcTemplate.update("INSERT INTO AVAILABILITY VALUES(?,?,?,?,?,?,?)",
								availability.getDate(), 
								availability.getBeginningHour(), 
								availability.getEndingHour(), 
								availability.isStateAvailable(), 
								availability.getUnsuscribeDate(),
								availability.getElderly_dni(), 
								availability.getVolunteer_usr()
								);
	}
	
	//Obtiene una disponibilidad concreta de un voluntario
	public Availability getAvailability(Date date, LocalTime beginningHour, String volunteer_usr){
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM AVAILABILITY WHERE DATE = ? AND BEGINNINGHOUR = ? AND VOLUNTEER_USR = ?", 
					new AvailabilityRowMapper(), date, beginningHour, volunteer_usr );
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
			
	//Obtiene todas las disponibilidades no asignadas y de voluntarios verificados
	public List<Availability> getFreeAvailabilities(){
		try{
			return jdbcTemplate.query("SELECT * FROM AVAILABILITY WHERE STATEAVAILABLE = TRUE AND ELDERLY_DNI IS NULL"
					+ " ORDER BY DATE, BEGINNINGHOUR", 
					new AvailabilityRowMapper());
		}catch(EmptyResultDataAccessException e){
			return new ArrayList<Availability>();
		}
	}
	
	//Obtiene todas las disponibilidades asignadas a una persona mayor
	public List<Availability> getAvailabilitiesFromElderly(String elderly_dni){
		try{
			return jdbcTemplate.query("SELECT * FROM AVAILABILITY WHERE ELDERLY_DNI = ? AND STATEAVAILABLE = TRUE"
					+ " ORDER BY DATE, BEGINNINGHOUR", 
					new AvailabilityRowMapper(), elderly_dni);
		}catch(EmptyResultDataAccessException e){
			return new ArrayList<Availability>();
		}
	}
	
	//Obtiene todas las disponibilidades de un voluntario que no estén finalizadas
	public List<Availability> getAvailabilitiesFromVolunteer(String volunteer){
		try{
			return jdbcTemplate.query("SELECT * FROM AVAILABILITY WHERE VOLUNTEER_USR = ? AND UNSUSCRIBEDATE IS NULL"
					+ " ORDER BY DATE, BEGINNINGHOUR", 
					new AvailabilityRowMapper(), volunteer);
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Availability>();
		}
	}
	
	//Actualiza los atributos de una disponibilidad
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
			
	//Asocia un beneficiario a una disponibilidad
	public void setElderly(Availability availability){
		jdbcTemplate.update("UPDATE AVAILABILITY SET ELDERLY_DNI = ? WHERE DATE = ? AND BEGINNINGHOUR = ? AND VOLUNTEER_USR = ?", 
					availability.getElderly_dni(),
					availability.getDate(),
					availability.getBeginningHour(),
					availability.getVolunteer_usr()
				);
	}
	
	//Finaliza una disponibilidad al dar de baja un servicio asignado
	public void finishAvailability(Availability availability){
		jdbcTemplate.update("UPDATE AVAILABILITY SET UNSUSCRIBEDATE = ?, STATEAVAILABLE = FALSE WHERE DATE = ? AND BEGINNINGHOUR = ? AND VOLUNTEER_USR = ?",
								availability.getUnsuscribeDate(),
								availability.getDate(), 
								availability.getBeginningHour(), 
								availability.getVolunteer_usr()
								);
	}
		
	//Elimina de la BBDD una disponibilidad no asignada
	public void deleteAvailability(Availability availability){
		jdbcTemplate.update("DELETE FROM AVAILABILITY WHERE DATE = ? AND BEGINNINGHOUR = ? AND VOLUNTEER_USR = ?",
								availability.getDate(), 
								availability.getBeginningHour(), 
								availability.getVolunteer_usr()
								);
	}
}
