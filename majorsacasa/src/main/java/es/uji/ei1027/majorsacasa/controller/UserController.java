package es.uji.ei1027.majorsacasa.controller;

import java.util.HashMap;
import java.util.Map;

import org.jasypt.util.password.BasicPasswordEncryptor;

import es.uji.ei1027.majorsacasa.dao.ElderlyDAO;
import es.uji.ei1027.majorsacasa.dao.SocialWorkerDAO;
import es.uji.ei1027.majorsacasa.dao.VolunteerDAO;
import es.uji.ei1027.majorsacasa.model.Elderly;
import es.uji.ei1027.majorsacasa.model.SocialWorker;
import es.uji.ei1027.majorsacasa.model.UserDetails;
import es.uji.ei1027.majorsacasa.model.Volunteer;

public class UserController {
	final Map<String, UserDetails> allUsers = new HashMap<String, UserDetails>();
	private BasicPasswordEncryptor passwordEncryptor;
	
	public UserController(ElderlyDAO elderlyDao, SocialWorkerDAO socialWorkerDao, VolunteerDAO volunteerDao) {
		this.passwordEncryptor = new BasicPasswordEncryptor();
		
		//Se obtienen todos los mayores y se guardan para hacer comprobaciones en el login
		for (Elderly elderly : elderlyDao.getElderlies()) {
			UserDetails user = new UserDetails();
			user.setEmail(elderly.getEmail());
			user.setPassword(passwordEncryptor.encryptPassword(elderly.getUserPwd()));
			user.setType("elderly");
			allUsers.put(user.getEmail(), user);
		}
		
		//Se obtienen todos los trabajadores sociales y se guardan para hacer comprobaciones en el login
		for (SocialWorker socialWorker : socialWorkerDao.getSocialWorkers()) {
			UserDetails user = new UserDetails();
			user.setEmail(socialWorker.getEmail());
			user.setPassword(passwordEncryptor.encryptPassword(socialWorker.getPwd()));
			user.setType("socialWorker");
			allUsers.put(user.getEmail(), user);
		}
		
		//Se obtienen todos los voluntarios y se guardan para hacer comprobaciones en el login
		for (Volunteer volunteer : volunteerDao.getVolunteers()) {
			UserDetails user = new UserDetails();
			user.setEmail(volunteer.getEmail());
			user.setPassword(passwordEncryptor.encryptPassword(volunteer.getPwd()));
			user.setType("volunteer");
			allUsers.put(user.getEmail(), user);
		}
	}
	
	public UserDetails checkCredentials(String email, String password) { 
		  UserDetails user = allUsers.get(email.trim());
		  if (user == null) //Usuario no registrado
			  return null; 
		 
		 // Contrasenya 
		 if (passwordEncryptor.checkPassword(password, user.getPassword())) {
			 user.setPassword(null);
			 return user; 
	     }else {//Credenciales incorrectas
			 return null; 
		 }
	}

}
