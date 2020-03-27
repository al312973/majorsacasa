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

import es.uji.ei1027.majorsacasa.dao.AvailabilityDAO;
import es.uji.ei1027.majorsacasa.model.Availability;


@Controller
@RequestMapping("/availability")
public class AvailabilityController {
	private AvailabilityDAO availabilityDao;
	
	@Autowired
	public void setAvailabilityDao(AvailabilityDAO availabilityDao) {
		this.availabilityDao=availabilityDao;
	}
	
	@RequestMapping(value="/list")
	public String listAvailabilitys(Model model) {
		model.addAttribute("Availabilitys", availabilityDao.getAvailabilitys());
		return "availability/list";
	}
	
	@RequestMapping(value="/add") 
    public String addAvailability(Model model) {
        model.addAttribute("availability", new Availability());
        return "availability/add";
    }

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("availability") Availability availability, BindingResult bindingResult) {
		availability.setDate(new Date());
		//availability.setBegginingHour(new LocalTime());
	
		if (bindingResult.hasErrors())
			return "availability/add";
		availabilityDao.addAvailability(availability);
        return "redirect:list";
    }

	//Variable interna en la que guardamos la fecha de creacion de un elderly para que no se
	// modifique cuando actualizamos sus datos
	//private Date date;
		
		
	@RequestMapping(value="/update/{availability}", method = RequestMethod.GET)
    public String editAvailability(Model model, @PathVariable Availability availability) {
		Availability availability1 = availabilityDao.getAvailability(availability);
		//date = availability.getDate();
        model.addAttribute("availability", availability1);
        return "availability/update"; 
    }

	
	@RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("availability") Availability availability, BindingResult bindingResult) {
		
        if (bindingResult.hasErrors()) 
        	return "availability/update";
        availabilityDao.updateAvailability(availability);
        return "redirect:list"; 
    }

}
