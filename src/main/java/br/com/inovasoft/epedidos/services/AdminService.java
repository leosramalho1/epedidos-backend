package br.com.inovasoft.epedidos.services;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import br.com.inovasoft.epedidos.models.entities.UserAdmin;

@ApplicationScoped
public class AdminService extends BaseService<UserAdmin> {

	@Transactional
	public void changePassword(String email, String newPassword) {
		UserAdmin userBase = UserAdmin.find("email", email).firstResult();
		userBase.setPassword(newPassword);
		userBase.persist();
	}

	@Transactional
	public void softDelete(Long id) {
		UserAdmin.update("set deletedOn = now() where id = ?1", id);
	}
}
