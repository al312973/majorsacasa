package es.uji.ei1027.majorsacasa.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import es.uji.ei1027.majorsacasa.model.Invoice;

@Repository
public class InvoiceDAO {
	private JdbcTemplate jdbcTemplate;

	@Autowired 
	public void setDataSource(DataSource dataSource){
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void addInvoice(Invoice invoice) {
		jdbcTemplate.update("INSERT INTO INVOICE VALUES(?,?,?,?,?)",
							invoice.getDate(),
							invoice.getNumber(),
							invoice.getAmount(),
							invoice.getConcept(),
							invoice.getElderly_dni()

							);
			}
	
	public void deleteInvoice(int number) {
		jdbcTemplate.update("DELETE FROM INVOICE WHERE NUMBER = ?", number);
	}
	
	public void updateInvoice(Invoice invoice){
		jdbcTemplate.update("UPDATE INVOICE SET DATE = ?, AMOUNT = ?, CONCEPT = ?, ELDERLY_DNI = ?"
							+ " WHERE NUMBER = ?",
							invoice.getDate(),
							invoice.getAmount(),
							invoice.getConcept(),
							invoice.getElderly_dni(),
							invoice.getNumber()
							);
	}
	
	public Invoice getInvoice(int number) {
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM INVOICE WHERE NUMBER = ?", new InvoiceRowMapper(), number);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	public List<Invoice> getInvoices(){
		try{
			return jdbcTemplate.query("SELECT * FROM INVOICE", new InvoiceRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Invoice>();
		}
		
	}
}
