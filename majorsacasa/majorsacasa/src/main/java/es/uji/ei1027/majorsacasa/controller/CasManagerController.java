package es.uji.ei1027.majorsacasa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ch.qos.logback.core.net.SyslogConstants;
import es.uji.ei1027.majorsacasa.dao.SocialWorkerDAO;
import es.uji.ei1027.majorsacasa.dao.CompanyDAO;
import es.uji.ei1027.majorsacasa.dao.ContractDAO;
import es.uji.ei1027.majorsacasa.model.Company;
import es.uji.ei1027.majorsacasa.model.Contract;
import es.uji.ei1027.majorsacasa.model.SocialWorker;
import es.uji.ei1027.majorsacasa.validator.CompanyValidator;
import es.uji.ei1027.majorsacasa.validator.SocialWorkerValidator;

@Controller
@RequestMapping("/casManager")
public class CasManagerController {
	
	private SocialWorkerDAO socialWorkerDao;
	private CompanyDAO companyDao;
	private ContractDAO contractDao;
	
	//Guardamos aqui las variables de los diferentes pasos, para luego guardarlo todo en caso de completar el tercer paso y para mostrar sus datos en caso de "atrás"
	private Company company;
	private SocialWorker socialWorker;
	private Contract contratoNuevo;
	
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
		CompanyValidator companyValidator = new CompanyValidator(companyDao, company);
		companyValidator.validate(company, bindingResult);
		
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
		SocialWorkerValidator socialWorkerValidator = new SocialWorkerValidator(socialWorkerDao, socialWorker);
		socialWorkerValidator.validate(socialWorker, bindingResult);
		if (bindingResult.hasErrors()){
			return "casManager/altaSocialWorker"; 
		}
		
		//Rellenamos los campos no solicitados mediante el formulario
		socialWorker.setName(company.getContactPersonName());
		socialWorker.setPhoneNumber(company.getContactPersonPhoneNumber());
		socialWorker.setEmail(company.getContactPersonEmail());
		this.socialWorker = socialWorker;
			
		return "redirect:altaContrato";
	}
	
	@RequestMapping(value="/altaContrato")
	public String addContract(Model model){
		model.addAttribute("contract", new Contract());
		return "casManager/altaContrato";
	}
	
	@RequestMapping(value="/altaContrato" , method=RequestMethod.POST)
	public String processAddContractSubmit(@ModelAttribute("contract") Contract contract, BindingResult bindingResult){
		
		//Rellenamos los campos no solicitados mediante el formulario
		contract.setCompany_cif(company.getCIF());
		contract.setNumber(contractDao.getNumberContracts() + 1);
		contract.setServiceType(company.getServiceType());
		
		//Guardamos la empresa, el contrato y el socialWorker porque estamos en el tercer paso
		
		companyDao.addCompany(company);
		contractDao.addContract(contract);
		socialWorkerDao.addSocialWorker(socialWorker);
		
		//Mandamos correo de confirmación a la persona de contacto para avisarle que ya tiene correo y contraseña para entrar en el sistema
		System.out.println("\nS'ha manat un correu de notificació a "+socialWorker.getEmail()
		+"\nNotificació d'alta com a nou usuari "+socialWorker.getName()+"\n"
		+"Les dades per a iniciar sesió son:\n"
		+ "\tUsuari: "+socialWorker.getUserCAS()+"\n"
		+ "\tContrasenya: "+socialWorker.getPwd()+"\n");
		
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
	
	//Método para listar todas las empresas que hay guardadas en la base de datos 
	@RequestMapping(value="/listarEmpresas")
	public String listarEmpresas(Model model){
		model.addAttribute("companys", companyDao.getCompanys());
		return "casManager/listarEmpresas";
	}
	
	//Método para añadir un nuevo contrato de una empresa que ya existe
	@RequestMapping(value="/altaNuevoContrato/{CIF}")
		public String altaNuevoContrato(Model model, @PathVariable String CIF){
			Company c = companyDao.getCompany(CIF);
			contratoNuevo = new Contract();
			contratoNuevo.setNumber(contractDao.getNumberContracts() + 1 );
			contratoNuevo.setServiceType(c.getServiceType());
			contratoNuevo.setCompany_cif(c.getCIF());
			model.addAttribute("contract", contratoNuevo);
			return "casManager/nuevoContrato";
		}
	
	//Método para introdur los datos del nuevo contrato en la base de datos y un vez terminado que vuelva a trás en la lista de empresas
	@RequestMapping(value="/altaNuevoContrato" , method=RequestMethod.POST)
	public String altaNuevoContrato(@ModelAttribute("contract") Contract contract, BindingResult bindingResult){
		contratoNuevo.setDateBeginning(contract.getDateBeginning());
		contratoNuevo.setDateEnding(contract.getDateEnding());
		contratoNuevo.setDescription(contract.getDescription());
		contratoNuevo.setQuantityServices(contract.getQuantityServices());
		contratoNuevo.setUnitsOfMeasure(contract.getUnitsOfMeasure());
		contratoNuevo.setPriceUnit(contract.getPriceUnit());
		contractDao.addContract(contratoNuevo);
		return "redirect:listarEmpresas";
	}

}
