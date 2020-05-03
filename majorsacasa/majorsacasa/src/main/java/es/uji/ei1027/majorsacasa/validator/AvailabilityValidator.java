package es.uji.ei1027.majorsacasa.validator;

import java.util.Calendar;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import es.uji.ei1027.majorsacasa.dao.AvailabilityDAO;
import es.uji.ei1027.majorsacasa.model.Availability;
import es.uji.ei1027.majorsacasa.model.Volunteer;

public class AvailabilityValidator implements Validator {
	private AvailabilityDAO availabilityDao;
	private Availability currentAvailability;
	private String volunteer_usr;

	public AvailabilityValidator(AvailabilityDAO availabilityDao, Availability currentAvailability, String volunteer) {
		this.availabilityDao = availabilityDao;
		this.currentAvailability = currentAvailability;
		this.volunteer_usr = volunteer;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Volunteer.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		//No comprobamos si los campos estan vacios porque de eso se encarga el formulario de la vista html
		Availability availability = (Availability) obj;
		
		//Comprobamos si ya hay un servicio registrado para el mismo día a la misma hora
		Availability existingAvailability = availabilityDao.getAvailability(availability.getDate(), availability.getBeginningHour(), volunteer_usr);
		if (existingAvailability!=null) {
			if (currentAvailability==null) { //Si se trata de una nueva disponibilidad
				errors.rejectValue("beginningHour", "availabilityregistrada", "Servei ja registrat");
				
			} else { //Si se modifica la disponibilidad actual
				if (!(currentAvailability.getDate().compareTo(existingAvailability.getDate())==0 &&
						currentAvailability.getBeginningHour().compareTo(existingAvailability.getBeginningHour())==0))
					errors.rejectValue("beginningHour", "availabilityregistrada", "Servei ja registrat");
			}
		}
		
		//Comprobamos que la fecha introducida sea posterior a la fecha actual
		Calendar date = Calendar.getInstance();
		date.setTime(availability.getDate());
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		if (date.compareTo(today)<0) {
			errors.rejectValue("date", "dataincorrecta", "La data ha de ser igual o posterior a la actual");
		}
		
		//Comprobamos que la hora final sea posterior a la hora inicial
		if (availability.getBeginningHour().compareTo(availability.getEndingHour())>=0) {
			errors.rejectValue("endingHour", "horafinalincorrecta", "L'hora final ha de ser posterior a l'hora inicial");
		}
	}
}
