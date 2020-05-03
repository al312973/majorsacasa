package es.uji.ei1027.majorsacasa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import es.uji.ei1027.majorsacasa.model.Elderly;

public final class ElderlyRowMapper implements RowMapper<Elderly> {

	public Elderly mapRow(ResultSet rs, int rowNum) throws SQLException {
		Elderly elderly = new Elderly();
		elderly.setName(rs.getString("name"));
		elderly.setDNI(rs.getString("DNI"));
		elderly.setSurname(rs.getString("surname"));
		elderly.setBirthDate(rs.getDate("birthDate"));
		elderly.setAddress(rs.getString("address"));
		elderly.setPhoneNumber(rs.getString("phoneNumber"));
		elderly.setBankAccountNumber(rs.getString("bankAccountNumber"));
		elderly.setEmail(rs.getString("email"));
		elderly.setUserPwd(rs.getString("userPwd"));
		elderly.setDateCreation(rs.getDate("dateCreation"));
		elderly.setAlergies(rs.getString("alergies"));
		elderly.setDiseases(rs.getString("diseases"));
		elderly.setSocialWorker_userCAS(rs.getString("socialWorker_userCAS"));
		return elderly;
	}
	
}
