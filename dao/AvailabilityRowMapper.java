package es.uji.ei1027.majorsacasa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.RowMapper;
import es.uji.ei1027.majorsacasa.model.Availability;

public final class AvailabilityRowMapper implements RowMapper<Availability>{

	public Availability mapRow(ResultSet rs, int rowNum) throws SQLException {
		Availability availability = new Availability();
		availability.setDate(rs.getDate("date"));
		availability.setBegginingHour(rs.getTime("begginingHour").toLocalTime());
		availability.setEndingHour(rs.getTime("endingHour").toLocalTime());
		availability.setStateAvailable(rs.getBoolean("stateAvailable"));
		availability.setElderly_dni(rs.getString("elderly_dni"));
		availability.setVolunteer_usr(rs.getString("volunteer_usr"));
		return availability;
	}
}
