package com.itechart.cargotrucking.core.solr.productowner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

public interface ProductOwnerSolrRepository extends SolrCrudRepository<ProductOwnerSolr, Long> {
    @Query("clientId:?0 AND name:?1*")
    Page<ProductOwnerSolr> findByClientId(long clientId, String name, Pageable pageable);
}
