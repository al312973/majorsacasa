package es.uji.ei1027.majorsacasa.controller;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.majorsacasa.dao.CompanyDAO;
import es.uji.ei1027.majorsacasa.dao.ContractDAO;
import es.uji.ei1027.majorsacasa.model.Company;
import es.uji.ei1027.majorsacasa.model.Contract;
import es.uji.ei1027.majorsacasa.validator.CompanyValidator;
import es.uji.ei1027.majorsacasa.validator.ContractValidator;

@Controller
@RequestMapping("/casManager")
public class CasManagerController {
	private CompanyDAO companyDao;
	private ContractDAO contractDao;
	private CompanyValidator companyValidator;
	private SimpleDateFormat formatter;
	
	//Variables company y contract que guarda la informacion introducida en los diferentes pasos
	private Company company;
	private Contract contract;
	
	@Autowired
	public void setContractDao(ContractDAO contractDao){
		this.contractDao = contractDao;
	}
	
	@Autowired
	public void setCompanyDao(CompanyDAO companyDao) {
		this.companyDao=companyDao;
	}
	
	//Primer paso de introducción de una empresa que muestra el formulario con los datos de la empresa
	@RequestMapping(value="/newCompany/step1", method=RequestMethod.GET)
    public String addCompany(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		if (formatter==null)
			this.formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		if (company==null)
			model.addAttribute("company", new Company());
		else
			model.addAttribute("company", company);
        return "casManager/newCompany"; 
    }
	
	//Primer paso de introducción de una empresa. Recoge los datos de la empresa introducidos por el casManager
	@RequestMapping(value="/newCompany/step1", method=RequestMethod.POST)
	public String processAddCompanySubmit(@ModelAttribute("company") Company company, BindingResult bindingResult, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		companyValidator = new CompanyValidator(companyDao);
		companyValidator.validate(company, bindingResult);
		
		if (bindingResult.hasErrors()){
			return "casManager/newCompany"; 
		}

		this.company = company;
		
		return "redirect:/casManager/newCompany/step2";
	}
	
	//Segundo paso de introducción de una empresa que muestra el formulario con los datos de la persona de contacto de la empresa
	@RequestMapping(value="/newCompany/step2", method=RequestMethod.GET)
	public String addContactPerson(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("company", this.company);
		return "casManager/contactPerson";
	}
	
	//Segundo paso de introducción de una empresa. Recoge los datos de la persona de contacto introducidos por el casManager
	@RequestMapping(value="/newCompany/step2", method=RequestMethod.POST)
	public String processAddContactPersonSubmit(@ModelAttribute("company") Company company, 
			BindingResult bindingResult, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		this.company.setContactPersonName(company.getContactPersonName());
		this.company.setContactPersonEmail(company.getContactPersonEmail());
		this.company.setContactPersonPhoneNumber(company.getContactPersonPhoneNumber());
		this.company.setPwd(company.getPwd());
		
		companyValidator.validate(this.company, bindingResult);
		
		if (bindingResult.hasErrors()){
			return "casManager/contactPerson"; 
		}
			
		return "redirect:/casManager/newCompany/step3";
	}
	
	//Tercer paso de introducción de una empresa que muestra el formulario para introducir el primer contrato de la empresa
	@RequestMapping(value="/newCompany/step3", method=RequestMethod.GET)
	public String addContract(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("serviceType", company.getServiceType());
		model.addAttribute("contract", new Contract());
		return "casManager/firstContract";
	}
	
	//Tercer paso de introducción de una empresa. Recoge los datos del primer contrato de prestacion de servicios
	@RequestMapping(value="/newCompany/step3", method=RequestMethod.POST)
	public String processAddContractSubmit(@ModelAttribute("contract") Contract contract,
			BindingResult bindingResult, Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		ContractValidator validator = new ContractValidator(); 
		validator.validate(contract, bindingResult);
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("serviceType", company.getServiceType());
			return "casManager/newContract";
		}
		
		//Si la información facilitada es correcta, rellenamos los campos no solicitados mediante el formulario
		contract.setCompany_cif(company.getCIF());
		contract.setNumber(contractDao.getNumberContracts() + 1);
		contract.setServiceType(company.getServiceType());
		if (company.getServiceType()==0) {
			contract.setPriceUnit(4);
			contract.setUnitsOfMeasure("€/menú");
		}else if (company.getServiceType()==1) {
			contract.setPriceUnit(10);
			contract.setUnitsOfMeasure("€/visita");
		} else {
			contract.setPriceUnit(7);
			contract.setUnitsOfMeasure("€/hora");
		}
		
		//Guardamos la empresa y el contrato en la base de datos
		this.contract = contract;
		companyDao.addCompany(company);
		contractDao.addContract(contract);
		
		//Mandamos correo de confirmación a la persona de contacto para avisarle que ya tiene correo y contraseña para entrar en el sistema
		System.out.println("\nS'ha manat un correu de notificació a "+company.getContactPersonName()
		+"\nNotificació d'alta com a nou usuari "+company.getContactPersonEmail()+"\n"
		+"La seva empresa denominada "+company.getName()+" amb CIF "+company.getCIF()+" ha sigut donada d'alta a la plataforma Majors a Casa\n"
		+"Les dades per a iniciar sesió i gestionar el compte de l'empresa son:\n"
		+ "\tEmail: "+company.getContactPersonEmail()+"\n"
		+ "\tContrasenya: "+company.getPwd()+"\n"
		+"Entra al teu compte el mes aviat posible i canvia la contrasenya. Salutacions.");
		
		//Ademas se notifica también del alta del nuevo contrato
		String correo ="\nS'ha manat un correu de notificació a "+company.getContactPersonEmail()
		+"\nBenvolgut "+company.getContactPersonName()+",\n"
		+"S'ha enregistrat un nou contracte de prestació de serveis per a l'empresa amb CIF: "+company.getCIF()+". Dades del contracte:\n"
		+ "\tData d'inici: "+formatter.format(contract.getDateBeginning())+"\n";
		if (contract.getDateEnding()!=null)
			correo+="\tData final: "+formatter.format(contract.getDateEnding())+"\n";
		correo+="\tDescripció: "+contract.getDescription()+"\n"
		+"\tQuantitat de serveis inclosos: "+contract.getQuantityServices()+"\n"
		+"\tPreu per servei: "+contract.getPriceUnit()+contract.getUnitsOfMeasure();
		
		System.out.println(correo);
		
		return "redirect:/casManager/newCompany/confirmation";
	}
	
	//Muestra la página de confirmación de alta de empresa y primer contrato 
	@RequestMapping(value="/newCompany/confirmation", method=RequestMethod.GET)
	public String showConfirmation(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("company", company);
		model.addAttribute("contract", contract);
		
		this.company = null;
		this.contract = null;
		
		return "casManager/confirmation";
	}
		
	//Método para listar todas las empresas que hay guardadas en la base de datos 
	@RequestMapping(value="/companiesList", method=RequestMethod.GET)
	public String showCompanies(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("companies", companyDao.getCompanies());
		return "casManager/companiesList";
	}
	
	//Método para añadir un nuevo contrato de una empresa que ya existe
	@RequestMapping(value="/companiesList/{CIF}/newContract", method=RequestMethod.GET)
	public String newContract(Model model, @PathVariable String CIF, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		Company company = companyDao.getCompanyByCIF(CIF);
		Contract contract = new Contract();

		model.addAttribute("CIF", CIF);
		model.addAttribute("serviceType", company.getServiceType());
		model.addAttribute("contract", contract);
		return "casManager/newContract";
	}

	//Método para introdur los datos del nuevo contrato en la base de datos y un vez terminado que vuelva a trás en la lista de empresas
	@RequestMapping(value="/companiesList/{CIF}/newContract" , method=RequestMethod.POST)
	public String processNewContractSubmit(@ModelAttribute("contract") Contract contract, @PathVariable String CIF,
									BindingResult bindingResult, Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		Company company = companyDao.getCompanyByCIF(CIF);
		
		ContractValidator validator = new ContractValidator(); 
		validator.validate(contract, bindingResult);
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("serviceType", company.getServiceType());
			return "casManager/newContract";
		}
		
		//Si no tiene errores completamos los campos que faltan y lo registramos en el sistema
		contract.setNumber(contractDao.getNumberContracts() + 1 );
		contract.setServiceType(company.getServiceType());
		contract.setCompany_cif(CIF);
		if (company.getServiceType()==0) {
			contract.setPriceUnit(4);
			contract.setUnitsOfMeasure("€/menú");
		}else if (company.getServiceType()==1) {
			contract.setPriceUnit(10);
			contract.setUnitsOfMeasure("€/visita");
		} else {
			contract.setPriceUnit(7);
			contract.setUnitsOfMeasure("€/hora");
		}
		
		contractDao.addContract(contract);
		
		//Mana un correu informatiu al responsable de l'empresa
		String correo ="\nS'ha manat un correu de notificació a "+company.getContactPersonEmail()
		+"\nBenvolgut "+company.getContactPersonName()+",\n"
		+"S'ha enregistrat un nou contracte de prestació de serveis per a l'empresa amb CIF: "+company.getCIF()+". Dades del contracte:\n"
		+ "\tData d'inici: "+formatter.format(contract.getDateBeginning())+"\n";
		if (contract.getDateEnding()!=null)
			correo+="\tData final: "+formatter.format(contract.getDateEnding())+"\n";
		correo+="\tDescripció: "+contract.getDescription()+"\n"
		+"\tQuantitat de serveis inclosos: "+contract.getQuantityServices()+"\n"
		+"\tPreu per servei: "+contract.getPriceUnit()+contract.getUnitsOfMeasure();
		
		System.out.println(correo);
		
		return "redirect:/casManager/companiesList";
	}
}
