package es.uji.ei1027.majorsacasa.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
import es.uji.ei1027.majorsacasa.model.Availability;
import es.uji.ei1027.majorsacasa.dao.ContractDAO;
import es.uji.ei1027.majorsacasa.model.Contract;
import es.uji.ei1027.majorsacasa.dao.ElderlyDAO;
import es.uji.ei1027.majorsacasa.dao.RequestDAO;
import es.uji.ei1027.majorsacasa.model.Request;
import es.uji.ei1027.majorsacasa.model.Elderly;
import es.uji.ei1027.majorsacasa.model.UserDetails;
import es.uji.ei1027.majorsacasa.validator.ElderlyValidator;


@Controller
@RequestMapping("/elderly")
public class ElderlyController {
	private ElderlyDAO elderlyDao;
	private Elderly currentElderly;
	private RequestDAO requestDao;
	private ContractDAO contractDao;
	private AvailabilityDAO availabilityDao;
	private int contract_number;
	
	@Autowired
	public void setAvailabilityDao(AvailabilityDAO availabilityDao){
		this.availabilityDao = availabilityDao;
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
  
   
   
   
   
   
   
   
   
   //Zona para listar o borrar un servicio de una empresa
   @RequestMapping(value="/elderlyRequest")
   public String listRequest(Model model, HttpSession session){
	   UserDetails user = (UserDetails) session.getAttribute("user");
	   this.currentElderly = elderlyDao.getElderlyByEmail(user.getEmail());
	   model.addAttribute("requests", requestDao.getRequestsFromElderly(currentElderly.getDNI()));
	   model.addAttribute("availabilities", availabilityDao.getAvailabilitiesFromElderly(currentElderly.getDNI()));
	   model.addAttribute("fechaActual", new java.sql.Date(new Date().getTime()));
	   return "elderly/elderlyRequest";
   }
   
   @RequestMapping(value="/elderlyDeleteRequest/{number}")
   public String processDeleteRequest(@PathVariable int number){
	   Request r = requestDao.getRequest(number);
	   java.sql.Date fechaActual = new java.sql.Date(new Date().getTime());
	   r.setEndDate(fechaActual);
	   requestDao.updateRequest(r);
	   return "redirect:../elderlyRequest";
   }
   
   
   
   
   
   
   
   //Zona para actualizar una disponibilidad con un voluntario, porque el mayor quiere darse de baja ante ese volunatrio
   @RequestMapping(value="/elderlyUpdateAvailability/{date}/{begginingHour}/{endingHour}")
   public String processUpdateAvailability(@PathVariable Date date, @PathVariable LocalTime begginingHour, @PathVariable LocalTime endingHour){   
	   Availability a = new Availability();
	   a.setDate(date);
	   a.setBegginingHour(begginingHour);
	   a.setEndingHour(endingHour);
	   a.setElderly_dni(currentElderly.getDNI());
	   
	   
	   availabilityDao.updateAvailability(a);
	   return "redirect:../elderlyRequest";
   }
   
   
   
   
   
   
   
   
   
   
   //Zona para listar los nuevos servicios a los que se quiera apuntar el mayor
   @RequestMapping(value="/elderlyNewServices")
   public String listNewServices(Model model){
	   model.addAttribute("contracts", contractDao.getFreeContracts());							//contratos que aun no están asignados, libres
	   model.addAttribute("availabilities", availabilityDao.getFreeAvailability());				//voluntarios que no tienen asignado ningún mayor en las fechas indicadas
	   return "elderly/elderlyNewServices";
   }
   
   @RequestMapping(value="/elderlyAddRequest/{number}")
   public String processAddRequest( Model model, @PathVariable int number){
	   contract_number = number;
	   model.addAttribute("request", new Request());
	   return "elderly/elderlyAddRequest";
   }
   
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
	   
	   //Conseguimos el número máximo del id
	   ArrayList<Request> listSolicitudes = new ArrayList<Request>(requestDao.getRequests());
	   int i = 0;
	   for (Request r: listSolicitudes){
		  if (r.getNumber() > i)
		  	i = r.getNumber();
	   }
	   request.setNumber(i + 1);
	   
	   //Ponemos la fecha actual como fecha de creación
	   java.sql.Date fechaActual = new java.sql.Date(new Date().getTime());
	   request.setCreationDate(fechaActual);
	   
	   request.setEndDate(contract.getDateEnding());
	   requestDao.addRequest(request);
	   return "redirect:elderlyNewServices";   
   }
   
   @RequestMapping(value="/elderlyUpdateAvailability")
   public String processUpdateNewAvailability(){
	   return "";
   }
   
   
   
   
   
   
   
   
   
   
   
   
   
   //Zona para editar el perfil del mayor
	@RequestMapping(value="/elderlyProfile", method = RequestMethod.GET)
    public String updateProfile(Model model) {
        model.addAttribute("elderly", currentElderly);
        return "elderly/elderlyProfile"; 
    }
	
	@RequestMapping(value="/elderlyProfile", method = RequestMethod.POST)
    public String processUpdateProfileSubmit(@ModelAttribute("elderly") Elderly elderly, BindingResult bindingResult, HttpSession session) {
		
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
}