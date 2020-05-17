package es.uji.ei1027.majorsacasa.controller;

import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import es.uji.ei1027.majorsacasa.dao.SocialWorkerDAO;
import es.uji.ei1027.majorsacasa.dao.CompanyDAO;
import es.uji.ei1027.majorsacasa.dao.ContractDAO;
import es.uji.ei1027.majorsacasa.model.Company;
import es.uji.ei1027.majorsacasa.model.Contract;
import es.uji.ei1027.majorsacasa.model.SocialWorker;

@Controller
@RequestMapping("/casManager")
public class CasManagerController {
	
	private SocialWorkerDAO socialWorkerDao;
	private CompanyDAO companyDao;
	private ContractDAO contractDao;
	
	//Guardamos aqui las variables de los diferentes pasos, para luego guardarlo todo en caso de completar el tercer paso
	private Company company;
	private SocialWorker socialWorker;
	
	@Autowired
	public void setContractDao(ContractDAO contractDao){
		this.contractDao = contractDao;
	}
	
	@Autowired
	public void setSocialWorkerDao(SocialWorkerDAO socialworkerDao) {
		this.socialWorkerDao=socialworkerDao;
	}
	
	@Autowired
	public void setCompanyDao(CompanyDAO companyDao) {
		this.companyDao=companyDao;
	}
	
	@RequestMapping(value="/altaEmpresa")
    public String addCompany(Model model) {
		model.addAttribute("company", new Company());
        return "casManager/altaEmpresa"; 
    }
	
	@RequestMapping(value="/altaEmpresa" , method=RequestMethod.POST)
	public String processAddCompanySubmit(@ModelAttribute("company") Company company, BindingResult bindingResult){
		if (bindingResult.hasErrors()){
			return "casManager/altaEmpresa"; 
		}
		this.company = company;
		return "redirect:altaSocialWorker";
	}
	
	@RequestMapping(value="/altaSocialWorker")
	public String addSocialWorker(Model model){
		model.addAttribute("socialWorker", new SocialWorker());
		return "casManager/altaSocialWorker";
	}
	
	@RequestMapping(value="/altaSocialWorker", method=RequestMethod.POST)
	public String processAddSocialWorkerSubmit(@ModelAttribute("socialWorker") SocialWorker socialWorker, BindingResult bindingResult){
		if (bindingResult.hasErrors()){
			return "casManager/altaEmpresa"; 
		}
		
		//Rellenamos los campos no solicitados mediante el formulario
		socialWorker.setName(company.getContactPersonName());
		socialWorker.setPhoneNumber(company.getContactPersonPhoneNumber());
		socialWorker.setEmail(company.getContactPersonEmail());
		this.socialWorker = socialWorker;
		
		//Mandamos correo de confirmación a la persona de contacto para avisarle que ya tiene correo y contraseña para entrar en el sistema
		System.out.println("\nS'ha manat un correu de notificació a "+socialWorker.getEmail()
		+"\nNotificació d'alta com a nou usuari "+socialWorker.getName()+"\n"
		+"Les dades per a iniciar sesió son:\n"
		+ "\tUsuari: "+socialWorker.getUserCAS()+"\n"
		+ "\tContrasenya: "+socialWorker.getPwd()+"\n");
		
		return "redirect:altaContrato";
	}
	
	
	
	@RequestMapping(value="/altaContrato")
	public String addContract(Model model){
		model.addAttribute("contract", new Contract());
		return "casManager/altaContrato";
	}
	
	@RequestMapping(value="/altaContrato" , method=RequestMethod.POST)
	public String processAddContractSubmit(@ModelAttribute("contract") Contract contract, BindingResult bindingResult){
		contract.setCompany_cif(company.getCIF());
		contract.setNumber(contractDao.getNumberContracts() + 1);
		contract.setServiceType(company.getServiceType());
		
		//Guardamos la empresa, el contrato y el socialWorker porque estmo en el tercer paso
		companyDao.addCompany(company);
		contractDao.addContract(contract);
		socialWorkerDao.addSocialWorker(socialWorker);
		
		return "casManager/mensajeConfirmacion";		
	}
	
	
	//Estos dos métodos siguientes los usamos para que , si el usuario da atrás en cualquier paso, que se muestren los datos que ya se habian introducido en el paso anterior
	@RequestMapping(value="/atrasSocialWorker")
	public String atrasSocialWorker(Model model){
		model.addAttribute("company", company);
		return "casManager/altaEmpresa";
	}
	
	@RequestMapping(value="/*atrasContract")
	public String atrasContract(Model model){
		model.addAttribute("socialWorker", socialWorker);
		return "casManager/altaSocialWorker";
	}

}
