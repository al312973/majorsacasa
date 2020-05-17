package es.uji.ei1027.majorsacasa.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.majorsacasa.model.UserDetails;

@Controller
@RequestMapping("/casVolunteer")
public class CasVolunteerController {
	
	@RequestMapping(value="/altaVoluntarios", method = RequestMethod.GET)
    public String showCasVolunteerPage(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
        return "casVolunteer/altaVoluntarios"; 
    }
}
