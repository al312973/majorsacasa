package es.uji.ei1027.majorsacasa.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.majorsacasa.dao.ElderlyDAO;
import es.uji.ei1027.majorsacasa.dao.SocialWorkerDAO;
import es.uji.ei1027.majorsacasa.dao.VolunteerDAO;
import es.uji.ei1027.majorsacasa.model.UserDetails;

@Controller
public class LoginRegisterController {
	@Autowired
	private ElderlyDAO elderlyDao;
	@Autowired
	private SocialWorkerDAO socialWorkerDao;
	@Autowired
	private VolunteerDAO volunteerDao;

	//maayro@gmail.com 
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
		
		//Autenticado correctamente. Guardamos los datos del usuario autenticado en la sesion
		session.setAttribute("user", user); 
			
		//Segun el tipo de usuario que ha hecho el login, lo manda a su pagina principal
		if (user.getType().equals("elderly")) {
			return "redirect:/elderly/list";
		} else {
			if (user.getType().equals("socialWorker")) {
				return "redirect:/socialWorker/list";
			} else {
				return "redirect:/volunteer/list";
			}
		}
	}
	
	@RequestMapping("/logout") 
	public String logout(HttpSession session) {
		session.invalidate(); 
		return "redirect:/";
	}
}
