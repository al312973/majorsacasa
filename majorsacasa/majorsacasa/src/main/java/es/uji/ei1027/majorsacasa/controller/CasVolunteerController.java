package es.uji.ei1027.majorsacasa.controller;


import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import es.uji.ei1027.majorsacasa.dao.VolunteerDAO;
import es.uji.ei1027.majorsacasa.model.Volunteer;

@Controller
@RequestMapping("/casVolunteer")
public class CasVolunteerController {
	private VolunteerDAO volunteerDao;
	
	@Autowired 
	public void setVolunteerDao(VolunteerDAO volunteerDao){
		this.volunteerDao = volunteerDao;
	}
	
	//Lista aquellos voluntarios pendientes de aceptacion, es decir, aquellos cuya fecha de aceptacion sea null y no tenga nada en fechaFin , porque fechaFin quiere decir que está rechazado
	@RequestMapping(value="/altaVoluntarios", method = RequestMethod.GET)
    public String listarVoluntariosPendientesAceptacion(Model model) {
		ArrayList<Volunteer> voluntarios = new ArrayList<Volunteer>(volunteerDao.getVolunteers()); 
		ArrayList<Volunteer> voluntariosPendientesAceptacion = new ArrayList<Volunteer>();
		
		for (Volunteer voluntario : voluntarios){
			if (voluntario.getAcceptationDate() == null) 
				if (voluntario.getEndDate() == null){
				voluntariosPendientesAceptacion.add(voluntario);
				}
		}
		
		model.addAttribute("volunteers", voluntariosPendientesAceptacion);
        return "casVolunteer/altaVoluntarios"; 
    }

	//Acepta un voluntario poniendo en su campo acceptationDate a la fecha actual y el campo accepted a true.
	@RequestMapping(value="/altaVolunteer/{usr}")
	public String addVolunteer(@PathVariable String usr){	
		volunteerDao.aceptaVoluntario(usr);
		return "redirect:../altaVoluntarios";
	}
	
	//Pone a un voluntario fechaFin a la fecha actual para que sepamos que está rechazado 
	@RequestMapping(value="/bajaVolunteer/{usr}")
	public String bajaVoluntarios(@PathVariable String usr){
		volunteerDao.deleteVolunteer(usr);
		return "redirect:../altaVoluntarios";
	}
	
	//Muestra los datos del voluntario seleccionado
	@RequestMapping(value="/showVolunteer/{usr}")
	public String showVolunteer(Model model, @PathVariable String usr){
		model.addAttribute("volunteer", volunteerDao.getVolunteerByUsr(usr));
		return "casVolunteer/volunteer";
	}
}
