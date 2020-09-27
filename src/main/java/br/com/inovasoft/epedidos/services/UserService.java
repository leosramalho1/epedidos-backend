package br.com.inovasoft.epedidos.services;


import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import br.com.inovasoft.epedidos.models.entities.UserPortal;

@ApplicationScoped
public class UserService extends BaseService<UserPortal> {

	@Transactional
	public void changePassword(String email, String newPassword) {
		UserPortal userBase = UserPortal.find("email", email).firstResult();
		userBase.setPassword(newPassword);
		userBase.persist();
	}
	
}
