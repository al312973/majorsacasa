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

import es.uji.ei1027.majorsacasa.dao.InvoiceDAO;
import es.uji.ei1027.majorsacasa.model.Invoice;


@Controller
@RequestMapping("/invoice")
public class InvoiceController {
	private InvoiceDAO invoiceDao;
	
	@Autowired
	public void setElderlyDao(InvoiceDAO invoiceDao) {
		this.invoiceDao=invoiceDao;
	}
	
	@RequestMapping(value="/list")
	public String listInvoices(Model model) {
		model.addAttribute("elderlies", invoiceDao.getInvoices());
		return "invoice/list";
	}
	
	@RequestMapping(value="/add") 
    public String addInvoice(Model model) {
        model.addAttribute("invoice", new Invoice());
        return "invoice/add";
    }

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("invoice") Invoice invoice, BindingResult bindingResult) {
		//Completa y/o modifica los campos con los atributos que se necesitan y no proporciona el usuario
		
		if (bindingResult.hasErrors())
			return "invoice/add";
		invoiceDao.addInvoice(invoice);
        return "redirect:list";
    }

	//Variable interna en la que guardamos la fecha de creacion de un elderly para que no se
	// modifique cuando actualizamos sus datos

		
	@RequestMapping(value="/update/{number}", method = RequestMethod.GET)
    public String editInvoice(Model model, @PathVariable int number) {
		Invoice invoice = invoiceDao.getInvoice(number);

		model.addAttribute("invoice", invoice);
        return "invoice/update"; 
    }

	
	@RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("invoice") Invoice invoice, BindingResult bindingResult) {
		//Completa y/o modifica los campos con los atributos que se necesitan y no proporciona el usuario
		
        if (bindingResult.hasErrors()) 
        	return "invoice/update";
        invoiceDao.updateInvoice(invoice);
        return "redirect:list"; 
    }

   @RequestMapping(value="/delete/{number}")
    public String processDelete(@PathVariable int number) {
	   invoiceDao.deleteInvoice(number);
           return "redirect:../list"; 
    }
}