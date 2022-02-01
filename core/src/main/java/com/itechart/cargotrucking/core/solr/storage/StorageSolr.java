package com.itechart.cargotrucking.core.solr.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@Getter
@Setter
@SolrDocument(collection = "Storage")
public class StorageSolr {
    @Id
    private Long id;

    @Indexed
    private String name;

    @Indexed
    private String address;

    @Indexed
    private Long clientId;
}
