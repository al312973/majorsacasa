package es.uji.ei1027.majorsacasa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.RowMapper;
import es.uji.ei1027.majorsacasa.model.Volunteer;

public final class VolunteerRowMapper implements RowMapper<Volunteer>{

	public Volunteer mapRow(ResultSet rs, int rowNum) throws SQLException {
		Volunteer volunteer = new Volunteer();
		volunteer.setName(rs.getString("name"));
		volunteer.setPhoneNumber(rs.getString("phoneNumber"));
		volunteer.setEmail(rs.getString("email"));
		volunteer.setUsr(rs.getString("usr"));
		volunteer.setPwd(rs.getString("pwd"));
		volunteer.setHobbies(rs.getString("hobbies"));
		volunteer.setApplicationDate(rs.getDate("applicationDate"));
		volunteer.setAcceptationDate(rs.getDate("acceptationDate"));
		volunteer.setAccepted(rs.getBoolean("accepted"));
		volunteer.setBirthDate(rs.getDate("birthDate"));
		return volunteer;
	}
}
