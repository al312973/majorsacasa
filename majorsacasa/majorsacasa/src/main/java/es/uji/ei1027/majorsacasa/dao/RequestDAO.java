package es.uji.ei1027.majorsacasa.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import es.uji.ei1027.majorsacasa.model.Request;

@Repository
public class RequestDAO {
	private JdbcTemplate jdbcTemplate;
		
	@Autowired 
	public void setDataSource(DataSource dataSource){
		jdbcTemplate  =new JdbcTemplate(dataSource);
	}
		
	//Añade una nueva solicitud a la BBDD
	public void addRequest(Request request){
		jdbcTemplate.update("INSERT INTO REQUEST VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
							request.getNumber(), 
							request.getServiceType(),
							request.getCreationDate(),
							request.getState(),
							request.getApprovedDate(),
							request.getRejectedDate(),
							request.getComments(),
							request.getBeginningHour(),
							request.getEndingHour(),
							request.getEndDate(),
							request.isFinished(),
							request.getElderly_dni(),
							request.getContract_number(),
							request.getUserCAS());
	}
	
	//Obtiene una solicitud concreta
	public Request getRequest(int number){
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM REQUEST WHERE NUMBER = ?", new RequestRowMapper(), number);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	//Obtiene todas las solicitudes de servicios de comida de una persona mayor
	public List<Request> getFoodRequestsFromElderly(String dni_elderly){
		try{
			return jdbcTemplate.query("SELECT * FROM REQUEST WHERE ELDERLY_DNI = ? AND SERVICETYPE = 0 AND FINISHED = FALSE"
					+ " ORDER BY CREATIONDATE",
					new RequestRowMapper(), dni_elderly);
		}catch(EmptyResultDataAccessException e){
			return new ArrayList<Request>();
		}
	}
	
	//Obtiene todas las solicitudes de servicios sanitarios de una persona mayor
	public List<Request> getHealthRequestsFromElderly(String dni_elderly){
		try{
			return jdbcTemplate.query("SELECT * FROM REQUEST WHERE ELDERLY_DNI = ? AND SERVICETYPE = 1 AND FINISHED = FALSE"
					+ " ORDER BY CREATIONDATE",
					new RequestRowMapper(), dni_elderly);
		}catch(EmptyResultDataAccessException e){
			return new ArrayList<Request>();
		}
	}
	
	//Obtiene todas las solicitudes de servicios sanitarios de una persona mayor
	public List<Request> getCleaningRequestsFromElderly(String dni_elderly){
		try{
			return jdbcTemplate.query("SELECT * FROM REQUEST WHERE ELDERLY_DNI = ? AND SERVICETYPE = 2 AND FINISHED = FALSE"
					+ " ORDER BY CREATIONDATE",
					new RequestRowMapper(), dni_elderly);
		}catch(EmptyResultDataAccessException e){
			return new ArrayList<Request>();
		}
	}
	
	//Obtiene el número de solicitudes que hay en la BBDD
	public int getNumberRequests() {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM REQUEST", Integer.class);
	}
	
	//Finaliza una solicitud con la fecha actual e indica que no se muestre
	public void finishRequest(Request request){
		jdbcTemplate.update("UPDATE REQUEST SET ENDDATE = ?, FINISHED = TRUE WHERE NUMBER = ?",
							request.getEndDate(),
							request.getNumber()
							);
	}
	
	public List<Request> getRequests(){
		try{
			return jdbcTemplate.query("SELECT * FROM REQUEST", new RequestRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Request>();
		}
	}
}
