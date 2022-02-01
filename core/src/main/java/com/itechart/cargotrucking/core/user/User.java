package com.itechart.cargotrucking.core.user;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
public class User {
    public User() {
    }

    public <T extends User> User(T user) {
        id = user.getId();
        deleteDate = user.getDeleteDate();
        name = user.getName();
        surname = user.getSurname();
        patronymic = user.getPatronymic();
        bornDate = user.getBornDate();
        email = user.getEmail();
        town = user.getTown();
        street = user.getStreet();
        house = user.getHouse();
        flat = user.getFlat();
        login = user.getLogin();
        password = user.getPassword();
        passportNum = user.getPassportNum();
        issuedBy = user.getIssuedBy();
        clientId = user.getClientId();
        userRoles = user.getUserRoles();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private Long id;

    @Column(name = "delete_date", updatable = false, insertable = false)
    private LocalDateTime deleteDate;

    @Column(name = "name")
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "born_date")
    private LocalDate bornDate;

    @Column(name = "email")
    private String email;

    @Column(name = "town")
    private String town;

    @Column(name = "street")
    private String street;

    @Column(name = "house")
    private String house;

    @Column(name = "flat")
    private String flat;

    @Column(name = "login", nullable = false, updatable = false)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "passport_num")
    private String passportNum;

    @Column(name = "issued_by")
    private String issuedBy;

    @Column(name = "client_id")
    private Long clientId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles;
}
