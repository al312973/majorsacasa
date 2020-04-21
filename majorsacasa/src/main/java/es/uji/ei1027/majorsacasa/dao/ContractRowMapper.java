package es.uji.ei1027.majorsacasa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import es.uji.ei1027.majorsacasa.model.Contract;

public final class ContractRowMapper implements RowMapper<Contract>{
	public Contract mapRow(ResultSet rs, int rowNum) throws SQLException {
		Contract contract = new Contract();
		contract.setNumber(rs.getInt("number"));
		contract.setDateBeginning(rs.getDate("dateBeginning"));
		contract.setDescription(rs.getString("description"));
		contract.setServiceType(rs.getInt("serviceType"));
		contract.setQuantityServices(rs.getInt("quantityServices"));
		contract.setUnitsOfMeasure(rs.getString("unitsOfMeasure"));
		contract.setPriceUnit(rs.getFloat("priceUnit"));
		contract.setBeginningHour(rs.getTime("beginningHour").toLocalTime());
		contract.setEndingHour(rs.getTime("endingHour").toLocalTime());
		contract.setCompany_cif(rs.getString("company_cif"));
		return contract;
	}

}
