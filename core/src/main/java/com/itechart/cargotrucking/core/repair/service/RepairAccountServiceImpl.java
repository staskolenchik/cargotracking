package com.itechart.cargotrucking.core.repair.service;

import com.itechart.cargotrucking.core.client.service.ClientService;
import com.itechart.cargotrucking.core.repair.RepairAccountRelations;
import com.itechart.cargotrucking.core.repair.exception.NoSuchLinkException;
import com.itechart.cargotrucking.core.repair.repository.RepairAccountRepository;
import com.itechart.cargotrucking.core.user.User;
import com.itechart.cargotrucking.core.user.exception.InactivatedUserException;
import com.itechart.cargotrucking.core.user.repository.UserRepository;
import com.itechart.cargotrucking.core.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RepairAccountServiceImpl implements RepairAccountService {
    private RepairAccountRepository repairAccountRepository;
    private UserRepository userRepository;
    private UserService userService;
    private ClientService clientService;

    @Autowired
    public RepairAccountServiceImpl(
            RepairAccountRepository repairAccountRepository,
            UserService userService,
            UserRepository userRepository,
            ClientService clientService
    ) {
        this.repairAccountRepository = repairAccountRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.clientService = clientService;
    }

    @Transactional
    @Override
    public void changePassword(String uuid, String password) {
        if (!repairAccountRepository.existsByUid(uuid)) {
            throw new NoSuchLinkException("Link is inactivated %s", uuid);
        }
        RepairAccountRelations relations = repairAccountRepository.findByUid(uuid);
        User user = userService.findById(relations.getUserId());
        if (!userService.existsById(user.getId()) || !clientService.existsById(user.getClientId())) {
            throw new InactivatedUserException("User with %s email is inactivated", user.getEmail());
        }

        userRepository.save(user);
        userService.updateUserPassword(user.getId(), password);
        repairAccountRepository.delete(relations.getUserId());
    }
}
