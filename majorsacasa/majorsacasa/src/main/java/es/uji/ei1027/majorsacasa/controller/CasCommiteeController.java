package es.uji.ei1027.majorsacasa.controller;

import java.util.ArrayList;
import java.util.List;

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
	
	//MÉTODO PARA LISTAR TODAS LAS SOLICITUDES CON ESTADO PENDIENTE DE ACEPTACION
	@RequestMapping(value="/gestionarSolicitudes", method = RequestMethod.GET)
    public String showCasCommiteePage(Model model) {
		List<Request> listaSolicitudes = new ArrayList<Request>();
		List<Request> listaSolicitudesPendientes = new ArrayList<Request>();
		listaSolicitudes = requestDao.getRequests();
		
		for (Request r : listaSolicitudes){
			if (r.getState() == 0){
				listaSolicitudesPendientes.add(r);
			}
		}
		
		model.addAttribute("requests", listaSolicitudesPendientes);
        return "casCommitee/gestionarSolicitudes"; 
    }
	
	//MÉTODO PARA DIRECCIONAR HACIA UNA LISTA CON LOS CONTRATOS QUE HAYA VIGENTES DEPENDIENDO DEL TIPO DE SERVICIO QUE TENGA LA SOLICITUD
	@RequestMapping(value="/gestionarSolicitudes/{number}")
	public String modifyRequest(@PathVariable Integer number){
		request = requestDao.getRequest(number);
		if (request.getServiceType() == 0)
			return "redirect:../cargarContratosComida";
		else if (request.getServiceType() == 1)
			return "redirect:../cargarContratosSanitarios";
		else
			return "redirect:../cargarContratosLimpieza";
	}
	
	@RequestMapping(value="/cargarContratosComida")
	public String contratosComida(Model model){
		List<Contract> lista = contractDao.getContracts();
		List<Contract> contratosComida = new ArrayList<Contract>();
		
		for(Contract c:lista){
			if (c.getServiceType() == 0)
				contratosComida.add(c);
		}

		model.addAttribute("contracts", contratosComida);
		return "casCommitee/contracts";
	}
	
	@RequestMapping(value="/cargarContratosSanitarios")
	public String contratosSanitarios(Model model){
		List<Contract> lista = contractDao.getContracts();
		List<Contract> contratosSanitarios = new ArrayList<Contract>();
		
		for(Contract c:lista){
			if (c.getServiceType() == 1)
				contratosSanitarios.add(c);
		}

		model.addAttribute("contracts", contratosSanitarios);
		return "casCommitee/contracts";
	}	
	
	@RequestMapping(value="/cargarContratosLimpieza")
	public String contratosLimpieza(Model model){
		List<Contract> lista = contractDao.getContracts();
		List<Contract> contratosLimpieza = new ArrayList<Contract>();
		
		for(Contract c:lista){
			if (c.getServiceType() == 2)
				contratosLimpieza.add(c);
		}

		model.addAttribute("contracts", contratosLimpieza);
		return "casCommitee/contracts";
	}
	
	//MÉTODO PARA MOSTRAR LOS DATOS DE MAYOR
	@RequestMapping(value="/showElderly/{elderly_dni}")
	public String showElderly(Model model, @PathVariable String elderly_dni){
		Elderly elderly = elderlyDao.getElderlyByDNI(elderly_dni);
		model.addAttribute("elderly", elderly);
		return "casCommitee/elderly";
	}
	
	//MÉTODO PARA MOSTRAR LOS DATOS DE LA EMPRESA
	@RequestMapping(value="/showCompany/{number}/{cif}")
	public String showCompany(Model model, @PathVariable Integer number, @PathVariable String cif){
		contract = contractDao.getContract(number);
		model.addAttribute("company", companyDao.getCompany(cif));
		return "casCommitee/company";
	}
	
	@RequestMapping(value="/atrasCompany")
	public String atrasCompany(){
		String direccion = "redirect:gestionarSolicitudes/" + contract.getServiceType();
		System.out.println(direccion);
		return direccion;
	}
	

}
