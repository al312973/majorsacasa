package es.uji.ei1027.majorsacasa.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import es.uji.ei1027.majorsacasa.model.Elderly;

public class ElderlyValidator implements Validator {
	 //@Override
	  public boolean supports(Class<?> cls) {
		  return Elderly.class.equals(cls);
		 // Or in case we wanted to manage also subclasses: 
		 // return Swimmer.class.isAssignableFrom(cls);
	  }
	 
	  //@Override
	  public void validate(Object obj, Errors errors) {
		 Elderly elderly = (Elderly)obj;
		 if (elderly.getDNI().trim().equals(""))
		       errors.rejectValue("DNI", "obligatory",
		                          "You must enter a value");
		 // Add here the validation for Age > 15 years	   
	  }

}
