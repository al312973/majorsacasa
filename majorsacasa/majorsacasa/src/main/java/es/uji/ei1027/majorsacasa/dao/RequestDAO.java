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
		
		public void addRequest(Request request){
			jdbcTemplate.update("INSERT INTO REQUEST VALUES(?,?,?,?,?,?,?,?,?,?)",
									request.getNumber(), 
									request.getServiceType(),
									request.getCreationDate(),
									request.getState(),
									request.getApprovedDate(),
									request.getRejectedDate(),
									request.getComments(),
									request.getEndDate(),
									request.getElderly_dni(),
									request.getContract_number());
			
		}
		
		public void deleteRequest(int number){
			jdbcTemplate.update("DELETE FROM REQUEST WHERE NUMBER = ?", number);
		}
		
		public void updateRequest(Request request){
			jdbcTemplate.update("UPDATE REQUEST SET SERVICETYPE = ?, CREATIONDATE = ?, STATE = ?, APPROVEDDATE = ?, REJECTEDDATE = ?,"
								+ " COMMENTS = ?, ENDDATE = ?, ELDERLY_DNI = ?, CONTRACT_NUMBER = ? "
								+ "WHERE NUMBER = ?",
								request.getServiceType(),
								request.getCreationDate(),
								request.getState(),
								request.getApprovedDate(),
								request.getRejectedDate(),
								request.getComments(),
								request.getEndDate(),
								request.getElderly_dni(),
								request.getContract_number(),
								request.getNumber()
								);
		}
		
		public Request getRequest(int number){
			try{
				return jdbcTemplate.queryForObject("SELECT * FROM REQUEST WHERE NUMBER = ?", new RequestRowMapper(), number);
			}catch (EmptyResultDataAccessException e){
				return null;
			}
		}
		
		public List<Request> getRequests(){
			try{
				return jdbcTemplate.query("SELECT * FROM REQUEST", new RequestRowMapper());
			}catch (EmptyResultDataAccessException e){
				return new ArrayList<Request>();
			}
			
		}
		
		public List<Request> getRequestsFromElderly(String dni_elderly){
			try{
				return jdbcTemplate.query("SELECT * FROM REQUEST WHERE ELDERLY_DNI = ?", new RequestRowMapper(), dni_elderly);
			}catch(EmptyResultDataAccessException e){
				return new ArrayList<Request>();
			}
			
		}
}
