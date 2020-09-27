package br.com.inovasoft.epedidos.security;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ElasticsearchService {

  /*  @Inject
    EntityManager entityManager;

    @Transactional 
    public void rebuildIndexes() throws InterruptedException { 
            Search.session(entityManager)
                    .massIndexer()
                    .startAndWait();
        
    }

    public <T> List<T> search(Class<T> clazz, String termo, Optional<Integer> tamanho, String... fields) {
        return Search.session(entityManager).search(clazz)
                    .where(
                            f -> StringUtils.isEmpty(termo) ? 
                            f.matchAll() : 
                            f.simpleQueryString()
                                .fields(fields).matching(termo))
                .fetchHits(tamanho.orElse(20));
    }*/

}