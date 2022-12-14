package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.UserPortalMapper;
import br.com.inovasoft.epedidos.models.dtos.OptionDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.dtos.UserPortalDto;
import br.com.inovasoft.epedidos.models.entities.UserPortal;
import br.com.inovasoft.epedidos.models.enums.RoleEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class UserService extends BaseService<UserPortal> {

	@Inject
	TokenService tokenService;

	@Inject
    UserPortalMapper mapper;

	public PaginationDataResponse<UserPortal> listAll(int page) {
		String query = "systemId = ?1 and deletedOn is null";
		PanacheQuery<UserPortal> listProducts = UserPortal.find(query, tokenService.getSystemId());

		List<UserPortal> dataList = listProducts.page(Page.of(page - 1, LIMIT_PER_PAGE)).list();

		return new PaginationDataResponse<>(dataList, LIMIT_PER_PAGE,
				(int) UserPortal.count(query, tokenService.getSystemId()));
	}

	public List<UserPortalDto> getSuggestions(String query) {
		List<UserPortal> dataList = UserPortal.list(
				"systemId = ?1 and upper(name) like ?2 and isBuyer in (?3) and deletedOn is null",
				tokenService.getSystemId(), query.toUpperCase() + "%",  Boolean.TRUE);

		return mapper.toDto(dataList);
	}

	public List<OptionDto> getListAllOptions() {
		List<UserPortal> dataList = UserPortal.list("systemId = ?1 and isBuyer in (?2) and deletedOn is null order by name",
				tokenService.getSystemId(), Boolean.TRUE);

		return dataList.stream().map(item -> new OptionDto(item.getId(), item.getName())).collect(Collectors.toList());
	}

	@Transactional
	public void changePassword(UserPortal userPortal) {
		checkPassword(userPortal.getPassword(), userPortal.getConfirmPassword());
		UserPortal userBase = UserPortal.find("email", tokenService.getJsonWebToken().getSubject()).firstResult();
		userBase.setPassword(userPortal.getPassword());
		userBase.persist();
	}

	@Transactional
	public void changePassword(String newPassword, String confirmPassword) {
		checkPassword(newPassword, confirmPassword);
		
		UserPortal userBase = UserPortal.find("email", tokenService.getJsonWebToken().getSubject()).firstResult();
		userBase.setPassword(newPassword);
		userBase.persist();
	}

	@Override
	@Transactional
	public void save(UserPortal entity) {
		checkPassword(entity.getPassword(), entity.getConfirmPassword());
		entity.setSystemId(tokenService.getSystemId());

		UserPortal.persist(entity);
	}

	@Transactional
	public UserPortal update(Long id, UserPortal dto) {
		checkPassword(dto.getPassword(), dto.getConfirmPassword());
		UserPortal entity = UserPortal.findById(id);

		try {
			BeanUtils.copyProperties(entity, dto);
			entity.setSystemId(tokenService.getSystemId());
			UserPortal.persist(entity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("Erro ao atualizar usuario", e);
		}

		return entity;
	}

	public void checkPassword(String newPass, String confirmPass) {
		if (!newPass.equals(confirmPass)) {
			throw new WebApplicationException(
					Response.status(400).entity("Confirma????o senha deve ser igual a senha.").build());
		}
	}

	@Transactional
	public void softDelete(Long id) {
		UserPortal.update("set deletedOn = now() where id = ?1", id);
	}
}
