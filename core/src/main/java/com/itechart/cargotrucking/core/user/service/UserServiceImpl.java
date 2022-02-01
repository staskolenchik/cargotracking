package com.itechart.cargotrucking.core.user.service;

import com.itechart.cargotrucking.core.client.exception.ClientNotFoundException;
import com.itechart.cargotrucking.core.client.repository.ClientRepository;
import com.itechart.cargotrucking.core.common.util.QuerydslUtil;
import com.itechart.cargotrucking.core.user.QUser;
import com.itechart.cargotrucking.core.user.QUserRole;
import com.itechart.cargotrucking.core.user.User;
import com.itechart.cargotrucking.core.user.UserRole;
import com.itechart.cargotrucking.core.user.UserRoleEnum;
import com.itechart.cargotrucking.core.user.UserRoleKey;
import com.itechart.cargotrucking.core.user.dto.UserAddDto;
import com.itechart.cargotrucking.core.user.dto.UserFilterDto;
import com.itechart.cargotrucking.core.user.dto.UserInfoDto;
import com.itechart.cargotrucking.core.user.dto.UserPersonDataUpdateDto;
import com.itechart.cargotrucking.core.user.dto.UserUpdateDto;
import com.itechart.cargotrucking.core.user.exception.DuplicatedEmailException;
import com.itechart.cargotrucking.core.user.exception.DuplicatedLoginException;
import com.itechart.cargotrucking.core.user.exception.EmptyUserRolesException;
import com.itechart.cargotrucking.core.user.exception.UserBadCredentialsException;
import com.itechart.cargotrucking.core.user.exception.UserNotFoundException;
import com.itechart.cargotrucking.core.user.repository.UserRepository;
import com.itechart.cargotrucking.core.user.repository.UserRoleRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
public class UserServiceImpl extends QuerydslUtil implements UserService {
    @Value("${pagination.page-size.min}")
    private int minPageSize;

    @Value("${pagination.page-size.max}")
    private int maxPageSize;

    @Value("${pagination.page-size.default}")
    private int defaultPageSize;

    private UserRepository userRepository;
    private ClientRepository clientRepository;
    private UserRoleRepository userRoleRepository;
    private ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            ClientRepository clientRepository,
            UserRoleRepository userRoleRepository,
            ModelMapper modelMapper
    ) {
        super(User.class);

        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.userRoleRepository = userRoleRepository;
        this.modelMapper = modelMapper;

        this.modelMapper.addMappings(new PropertyMap<UserAddDto, User>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });
        this.modelMapper.addMappings(new PropertyMap<UserUpdateDto, User>() {
            @Override
            protected void configure() {
                skip(destination.getUserRoles());
            }
        });
    }

    @Override
    public long add(UserAddDto userAddDto) {
        validateAdd(userAddDto);

        User user = modelMapper.map(userAddDto, User.class);

        user.setUserRoles(new HashSet<>());
        userRepository.save(user);
        long userId = user.getId();

        Set<UserRole> userRoles = new HashSet<>();
        for (UserRoleEnum role : userAddDto.getUserRoles()) {
            userRoles.add(new UserRole(new UserRoleKey(userId, role)));
        }
        user.setUserRoles(userRoles);

        return userId;
    }

    @Override
    public void update(long id, UserUpdateDto userUpdateDto) {
        validateUpdate(id, userUpdateDto);

        User user = userRepository.getOne(id);

        String userPassword = user.getPassword();
        modelMapper.map(userUpdateDto, user);
        if (!userUpdateDto.getIsChangePassword()) {
            user.setPassword(userPassword);
        }

        for (UserRole userRole : user.getUserRoles()) {
            boolean deleted = true;
            for (UserRoleEnum updateUserRole : userUpdateDto.getUserRoles()) {
                if (updateUserRole.equals(userRole.getId().getRole())) {
                    deleted = false;
                    if (userRole.getCancelDate() == null) {
                        userUpdateDto.getUserRoles().remove(updateUserRole);
                    }
                    break;
                }
            }
            if (deleted) {
                userRoleRepository.delete(userRole.getId().getUserId());
            }
        }

        for (UserRoleEnum userRoleEnum: userUpdateDto.getUserRoles()) {
            userRoleRepository.save(new UserRole(new UserRoleKey(id, userRoleEnum)));
        }
    }

    @Override
    public void updatePersonData(long id, UserPersonDataUpdateDto userDto) {
        validateExist(id);

        User user = userRepository.getOne(id);
        modelMapper.map(userDto, user);
    }

    @Override
    public void updateUserPassword(long id, String password) {
        validateExist(id);

        userRepository.updatePassword(id, password);
    }

    @Override
    public void updateUserEmail(long id, String email) {
        validateExist(id);

        userRepository.updateEmail(id, email);
    }

    @Override
    public void delete(long... ids) {
        for(long id : ids){
            validateDelete(id);
            userRepository.delete(id);
        }
    }

    @Override
    public Page<UserInfoDto> find(long userId, UserFilterDto filter, Pageable pageable) {
        if (pageable.getPageSize() < minPageSize || pageable.getPageSize() > maxPageSize) {
            pageable = PageRequest.of(pageable.getPageNumber(), defaultPageSize, Sort.Direction.ASC, "id");
        }

        User currentUser = userRepository.getOne(userId);
        JPQLQuery<User> query = from(QUser.user).where(QUser.user.clientId.eq(currentUser.getClientId()));
        if (filter.getUserRoles() != null && filter.getUserRoles().size() != 0) {
            query = query.distinct().innerJoin(QUser.user.userRoles, QUserRole.userRole);
        }
        query = query.where(buildWhere(filter));

        return fetch(query, pageable).map(user -> {
            user.setUserRoles(user.getUserRoles().stream()
                    .filter(userRole -> userRole.getCancelDate() == null)
                    .collect(Collectors.toSet())
            );
            return modelMapper.map(user, UserInfoDto.class);
        });
    }

    @Override
    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findById(long id) {
        validateExist(id);

        return userRepository.getOne(id);
    }

    @Override
    public UserInfoDto findInfoById(long id) {
        validateExist(id);

        User user = userRepository.getOne(id);
        UserInfoDto userInfoDto = modelMapper.map(user, UserInfoDto.class);
        userInfoDto.setUserRoles(userInfoDto.getUserRoles().stream()
                .filter(userRole -> userRole.getCancelDate() == null)
                .collect(Collectors.toSet())
        );

        return userInfoDto;
    }

    @Override
    public Long getClientId(long id) {
        validateExist(id);

        return userRepository.getClientId(id);
    }

    @Override
    public String getUserFullName(long id) {
        User user = userRepository.getOne(id);

        String fullName = user.getSurname();
        fullName += StringUtils.isEmpty(user.getName()) ? "" : ' ' + user.getName();
        fullName += StringUtils.isEmpty(user.getPatronymic()) ? "" : ' ' + user.getPatronymic();

        return fullName;
    }

    @Override
    public boolean existsById(long id) {
        return userRepository.existsByIdAndDeleteDateIsNull(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByIdAndDriverRole(long id) {
        return userRepository.existsByIdAndDeleteDateIsNull(id)
                && userRoleRepository.existsByIdAndCancelDateIsNull(new UserRoleKey(id, UserRoleEnum.DRIVER));
    }

    @Override
    public boolean existsByIdAndManagerRole(long id) {
        return userRepository.existsByIdAndDeleteDateIsNull(id)
                && userRoleRepository.existsByIdAndCancelDateIsNull(new UserRoleKey(id, UserRoleEnum.MANAGER));
    }

    @Override
    public boolean existsByIdAndDispatcherRole(long id) {
        return userRepository.existsByIdAndDeleteDateIsNull(id)
                && userRoleRepository.existsByIdAndCancelDateIsNull(new UserRoleKey(id, UserRoleEnum.DISPATCHER));
    }

    private void validateAdd(UserAddDto userAddDto) {
        if (userAddDto.getClientId() == null) {
            throw new UserBadCredentialsException("User cannot have null client id");
        }

        if (userAddDto.getUserRoles().contains(UserRoleEnum.SYS_ADMIN)) {
            throw new UserBadCredentialsException("User cannot have role SYS_ADMIN");
        }

        if (!clientRepository.existsByIdAndDeleteDateIsNull(userAddDto.getClientId())) {
            throw new ClientNotFoundException("Company with id %s doesn't exists", userAddDto.getClientId());
        }

        if (userRepository.existsByLogin(userAddDto.getLogin())) {
            throw new DuplicatedLoginException("User login %s already used", userAddDto.getLogin());
        }

        if (userRepository.existsByEmail(userAddDto.getEmail())) {
            throw new DuplicatedEmailException("Email %s already used", userAddDto.getEmail());
        }

        if (!userAddDto.isPasswordsEquals()) {
            throw new UserBadCredentialsException("User passwords does not match. Password = %s. PasswordConfirm = %s",
                    userAddDto.getPassword(),
                    userAddDto.getPasswordConfirm());
        }

        if (userAddDto.getUserRoles() == null || userAddDto.getUserRoles().size() == 0) {
            throw new EmptyUserRolesException("User roles cannot be null or empty");
        }
    }

    private void validateUpdate(long id, UserUpdateDto userUpdateDto) {
        if (!userRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new UserNotFoundException("User with id %s does not exist", id);
        }

        User user = userRepository.getOne(id);
        if (!user.getLogin().equals(userUpdateDto.getLogin()) && userRepository.existsByLogin(userUpdateDto.getLogin())) {
            throw new DuplicatedLoginException("User login %s already used", userUpdateDto.getLogin());
        }

        if (!user.getEmail().equals(userUpdateDto.getEmail()) && userRepository.existsByEmail(userUpdateDto.getEmail())) {
            throw new DuplicatedEmailException("Email %s already used", userUpdateDto.getEmail());
        }

        if (!userUpdateDto.isPasswordsEquals()) {
            throw new UserBadCredentialsException("User passwords does not match. Password = %s. PasswordConfirm = %s",
                    userUpdateDto.getPassword(),
                    userUpdateDto.getPasswordConfirm());
        }

        if (userUpdateDto.getUserRoles() == null || userUpdateDto.getUserRoles().size() == 0) {
            throw new EmptyUserRolesException("User roles cannot be null or empty");
        }
    }

    private void validateDelete(long id) {
        if (!userRepository.existsByIdAndDeleteDateIsNull(id) &&
                !userRoleRepository.existsByIdAndCancelDateIsNull(new UserRoleKey(id, UserRoleEnum.ADMIN))
        ) {
            throw new UserNotFoundException("User with id %s does not exist", id);
        }
    }

    private void validateExist(long id) {
        if (!userRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new UserNotFoundException("User with id %s doesn't exists", id);
        }
    }

    private BooleanBuilder buildWhere(UserFilterDto filter) {
        BooleanBuilder whereBuilder = new BooleanBuilder(QUser.user.deleteDate.isNull());

        if (!StringUtils.isEmpty(filter.getName())) {
            whereBuilder.and(QUser.user.name.startsWithIgnoreCase(filter.getName()));
        }
        if (!StringUtils.isEmpty(filter.getSurname())) {
            whereBuilder.and(QUser.user.surname.startsWithIgnoreCase(filter.getSurname()));
        }
        if (!StringUtils.isEmpty(filter.getPatronymic())) {
            whereBuilder.and(QUser.user.patronymic.startsWithIgnoreCase(filter.getPatronymic()));
        }

        if (filter.getAfterBornDate() != null) {
            whereBuilder.and(QUser.user.bornDate.after(filter.getAfterBornDate()));
        }
        if (filter.getBeforeBornDate() != null) {
            whereBuilder.and(QUser.user.bornDate.before(filter.getBeforeBornDate()));
        }

        if (!StringUtils.isEmpty(filter.getTown())) {
            whereBuilder.and(QUser.user.town.startsWithIgnoreCase(filter.getTown()));
        }
        if (!StringUtils.isEmpty(filter.getStreet())) {
            whereBuilder.and(QUser.user.street.startsWithIgnoreCase(filter.getStreet()));
        }
        if (!StringUtils.isEmpty(filter.getHouse())) {
            whereBuilder.and(QUser.user.house.startsWithIgnoreCase(filter.getHouse()));
        }

        if (!StringUtils.isEmpty(filter.getFlat())) {
            whereBuilder.and(QUser.user.flat.startsWithIgnoreCase(filter.getFlat()));
        }

        if (filter.getUserRoles() != null && filter.getUserRoles().size() != 0) {
            whereBuilder.and(QUserRole.userRole.id.role.in(filter.getUserRoles()).and(QUserRole.userRole.cancelDate.isNull()));
        }

        return whereBuilder;
    }
}
