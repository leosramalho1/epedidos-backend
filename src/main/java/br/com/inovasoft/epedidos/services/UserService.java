package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.UserMapper;
import br.com.inovasoft.epedidos.models.dtos.OptionDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.dtos.UserPortalDto;
import br.com.inovasoft.epedidos.models.entities.UserPortal;
import br.com.inovasoft.epedidos.models.enums.RoleEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.apache.commons.beanutils.BeanUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService extends BaseService<UserPortal> {

	@Inject
	TokenService tokenService;

	@Inject
	UserMapper mapper;

	public PaginationDataResponse<UserPortal> listAll(int page) {
		PanacheQuery<UserPortal> listProducts = UserPortal.find(
				"select p from UserPortal p where p.systemId = ?1 and p.deletedOn is null", tokenService.getSystemId());

		List<UserPortal> dataList = listProducts.page(Page.of(page - 1, limitPerPage)).list();

		return new PaginationDataResponse<>(dataList, limitPerPage,
				(int) UserPortal.count("systemId  = ?1 and deletedOn is null", tokenService.getSystemId()));
	}

	public List<UserPortalDto> getSuggestions(String query) {
		List<UserPortal> dataList = UserPortal.list(
				"systemId = ?1 and upper(name) like ?2 and role = ?3 and deletedOn is null",
				tokenService.getSystemId(), query.toUpperCase() + "%", RoleEnum.BUYER);

		return mapper.toDto(dataList);
	}

	public List<OptionDto> getListAllOptions() {
		List<UserPortal> dataList = UserPortal.list("systemId = ?1 and  role = ?2 and deletedOn is null order by name",
				tokenService.getSystemId(), RoleEnum.BUYER);

		return dataList.stream().map(item -> new OptionDto(item.getId(), item.getName())).collect(Collectors.toList());
	}

	@Transactional
	public void changePassword(UserPortal userPortal) {
		checkPassword(userPortal);
		UserPortal userBase = UserPortal.find("email", tokenService.getJsonWebToken().getSubject()).firstResult();
		userBase.setPassword(userPortal.getPassword());
		userBase.persist();
	}

	

	

	@Transactional
	public void save(UserPortal entity) {
		checkPassword(entity);
		entity.setSystemId(tokenService.getSystemId());

		UserPortal.persist(entity);
	}

	@Transactional
	public UserPortal update(Long id, UserPortal dto) {
		checkPassword(dto);
		UserPortal entity = UserPortal.findById(id);

		try {
			BeanUtils.copyProperties(entity, dto);
			entity.setSystemId(tokenService.getSystemId());
			UserPortal.persist(entity);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return entity;
	}

	public void checkPassword(UserPortal userPortal) {
		if (!userPortal.getConfirmPassword().equals(userPortal.getPassword())) {
			throw new WebApplicationException(
					Response.status(400).entity("Confirmação senha deve ser igual a senha.").build());
		}
	}
}
