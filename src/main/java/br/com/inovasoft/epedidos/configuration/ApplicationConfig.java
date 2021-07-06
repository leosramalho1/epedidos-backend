package br.com.inovasoft.epedidos.configuration;

import br.com.inovasoft.epedidos.models.entities.Setup;
import br.com.inovasoft.epedidos.models.enums.SetupEnum;
import br.com.inovasoft.epedidos.services.OrderService;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.parser.JSONParser;
import org.quartz.SchedulerException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApplicationScoped
public class ApplicationConfig {

    @Inject
    OrderService orderService;

    void onStart(@Observes StartupEvent event) {
        List<Setup> setupList = Setup
                .list("deletedOn is null and key = ?1",
                        SetupEnum.CRON_ORDER_TO_PURCHASE);

        if(CollectionUtils.isNotEmpty(setupList)) {
            setupList.forEach(setup -> {
                try {
                    orderService.schedulerOrderToPurchase(setup.getValue(), setup.getSystemId());
                } catch (SchedulerException e) {
                    log.error("Erro ao inicializar o scheduler", e);
                    log.error("Scheduler {}", setup);
                    throw new RuntimeException("Erro ao inicializar o scheduler", e);
                }
            });
        }
    }

    @Produces
    public JSONParser jsonParser(){
        return new JSONParser();
    }

}
