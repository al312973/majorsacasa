package es.uji.ei1027.majorsacasa.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
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
	private SimpleDateFormat formatter;
	
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
	
	//Muestra el listado de todas las disponibilidades del voluntario no finalizadas
	@RequestMapping(value="/services", method = RequestMethod.GET)
	public String listServices(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		UserDetails user = (UserDetails) session.getAttribute("user");
		if (currentVolunteer==null)
			this.currentVolunteer = volunteerDao.getVolunteerByEmail(user.getEmail());
		if (formatter==null)
			this.formatter = new SimpleDateFormat("dd/MM/yyyy");
		session.setAttribute("validated", currentVolunteer.isAccepted());
		model.addAttribute("availabilities", availabilityDao.getAvailabilitiesFromVolunteer(currentVolunteer.getUsr()));
		return "volunteer/services";
	}
	
	//Muestra información de la persona mayor que ha solicitado un servicio
	@RequestMapping(value="/services/beneficiary/{elderly_dni}", method = RequestMethod.GET)
    public String showElderlyInfo(Model model, @PathVariable String elderly_dni, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		Elderly elderly = elderlyDao.getElderlyByDNI(elderly_dni);
        model.addAttribute("elderly", elderly);
        return "volunteer/beneficiary"; 
    }
	
	//Muestra la información personal
	@RequestMapping(value="/profile", method = RequestMethod.GET)
    public String updateProfile(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
        model.addAttribute("volunteer", currentVolunteer);
        return "volunteer/profile"; 
    }
	
	//Actualiza la información personal
	@RequestMapping(value="/profile", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("volunteer") Volunteer volunteer, BindingResult bindingResult, 
    		HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		//Comprobamos que no haya errores
		VolunteerValidator volunteerValidator = new VolunteerValidator(volunteerDao, currentVolunteer); 
		volunteerValidator.validate(volunteer, bindingResult);
		if (bindingResult.hasErrors()) 
        	return "volunteer/profile";
		
        volunteerDao.updateVolunteer(volunteer);
        
        //Cambiamos los datos de la sesion
        UserDetails user = new UserDetails(volunteer.getEmail(), null, "volunteer", true);
        session.setAttribute("user", user); 
        session.setAttribute("validated", volunteer.isAccepted());
        currentVolunteer=volunteer;
        return "redirect:services"; 
    }
	
	
	//Redirige a la pagina que permite especificar una fecha a partir de la cual ya no estará disponible
	@RequestMapping(value="/timeoffsick", method = RequestMethod.GET) 
    public String setTimeOffSick(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("volunteer", currentVolunteer);
        return "volunteer/timeOffSick";
    }
	
	//Actualiza la información personal
	@RequestMapping(value="/timeoffsick", method = RequestMethod.POST)
    public String processSetTimeOffSickSubmit(@ModelAttribute("volunteer") Volunteer volunteer, 
    		BindingResult bindingResult, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		//Comprobamos que la fecha introducida sea posterior a la fecha actual
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(volunteer.getEndDate());

		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		if (endDate.compareTo(today)<0)
			bindingResult.rejectValue("endDate", "dataincorrecta", "La data ha de ser igual o posterior a la actual");
		
		if (bindingResult.hasErrors())
			return "volunteer/timeoffsick";
		
		//Actualiza el perfil del voluntario estableciendo la fecha fin
		volunteerDao.setVolunteerEndDate(endDate.getTime(), currentVolunteer.getUsr());
		currentVolunteer.setEndDate(endDate.getTime());
		
		//Busca los servicios que tenía a partir de la fecha establecida y los cancela (se establecen como no disponibles
		// y no los muestra en el listado). Además, se manda un correo de cancelación si hay un beneficiario asignado
		for (Availability availability : availabilityDao.getAvailabilitiesFromVolunteer(currentVolunteer.getUsr())) {
			if (availability.getDate().compareTo(endDate.getTime())>=0){
				finishAvailability(availability, endDate.getTime());
			}
		}
		return "redirect:profile";
	}
	
	//Redirige a la pagina que permite añadir una nueva disponibilidad
	@RequestMapping(value="/addService", method = RequestMethod.GET) 
    public String addAvailability(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
        model.addAttribute("availability", new Availability());
        return "volunteer/addService";
    }

	//Añade una nueva disponibilidad
	@RequestMapping(value="/addService", method=RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("availability") Availability availability, 
			BindingResult bindingResult, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		//Rellena los campos no solicitados mediante el formulario
		if (currentVolunteer.getAcceptationDate()!=null)
			availability.setStateAvailable(true);
		else
			availability.setStateAvailable(false);
		availability.setUnsuscribeDate(null);
		availability.setElderly_dni(null);
		availability.setVolunteer_usr(currentVolunteer.getUsr());
		
		AvailabilityValidator availabilityValidator = new AvailabilityValidator(availabilityDao, null, currentVolunteer.getEndDate());
		availabilityValidator.validate(availability, bindingResult);
		
		if (bindingResult.hasErrors())
			return "volunteer/addService";
		
		availabilityDao.addAvailability(availability);
        return "redirect:services";
    }
	
	//Redirige a la pagina que permite modificar una disponibilidad
	@RequestMapping(value="/updateService/{date}/{beginningHour}", method = RequestMethod.GET)
	public String editAvailability(Model model, @PathVariable String date, @PathVariable String beginningHour, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
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
	
	//Modifica la información de una disponibilidad
	@RequestMapping(value="/updateService", method = RequestMethod.POST) 
	public String processUpdateSubmit(@ModelAttribute("availability") Availability availability, 
			BindingResult bindingResult, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		AvailabilityValidator availabilityValidator = new AvailabilityValidator(availabilityDao, currentAvailability,  currentVolunteer.getEndDate());
		availability.setVolunteer_usr(currentVolunteer.getUsr());
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
			+ "\tDía: "+formatter.format(currentAvailability.getDate())+"\n"
			+ "\tDesde les: "+currentAvailability.getBeginningHour()+" hores\n"
			+ "\tFins les: "+currentAvailability.getEndingHour()+" hores\n"
			+"\nAl:\n"
			+ "\tDía: "+new SimpleDateFormat("yyyy-MM-dd").format(availability.getDate())+"\n"
			+ "\tDesde les: "+availability.getBeginningHour()+" hores\n"
			+ "\tFins les: "+availability.getEndingHour()+" hores");
		}
		
		return "redirect:services"; 
	}
	
	//Muestra la página de confirmación antes de finalizar o borrar un servicio
	@RequestMapping(value="/services/delete/confirm/{deletionType}/{date}/{beginningHour}", method = RequestMethod.GET)
	public String confirmDeleteService(Model model, @PathVariable int deletionType, @PathVariable String date, 
			@PathVariable String beginningHour, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		try {
			Date availabilityDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
			LocalTime availabilitiBeginningHour = LocalTime.parse(beginningHour);
			Availability availability = availabilityDao.getAvailability(availabilityDate, availabilitiBeginningHour, currentVolunteer.getUsr());
			
			model.addAttribute("deletionType", deletionType);
			model.addAttribute("availability", availability);
			if (availability.getElderly_dni()!=null) {
				Elderly elderly = elderlyDao.getElderlyByDNI(availability.getElderly_dni());
				model.addAttribute("elderly", elderly.getName()+" "+elderly.getSurname());
			}
			
		}catch (Exception ignore) {
		}
	   return "volunteer/deleteservice"; 
	}

	//Borra o da de baja un servicio
	@RequestMapping(value="/services/delete/{date}/{beginningHour}", method = RequestMethod.GET)
    public String processDelete(@PathVariable String date, @PathVariable String beginningHour, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
	   try {
			Date availabilityDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
			LocalTime availabilityBeginningHour = LocalTime.parse(beginningHour);
			
			Availability availability = availabilityDao.getAvailability(availabilityDate, availabilityBeginningHour, currentVolunteer.getUsr());
			
			//Establece la fecha de comparación a la fecha actual
			Calendar today = Calendar.getInstance();
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);
			
			finishAvailability(availability, today.getTime());
		} catch (ParseException ignore) {
		}
	
	   	return "redirect:../../../services"; 
    }
	
	//Finaliza o borra un servicio concreto. Si hay una persona mayor asignada y la fecha del servicio es igual o posterior
	// a la fecha de comparación (la actual si se cancela solo una actividad, o la de fecha fin de prestación de servicios
	// si se ha indicado), se le envía un correo de notificación al beneficiario
	private void finishAvailability(Availability availability, Date dateToCompare) {		
		if (availability.getElderly_dni()!=null && availability.getDate().compareTo(dateToCompare)>=0) {
			Elderly elderly = elderlyDao.getElderlyByDNI(availability.getElderly_dni());
			
			System.out.println("\nS'ha manat un correu de notificació a "+elderly.getEmail()
			+"\nNotificació de cancelació del servei contractat amb el voluntari "+currentVolunteer.getName()+"\n"
			+"La seva cita del:\n"
			+ "\tDía: "+formatter.format(availability.getDate())+"\n"
			+ "\tDesde les: "+availability.getBeginningHour()+" hores\n"
			+ "\tFins les: "+availability.getEndingHour()+" hores\n"
			+"Ha tingut que ser cancelada. Sentim les molèsties.");
		}
		
		availability.setUnsuscribeDate(new Date());
		availabilityDao.finishAvailability(availability);
	}
}