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

import es.uji.ei1027.majorsacasa.dao.ContractDAO;
import es.uji.ei1027.majorsacasa.model.Contract;

@Controller
@RequestMapping("/contract")
public class ContractController {
	private ContractDAO contractDao;
	
	@Autowired
	public void setElderlyDao(ContractDAO contractDao) {
		this.contractDao=contractDao;
	}
	
	@RequestMapping(value="/list")
	public String listContracts(Model model) {
		model.addAttribute("contracts", contractDao.getContracts());
		return "contract/list";
	}
	
	@RequestMapping(value="/add") 
    public String addContract(Model model) {
        model.addAttribute("contract", new Contract());
        return "contract/add";
    }

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("contract") Contract contract, BindingResult bindingResult) {
		contract.setDateBeginning(new Date());
				
		if (bindingResult.hasErrors())
			return "contract/add";
        contractDao.addContract(contract);
        return "redirect:list";
    }

	//Variable interna en la que guardamos la fecha de creacion de un contrato para que no se
	// modifique cuando actualizamos sus datos
	private Date dateBeginning;
		
		
	@RequestMapping(value="/update/{number}", method = RequestMethod.GET)
    public String editContract(Model model, @PathVariable int number) {
		Contract contract = contractDao.getContract(number);
		dateBeginning = contract.getDateBeginning();
        model.addAttribute("contract", contract);
        return "contract/update"; 
    }

	
	@RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("contract") Contract contract, BindingResult bindingResult) {
		contract.setDateBeginning(dateBeginning);
				
        if (bindingResult.hasErrors()) 
        	return "contract/update";
        contractDao.updateContract(contract);
        return "redirect:list"; 
    }

   @RequestMapping(value="/delete/{number}")
    public String processDelete(@PathVariable int number) {
           contractDao.deleteContract(number);
           return "redirect:../list"; 
    }
}