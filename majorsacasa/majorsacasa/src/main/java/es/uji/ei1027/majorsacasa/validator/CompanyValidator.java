package es.uji.ei1027.majorsacasa.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import es.uji.ei1027.majorsacasa.dao.CompanyDAO;
import es.uji.ei1027.majorsacasa.model.Company;

public class CompanyValidator  implements Validator{
	private CompanyDAO companyDao;
	
	public CompanyValidator(CompanyDAO companyDao){
		this.companyDao = companyDao;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Company.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		Company company = (Company) obj;
		
		//Comprobamos si la empresa ya ha sido registrada
		Company existingCompany = companyDao.getCompanyByCIF(company.getCIF());
		if (existingCompany!=null) {
			errors.rejectValue("CIF", "CIFregistrat", "Aquesta empresa ja ha sigut donada d'alta al sistema");
		}
		
		existingCompany = companyDao.getCompanyByName(company.getName());
		if (existingCompany!=null) {
			errors.rejectValue("name", "nameregistrat", "Nom no disponible");
		}
		
		//Comprobamos los criterios de longitud de cada campo de acuerdo a las restricciones de la BBDD
		if (company.getName().length() > 50){
			errors.rejectValue("name", "nameincorrecte", "El nom de l'empresa no pot tindre una longitud superior a 50 caracters");
		}
		if (company.getCIF().length() != 9){
			errors.rejectValue("CIF", "CIFincorrecte", "El CIF ha de tindre una longitud de 9 caracters");
		}
		if (company.getAddress().length() > 100){
			errors.rejectValue("address", "addressincorrecta", "La direcció no pot tindre una longitud superior a 100 caracters");
		}
		if (company.getContactPersonName()!= null) {
			if (company.getContactPersonName().length() > 50){
				errors.rejectValue("contactPersonName", "nomincorrecte", "El nom de la persona de contacte no pot tindre una longitud superior a 50 caracters");
			}
			if (company.getContactPersonPhoneNumber().length() != 9){
				errors.rejectValue("contactPersonPhoneNumber", "telefonincorrecte", "Telèfon invàlid");
			}
			if (company.getContactPersonEmail().length() > 50){
				errors.rejectValue("contactPersonEmail", "mailincorrecte", "El email de la persona de contacte no pot tindre una longitud superior a 50 caracters");
			}
			if (company.getPwd().length() > 20) {
				errors.rejectValue("pwd", "pwdincorrecte", "La contrasenya de la persona de contacte no pot tindre una longitud superior a 20 caracters");
			}
		}	
	}
	
}