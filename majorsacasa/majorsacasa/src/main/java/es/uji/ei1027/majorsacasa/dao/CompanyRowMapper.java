package es.uji.ei1027.majorsacasa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import es.uji.ei1027.majorsacasa.model.Company;

public final class CompanyRowMapper implements RowMapper<Company> {
	public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
		Company company = new Company();
		company.setName(rs.getString("name"));
		company.setCIF(rs.getString("CIF"));
		company.setAddress(rs.getString("address"));
		company.setContactPersonName(rs.getString("contactPersonName"));
		company.setContactPersonPhoneNumber(rs.getString("contactPersonPhoneNumber"));
		company.setContactPersonEmail(rs.getString("contactPersonEmail"));
		company.setServiceType(rs.getInt("serviceType"));
		return company;
	}

}