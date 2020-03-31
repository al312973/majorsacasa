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

import es.uji.ei1027.majorsacasa.dao.SocialWorkerDAO;
import es.uji.ei1027.majorsacasa.model.SocialWorker;


@Controller
@RequestMapping("/socialworker")
public class SocialWorkerController {
	private SocialWorkerDAO socialworkerDao;
	
	@Autowired
	public void setSocialWorkerDao(SocialWorkerDAO socialworkerDao) {
		this.socialworkerDao=socialworkerDao;
	}
	
	@RequestMapping(value="/list")
	public String listSocialWorkers(Model model) {
		model.addAttribute("elderlies", socialworkerDao.getSocialWorkers());
		return "socialworker/list";
	}
	
	@RequestMapping(value="/add") 
    public String addSocialWorker(Model model) {
        model.addAttribute("socialworker", new SocialWorker());
        return "socialworker/add";
    }

//	@RequestMapping(value="/add", method=RequestMethod.POST)
//	public String processAddSubmit(@ModelAttribute("socialworker") SocialWorker socialworker, BindingResult bindingResult) {
//		//Completa y/o modifica los campos con los atributos que se necesitan y no proporciona el usuario
//		
//		if (bindingResult.hasErrors())
//			return "socialworker/add";
//		socialworkerDao.addSocialWorker(socialworker);
//        return "redirect:list";
//    }

		
		
	@RequestMapping(value="/update/{USERCAS}", method = RequestMethod.GET)
    public String editSocialWorker(Model model, @PathVariable String USERCAS) {
		SocialWorker socialworker = socialworkerDao.getSocialWorker(USERCAS);
        model.addAttribute("socialworker", socialworker);
        return "socialworker/update"; 
    }

	
	@RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("socialworker") SocialWorker socialworker, BindingResult bindingResult) {
		//Completa y/o modifica los campos con los atributos que se necesitan y no proporciona el usuario
				
        if (bindingResult.hasErrors()) 
        	return "socialworker/update";
        socialworkerDao.updateSocialWorker(socialworker);
        return "redirect:list"; 
    }

   @RequestMapping(value="/delete/{USERCAS}")
    public String processDelete(@PathVariable String USERCAS) {
	   socialworkerDao.deleteSocialWorker(USERCAS);
           return "redirect:../list"; 
    }
}