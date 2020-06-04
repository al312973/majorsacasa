package es.uji.ei1027.majorsacasa.validator;

import java.util.Calendar;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import es.uji.ei1027.majorsacasa.model.Contract;

public class ContractValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Contract.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		Contract contract = (Contract) obj;
		
		//Comprobamos que la longitud del campo de descripción no sea superior a 150 caracteres
		if (contract.getDescription().length() > 150){
			errors.rejectValue("description", "descriptionllarg", "La descripció del contracte no pot tindre una longitud superior a 150 caracters");
		}
		
		//Comprobamos que la fecha de inicio del contrato sea igual o superior a la fecha actual
		Calendar dateBeginning = Calendar.getInstance();
		dateBeginning.setTime(contract.getDateBeginning());
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		if (dateBeginning.compareTo(today)<0)
			errors.rejectValue("dateBeginning", "datainicialincorrecta", "La data d'inici ha de ser igual o posterior a la actual");
		
		//Comprobamos que la fecha fin (si la hay) sea superior a la fecha de inicio
		if (contract.getDateEnding()!=null) {
			Calendar dateEnding = Calendar.getInstance();
			dateEnding.setTime(contract.getDateEnding());
			if (dateEnding.compareTo(dateBeginning)<=0) {
				errors.rejectValue("dateEnding", "datafinalincorrecta", "Data fi invàlida. La data fi ha de ser posterior a la d'inici.");
			}
		}
		
		//Comprobamos que el numero de servicios que se pueden prestar sea superior a 0
		if (contract.getQuantityServices()<=0) {
			errors.rejectValue("quantityServices", "quantitatincorrecta", "El número de serveis inclosos ha de ser major a 0");
		}
	}

}
