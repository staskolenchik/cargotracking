package com.itechart.cargotrucking.core.solr.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

public interface StorageSolrRepository extends SolrCrudRepository<StorageSolr, Long> {
    @Query("clientId:?0 AND name:?1*")
    Page<StorageSolr> findByClientId(long clientId, String name, Pageable pageable);
}
