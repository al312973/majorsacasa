package es.uji.ei1027.majorsacasa.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.majorsacasa.dao.AvailabilityDAO;
import es.uji.ei1027.majorsacasa.dao.ElderlyDAO;
import es.uji.ei1027.majorsacasa.dao.VolunteerDAO;
import es.uji.ei1027.majorsacasa.model.Availability;
import es.uji.ei1027.majorsacasa.model.Elderly;
import es.uji.ei1027.majorsacasa.model.UserDetails;
import es.uji.ei1027.majorsacasa.model.Volunteer;
import es.uji.ei1027.majorsacasa.validator.AvailabilityValidator;
import es.uji.ei1027.majorsacasa.validator.VolunteerValidator;

@Controller
@RequestMapping("/volunteer")
public class VolunteerController {
	private VolunteerDAO volunteerDao;
	private AvailabilityDAO availabilityDao;
	private ElderlyDAO elderlyDao;
	private Volunteer currentVolunteer;
	
	@Autowired
	public void setVolunteerDao(VolunteerDAO volunteerDao) {
		this.volunteerDao=volunteerDao;
	}
	
	@Autowired
	public void setAvailabilityDao(AvailabilityDAO availabilityDao) {
		this.availabilityDao=availabilityDao;
	}
	
	@Autowired
	public void setElderlyDao(ElderlyDAO elderlyDao) {
		this.elderlyDao=elderlyDao;
	}
	
	
	@RequestMapping(value="/services")
	public String listServices(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		this.currentVolunteer = volunteerDao.getVolunteerByEmail(user.getEmail());
		model.addAttribute("availabilities", availabilityDao.getAvailabilitiesFromVolunteer(currentVolunteer.getUsr()));
		return "volunteer/services";
	}
	
	@RequestMapping(value="/services/beneficiary/{elderly_dni}", method = RequestMethod.GET)
    public String showElderlyInfo(Model model, @PathVariable String elderly_dni) {
		Elderly elderly = elderlyDao.getElderlyByDNI(elderly_dni);
        model.addAttribute("elderly", elderly);
        return "volunteer/beneficiary"; 
    }
	
	@RequestMapping(value="/profile", method = RequestMethod.GET)
    public String updateProfile(Model model) {
        model.addAttribute("volunteer", currentVolunteer);
        return "volunteer/profile"; 
    }
	
	@RequestMapping(value="/profile", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("volunteer") Volunteer volunteer, BindingResult bindingResult, HttpSession session) {
		
		//Comprobamos que no haya errores
		VolunteerValidator volunteerValidator = new VolunteerValidator(volunteerDao, currentVolunteer); 
		volunteerValidator.validate(volunteer, bindingResult);
		if (bindingResult.hasErrors()) 
        	return "volunteer/profile";
		
        volunteerDao.updateVolunteer(volunteer);
        
        //Cambiamos los datos de la sesion
        UserDetails user = new UserDetails(volunteer.getEmail(), null, "volunteer", true);
        session.setAttribute("user", user); 
        return "redirect:services"; 
    }
	
	
	@RequestMapping(value="/addService") 
    public String addAvailability(Model model) {
        model.addAttribute("availability", new Availability());
        return "volunteer/addService";
    }

	@RequestMapping(value="/addService", method=RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("availability") Availability availability, BindingResult bindingResult) {
		availability.setStateAvailable(false);
		availability.setVolunteer_usr(currentVolunteer.getUsr());
		
		AvailabilityValidator availabilityValidator = new AvailabilityValidator(availabilityDao, null, currentVolunteer.getUsr());
		availabilityValidator.validate(availability, bindingResult);
		
		if (bindingResult.hasErrors())
			return "volunteer/addService";
		
		availabilityDao.addAvailability(availability);
        return "redirect:services";
    }
	
	@RequestMapping(value="/updateService/{date}/{beginningHour}", method = RequestMethod.GET)
	public String editAvailability(Model model, @PathVariable String date, @PathVariable String beginningHour) {
		try {
			Date availabilityDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
			LocalTime availabilityBeginningHour = LocalTime.parse(beginningHour);
			
			currentAvailability = availabilityDao.getAvailability(availabilityDate, availabilityBeginningHour, currentVolunteer.getUsr());
			model.addAttribute("availability", currentAvailability);
		} catch (ParseException ignore) {
		}
	    
		return "volunteer/updateService"; 
	}
	
	//Variable en la que guardamos la disponibilidad anterior para compararla con la nueva al editar
	private Availability currentAvailability;
	   
	@RequestMapping(value="/updateService", method = RequestMethod.POST) 
	public String processUpdateSubmit(@ModelAttribute("availability") Availability availability, BindingResult bindingResult) {
		AvailabilityValidator availabilityValidator = new AvailabilityValidator(availabilityDao, currentAvailability, currentVolunteer.getUsr());
		availabilityValidator.validate(availability, bindingResult);
		
		if (bindingResult.hasErrors()) 
			return "volunteer/updateService";
		
		availabilityDao.updateAvailability(currentAvailability, availability);
		
		//Si hay una persona mayor asignada, se le envía un correo de notificación del cambio
		if (currentAvailability.getElderly_dni()!=null) {
			Elderly elderly = elderlyDao.getElderlyByDNI(currentAvailability.getElderly_dni());
			
			System.out.println("\nS'ha manat un correu de notificació a "+elderly.getEmail()
			+"\nNotificació de canvi d'horari en el servei contractat amb el voluntari "+currentVolunteer.getName()+"\n"
			+"La seva cita passa de ser del:\n"
			+ "\tDía: "+currentAvailability.getDate()+"\n"
			+ "\tDesde les: "+currentAvailability.getBeginningHour()+" hores\n"
			+ "\tFins les: "+currentAvailability.getEndingHour()+" hores\n"
			+"\nAl:\n"
			+ "\tDía: "+new SimpleDateFormat("yyyy-MM-dd").format(availability.getDate())+"\n"
			+ "\tDesde les: "+availability.getBeginningHour()+" hores\n"
			+ "\tFins les: "+availability.getEndingHour()+" hores");
		}
		
		return "redirect:services"; 
	}

	@RequestMapping(value="/services/delete/{date}/{beginningHour}")
    public String processDelete(@PathVariable String date, @PathVariable String beginningHour) {
	   try {
		Date availabilityDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		LocalTime availabilityBeginningHour = LocalTime.parse(beginningHour);
		
		Availability availability = availabilityDao.getAvailability(availabilityDate, availabilityBeginningHour, currentVolunteer.getUsr());
		
		//Si hay una persona mayor asignada, se le envía un correo de notificación de la cancelación
		if (availability.getElderly_dni()!=null) {
			Elderly elderly = elderlyDao.getElderlyByDNI(availability.getElderly_dni());
			
			System.out.println("\nS'ha manat un correu de notificació a "+elderly.getEmail()
			+"\nNotificació de cancelació del servei contractat amb el voluntari "+currentVolunteer.getName()+"\n"
			+"La seva cita del:\n"
			+ "\tDía: "+availability.getDate()+"\n"
			+ "\tDesde les: "+availability.getBeginningHour()+" hores\n"
			+ "\tFins les: "+availability.getEndingHour()+" hores\n"
			+"Ha tingut que ser cancelada. Sentim les molèsties.");
		}
		
		availabilityDao.deleteAvailability(availability);
		} catch (ParseException ignore) {
		}
	
	   	return "redirect:../../../services"; 
    }
}