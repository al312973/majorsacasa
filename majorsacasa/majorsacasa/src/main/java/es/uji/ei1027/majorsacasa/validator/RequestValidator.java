package es.uji.ei1027.majorsacasa.validator;

import java.util.ArrayList;
import java.util.Calendar;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import es.uji.ei1027.majorsacasa.model.Request;

public class RequestValidator implements Validator {
	private ArrayList<Boolean> dias;
	
	public RequestValidator(ArrayList<Boolean> dias) {
		this.dias = dias;
	}
	
	public boolean supports(Class<?> clazz) {
		return Request.class.isAssignableFrom(clazz);
	}
	
	public void validate(Object obj, Errors errors) {
		Request request = (Request) obj;
		
		//Comprobamos que el campo comentarios tenga longitud máxima de 400 caracteres (resricción BBDD)
		if (request.getComments().length()>400)
			errors.rejectValue("comments", "comentarisllarg", "Els comentaris son massa llargs. Resumeix-los en menys paraules.");
		
		//Comprobamos si la fecha de fin de los servicios es posterior o igual a la actual
		if (request.getEndDate()!=null) {
			Calendar endDate = Calendar.getInstance();
			endDate.setTime(request.getEndDate());
			Calendar today = Calendar.getInstance();
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);
			if (endDate.compareTo(today)<=0) {
				errors.rejectValue("endDate", "dataincorrecta", "Data final invàlida. La data ha de ser posterior a la actual.");
			}
		}
		
		//Comprobamos que se haya indicado al menos un día para recibir los servicios
		boolean valid = false;
		for (boolean b : dias) {
			if (b) valid = true;
		}
		if (!valid)
			//Utilizamos el campo elderly_dni de la solicitud para indicar el error ya que no es visible para el usuario
			errors.rejectValue("elderly_dni", "diesincorrectes", "Has de seleccionar mínim un día d'atenció.");
	}
}
