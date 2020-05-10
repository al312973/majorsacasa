package es.uji.ei1027.majorsacasa.validator;

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
		
		//Comprobamos que el nombre tenga longitud máxima de 50 caracteres (resricción BBDD)
		if (volunteer.getName().length()>50)
			errors.rejectValue("name", "nomllarg", "El nom no pot tindre una longitud superior a 50 caracters");
		
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
		
		//Comprobamos que el nombre de usuario tenga longitud máxima de 20 caracteres (resricción BBDD)
		if (volunteer.getUsr().length()>20)
			errors.rejectValue("usr", "usrllarg", "El nom d'usuari no pot tindre una longitud superior a 20 caracters");
				
		//Comprobamos que el email tenga longitud máxima de 50 caracteres (resricción BBDD)
		if (volunteer.getEmail().length()>50)
			errors.rejectValue("email", "emailllarg", "El email no pot tindre una longitud superior a 50 caracters");
		
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
		
		//Comprobamos que la contraseña tenga longitud máxima de 20 caracteres (resricción BBDD)
		if (volunteer.getPwd().length()>20)
			errors.rejectValue("pwd", "pwdllarg", "La contrasenya no pot tindre una longitud superior a 20 caracters");
				
		//Comprobamos si el numero de telefono es correcto
		if (volunteer.getPhoneNumber().length()!=9)
			errors.rejectValue("phoneNumber", "telefonincorrecte", "Telèfon invàlid");
		
		//Comprobamos que la longitud del texto de los hobbies no supere los 150 caracteres (resricción BBDD)
		if (volunteer.getHobbies().length()>150)
			errors.rejectValue("hobbies", "hobbiesllarg", "Els hobbies es deuen resumir en màxim 150 caracters");
	}
}