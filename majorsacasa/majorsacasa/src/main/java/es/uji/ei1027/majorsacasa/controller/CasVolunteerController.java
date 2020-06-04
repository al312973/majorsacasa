package es.uji.ei1027.majorsacasa.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.majorsacasa.dao.AvailabilityDAO;
import es.uji.ei1027.majorsacasa.dao.VolunteerDAO;
import es.uji.ei1027.majorsacasa.model.Availability;
import es.uji.ei1027.majorsacasa.model.Volunteer;

@Controller
@RequestMapping("/casVolunteer")
public class CasVolunteerController {
	private VolunteerDAO volunteerDao;
	private AvailabilityDAO availabilityDao;
	
	@Autowired 
	public void setVolunteerDao(VolunteerDAO volunteerDao){
		this.volunteerDao = volunteerDao;
	}
	
	@Autowired 
	public void setAvailabilityDao(AvailabilityDAO availabilityDao){
		this.availabilityDao = availabilityDao;
	}
	
	//Lista aquellos voluntarios pendientes de aceptacion, es decir, aquellos cuya fecha de aceptacion sea null y no tenga nada en fechaFin , porque fechaFin quiere decir que está rechazado
	@RequestMapping(value="/volunteersList", method = RequestMethod.GET)
    public String listarVoluntariosPendientesAceptacion(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		ArrayList<Volunteer> voluntariosPendientesAceptacion = new ArrayList<Volunteer>(volunteerDao.getNotAcceptedVolunteers()); 
		
		model.addAttribute("volunteers", voluntariosPendientesAceptacion);
        return "casVolunteer/volunteerslist"; 
    }

	//Muestra la página de confirmación antes de aceptar o rechazar un voluntario
	@RequestMapping(value="/volunteersList/confirmAction/{actionType}/{usr}", method = RequestMethod.GET)
	public String confirmAcceptRejectVolunteer(Model model, @PathVariable int actionType, @PathVariable String usr, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		try {		
			Volunteer volunteer = volunteerDao.getVolunteerByUsr(usr);
			
			model.addAttribute("actionType", actionType);
			model.addAttribute("volunteer", volunteer);
			
		}catch (Exception ignore) {
		}
	   return "casVolunteer/acceptrejectvolunteer"; 
	}
	
	//Acepta un voluntario poniendo en su campo acceptationDate a la fecha actual y el campo accepted a true.
	//Además define el campo stateavailable de sus disponibilidades a true parar que puedan ser mostradas a las personas mayores
	@RequestMapping(value="/accept/{usr}")
	public String acceptVolunteer(@PathVariable String usr, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		volunteerDao.acceptVolunteer(usr);
		
		//Establece las disponibilidades del voluntario para ser mostradas
		for (Availability availability : availabilityDao.getAvailabilitiesFromVolunteer(usr)) {
			availabilityDao.setAvailabilityToShow(availability);
		}
		
		//Manda un correo informando al voluntario que ha sido aceptado
		Volunteer volunteer = volunteerDao.getVolunteerByUsr(usr);
		String correo ="\nS'ha manat un correu de notificació a "+volunteer.getEmail()
		+"\nNotificació d'acceptació"
		+"\nBenvolgut "+volunteer.getName()+",\n"
		+"El teu compte com a voluntari registrat a l'aplicació Majors a Casa amb les credencials:\n"
		+ "\tUsuari: "+volunteer.getUsr()+"\n"
		+ "\tContrasenya: "+volunteer.getPwd()+"\n"
		+"Ha sigut acceptat. Les teves disponibilitats ja estàn sent mostrades a les persones majors per a que les puguen reservar.";
		
		System.out.println(correo);
		return "redirect:../volunteersList";
	}
	
	//Pone el campo fechaFin de un voluntario a la fecha actual para que sepamos que está rechazado 
	@RequestMapping(value="/reject/{usr}")
	public String rejectVolunteer(@PathVariable String usr, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		volunteerDao.deleteVolunteer(usr);
		
		//Manda un correo informando al voluntario que ha sido aceptado
		Volunteer volunteer = volunteerDao.getVolunteerByUsr(usr);
		String correo ="\nS'ha manat un correu de notificació a "+volunteer.getEmail()
		+"\nNotificació de rebutjament"
		+"\nBenvolgut "+volunteer.getName()+",\n"
		+"El teu compte com a voluntari registrat a l'aplicació Majors a Casa amb les credencials:\n"
		+ "\tUsuari: "+volunteer.getUsr()+"\n"
		+ "\tContrasenya: "+volunteer.getPwd()+"\n"
		+"Ha sigut rebutjat. No compleixes els criteris necessaris per a participar a la plataforma. Sentim les molèsties.";
		
		System.out.println(correo);
		return "redirect:../volunteersList";
	}
	
	//Muestra los datos del voluntario seleccionado
	@RequestMapping(value="/showVolunteer/{usr}")
	public String showVolunteer(Model model, @PathVariable String usr, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("volunteer", volunteerDao.getVolunteerByUsr(usr));
		return "casVolunteer/volunteer";
	}
}
