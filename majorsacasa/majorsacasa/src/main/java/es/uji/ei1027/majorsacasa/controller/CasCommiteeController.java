package es.uji.ei1027.majorsacasa.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.majorsacasa.model.UserDetails;

@Controller
@RequestMapping("/casCommitee")
public class CasCommiteeController {
	
	@RequestMapping(value="/gestionarSolicitudes", method = RequestMethod.GET)
    public String showCasCommiteePage(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
        return "casCommitee/gestionarSolicitudes"; 
    }

}
