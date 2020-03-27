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

import es.uji.ei1027.majorsacasa.dao.PayDAO;
import es.uji.ei1027.majorsacasa.model.Pay;


@Controller
@RequestMapping("/pay")
public class PayController {
	private PayDAO payDao;
	
	@Autowired
	public void setPayDao(PayDAO payDao) {
		this.payDao=payDao;
	}
	
	@RequestMapping(value="/list")
	public String listpays(Model model) {
		model.addAttribute("elderlies", payDao.getpays());
		return "pay/list";
	}
	
	@RequestMapping(value="/add") 
    public String addPay(Model model) {
        model.addAttribute("pay", new Pay());
        return "pay/add";
    }

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("pay") Pay pay, BindingResult bindingResult) {
		//Completa y/o modifica los campos con los atributos que se necesitan y no proporciona el usuario
			
		if (bindingResult.hasErrors())
			return "pay/add";
		payDao.addPay(pay);
        return "redirect:list";
    }

	//Variable interna en la que guardamos la fecha de creacion de un elderly para que no se
	// modifique cuando actualizamos sus datos
		
//	@RequestMapping(value="/update/{request_number, invoice_number}", method = RequestMethod.GET)
//    public String editEldery(Model model, @PathVariable Pay Pay) {
//		Pay pay = payDao.getPay(Pay);
//        model.addAttribute("pay", pay);
//        return "pay/update"; 
//    }

	
//	@RequestMapping(value="/update", method = RequestMethod.POST)
//    public String processUpdateSubmit(@ModelAttribute("pay") Pay Pay, BindingResult bindingResult) {
//		//Completa y/o modifica los campos con los atributos que se necesitan y no proporciona el usuario
//	
//        if (bindingResult.hasErrors()) 
//        	return "pay/update";
//        payDao.updatePay(Pay);
//        return "redirect:list"; 
//    }

   @RequestMapping(value="/delete/{request_number}")
    public String processDelete(@PathVariable Pay pay) {
	   	   payDao.deletePay(pay);
           return "redirect:../list"; 
    }
}