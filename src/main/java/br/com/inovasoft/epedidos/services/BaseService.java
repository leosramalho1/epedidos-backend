package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.models.BaseEntity;

import javax.transaction.Transactional;
import java.util.List;

public abstract class BaseService<E extends BaseEntity> {

	public static final int limitPerPage = 25;

	public List<E> listAll() {
		return E.listAll();
	}

	public List<E> listAllIncludeExclusions() {
		return E.listAll();
	}

	@Transactional
	public void save(E entity) {
		entity.persist();
	}

	@Transactional
	public void hardDelete(Long id) {
		E.deleteById(id);
	}

	@Transactional
	public void softDelete(Long id) {
		E.update("set deletedOn = now() where id = ?1", id);
	}

}