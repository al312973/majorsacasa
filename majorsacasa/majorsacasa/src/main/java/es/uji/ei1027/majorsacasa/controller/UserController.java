package es.uji.ei1027.majorsacasa.controller;

import es.uji.ei1027.majorsacasa.dao.ElderlyDAO;
import es.uji.ei1027.majorsacasa.dao.SocialWorkerDAO;
import es.uji.ei1027.majorsacasa.dao.VolunteerDAO;
import es.uji.ei1027.majorsacasa.model.Elderly;
import es.uji.ei1027.majorsacasa.model.SocialWorker;
import es.uji.ei1027.majorsacasa.model.UserDetails;
import es.uji.ei1027.majorsacasa.model.Volunteer;

public class UserController {
	private ElderlyDAO elderlyDao;
	private SocialWorkerDAO socialWorkerDao;
	private VolunteerDAO volunteerDao;
	
	
	public UserController(ElderlyDAO elderlyDao, SocialWorkerDAO socialWorkerDao, VolunteerDAO volunteerDao) {
		this.elderlyDao = elderlyDao;
		this.socialWorkerDao = socialWorkerDao;
		this.volunteerDao = volunteerDao;
	}
	
	public UserDetails checkCredentials(String userEmail, String password) { 
		//Comprobamos si es una persona mayor
		//Las personas mayores solo pueden acceder a la aplicación con email
		Elderly elderly = elderlyDao.getElderlyByEmail(userEmail.trim());
		if (elderly!=null) {
			UserDetails user = new UserDetails();
			user.setEmail(elderly.getEmail());
			user.setPassword(null);
			user.setType("elderly");
			if (password.equals(elderly.getUserPwd())) {
				 user.setPwdCorrect(true);
			}else {
				user.setPwdCorrect(false);
			}
			return user;
		}
		
		//Comprobamos si es un trabajador social
		SocialWorker socialWorker;
		if (userEmail.contains("@")) {
			socialWorker = socialWorkerDao.getSocialWorkerByEmail(userEmail.trim());
		} else {
			socialWorker = socialWorkerDao.getSocialWorkerByUserCAS(userEmail.trim());
		}
		if (socialWorker!=null) {
			UserDetails user = new UserDetails();
			user.setEmail(socialWorker.getEmail());
			user.setPassword(null);
			user.setType("socialWorker");
			if (password.equals(socialWorker.getPwd())) {
				 user.setPwdCorrect(true);
			}else {
				user.setPwdCorrect(false);
			}
			return user;
		}
		
		//Comprobamos si es un trabajador social
		Volunteer volunteer;
		if (userEmail.contains("@")) {
			volunteer = volunteerDao.getVolunteerByEmail(userEmail.trim());
		} else {
			volunteer = volunteerDao.getVolunteerByUsr(userEmail.trim());
		}
		if (volunteer!=null) {
			UserDetails user = new UserDetails();
			user.setEmail(volunteer.getEmail());
			user.setPassword(null);
			user.setType("volunteer");
			if (password.equals(volunteer.getPwd())) {
				 user.setPwdCorrect(true);
			}else {
				user.setPwdCorrect(false);
			}
			return user;
		}
		
		//El usuario no esta registrado
		return null;
	}
}
