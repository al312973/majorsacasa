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
	private SocialWorkerDAO socialworkerDao;
	
	@Autowired
	public void setSocialWorkerDao(SocialWorkerDAO socialworkerDao) {
		this.socialworkerDao=socialworkerDao;
	}
	
	@RequestMapping(value="/list")
	public String listSocialWorkers(Model model) {
		model.addAttribute("socialWorkers", socialworkerDao.getSocialWorkers());
		return "socialWorker/list";
	}
	
	@RequestMapping(value="/add") 
    public String addSocialWorker(Model model) {
        model.addAttribute("socialWorker", new SocialWorker());
        return "socialWorker/add";
    }

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("socialWorker") SocialWorker socialworker, BindingResult bindingResult) {
		if (bindingResult.hasErrors())
			return "socialWorker/add";
		socialworkerDao.addSocialWorker(socialworker);
        return "redirect:list";
    }
		
	@RequestMapping(value="/update/{USERCAS}", method = RequestMethod.GET)
    public String editSocialWorker(Model model, @PathVariable String USERCAS) {
		SocialWorker socialworker = socialworkerDao.getSocialWorkerByUserCAS(USERCAS);
        model.addAttribute("socialWorker", socialworker);
        return "socialWorker/update"; 
    }
	
	@RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("socialWorker") SocialWorker socialworker, BindingResult bindingResult) {		
        if (bindingResult.hasErrors()) 
        	return "socialWorker/update";
        socialworkerDao.updateSocialWorker(socialworker);
        return "redirect:list"; 
    }

   @RequestMapping(value="/delete/{USERCAS}")
    public String processDelete(@PathVariable String USERCAS) {
	   socialworkerDao.deleteSocialWorker(USERCAS);
           return "redirect:../list"; 
    }
}