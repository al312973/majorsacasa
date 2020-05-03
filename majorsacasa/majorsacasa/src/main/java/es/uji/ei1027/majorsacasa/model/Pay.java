package es.uji.ei1027.majorsacasa.model;

public class Pay {
	private int request_number;
	private int invoice_number;
	
	public Pay() {
		
	}

	@Override
	public String toString() {
		return "Pay [request_number=" + request_number + ", invoice_number=" + invoice_number + "]";
	}

	public int getRequest_number() {
		return request_number;
	}

	public void setRequest_number(int request_number) {
		this.request_number = request_number;
	}

	public int getInvoice_number() {
		return invoice_number;
	}

	public void setInvoice_number(int invoice_number) {
		this.invoice_number = invoice_number;
	}
}
