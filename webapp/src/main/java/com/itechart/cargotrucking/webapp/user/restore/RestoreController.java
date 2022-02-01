package com.itechart.cargotrucking.webapp.user.restore;

import com.itechart.cargotrucking.core.repair.dto.RestorePasswordDto;
import com.itechart.cargotrucking.core.repair.service.RepairAccountServiceImpl;
import com.itechart.cargotrucking.webapp.user.exception.PasswordsNotEqualsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/restore")
public class RestoreController {
    private RepairAccountServiceImpl repairAccountService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public RestoreController(RepairAccountServiceImpl repairAccountService, PasswordEncoder passwordEncoder) {
        this.repairAccountService = repairAccountService;
        this.passwordEncoder = passwordEncoder;
    }

    @PutMapping("/{uuid}")
    public void restorePassword(@RequestBody @Valid RestorePasswordDto restorePasswordDto, @PathVariable String uuid) {
        if (!restorePasswordDto.comparePasswords()) {
            throw new PasswordsNotEqualsException();
        }

        String encryptPassword = passwordEncoder.encode(restorePasswordDto.getPassword());
        repairAccountService.changePassword(uuid, encryptPassword);
    }
}
