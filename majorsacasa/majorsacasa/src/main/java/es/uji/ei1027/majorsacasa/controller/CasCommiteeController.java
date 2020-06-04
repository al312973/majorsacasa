package es.uji.ei1027.majorsacasa.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import es.uji.ei1027.majorsacasa.model.Request;
import es.uji.ei1027.majorsacasa.dao.CompanyDAO;
import es.uji.ei1027.majorsacasa.dao.ContractDAO;
import es.uji.ei1027.majorsacasa.dao.ElderlyDAO;
import es.uji.ei1027.majorsacasa.dao.RequestDAO;
import es.uji.ei1027.majorsacasa.model.Elderly;
import es.uji.ei1027.majorsacasa.model.Contract;

@Controller
@RequestMapping("/casCommitee")
public class CasCommiteeController {
	private RequestDAO requestDao;
	private ElderlyDAO elderlyDao;
	private ContractDAO contractDao;
	private CompanyDAO companyDao;
	private SimpleDateFormat formatter;
	private Request request;
	private Contract contract;
	
	@Autowired 
	public void setRequestDao(RequestDAO requestDao){
		this.requestDao = requestDao;
	}
	
	@Autowired
	public void setElderlyDao(ElderlyDAO elderlyDao){
		this.elderlyDao = elderlyDao;
	}
	
	@Autowired 
	public void setContractDao(ContractDAO contractDao){
		this.contractDao = contractDao;
	}
	
	@Autowired
	public void setCompanyDao(CompanyDAO companyDao){
		this.companyDao = companyDao;
	}
	
	//Método para listar todas las solicitudes con estado pendiente de aceptación
	@RequestMapping(value="/requests", method = RequestMethod.GET)
    public String showRequests(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		if (formatter==null)
			this.formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		List<Request> requests = requestDao.getPendingRequests();
		
		model.addAttribute("requests", requests);
        return "casCommitee/requests"; 
    }
	
	//Método para rechazar una solicitud
	@RequestMapping(value="/requests/reject/{number}", method=RequestMethod.GET)
	public String rejectRequest(@PathVariable Integer number, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		request = requestDao.getRequest(number);
		requestDao.rejectRequest(number);
		
		//Manda un correo notificando que la solicitud ha sido rechazada
		Elderly elderly = elderlyDao.getElderlyByDNI(request.getElderly_dni());
		String correo ="\nS'ha manat un correu de notificació a "+elderly.getName()+" "+elderly.getSurname()+"\n"
		+"La sol·licitud:\n"
		+ "\tRealitzada el: "+formatter.format(request.getCreationDate())+"\n";
		if (request.getServiceType()==0) {
			correo+="\tDel tipus: servei de menjar\n";
		} else if (request.getServiceType()==1) {
			correo+="\tDel tipus: servei sanitari\n";
		} else {
			correo+="\tDel tipus: servei de neteja\n";
		}
		
		if (request.getEndDate()!=null) {
			correo+="\tSol·licitada fins al: "+formatter.format(request.getEndDate())+"\n";
		}
		correo+="\tAmb comentaris: "+request.getComments()+"\n"
		+"Ha sigut rebutjada per no complir els criteris necessaris. Sentim les molèsties";
		
		System.out.println(correo);
		
		return "redirect:/casCommitee/requests";
	}
		
	//Método para redireccionar a la lista de contratos del tipo de servicio solicitado
	@RequestMapping(value="/requests/accept/{number}", method=RequestMethod.GET)
	public String modifyRequest(Model model, @PathVariable Integer number, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		request = requestDao.getRequest(number);
		List<Contract> contracts;
		
		//Si es una solicitud de servicios de comida, muestra el listado de contratos de comida que se pueden asignar
		if (request.getServiceType()==0) {
			contracts = contractDao.getFoodContracts();
		} else if (request.getServiceType()==1) {
			//Si es una solicitud de servicios sanitarios, muestra el listado de contratos sanitarios que se pueden asignar
			contracts = contractDao.getHealthContracts();
		} else {
			//Si es una solicitud de servicios de limpieza, muestra el listado de contratos de limpieza que se pueden asignar
			contracts = contractDao.getCleaningContracts();
		}
		
		//Mostramos el numero de servicios disponibles de cada contrato (total-veces asignado)
		for (Contract contract : contracts) {
			int total = contract.getQuantityServices();
			int assigned = requestDao.getAssignedRequestsToContract(contract.getNumber());
			contract.setQuantityServices(total-assigned);
		}
		
		model.addAttribute("serviceType", request.getServiceType());
		model.addAttribute("requestNumber", number);
		model.addAttribute("contracts", contracts);
		return "casCommitee/contracts";
	}
	
		
	//Muestra los datos de la persona mayor que ha solicitado un servicio
	@RequestMapping(value="/showElderly/{elderly_dni}", method=RequestMethod.GET)
	public String showElderly(Model model, @PathVariable String elderly_dni, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		Elderly elderly = elderlyDao.getElderlyByDNI(elderly_dni);
		model.addAttribute("elderly", elderly);
		return "casCommitee/elderly";
	}
	
	//Muestra los datos de una empresa en particular
	@RequestMapping(value="/showCompany/{cif}", method=RequestMethod.GET)
	public String showCompany(Model model, @PathVariable String cif, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("requestNumber", request.getNumber());
		model.addAttribute("company", companyDao.getCompanyByCIF(cif));
		return "casCommitee/company";
	}
	
	//Método para asignar una solicitud a un contrato
	@RequestMapping(value="/requests/accept/{requestNumber}/{contractNumber}", method=RequestMethod.GET)
	public String assignRequest(@PathVariable Integer requestNumber, @PathVariable Integer contractNumber, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		request = requestDao.getRequest(requestNumber);
		contract = contractDao.getContract(contractNumber);
		
		requestDao.acceptRequest(requestNumber, contractNumber, "casCommitee");
		
		//Manda un correo notificando que la solicitud ha sido aceptada y asignada a un contrato
		Elderly elderly = elderlyDao.getElderlyByDNI(request.getElderly_dni());
		String correo ="\nS'ha manat un correu de notificació a "+elderly.getName()+" "+elderly.getSurname()+"\n"
		+"La sol·licitud:\n"
		+ "\tRealitzada el: "+formatter.format(request.getCreationDate())+"\n";
		if (request.getServiceType()==0) {
			correo+="\tDel tipus: servei de menjar\n";
		} else if (request.getServiceType()==1) {
			correo+="\tDel tipus: servei sanitari\n";
		} else {
			correo+="\tDel tipus: servei de neteja\n";
		}
		
		if (request.getEndDate()!=null) {
			correo+="\tSol·licitada fins al: "+formatter.format(request.getEndDate())+"\n";
		}
		correo+="\tAmb comentaris: "+request.getComments()+"\n"
		+"Ha sigut acceptada. S'han assignat unes condicions i en breu s'assignarà un horari d'atenció.";
		
		System.out.println(correo);
		
		//Después faltaría que la empresa establezca el horario de atención (parte de Kim no implementada)
		
		//Redirige a la página de confirmación
		return "redirect:/casCommitee/requests/accept/confirmation";
	}
	
	//Muestra la confirmación de asignación de un contrato a una solicitud
	@RequestMapping(value="/requests/accept/confirmation", method=RequestMethod.GET)
	public String assignRequest(Model model, HttpSession session) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("request", request);
		model.addAttribute("contract", contract);
		
		this.request = null;
		this.contract = null;
		
		return "/casCommitee/confirmation";
	}
}
