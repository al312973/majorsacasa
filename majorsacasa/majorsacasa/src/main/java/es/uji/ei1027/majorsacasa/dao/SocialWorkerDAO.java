package es.uji.ei1027.majorsacasa.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import es.uji.ei1027.majorsacasa.model.SocialWorker;

@Repository
public class SocialWorkerDAO {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired 
	public void setDataSource(DataSource dataSource){
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	//Añade un trabajador social
	public void addSocialWorker(SocialWorker socialworker){
		jdbcTemplate.update("INSERT INTO SOCIALWORKER VALUES(?,?,?,?,?)",
								socialworker.getName(), 
								socialworker.getUserCAS(), 
								socialworker.getPwd(), 
								socialworker.getPhoneNumber(), 
								socialworker.getEmail()
								);	
	}
	
	//obtiene un listado de todos los trabajadores sociales
	public List<SocialWorker> getSocialWorkers(){
		try{
			return jdbcTemplate.query("SELECT * FROM SOCIALWORKER", new SocialWorkerRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<SocialWorker>();
		}
		
	}
		
	//Obtiene los datos de un trabajador social a partir de su nombre de usuario
	public SocialWorker getSocialWorkerByUserCAS(String UserCAS){
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM SOCIALWORKER WHERE USERCAS = ?", new SocialWorkerRowMapper(), UserCAS);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	//Obtiene los datos de un trabajador social a partir de su correo electrónico
	public SocialWorker getSocialWorkerByEmail(String Email){
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM SOCIALWORKER WHERE EMAIL = ?", new SocialWorkerRowMapper(), Email);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	//Actualiza la información de un trabajador social concreto
	public void updateSocialWorker(SocialWorker socialworker){
		jdbcTemplate.update("UPDATE SOCIALWORKER SET NAME = ?, PWD = ?, PHONENUMBER = ?, EMAIL = ?"
							+ " WHERE USERCAS = ?",
							socialworker.getName(),
							socialworker.getPwd(),
							socialworker.getPhoneNumber(),
							socialworker.getEmail(),
							socialworker.getUserCAS()

							);
	}
	
	//Elimina un trabajador social
	public void deleteSocialWorker(String UserCAS){
		jdbcTemplate.update("DELETE FROM SOCIALWORKER WHERE USERCAS = ?", UserCAS);
	}
	
}