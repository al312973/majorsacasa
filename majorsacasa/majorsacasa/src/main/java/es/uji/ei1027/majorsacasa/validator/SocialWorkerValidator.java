package es.uji.ei1027.majorsacasa.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import es.uji.ei1027.majorsacasa.dao.SocialWorkerDAO;
import es.uji.ei1027.majorsacasa.model.SocialWorker;

public class SocialWorkerValidator implements Validator{
	private SocialWorkerDAO socialWorkerDao;
	private SocialWorker socialWorker;
	
	public SocialWorkerValidator(SocialWorkerDAO socialWorkerDao, SocialWorker socialWorker) {
		this.socialWorkerDao = socialWorkerDao;
		this.socialWorker = socialWorker;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return SocialWorker.class.isAssignableFrom(clazz);
	}
	@Override
	public void validate(Object obj, Errors errors) {
		SocialWorker socialWorker = (SocialWorker) obj;
		
		if (socialWorker.getUserCAS().length() > 20){
			errors.rejectValue("userCAS", "usuariincorrecte", "Usuari massa llarg");
		}
		if (socialWorker.getPwd().length() > 20){
			errors.rejectValue("pwd", "contrasenyaincorrecta", "Contrasenya massa llarga");
		}
	}

}
