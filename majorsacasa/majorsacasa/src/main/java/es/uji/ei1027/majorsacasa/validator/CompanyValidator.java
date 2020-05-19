package es.uji.ei1027.majorsacasa.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import es.uji.ei1027.majorsacasa.dao.CompanyDAO;
import es.uji.ei1027.majorsacasa.model.Company;

public class CompanyValidator  implements Validator{
	private CompanyDAO companyDao;
	private Company company;
	
	public CompanyValidator(CompanyDAO companyDao, Company company){
		this.companyDao = companyDao;
		this.company = company;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Company.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		Company company = (Company) obj;
		
		if (company.getName().length() > 50){
			errors.rejectValue("name", "nameincorrecte", "Nom massa llarg");
		}
		if (company.getCIF().length() != 9){
			errors.rejectValue("CIF", "CIFincorrecte", "El CIF té 9 caracters");
		}
		if (company.getAddress().length() > 100){
			errors.rejectValue("address", "addressincorrecta", "La direcció es massa llarga");
		}
		if (company.getContactPersonName().length() > 50){
			errors.rejectValue("contactPersonName", "nomincorrecte", "El nom es massa llarg");
		}
		if (company.getContactPersonPhoneNumber().length() != 9){
			errors.rejectValue("contactPersonPhoneNumber", "telefonincorrecte", "El telèfon té 9 cifres");
		}
		if (company.getContactPersonEmail().length() > 50){
			errors.rejectValue("contactPersonEmail", "mailincorrecte", "El email es massa llarg");
		}
		
	}
	
}