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
	
	//Añade una nueva factura a la base de datos
	public void addInvoice(Invoice invoice) {
		jdbcTemplate.update("INSERT INTO INVOICE VALUES(?,?,?,?,?)",
							invoice.getDate(),
							invoice.getNumber(),
							invoice.getAmount(),
							invoice.getConcept(),
							invoice.getElderly_dni()

							);
			}
	
	//Obtiene los datos de una factura específica
	public Invoice getInvoice(int number) {
		try{
			return jdbcTemplate.queryForObject("SELECT * FROM INVOICE WHERE NUMBER = ?", new InvoiceRowMapper(), number);
		}catch (EmptyResultDataAccessException e){
			return null;
		}
	}
	
	//Obtiene una lista con todas las facturas registradas en el sistema
	public List<Invoice> getInvoices(){
		try{
			return jdbcTemplate.query("SELECT * FROM INVOICE", new InvoiceRowMapper());
		}catch (EmptyResultDataAccessException e){
			return new ArrayList<Invoice>();
		}	
	}
		
	//Actualiza los datos de una factura específica
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
	
	//Elimina una factura determinada
	public void deleteInvoice(int number) {
		jdbcTemplate.update("DELETE FROM INVOICE WHERE NUMBER = ?", number);
	}
	
}
