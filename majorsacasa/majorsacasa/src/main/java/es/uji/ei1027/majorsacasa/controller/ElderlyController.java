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
import org.springframework.web.bind.annotation.RequestParam;

import es.uji.ei1027.majorsacasa.dao.AvailabilityDAO;
import es.uji.ei1027.majorsacasa.dao.ContractDAO;
import es.uji.ei1027.majorsacasa.dao.ElderlyDAO;
import es.uji.ei1027.majorsacasa.dao.RequestDAO;
import es.uji.ei1027.majorsacasa.dao.SocialWorkerDAO;
import es.uji.ei1027.majorsacasa.model.Elderly;
import es.uji.ei1027.majorsacasa.model.UserDetails;
import es.uji.ei1027.majorsacasa.dao.VolunteerDAO;
import es.uji.ei1027.majorsacasa.model.Volunteer;
import es.uji.ei1027.majorsacasa.dao.CompanyDAO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

import es.uji.ei1027.majorsacasa.model.Availability;
import es.uji.ei1027.majorsacasa.model.Company;
import es.uji.ei1027.majorsacasa.model.Contract;
import es.uji.ei1027.majorsacasa.model.Request;
import es.uji.ei1027.majorsacasa.validator.ElderlyValidator;
import es.uji.ei1027.majorsacasa.validator.RequestValidator;

@Controller
@RequestMapping("/elderly")
public class ElderlyController {
	private Elderly currentElderly;
	private ElderlyDAO elderlyDao;
	private RequestDAO requestDao;
	private ContractDAO contractDao;
	private AvailabilityDAO availabilityDao;
	private VolunteerDAO volunteerDao;
	private CompanyDAO companyDao;
	private SocialWorkerDAO socialWorkerDao;
	private SimpleDateFormat formatter;
	
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
	
	@Autowired
	public void setSocialWorkerDao(SocialWorkerDAO socialWorkerDao){
		this.socialWorkerDao = socialWorkerDao;
	}
		
		
	//Muestra la página de selección de solicitudes por tipo
	@RequestMapping(value="/requests", method = RequestMethod.GET)
    public String showRequestsPage(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		UserDetails user = (UserDetails) session.getAttribute("user");
		if (currentElderly==null)
			this.currentElderly = elderlyDao.getElderlyByEmail(user.getEmail());
		if (formatter==null)
			this.formatter = new SimpleDateFormat("dd/MM/yyyy");
        return "elderly/requests"; 
    }

	//Muestra las solicitudes actuales de servicios de comida de la persona mayor
	@RequestMapping(value="/requests/foodrequests", method = RequestMethod.GET)
    public String showFoodRequests(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("requests", requestDao.getFoodRequestsFromElderly(currentElderly.getDNI()));
		model.addAttribute("requestType", 0);
		requestPage = 0;
		
        return "elderly/foodhealthcleaningrequests"; 
    }
		
	//Muestra las solicitudes actuales de servicios de limpieza de la persona mayor
	@RequestMapping(value="/requests/volunteerrequests", method = RequestMethod.GET)
    public String showVolunteerRequests(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("availabilities", availabilityDao.getAvailabilitiesFromElderly(currentElderly.getDNI()));
		availabilitiesPage = 0;
		
        return "elderly/volunteerrequests"; 
    }
	
	//Guardamos la página desde la que accedemos a la información de un voluntario para establecer el botón hacia atrás
	private int availabilitiesPage;
		
	//Muestra información sobre el voluntario asignado a un servicio de compañia contratado
	@RequestMapping(value="/volunteer/{volunteer_usr}", method = RequestMethod.GET)
	public String showVolunteer(Model model, @PathVariable String volunteer_usr, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("volunteer", volunteerDao.getVolunteerByUsr(volunteer_usr));
		model.addAttribute("availabilitiesPage", availabilitiesPage);
		
		return "elderly/volunteer";
	}
	
	//Muestra la página de confirmación antes de finalizar un servicio de compañia
	@RequestMapping(value="/requests/volunteerrequests/delete/confirm/{deletionType}/{date}/{beginningHour}/{volunteer_usr}", method = RequestMethod.GET)
	public String confirmDeleteCompanyRequest(Model model, @PathVariable int deletionType, @PathVariable String date, 
			@PathVariable String beginningHour, @PathVariable String volunteer_usr, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		try {
			Date availabilityDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
			LocalTime availabilitiBeginningHour = LocalTime.parse(beginningHour);
			Availability availability = availabilityDao.getAvailability(availabilityDate, availabilitiBeginningHour, volunteer_usr);
			
			Volunteer volunteer = volunteerDao.getVolunteerByUsr(availability.getVolunteer_usr());
			
			model.addAttribute("deletionType", deletionType);
			model.addAttribute("availability", availability);
			model.addAttribute("volunteer", volunteer.getName());
			
		}catch (Exception ignore) {
		}
	   return "elderly/deletevolunteerrequest"; 
	}
		
	//Da de baja un servicio de compañia reservado con un voluntario
	@RequestMapping(value="/requests/volunteerrequests/delete/{date}/{beginningHour}/{volunteer_usr}", method = RequestMethod.GET)
	public String processDeleteCompanyRequest(@PathVariable String date, @PathVariable String beginningHour, 
			@PathVariable String volunteer_usr, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		try {
			Date availabilityDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
			LocalTime availabilitiBeginningHour = LocalTime.parse(beginningHour);
			Availability availability = availabilityDao.getAvailability(availabilityDate, availabilitiBeginningHour, volunteer_usr);
			
			Calendar today = Calendar.getInstance();
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);
			
			//Si la cita es posterior o igual a la fecha actual, se envía un correo al voluntario notificando que 
			// el beneficiario ha dado de baja la cita
			if (availability.getDate().compareTo(today.getTime())>=0) {
				Volunteer volunteer = volunteerDao.getVolunteerByUsr(volunteer_usr);
				String correo ="\nS'ha manat un correu de notificació a "+volunteer.getEmail()
				+"\nNotificació de cancelació del servei contractat amb el beneficiari "+currentElderly.getName()+" "+
						currentElderly.getSurname()+"\n"
				+"La seva cita del:\n"
				+ "\tDía: "+formatter.format(availability.getDate())+"\n"
				+ "\tDesde les: "+availability.getBeginningHour()+" hores\n"
				+ "\tFins les: "+availability.getEndingHour()+" hores\n"
				+"Ha sigut cancelada pel beneficiari. Sentim les molèsties.";
				
				System.out.println(correo);
				
				//Y se establece la fecha de cancelación como la fecha actual
				availability.setUnsuscribeDate(new Date());
			}
			
			availabilityDao.finishAvailability(availability);
		}catch (Exception ignore) {
		}
		return "redirect:/elderly/requests/volunteerrequests";
	}
		
	//Muestra las solicitudes actuales de servicios sanitarios de la persona mayor
	@RequestMapping(value="/requests/healthrequests", method = RequestMethod.GET)
    public String showHealthRequests(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("requests", requestDao.getHealthRequestsFromElderly(currentElderly.getDNI()));
		model.addAttribute("requestType", 1);
		requestPage = 1;
		
        return "elderly/foodhealthcleaningrequests"; 
    }
	
	//Muestra las solicitudes actuales de servicios de limpieza de la persona mayor
	@RequestMapping(value="/requests/cleaningrequests", method = RequestMethod.GET)
    public String showCleaningRequests(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("requests", requestDao.getCleaningRequestsFromElderly(currentElderly.getDNI()));
		model.addAttribute("requestType", 2);
		requestPage = 2;
		
        return "elderly/foodhealthcleaningrequests"; 
    }
	
	//Guardamos la página desde la que accedemos a la información de un contrato para establecer el botón hacia atrás
	private int requestPage;
	
	//Muestra información detallada de una solicitud (incluyendo datos del contrato)
	@RequestMapping(value="/requests/terms/{number}", method = RequestMethod.GET)
    public String showContract(Model model, @PathVariable int number, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		Request request = requestDao.getRequest(number);
		model.addAttribute("request", request);
		
		if (request.getUserCAS()!=null)
			model.addAttribute("userCAS", socialWorkerDao.getSocialWorkerByUserCAS(request.getUserCAS()));
		
		if (request.getContract_number()!=0) {
			Contract contract = contractDao.getContract(request.getContract_number());	
			model.addAttribute("contract", contract);
			model.addAttribute("company", companyDao.getCompanyByCIF(contract.getCompany_cif()));
		}
		
		model.addAttribute("requestPage", requestPage);
		requestNumber = number;
		
        return "elderly/terms"; 
    }
	
	//Guardamos las condiciones de la última solicitud visitada para establecer el botón hacia atrás
	private int requestNumber;
		
	//Muestra la información de una empresa prestadora de un servicio asociado a una solicitud
	@RequestMapping(value="/requests/terms/company/{company_cif}", method = RequestMethod.GET)
    public String showCompany(Model model, @PathVariable String company_cif, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("company", companyDao.getCompanyByCIF(company_cif));
		model.addAttribute("requestNumber", requestNumber);
		
        return "elderly/company"; 
    }
	
	//Muestra la información del trabajador social que supervisa una solicitud
	@RequestMapping(value="/requests/terms/socialWorker/{userCAS}", method = RequestMethod.GET)
    public String showSocialWorker(Model model, @PathVariable String userCAS, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("socialWorker", socialWorkerDao.getSocialWorkerByUserCAS(userCAS));
		model.addAttribute("requestNumber", requestNumber);
		
        return "elderly/socialWorker"; 
    }
	
	//Muestra la página de selección de solicitudes por tipo
	@RequestMapping(value="/newrequest", method = RequestMethod.GET)
    public String showNewRequestsPage(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
        return "elderly/newrequest"; 
    }
	
	//Muestra las todas las disponibilidades de voluntarios
	@RequestMapping(value="/newrequest/newvolunteerrequest", method = RequestMethod.GET)
    public String showVolunteerAvailabilities(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("availabilities", availabilityDao.getFreeAvailabilities());
		availabilitiesPage = 1;
		
        return "elderly/newvolunteerrequest"; 
    }
	
	//Muestra la información de una empresa asociada a un contrato
	@RequestMapping(value="/newrequest/newvolunteerrequest/add/{date}/{beginningHour}/{volunteer_usr}", method = RequestMethod.GET)
    public String processAddVolunteerService(Model model, @PathVariable String date, @PathVariable String beginningHour,
    		@PathVariable String volunteer_usr, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		try {
			Date availabilityDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
			LocalTime availabilityBeginningHour = LocalTime.parse(beginningHour);
			
			Availability availability = availabilityDao.getAvailability(availabilityDate, availabilityBeginningHour, volunteer_usr);
			availability.setElderly_dni(currentElderly.getDNI());
			availabilityDao.setElderly(availability);
			
			//Manda un correo electrónico de confirmación al voluntario para notificar que la persona mayor ha solicitado su servicio
			  Volunteer volunteer = volunteerDao.getVolunteerByUsr(volunteer_usr);
			  System.out.println("\nS'ha manat un correu de notificació a "+volunteer.getEmail()
			  +"\nNotificació d'assignació d'un servei. La seva disponibilitat del:\n"
			  + "\tDía: "+formatter.format(availability.getDate())+"\n"
			  + "\tDesde les: "+availability.getBeginningHour()+" hores\n"
			  + "\tFins les: "+availability.getEndingHour()+" hores\n"
			  + "Ha sigut assignada al beneficiari: "+currentElderly.getName()+" "+currentElderly.getSurname()+".\n"
			  + "Pots trobar mes informació als detalls del servei.");
		} catch (ParseException ignore) {
		}
		return "redirect:/elderly/requests/volunteerrequests";
	}
	
	//Muestra la información de una empresa asociada a un contrato
	@RequestMapping(value="/newrequest/newfoodhealthcleaningrequest/{type}", method = RequestMethod.GET)
    public String newRequest(Model model, @PathVariable int type, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		Request request = new Request();
		request.setServiceType(type);
		model.addAttribute("request", request);
		typeRequest = type;
        return "elderly/newfoodhealthcleaningrequest"; 
    }
		
	//Guardamos el tipo de solicitud que se va a realizar
	private int typeRequest;
		
	//Actualiza la información personal de la persona mayor
	@RequestMapping(value="/newrequest/newfoodhealthcleaningrequest", method = RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("request") Request request, 
			@RequestParam(value = "monday", required = false) boolean monday,
			@RequestParam(value = "tuesday", required = false) boolean tuesday,
			@RequestParam(value = "wednesday", required = false) boolean wednesday,
			@RequestParam(value = "thursday", required = false) boolean thursday,
			@RequestParam(value = "friday", required = false) boolean friday,
			@RequestParam(value = "saturday", required = false) boolean saturday,
			@RequestParam(value = "sunday", required = false) boolean sunday, BindingResult bindingResult, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		//Adaptamos los campos leídos y completamos con la información que la vista no proporciona
		//Guardamos una copia de los comentarios por si hay errores
		String initialComments = request.getComments();
		String comments = request.getComments()+" Dies d'atenció:";
		if (monday)
			comments += " Dilluns";
		if (tuesday)
			comments += " Dimarts";
		if (wednesday)
			comments += " Dimecres";
		if (thursday)
			comments += " Dijous";
		if (friday)
			comments += " Divendres";
		if (saturday)
			comments += " Dissabte";
		if (sunday)
			comments += " Diumenge";
		
		request.setComments(comments);
		request.setNumber(requestDao.getNumberRequests()+1);
		request.setElderly_dni(currentElderly.getDNI());
		request.setServiceType(typeRequest);
		request.setState(0);
		request.setBeginningHour(null);
		request.setEndingHour(null);
		request.setContract_number(null);
		request.setUserCAS(null);
		request.setCreationDate(new Date());
		request.setFinished(false);
		
		//Comprobamos que no haya errores
		ArrayList<Boolean> dias = new ArrayList<>();
		dias.add(monday);
		dias.add(tuesday);
		dias.add(wednesday);
		dias.add(thursday);
		dias.add(friday);
		dias.add(saturday);
		dias.add(sunday);
		
		RequestValidator requestValidator = new RequestValidator(dias); 
		requestValidator.validate(request, bindingResult);
		
		if (bindingResult.hasErrors()) {
			request.setComments(initialComments);
			return "elderly/newfoodhealthcleaningrequest";
		}
        
		//Si no hay errores, se registra la solicitud en el sistema
		requestDao.addRequest(request);
		
	    if (request.getServiceType()==0)
	    	return "redirect:/elderly/requests/foodrequests";
	    if (request.getServiceType()==1)
	    	return "redirect:/elderly/requests/healthrequests";
	    else return "redirect:/elderly/requests/cleaningrequests";
	}
   
	//Muestra la página de confirmación antes de finalizar una solicitud de una empresa
	@RequestMapping(value="/requests/delete/confirm/{deletionType}/{number}", method = RequestMethod.GET)
	public String confirmDeleteRequest(Model model, @PathVariable int deletionType, @PathVariable int number, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
	   Request request = requestDao.getRequest(number);
	   model.addAttribute("deletionType", deletionType);
	   model.addAttribute("request", request);
	   
	   return "elderly/deletefoodhealthcleaningrequest"; 
	}
	
	//Establece una solicitud de una empresa como finalizada
	@RequestMapping(value="/requests/delete/{number}", method = RequestMethod.GET)
	public String processDeleteRequest(@PathVariable int number, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		Request request = requestDao.getRequest(number);
	   
	   Date previousEndDate=null; 
	   if (request.getEndDate()!=null)
		   previousEndDate = request.getEndDate();
	   
	   if (request.getState()!=2 && request.getEndDate()!=null && request.getEndDate().after(new Date()))
		   request.setEndDate(new Date());
	   requestDao.finishRequest(request);
	   
	   //Si hay un contrato asociado, y la fecha fin de la solicitud aún no se ha alcanzado, manda un correo a la empresa para notificar la baja del servicio
	   if (request.getContract_number()!=0 && previousEndDate!=null && previousEndDate.after(new Date())) {
		   Contract contract = contractDao.getContract(request.getContract_number());
		   Company company = companyDao.getCompanyByCIF(contract.getCompany_cif());
		   
		   String servicio ="";
		   if (request.getServiceType() == 0){
			   servicio = "servei de menjar";
		   }else if(request.getServiceType() == 1){
			   servicio = "servei sanitari";
		   }else{
			   servicio = "servei de neteja";   
		   }
		   
		   String correo = "\nS'ha manat un correu de notificació a "+company.getContactPersonEmail()
		   +"\nNotificació de baixa del servei contractat pel beneficiari "+currentElderly.getName()+" "+currentElderly.getSurname()+"\n"
		   +"La sol·licitud del "+servicio+" associada al contracte: "+contract.getNumber()+"\n"
		   + "\t amb data d'aprovació: "+formatter.format(request.getApprovedDate())+"\n";
		   if (previousEndDate !=null)
			   correo +="\t data final: "+formatter.format(previousEndDate)+"\n";
		   correo+="\t i descripció: "+contract.getDescription()+"\n"
		   +"Ha sigut donat de baixa pel beneficiari. S'ha suprimit el servei del contracte.";
		   
		   System.out.println(correo);
	   }	   
	   
	   if (request.getServiceType()==0)
		   return "redirect:/elderly/requests/foodrequests";
	   if (request.getServiceType()==1)
		   return "redirect:/elderly/requests/healthrequests";
	   
	   return "redirect:/elderly/requests/cleaningrequests";
   	}

	//Muestra la información personal de la persona mayor
	@RequestMapping(value="/profile", method = RequestMethod.GET)
    public String updateProfile(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
        model.addAttribute("elderly", currentElderly);
        return "elderly/profile"; 
    }

	//Actualiza la información personal de la persona mayor
	@RequestMapping(value="/profile", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("elderly") Elderly elderly, BindingResult bindingResult, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		//Adaptamos los campos leídos y completamos con la información que la vista no proporciona
		if (elderly.getAlergies().equals(""))
			elderly.setAlergies(null);
		
		if (elderly.getDiseases().equals(""))
			elderly.setDiseases(null);
		
		//Comprobamos que no haya errores
		ElderlyValidator elderlyValidator = new ElderlyValidator(elderlyDao, currentElderly); 
		elderlyValidator.validate(elderly, bindingResult);
		
		if (bindingResult.hasErrors()) 
        	return "elderly/profile";
		
        elderlyDao.updateElderly(elderly);
        currentElderly = elderly;
        
        //Cambiamos los datos de la sesion
        UserDetails user = new UserDetails(elderly.getEmail(), null, "elderly", true);
        session.setAttribute("user", user); 
        return "redirect:/elderly/requests"; 
    }   
}
