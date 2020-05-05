package es.uji.ei1027.majorsacasa.controller;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.majorsacasa.dao.AvailabilityDAO;
import es.uji.ei1027.majorsacasa.dao.ContractDAO;
import es.uji.ei1027.majorsacasa.dao.ElderlyDAO;
import es.uji.ei1027.majorsacasa.dao.RequestDAO;
import es.uji.ei1027.majorsacasa.model.Elderly;
import es.uji.ei1027.majorsacasa.model.UserDetails;
import es.uji.ei1027.majorsacasa.dao.VolunteerDAO;
import es.uji.ei1027.majorsacasa.model.Volunteer;
import es.uji.ei1027.majorsacasa.dao.CompanyDAO;

import java.awt.List;
import java.time.LocalTime;
import java.util.ArrayList;
import es.uji.ei1027.majorsacasa.model.Availability;
import es.uji.ei1027.majorsacasa.model.Company;
import es.uji.ei1027.majorsacasa.model.Contract;
import es.uji.ei1027.majorsacasa.model.Request;
import es.uji.ei1027.majorsacasa.validator.ElderlyValidator;


@Controller
@RequestMapping("/elderly")
public class ElderlyController {
	private ElderlyDAO elderlyDao;
	private Elderly currentElderly;
	private RequestDAO requestDao;
	private ContractDAO contractDao;
	private AvailabilityDAO availabilityDao;
	private VolunteerDAO volunteerDao;
	private int contract_number;
	private CompanyDAO companyDao;
	
	@Autowired
	public void setAvailabilityDao(AvailabilityDAO availabilityDao){
		this.availabilityDao = availabilityDao;
	}
	
	@Autowired
	public void setCompanyDao(CompanyDAO companyDao){
		this.companyDao = companyDao;
	}
	
	
	@Autowired
	public void setContractDao(ContractDAO contractDao){
		this.contractDao = contractDao;
	}
	
	@Autowired
	public void setRequestDao(RequestDAO requestDao){
		this.requestDao = requestDao;
	}
	
	@Autowired
	public void setElderlyDao(ElderlyDAO elderlyDao) {
		this.elderlyDao=elderlyDao;
	}
	
	@Autowired
	public void setVolunteerDao(VolunteerDAO volunteerDao){
		this.volunteerDao = volunteerDao;
	}
	
	@RequestMapping(value="/list")
	public String listElderlies(Model model) {
		model.addAttribute("elderlies", elderlyDao.getElderlies());
		return "elderly/list";
	}
	
	@RequestMapping(value="/add") 
    public String addElderly(Model model) {
        model.addAttribute("elderly", new Elderly());
        return "elderly/add";
    }

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("elderly") Elderly elderly, BindingResult bindingResult) {
		//Completa y/o modifica los campos con los atributos que se necesitan y no proporciona el usuario
		elderly.setDateCreation(new Date());
		if (elderly.getAlergies().equals(""))
			elderly.setAlergies(null);
		if (elderly.getDiseases().equals(""))
			elderly.setDiseases(null);
		
		if (bindingResult.hasErrors())
			return "elderly/add";
        elderlyDao.addElderly(elderly);
        return "redirect:list";
    }

	//Variable interna en la que guardamos la fecha de creacion de un elderly para que no se
	// modifique cuando actualizamos sus datos
	private Date dateCreation;
		
		
	@RequestMapping(value="/update/{DNI}", method = RequestMethod.GET)
    public String editEldery(Model model, @PathVariable String DNI) {
		Elderly elderly = elderlyDao.getElderlyByDNI(DNI);
		dateCreation = elderly.getDateCreation();
        model.addAttribute("elderly", elderly);
        return "elderly/update"; 
    }

	
	@RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("elderly") Elderly elderly, BindingResult bindingResult) {
		//Completa y/o modifica los campos con los atributos que se necesitan y no proporciona el usuario
		elderly.setDateCreation(dateCreation);
		if (elderly.getAlergies().equals(""))
			elderly.setAlergies(null);
		if (elderly.getDiseases().equals(""))
			elderly.setDiseases(null);
		
        if (bindingResult.hasErrors()) 
        	return "elderly/update";
        elderlyDao.updateElderly(elderly);
        return "redirect:list"; 
    }

   @RequestMapping(value="/delete/{DNI}")
    public String processDelete(@PathVariable String DNI) {
           elderlyDao.deleteElderly(DNI);
           return "redirect:../list"; 
    }
   
   
   
   
   
  //MÉTODO PARA LISTAR LOS SERVICIOS CONTRATADOS
   @RequestMapping(value="/elderlyRequest")
   public String listRequest(Model model, HttpSession session){
	   UserDetails user = (UserDetails) session.getAttribute("user");
	   this.currentElderly = elderlyDao.getElderlyByEmail(user.getEmail());

	   //PONERMOS VALORES DE TRUE O FALSE EN CADA REQUEST PARA SABER SI SE TIENE QUE HABILITAR O NO EL BOTÓN DE SUPRIMIR
	   Date fechaActual = new Date();
	   ArrayList<Request> lista = new ArrayList<Request>(requestDao.getRequestsFromElderly(currentElderly.getDNI()));
	   for (Request r : lista){
		   if (r.getState() == 2) {
			   r.habilitado = false;
		   }else if(r.getEndDate().before(fechaActual)){
			   r.habilitado = false;
		   }else
			   r.habilitado = true;
		   System.out.println(r.habilitado);
	   }
	   
	   model.addAttribute("requests", lista);
	   model.addAttribute("availabilities", availabilityDao.getAvailabilitiesFromElderly(currentElderly.getDNI()));	   
	   return "elderly/elderlyRequest";
   }
   
   //MÉTODO PARA BORRAR UNA SOLICITUD DE UNA EMPERSA, DONDE SE PONE LA FECHA FIN A LA ACTUAL
   @RequestMapping(value="/elderlyDeleteRequest/{number}")
   public String processDeleteRequest(@PathVariable int number){
	   Request r = requestDao.getRequest(number);
	   java.sql.Date fechaActual = new java.sql.Date(new Date().getTime());
	   r.setEndDate(fechaActual);
	   requestDao.updateRequest(r);
	   
	   //Correo electrónico para avisar de la baja del servicio
	   Contract contract = contractDao.getContract(r.getContract_number());
	   Company company = companyDao.getCompany(contract.getCompany_cif());
	   String servicio ="";
	   if (r.getServiceType() == 0){
		   servicio = "servici de menjar";
	   }else if(r.getServiceType() == 1){
		   servicio = "servici sanitari";
	   }else if(r.getServiceType() == 2){
		   servicio = "servici de neteja";
	   }else{
		   servicio = "servici de companyia";
	   }
		    
	   System.out.println("\nS'ha manat un correu de notificació a "+company.getContactPersonEmail()
	   +"\nNotificació de cancelació del servei contractat amb el major "+currentElderly.getName()+"\n"
	   +"El contract:\n"
	   + "\tData inici: "+r.getCreationDate()+"\n"
	   + "\tData fi: "+r.getEndDate()+" hores\n"
	   + "\tTipus servei: "+servicio+" hores\n"
	   +"Ha tingut que ser cancelada. Sentim les molèsties.");	   
	   
	   return "redirect:../elderlyRequest";
   }
   
   //MÉTODO PARA BORRAR UNA DISPONIBILIDAD , ES DECIR, BORRAR AL MAYOR A UNA DE LAS DISPONIBILIDADES DE UN VOLUNTARIO
   @RequestMapping(value="/elderlyDeleteAvailability/{date}/{beginningHour}/{volunteer_usr}/{endingHour}")
   public String processDeleteAvailability(@PathVariable java.sql.Date date, @PathVariable LocalTime beginningHour, @PathVariable String volunteer_usr, @PathVariable LocalTime endingHour){   
	   Availability a = new Availability();
	   a.setElderly_dni(null);
	   a.setDate(date);
	   a.setVolunteer_usr(volunteer_usr);
	   a.setBeginningHour(beginningHour);
	   a.setEndingHour(endingHour);
	   availabilityDao.setElderly(a);
	   
	   //Correo electrónico al voluntario diciéndole que el mayor se ha dado de baja
	   
	   Volunteer volunteer = volunteerDao.getVolunteerByUsr(a.getVolunteer_usr());
	   System.out.println("\nS'ha manat un correu de notificació a "+volunteer.getEmail()
	   +"\nNotificació de cancelació del servei contractat amb el major "+currentElderly.getName()+"\n"
	   +"La seva cita del:\n"
	   + "\tDía: "+a.getDate()+"\n"
	   + "\tDesde les: "+a.getBeginningHour()+" hores\n"
	   + "\tFins les: "+a.getEndingHour()+" hores\n"
	   +"Ha tingut que ser cancelada. Sentim les molèsties.");
		
	   return "redirect:/elderly/elderlyRequest";	   
   }
   
   
   
   
   
   //MÉTODO PARA LISTAR LOS NUEVOS SERVICIOS
   @RequestMapping(value="/elderlyNewServices")
   public String listNewServices(Model model){
	   ArrayList<Request> request = new ArrayList<Request>(requestDao.getRequestsFromElderly(currentElderly.getDNI()));
	   Boolean imprimir = true;
	   for (Request r : request){
		   if (r.getState() == 0){
			   imprimir = false;
			   break;
		   }
	   }
	   model.addAttribute("contracts", contractDao.getFreeContracts());
	   model.addAttribute("availabilities", availabilityDao.getFreeAvailability());
	   model.addAttribute("imprimir", imprimir);
	   return "elderly/elderlyNewServices";
   }
   
   //MÉTODO PARA AÑADIR UNA SOLICITUD DE UNA EMPRESA
   @RequestMapping(value="/elderlyAddRequest/{number}")
   public String processAddRequest( Model model, @PathVariable int number){
	   contract_number = number;
	   model.addAttribute("request", new Request());
	   model.addAttribute("cuentaBancaria", currentElderly.getBankAccountNumber());
	   return "elderly/elderlyAddRequest";
   }
   
   //MÉTODO PARA AÑADIR UNA SOLICITUD DE UNA EMPRESA
   @RequestMapping(value="/elderlyAddRequest", method=RequestMethod.POST)
   public String processAddRequestSubmit(@ModelAttribute("request") Request request , BindingResult bindingResult){
	   if (bindingResult.hasErrors()) 
       		return "elderly/elderlyAddRequest";
	   
	   //Completa y/o modifica los campos con los atributos que se necesitan y no proporciona el usuario
	   Contract contract = contractDao.getContract(contract_number);
	   request.setServiceType(contract.getServiceType());
	   request.setContract_number(contract.getNumber());
	   request.setElderly_dni(currentElderly.getDNI());
	   request.setApprovedDate(null);
	   request.setRejectedDate(null);
	   request.setState(0);
	   request.setCreationDate(contract.getDateBeginning());
	   request.setEndDate(contract.getDateEnding());
	   
	   //Conseguimos el número máximo del id
	   ArrayList<Request> listSolicitudes = new ArrayList<Request>(requestDao.getRequests());
	   int i = 0;
	   for (Request r: listSolicitudes){
		  if (r.getNumber() > i)
		  	i = r.getNumber();
	   }
	   request.setNumber(i + 1);
	   requestDao.addRequest(request);
	   
	   //Correo electrónico para notificar a la persona de contacto que un mayor ha aceptado su servicio 
	   String servicio ="";
	   if (request.getServiceType() == 0){
		   servicio = "servici de menjar";
	   }else if(request.getServiceType() == 1){
		   servicio = "servici sanitari";
	   }else if(request.getServiceType() == 2){
		   servicio = "servici de neteja";
	   }else{
		   servicio = "servici de companyia";
	   }
	   Company company = companyDao.getCompany(contract.getCompany_cif());
	   System.out.println("\nS'ha manat un correu de notificació a "+company.getContactPersonEmail()
	   +"\nNotificació de alta del servei contractat amb el major "+currentElderly.getName()+"\n"
	   +"El contract:\n"
	   + "\tData inici: "+request.getCreationDate()+"\n"
	   + "\tData fi: "+request.getEndDate()+" hores\n"
	   + "\tTipus servei: "+servicio+" hores\n");	
	   
	   
	   return "redirect:elderlyNewServices";   
   }

   //MÉTODO PARA AÑADIR UNA DISPONIBILIDAD, ES DECIR, ASIGNAR EL DNI DEL MAYOR A UNA DISPONIBILIDAD DE UN VOLUNTARIO
   @RequestMapping(value="/elderlyNewAvailability/{date}/{volunteer_usr}/{beginningHour}/{endingHour}")
   public String processUpdateNewAvailability(@PathVariable java.sql.Date date, @PathVariable String volunteer_usr, @PathVariable LocalTime beginningHour, @PathVariable LocalTime endingHour){
	   Availability a = new Availability();
	   a.setElderly_dni(currentElderly.getDNI());
	   a.setDate(date);
	   a.setVolunteer_usr(volunteer_usr);
	   a.setBeginningHour(beginningHour);
	   a.setEndingHour(endingHour);
	   availabilityDao.setElderly(a);
	   
	   //Correo electrónico para el voluntario avisandole que tiene un nuevo mayor al que atender
	   
	    Volunteer volunteer = volunteerDao.getVolunteerByUsr(a.getVolunteer_usr());
		System.out.println("\nS'ha manat un correu de notificació a "+volunteer.getEmail()
		+"\nNotificació de alta del servei contractat amb el major "+currentElderly.getName()+"\n"
		+"La seva cita del:\n"
		+ "\tDía: "+a.getDate()+"\n"
		+ "\tDesde les: "+a.getBeginningHour()+" hores\n"
		+ "\tFins les: "+a.getEndingHour()+" hores\n");
		
	   return "redirect:/elderly/elderlyRequest";
   }

	   
	   
	   
	   
   //MÉTODO PARA MOSTRAR LOS DATOS DEL MAYOR
	@RequestMapping(value="/elderlyProfile", method = RequestMethod.GET)
    public String updateProfile(Model model) {
        model.addAttribute("elderly", currentElderly);
        return "elderly/elderlyProfile"; 
    }
	
	//MÉTODO PARA ACTUALOZAR LOS DATOS DEL MAYOR
	@RequestMapping(value="/elderlyProfile", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("elderly") Elderly elderly, BindingResult bindingResult, HttpSession session) {
		
		//Comprobamos que no haya errores
		ElderlyValidator elderlyValidator = new ElderlyValidator(elderlyDao, currentElderly); 
		elderlyValidator.validate(elderly, bindingResult);
		
		if (bindingResult.hasErrors()) 
        	return "elderly/elderlyProfile";
		
		elderly.setDNI(currentElderly.getDNI());
        elderlyDao.updateElderly(elderly);
        
        //Cambiamos los datos de la sesion
        UserDetails user = new UserDetails(elderly.getEmail(), null, "elderly", true);
        session.setAttribute("user", user); 
        return "redirect:elderlyRequest"; 
    }
	
	
	
	

   //MÉTODO PARA MOSTRAR LOS DATOS DEL VOLUNTARIO 
   @RequestMapping(value="/showVolunteer/{volunteer_usr}")
   public String showVolunteer(@PathVariable String volunteer_usr, Model model){
	   Volunteer v = volunteerDao.getVolunteerByUsr(volunteer_usr);
	   model.addAttribute("volunteer", v);
	   return "elderly/showVolunteer";
   }
	   
}