package es.uji.ei1027.majorsacasa.controller;

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
@RequestMapping("/socialWorker")
public class SocialWorkerController {
	private SocialWorkerDAO socialWorkerDao;
	
	@Autowired
	public void setSocialWorkerDao(SocialWorkerDAO socialWorkerDao) {
		this.socialWorkerDao=socialWorkerDao;
	}
	
	@RequestMapping(value="/list")
	public String listSocialWorkers(Model model) {
		model.addAttribute("socialWorkers", socialWorkerDao.getSocialWorkers());
		return "socialWorker/list";
	}
	
	@RequestMapping(value="/add") 
    public String addSocialWorker(Model model) {
        model.addAttribute("socialWorker", new SocialWorker());
        return "socialWorker/add";
    }

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("socialWorker") SocialWorker socialWorker, BindingResult bindingResult) {	
		if (bindingResult.hasErrors())
			return "socialWorker/add";
        socialWorkerDao.addSocialWorker(socialWorker);
        return "redirect:list";
    }
	
	@RequestMapping(value="/update/{userCAS}", method = RequestMethod.GET)
    public String editSocialWorker(Model model, @PathVariable String userCAS) {
		SocialWorker socialWorker = socialWorkerDao.getSocialWorker(userCAS);
        model.addAttribute("socialWorker", socialWorker);
        return "socialWorker/update"; 
    }
	
	@RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("socialWorker") SocialWorker socialWorker, BindingResult bindingResult) {		
        if (bindingResult.hasErrors()) 
        	return "socialWorker/update";
        socialWorkerDao.updateSocialWorker(socialWorker);
        return "redirect:list"; 
    }

   @RequestMapping(value="/delete/{userCAS}")
    public String processDelete(@PathVariable String userCAS) {
           socialWorkerDao.deleteSocialWorker(userCAS);
           return "redirect:../list"; 
    }
}
