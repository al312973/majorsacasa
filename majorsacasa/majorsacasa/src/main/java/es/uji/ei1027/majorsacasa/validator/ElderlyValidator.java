package es.uji.ei1027.majorsacasa.validator;

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
	public boolean supports(Class<?> clazz) {
		return Elderly.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		//No comprobamos si los campos estan vacios porque de eso se encarga el formulario de la vista html
		Elderly elderly = (Elderly) obj;
		
		//Comprobamos que el DNI sea válido
		if (elderly.getDNI().length()!=9)
			errors.rejectValue("DNI", "dniincorrecte", "DNI invàlid");
				
		//Comprobamos que el nombre tenga longitud máxima de 50 caracteres (resricción BBDD)
		if (elderly.getName().length()>50)
			errors.rejectValue("name", "nomllarg", "El nom no pot tindre una longitud superior a 50 caracters");
		
		//Comprobamos que el apellido tenga longitud máxima de 50 caracteres (resricción BBDD)
		if (elderly.getSurname().length()>50)
			errors.rejectValue("surname", "cognomllarg", "El cognom no pot tindre una longitud superior a 50 caracters");
		
		//Comprobamos si el numero de telefono es correcto
		if (elderly.getPhoneNumber().length()!=9) {
			errors.rejectValue("phoneNumber", "telefonincorrecte", "Telèfon invàlid");
		}
		
		//Comprobamos si el número de la cuenta es correcto (restricción BBDD)
		if (elderly.getBankAccountNumber().length()>24){
			errors.rejectValue("bankAccountNumber", "contebancariincorrecte", "Conte bancari invàlid");
		}
		
		//Comprobamos que la dirección tenga longitud máxima de 100 caracteres (resricción BBDD)
		if (elderly.getAddress().length()>100)
			errors.rejectValue("address", "adreçallarga", "L'adreça pot tindre una longitud superior a 100 caracters");
				
		//Comprobamos que el email tenga longitud máxima de 50 caracteres (resricción BBDD)
		if (elderly.getEmail().length()>50)
			errors.rejectValue("email", "emailllarg", "El email no pot tindre una longitud superior a 50 caracters");
		
		//No diferenciamos entre si es el registro o la modificación porque en el caso base que implementamos no
		// abarcamos el registro. Por tanto hacemos comprobaciones teniendo en cuenta que el validador siempre
		// siempre validará modificaciones de datos de la persona mayor
		Elderly existingUsr = elderlyDao.getElderlyByEmail(elderly.getEmail());
		if (existingUsr!=null) {
			if (!currentElderly.getEmail().equals(existingUsr.getEmail()))
				errors.rejectValue("email", "emailregistrat", "Aquest email ja ha sigut registrat a l'aplicació");
		}
		
		//Comprobamos que la contraseña tenga longitud máxima de 20 caracteres (resricción BBDD)
		if (elderly.getUserPwd().length()>20)
			errors.rejectValue("userPwd", "pwdllarg", "La contrasenya no pot tindre una longitud superior a 20 caracters");
		
		//Comprobamos que la longitud del texto de las alergias no supere los 150 caracteres (resricción BBDD)
		if (elderly.getAlergies()!=null && elderly.getAlergies().length()>150)
			errors.rejectValue("alergies", "alergiesllarg", "Les alèrgies es deuen resumir en màxim 150 caracters");
		
		//Comprobamos que la longitud del texto de las enfermedades no supere los 150 caracteres (resricción BBDD)
		if (elderly.getDiseases()!=null && elderly.getDiseases().length()>150)
			errors.rejectValue("diseases", "enfermetatsllarg", "Les enfermetats es deuen resumir en màxim 150 caracters");
	}

}
