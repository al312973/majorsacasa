package es.uji.ei1027.majorsacasa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.majorsacasa.dao.ElderlyDAO;
import es.uji.ei1027.majorsacasa.model.Elderly;


@Controller
@RequestMapping("/elderly")
public class ElderlyController {
	private ElderlyDAO elderlyDao;
	
	@Autowired
	public void setElderlyDao(ElderlyDAO elderlyDao) {
		this.elderlyDao=elderlyDao;
	}
	
	@RequestMapping(value="/list")
	public String listElderlies(Model model) {
		model.addAttribute("elderlies", elderlyDao.getElderlies());
		return "elderly/list";
	}
	
	@RequestMapping(value="/add") 
    public String addElderly(Model model) {
        model.addAttribute("nadador", new Elderly());
        return "elderly/add";
    }

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("elderly") Elderly elderly, BindingResult bindingResult) {
		if (bindingResult.hasErrors())
			return "elderly/add";
        elderlyDao.addElderly(elderly);
        return "redirect:list.html";
    }

	@RequestMapping(value="/update/{DNI}", method = RequestMethod.GET)
    public String editEldery(Model model, @PathVariable String DNI) {
        model.addAttribute("elderly", elderlyDao.getElderly(DNI));
        return "elderly/update"; 
    }

	@RequestMapping(value="/update", method = RequestMethod.POST) 
    public String processUpdateSubmit(@ModelAttribute("elderly") Elderly elderly, BindingResult bindingResult) {
         if (bindingResult.hasErrors()) 
             return "elderly/update";
         elderlyDao.updateElderly(elderly);
         return "redirect:list.html"; 
    }

   @RequestMapping(value="/delete/{DNI}")
    public String processDelete(@PathVariable String DNI) {
           elderlyDao.deleteElderly(DNI);
           return "redirect:../list"; 
    }


}