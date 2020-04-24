package es.uji.ei1027.majorsacasa.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.majorsacasa.dao.VolunteerDAO;
import es.uji.ei1027.majorsacasa.model.Volunteer;


@Controller
@RequestMapping("/volunteer")
public class VolunteerController {
private VolunteerDAO volunteerDao;
	
	@Autowired
	public void setVolunteerDao(VolunteerDAO volunteerDao) {
		this.volunteerDao=volunteerDao;
	}
	
	@RequestMapping(value="/list")
	public String listVolunteers(Model model) {
		model.addAttribute("volunteers", volunteerDao.getVolunteers());
		return "volunteer/list";
	}
	
	@RequestMapping(value="/add") 
    public String addVolunteer(Model model) {
        model.addAttribute("volunteer", new Volunteer());
        return "volunteer/add";
    }

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("volunteer") Volunteer volunteer, BindingResult bindingResult) {
		volunteer.setApplicationDate(new Date());

		if (bindingResult.hasErrors())
			return "volunteer/add";
		volunteerDao.addVolunteer(volunteer);
        return "redirect:list";
    } 
		
	@RequestMapping(value="/update/{USR}", method = RequestMethod.GET)
    public String editVolunteer(Model model, @PathVariable String USR) {
		Volunteer volunteer = volunteerDao.getVolunteerByUsr(USR);
        model.addAttribute("volunteer", volunteer);
        return "volunteer/update"; 
    }
	
	@RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("volunteer") Volunteer volunteer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) 
        	return "volunteer/update";
        volunteerDao.updateVolunteer(volunteer);
        return "redirect:list"; 
    }

   @RequestMapping(value="/delete/{USR}")
    public String processDelete(@PathVariable String USR) {
           volunteerDao.deleteVolunteer(USR);
           return "redirect:../list"; 
    }
}
