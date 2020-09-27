package br.com.inovasoft.epedidos.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import br.com.inovasoft.epedidos.mappers.CompanyMapper;
import br.com.inovasoft.epedidos.mappers.CompanySystemMapper;
import br.com.inovasoft.epedidos.models.dtos.CompanyDto;
import br.com.inovasoft.epedidos.models.dtos.CompanySystemDto;
import br.com.inovasoft.epedidos.models.entities.Company;
import br.com.inovasoft.epedidos.models.entities.CompanySystem;
import br.com.inovasoft.epedidos.models.entities.Systems;
import br.com.inovasoft.epedidos.models.entities.UserPortal;

@ApplicationScoped
public class CompanyService extends BaseService<Company> {

    @Inject
    CompanyMapper mapper;

    @Inject
    CompanySystemMapper mapperCompanySystem;

    public CompanyDto saveDto(CompanyDto dto) {
        Company entity = mapper.toEntity(dto);

        super.save(entity);

        saveSystemDto(entity, dto.getSystems());

        return mapper.toDto(entity);
    }

    private void saveSystemDto(Company entity, List<CompanySystemDto> systems) {
        systems.forEach(item -> {
            CompanySystem storeCompanySystem = null;
            if (item.getId() == null) {
                storeCompanySystem = mapperCompanySystem.toEntity(item);
                storeCompanySystem.setCompany(entity);
                storeCompanySystem.setSystem(Systems.findById(item.getIdSystem()));
                if (storeCompanySystem.getCreatedOn() == null)
                    storeCompanySystem.setCreatedOn(LocalDateTime.now());
            } else {
                storeCompanySystem = CompanySystem.findById(item.getId());
                storeCompanySystem.setSystem(Systems.findById(item.getIdSystem()));
                mapperCompanySystem.updateEntityFromDto(item, storeCompanySystem);

            }
            validateAcesskey(item.getId(), item.getSystemKey());
            CompanySystem.persist(storeCompanySystem);
            createUser(storeCompanySystem);

        });
    }

    private void createUser(CompanySystem companySystem) {
        long countUser = UserPortal.count("from UserPortal where email=?1 and system.id=?2",
                companySystem.getEmailAdmin(), companySystem.getId());
        if (countUser == 0) {
            UserPortal newUSer = new UserPortal();
            newUSer.setSystem(companySystem);
            newUSer.setEmail(companySystem.getEmailAdmin());
            newUSer.setName("Admin");
            newUSer.setPassword("123456");
            newUSer.setCreatedOn(LocalDateTime.now());
            newUSer.persist();
        }
    }

    private void validateAcesskey(Long id, String systemKey) {
        long total = 0;
        if (id != null) {
            total = CompanySystem.count("from CompanySystem where systemKey =?1 and id !=?2", systemKey, id);
        } else {
            total = CompanySystem.count("from CompanySystem where systemKey =?1 ", systemKey);
        }
        if (total > 0) {
            throw new WebApplicationException(Response.status(403).entity(String.format(
                    "Atenção, já existe um sistema cadastrado com a chave: %s. Favor informar uma chave diferente!",
                    systemKey)).build());
        }
    }

    public CompanyDto update(Long id, CompanyDto dto) {
        Company entity = Company.findById(id);
        mapper.updateEntityFromDto(dto, entity);

        Company.persist(entity);

        saveSystemDto(entity, dto.getSystems());

        return mapper.toDto(entity);
    }

    public CompanyDto findById(Long companyId) {
        Company entity = Company.findById(companyId);
        CompanyDto result = mapper.toDto(entity);

        List<CompanySystem> systems = CompanySystem.list("from CompanySystem where company.id=?1", companyId);

        result.setSystems(systems.stream().map(item -> {
            CompanySystemDto companySystem = mapperCompanySystem.toDto(item);
            companySystem.setIdSystem(item.getSystem().getId());
            companySystem.setNameSystem(item.getSystem().getName());
            return companySystem;
        }).collect(Collectors.toList()));
        result.setId(companyId);
        return result;
    }

}