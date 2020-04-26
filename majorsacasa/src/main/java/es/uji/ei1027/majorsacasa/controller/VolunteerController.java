package es.uji.ei1027.majorsacasa.controller;

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
	public String listVolunteers(Model model, HttpSession session) {
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
	
	
	@RequestMapping(value="/services/add") 
    public String addAvailability(Model model) {
        model.addAttribute("availability", new Availability());
        return "volunteer/addAvailability";
    }

	@RequestMapping(value="/services/add", method=RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("availability") Availability availability, BindingResult bindingResult) {

		if (bindingResult.hasErrors())
			return "volunteer/add";
		availabilityDao.addAvailability(availability);
        return "redirect:services";
    } 

//   @RequestMapping(value="/delete/{USR}")
//    public String processDelete(@PathVariable String USR) {
//           volunteerDao.deleteVolunteer(USR);
//           return "redirect:../list"; 
//    }
}
