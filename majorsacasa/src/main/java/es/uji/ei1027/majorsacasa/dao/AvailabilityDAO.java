package es.uji.ei1027.majorsacasa.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import es.uji.ei1027.majorsacasa.model.Availability;

@Repository
public class AvailabilityDAO {
	private JdbcTemplate jdbcTemplate;

	@Autowired 
	public void setDataSource(DataSource dataSource){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		
		
		
		
	}
	
	//Crea una nueva disponibilidad
	public void addAvailability(Availability availability){
		jdbcTemplate.update("INSERT INTO AVAILABILITY VALUES(?,?,?,?,?)",
								availability.getDate(), 
								availability.getBegginingHour(), 
								availability.getEndingHour(), 
								availability.isStateAvailable(), 
								availability.getElderly_dni(), 
								availability.getVolunteer_usr()

								);
	}
	
//	//Lista todas las disponibilidades registradas en la BBDD
//	public List<Availability> getAvailabilities(){
//		try{
//			return jdbcTemplate.query("SELECT * FROM AVAILABILITY", new AvailabilityRowMapper());
//		}catch (EmptyResultDataAccessException e){
//			return new ArrayList<Availability>();
//		}
//	}
	
	//Lista todas las disponibilidades de un voluntario concreto
	public List<Availability> getAvailabilitiesFromVolunteer(String volunteer){
		try{
			return jdbcTemplate.query("SELECT * FROM AVAILABILITY WHERE VOLUNTEER_USR = ?", 
					new AvailabilityRowMapper(), volunteer);
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Availability>();
		}
	}
	
	public void deleteAvailability(Availability availability){
		jdbcTemplate.update("DELETE FROM AVAILABILAITY WHERE DATE = ?, BEGGININGHOUR = ?, ENDINGHOUR = ?",
								availability.getDate(), 
								availability.getBegginingHour(), 
								availability.getEndingHour()
								);
	}
	
	public void updateAvailability(Availability availability){
		jdbcTemplate.update("UPDATE AVAILABILAITY SET ELDERLY_DNI = ?, VOLUNTEER_USR = ?"
							+ " WHERE DATE = ?, BEGGININGHOUR = ?, ENDINGHOUR = ?",
//								
								//availability.getStateAvailable(), 
								availability.getElderly_dni(), 
								availability.getVolunteer_usr(),
								availability.getDate(), 
								availability.getBegginingHour(), 
								availability.getEndingHour() 
							);
	}
	
	public Availability getAvailability(Availability availability){
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM AVAILABILAITY WHERE DATE = ?, BEGGININGHOUR = ?, ENDINGHOUR = ?", 
					new AvailabilityRowMapper(),
					availability.getDate(), 
					availability.getBegginingHour(), 
					availability.getEndingHour() 
					);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
}

