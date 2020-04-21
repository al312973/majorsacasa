package es.uji.ei1027.majorsacasa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import es.uji.ei1027.majorsacasa.model.Pay;

public final class PayRowMapper implements RowMapper<Pay>{
	public Pay mapRow(ResultSet rs, int rowNum) throws SQLException {
		Pay pay = new Pay();
		pay.setRequest_number(rs.getInt("request_number"));
		pay.setInvoice_number(rs.getInt("invoice_number"));
		return pay;
		
	}

}
