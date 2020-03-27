package es.uji.ei1027.majorsacasa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.RowMapper;
import es.uji.ei1027.majorsacasa.model.SocialWorker;

public final class SocialWorkerRowMapper implements RowMapper<SocialWorker> {
		public SocialWorker mapRow(ResultSet rs, int rowNum) throws SQLException {
			SocialWorker sw = new SocialWorker();
			sw.setName(rs.getString("name"));
			sw.setUserCAS(rs.getString("userCAS"));
			sw.setPwd(rs.getString("pwd"));
			sw.setPhoneNumber(rs.getString("phoneNumber"));
			sw.setEmail(rs.getString("email"));
			return sw;
		}
}

