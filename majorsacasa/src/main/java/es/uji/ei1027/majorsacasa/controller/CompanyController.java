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

import es.uji.ei1027.majorsacasa.dao.CompanyDAO;
import es.uji.ei1027.majorsacasa.model.Company;


@Controller
@RequestMapping("/company")
public class CompanyController {
	private CompanyDAO companyDao;
	
	@Autowired
	public void setCompanyDao(CompanyDAO companyDao) {
		this.companyDao=companyDao;
	}
	
	@RequestMapping(value="/list")
	public String listCompanys(Model model) {
		model.addAttribute("companys", companyDao.getCompanys());
		return "company/list";
	}
	
	@RequestMapping(value="/add") 
    public String addCompany(Model model) {
        model.addAttribute("company", new Company());
        return "company/add";
    }

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("company") Company company, BindingResult bindingResult) {
	
		if (bindingResult.hasErrors())
			return "company/add";
        companyDao.addCompany(company);
        return "redirect:list";
    }		
		
	@RequestMapping(value="/update/{CIF}", method = RequestMethod.GET)
    public String editCompany(Model model, @PathVariable String CIF) {
		Company company = companyDao.getCompany(CIF);

		model.addAttribute("company", company);
        return "company/update"; 
    }

	
	@RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("company") Company company, BindingResult bindingResult) {
		
        if (bindingResult.hasErrors()) 
        	return "company/update";
        companyDao.updateCompany(company);
        return "redirect:list"; 
    }

   @RequestMapping(value="/delete/{CIF}")
    public String processDelete(@PathVariable String CIF) {
           companyDao.deleteCompany(CIF);
           return "redirect:../list"; 
    }
}
