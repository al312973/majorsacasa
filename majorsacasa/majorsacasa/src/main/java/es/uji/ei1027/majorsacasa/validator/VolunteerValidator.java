package es.uji.ei1027.majorsacasa.validator;

import java.util.Calendar;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import es.uji.ei1027.majorsacasa.dao.VolunteerDAO;
import es.uji.ei1027.majorsacasa.model.Volunteer;

public class VolunteerValidator implements Validator {
	private VolunteerDAO volunteerDao;
	private Volunteer currentVolunteer;
	
	public VolunteerValidator(VolunteerDAO volunteerDao, Volunteer currentVolunteer) {
		this.volunteerDao = volunteerDao;
		this.currentVolunteer = currentVolunteer;
	}
	
	public boolean supports(Class<?> clazz) {
		return Volunteer.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) {
		//No comprobamos si los campos estan vacios porque de eso se encarga el formulario de la vista html
		Volunteer volunteer = (Volunteer) obj;
		
		//Comprobamos si el nombre de usuario está disponible
		Volunteer existingUsr = volunteerDao.getVolunteerByUsr(volunteer.getUsr());
		if (existingUsr!=null) {
			if (currentVolunteer==null) { //Si se trata del registro
				errors.rejectValue("usr", "usrocupat", "Nom d'usuari no disponible");
				
			} else { //Modificacion del perfil
				if (!currentVolunteer.getUsr().equals(existingUsr.getUsr()))
					errors.rejectValue("usr", "usrocupat", "Nom d'usuari no disponible");
			}
			
		}
		
		//Comprobamos si el email ya ha sido registrado
		existingUsr = volunteerDao.getVolunteerByEmail(volunteer.getEmail());
		if (existingUsr!=null) {
			if (currentVolunteer==null) { //Si se trata del registro
				errors.rejectValue("email", "emailregistrat", "Aquest email ja ha sigut registrat a l'aplicació");
				
			} else { //Modificacion del perfil
				if (!currentVolunteer.getEmail().equals(existingUsr.getEmail()))
					errors.rejectValue("email", "emailregistrat", "Aquest email ja ha sigut registrat a l'aplicació");
			}
		}
		
		//Comprobamos si el numero de telefono es correcto
		if (volunteer.getPhoneNumber().length()!=9) {
			errors.rejectValue("phoneNumber", "telefonincorrecte", "Telèfon invàlid");
		}
		//Comprobamos si la fecha de inicio de los servicios es posterior o igual a la actual (para el registro)
		if (currentVolunteer==null) {
			Calendar applicationDate = Calendar.getInstance();
			applicationDate.setTime(volunteer.getApplicationDate());
			Calendar today = Calendar.getInstance();
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);
			if (applicationDate.compareTo(today)<0) {
				errors.rejectValue("applicationDate", "telefonincorrecte", "Data d'inici invàlida");
			}
		}
	}
}