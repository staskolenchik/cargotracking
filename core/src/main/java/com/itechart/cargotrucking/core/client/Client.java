package com.itechart.cargotrucking.core.client;

import com.itechart.cargotrucking.core.car.Car;
import com.itechart.cargotrucking.core.common.enumparser.EnumType;
import com.itechart.cargotrucking.core.common.enumparser.Enumerated;
import com.itechart.cargotrucking.core.productowner.ProductOwner;
import com.itechart.cargotrucking.core.storage.Storage;
import com.itechart.cargotrucking.core.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "client_company")
@Getter
@Setter
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.POSTGRES)
    private ClientType status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clientId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Car> cars;

    @Column(name = "delete_date", insertable = false)
    private LocalDateTime deleteDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clientId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClientStatusHistory> clientStatusHistorySet;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clientId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductOwner> productOwners;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clientId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Storage> storages;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clientId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<User> users;
}
