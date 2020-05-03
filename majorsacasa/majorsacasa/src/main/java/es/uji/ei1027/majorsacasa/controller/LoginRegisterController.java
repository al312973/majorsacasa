package es.uji.ei1027.majorsacasa.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.majorsacasa.dao.ElderlyDAO;
import es.uji.ei1027.majorsacasa.dao.SocialWorkerDAO;
import es.uji.ei1027.majorsacasa.dao.VolunteerDAO;
import es.uji.ei1027.majorsacasa.model.UserDetails;
import es.uji.ei1027.majorsacasa.model.Volunteer;
import es.uji.ei1027.majorsacasa.validator.VolunteerValidator;

@Controller
public class LoginRegisterController {
	@Autowired
	private ElderlyDAO elderlyDao;
	@Autowired
	private SocialWorkerDAO socialWorkerDao;
	@Autowired
	private VolunteerDAO volunteerDao;

	private UserController userController;
	
	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("user", new UserDetails());
		if (userController==null) {
			userController = new UserController(elderlyDao, socialWorkerDao, volunteerDao);
		}
		return "login";
	}

	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String checkLogin(@ModelAttribute("user") UserDetails user, BindingResult bindingResult, HttpSession session) {
		if (bindingResult.hasErrors()) {
			return "login";
		}
	    
		//Comprueba que el login sea correcto e intenta cargar sus datos
		user = userController.checkCredentials(user.getEmail(), user.getPassword()); 
		if (user == null) {
			bindingResult.rejectValue("email", "badcredentials", "Credencials errònies");
			bindingResult.rejectValue("password", "badcredentials", "Credencials errònies");
			return "login";
		}
		
		//Comprobamos si la contraseña es correcta
		if (!user.isPwdCorrect()) {
			bindingResult.rejectValue("password", "badpassword", "Contrasenya incorrecta");
			return "login";
		}
		
		//Autenticado correctamente. Guardamos los datos del usuario autenticado en la sesion
		session.setAttribute("user", user); 
			
		//Segun el tipo de usuario que ha hecho el login, lo manda a su pagina principal
		if (user.getType().equals("elderly")) {
			return "redirect:/elderly/elderlyRequest";
		} else {
			if (user.getType().equals("socialWorker")) {
				return "redirect:/socialWorker/list";
			} else {
				return "redirect:/volunteer/services";
			}
		}
	}
	
	@RequestMapping("/register")
	public String register(Model model) {
		return "register";
	}
	
	@RequestMapping("/register/elderly")
	public String registerElderly(Model model) {
		return "register/elderly";
	}
	
	@RequestMapping("/register/volunteer")
	public String registerVolunteer(Model model) {
		model.addAttribute("volunteer", new Volunteer());
		return "register/volunteer";
	}
	
	@RequestMapping(value="/register/volunteer", method=RequestMethod.POST)
	public String newVolunteer(@ModelAttribute("volunteer") Volunteer volunteer, BindingResult bindingResult, HttpSession session) {
		if (bindingResult.hasErrors()) {
			return "register/volunteer";
		}
		
	    //Comprobamos que no haya errores
		VolunteerValidator volunteerValidator = new VolunteerValidator(volunteerDao, null); 
		volunteerValidator.validate(volunteer, bindingResult);
		if (bindingResult.hasErrors()) {
			return "register/volunteer";
		}
		
		//Si no hay errores registramos el nuevo voluntario en la BBDD
		volunteer.setAcceptationDate(null); //No ha sido aceptado
		volunteer.setAccepted(false);
		volunteer.setEndDate(null); //Se asume que en principio no hay fecha de fin y cuando el voluntario quiera darse de baja, se modificara este atributo
		volunteerDao.addVolunteer(volunteer);
		
		//Dejamos el usuario como autenticado en la sesion
		UserDetails user = new UserDetails(volunteer.getEmail(), null, "volunteer", true);
		session.setAttribute("user", user); 
		
		//Se envía un correo de confirmación
		System.out.println("\nS'ha manat un correu de confirmació a "+volunteer.getEmail()
				+"\nUs heu registrat correctament com a voluntari a l'aplicació majors a casa.\n"
				+"Les vostres credencials son:\n"
				+ "\tNom: "+volunteer.getName()+"\n"
				+ "\tUsuari: "+volunteer.getUsr()+"\n"
				+ "\tContrasenya: "+volunteer.getPwd());
		return "redirect:/volunteer/services";
	}
	
	@RequestMapping("/logout") 
	public String logout(HttpSession session) {
		session.invalidate(); 
		return "redirect:/";
	}
}
