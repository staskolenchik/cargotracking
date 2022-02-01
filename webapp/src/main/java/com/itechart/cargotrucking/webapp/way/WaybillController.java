package com.itechart.cargotrucking.webapp.way;

import com.itechart.cargotrucking.core.way.dto.CheckpointInfoDto;
import com.itechart.cargotrucking.core.way.dto.WaybillAddDto;
import com.itechart.cargotrucking.core.way.dto.WaybillFilterDto;
import com.itechart.cargotrucking.core.way.dto.WaybillInfoDto;
import com.itechart.cargotrucking.core.way.service.WaybillService;
import com.itechart.cargotrucking.webapp.security.Role;
import com.itechart.cargotrucking.webapp.security.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/waybills")
public class WaybillController {
    private WaybillService waybillService;

    @Autowired
    public WaybillController(WaybillService waybillService) {
        this.waybillService = waybillService;
    }

    @GetMapping
    public Page<WaybillInfoDto> find(
            @PageableDefault (sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable,
            WaybillFilterDto filter,
            Authentication authentication
    ) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        Long userId = null;
        Long clientId = null;
        if (!authentication.getAuthorities().contains(Role.COMPANY_OWNER)
                && !authentication.getAuthorities().contains(Role.MANAGER)
        ) {
            userId = userCredentials.getUserId();
        } else {
            clientId = userCredentials.getClientId();
        }

        return waybillService.find(userId, clientId, filter, pageable);
    }

    @GetMapping("/{id}")
    public List<CheckpointInfoDto> findCheckpoints(@PathVariable long id) {
        return waybillService.findCheckpoints(id);
    }

    @PutMapping("/{id}")
    public void reachCheckpoint(@PathVariable long id, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        waybillService.reachCheckpoint(id, userCredentials.getUserId());
    }

    @PostMapping
    public ResponseEntity<Long> createWaybill(@Valid @RequestBody WaybillAddDto waybill, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        waybill.setVerifierId(userCredentials.getUserId());
        long id = waybillService.createWaybill(waybill);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id).toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping
    public void deleteWaybill(@RequestBody long... ids) {
        waybillService.deleteWaybill(ids);
    }
}
