package es.uji.ei1027.majorsacasa.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.majorsacasa.model.UserDetails;

@Controller
@RequestMapping("/casManager")
public class CasManagerController {

	@RequestMapping(value="/altaEmpresa")
    public String showCasManagerPage(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
        return "casManager/altaEmpresa"; 
    }

}
