package es.uji.ei1027.majorsacasa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import es.uji.ei1027.majorsacasa.model.Invoice;

public final class InvoiceRowMapper implements RowMapper<Invoice> {
	public Invoice mapRow(ResultSet rs, int rowNum) throws SQLException {
		Invoice invoice = new Invoice();
		invoice.setDate(rs.getDate("date"));
		invoice.setNumber(rs.getInt("number"));
		invoice.setAmount(rs.getFloat("amount"));
		invoice.setConcept(rs.getString("concept"));
		invoice.setElderly_dni("elderly_dni");
		return invoice;
	}
}



