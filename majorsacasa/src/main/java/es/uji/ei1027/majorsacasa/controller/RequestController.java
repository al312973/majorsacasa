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

import es.uji.ei1027.majorsacasa.dao.RequestDAO;
import es.uji.ei1027.majorsacasa.model.Request;


@Controller
@RequestMapping("/request")
public class RequestController {
	private RequestDAO requestDao;
	
	@Autowired
	public void setRequestDao(RequestDAO requestDao) {
		this.requestDao=requestDao;
	}
	
	@RequestMapping(value="/list")
	public String listRequests(Model model) {
		model.addAttribute("requests", requestDao.getRequests());
		return "request/list";
	}
	
	@RequestMapping(value="/add") 
    public String addRequest(Model model) {
        model.addAttribute("request", new Request());
        return "request/add";
    }

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("request") Request request, BindingResult bindingResult) {
		//Completa y/o modifica los campos con los atributos que se necesitan y no proporciona el usuario
		request.setCreationDate(new Date());
		if (request.getComments().equals(""))
			request.setComments(null);
		
		if (bindingResult.hasErrors())
			return "request/add";
		requestDao.addRequest(request);
        return "redirect:list";
    }

	//Variable interna en la que guardamos la fecha de creacion de un elderly para que no se
	// modifique cuando actualizamos sus datos
	private Date dateCreation;
		
	@RequestMapping(value="/update/{number}", method = RequestMethod.GET)
    public String editEldery(Model model, @PathVariable int number) {
		Request request = requestDao.getRequest(number);
		dateCreation = request.getCreationDate();
        model.addAttribute("request", request);
        return "request/update"; 
    }

	
	@RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("request") Request request, BindingResult bindingResult) {
		//Completa y/o modifica los campos con los atributos que se necesitan y no proporciona el usuario     
    	request.setCreationDate(dateCreation);
		if (request.getComments().equals(""))
			request.setComments(null);
		
		if (bindingResult.hasErrors())
			return "request/add";
		requestDao.addRequest(request);
        return "redirect:list";
    }

   @RequestMapping(value="/delete/{number}")
    public String processDelete(@PathVariable int number) {
	   
	   	   requestDao.deleteRequest(number);
           return "redirect:../list"; 
    }
}