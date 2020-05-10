package es.uji.ei1027.majorsacasa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import es.uji.ei1027.majorsacasa.model.Request;

public final class RequestRowMapper implements RowMapper<Request>{

	public Request mapRow(ResultSet rs, int rowNum) throws SQLException {
		Request request = new Request();
		request.setNumber(rs.getInt("number"));
		request.setServiceType(rs.getInt("serviceType"));
		request.setCreationDate(rs.getDate("creationDate"));
		request.setState(rs.getInt("state"));
		request.setApprovedDate(rs.getDate("approvedDate"));
		request.setRejectedDate(rs.getDate("rejectedDate"));
		request.setComments(rs.getString("comments"));
		request.setEndDate(rs.getDate("endDate"));
		request.setFinished(rs.getBoolean("finished"));
		request.setElderly_dni(rs.getString("elderly_dni"));
		request.setContract_number(rs.getInt("contract_number"));
		request.setUserCAS(rs.getString("userCAS"));
		return request;
	}
}
