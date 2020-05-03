package es.uji.ei1027.majorsacasa.validator;

import java.util.Calendar;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import es.uji.ei1027.majorsacasa.dao.ElderlyDAO;
import es.uji.ei1027.majorsacasa.model.Elderly;

public class ElderlyValidator implements Validator{
	
	private ElderlyDAO elderlyDao;
	private Elderly currentElderly;
	
	public ElderlyValidator(ElderlyDAO elderlyDao, Elderly currentElderly) {
		this.elderlyDao = elderlyDao;
		this.currentElderly = currentElderly;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return Elderly.class.isAssignableFrom(arg0);
	}

	@Override
	public void validate(Object arg0, Errors arg1) {
		Elderly elderly = (Elderly) arg0;
		
		
		//Comprobamos si el numero de telefono es correcto
		if (elderly.getPhoneNumber().length()!=9) {
			arg1.rejectValue("phoneNumber", "telefonincorrecte", "Telèfon invàlid");
		}
		
		//Comprobamos si el número de la cuenta es incorrecto
		if (elderly.getBankAccountNumber().length() != 19){
			arg1.rejectValue("bankAccountNumber", "contebancariincorrecte", "Conte bancari invàlid");
		}
			
		
		//Comprobamos si el email ya ha sido registrado
		Elderly existingUsr = elderlyDao.getElderlyByEmail(elderly.getEmail());
		if (existingUsr!=null) {
			if (currentElderly==null) { //Si se trata del registro
				arg1.rejectValue("email", "emailregistrat", "Aquest email ja ha sigut registrat a l'aplicació");
				
			} else { //Modificacion del perfil
				if (!currentElderly.getEmail().equals(existingUsr.getEmail()))
					arg1.rejectValue("email", "emailregistrat", "Aquest email ja ha sigut registrat a l'aplicació");
			}
		}	
	}

}
