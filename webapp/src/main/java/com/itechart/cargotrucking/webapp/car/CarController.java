package com.itechart.cargotrucking.webapp.car;

import com.itechart.cargotrucking.core.car.dto.CarAddDto;
import com.itechart.cargotrucking.core.car.dto.CarFilterDto;
import com.itechart.cargotrucking.core.car.dto.CarInfoDto;
import com.itechart.cargotrucking.core.car.dto.CarUpdateDto;
import com.itechart.cargotrucking.core.car.service.CarService;
import com.itechart.cargotrucking.webapp.security.UserCredentials;
import com.itechart.cargotrucking.webapp.security.exception.AccessException;
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

@RestController
@RequestMapping("/api/cars")
public class CarController {
    private CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping
    public ResponseEntity<String> addCar(@RequestBody @Valid CarAddDto car, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        car.setClientId(userCredentials.getClientId());

        long id = carService.add(car);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public void updateCar(@PathVariable long id, @RequestBody @Valid CarUpdateDto car, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        Long clientId = carService.getClientId(id);

        if (!userCredentials.getClientId().equals(clientId)) {
            throw new AccessException("Authenticated user haven't privileges to update cars of company with id %s", clientId);
        }

        carService.update(id, car);
    }

    @DeleteMapping
    public void deleteCar(Authentication authentication, @RequestBody long... ids) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        for (long id : ids) {
            Long clientId = carService.getClientId(id);

            if (!userCredentials.getClientId().equals(clientId)) {
                throw new AccessException("Authenticated user haven't privileges to delete cars of company with id %s", clientId);
            }
        }

        carService.delete(ids);
    }

    @GetMapping
    public Page<CarInfoDto> carListByFilters(
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.ASC) Pageable pageable,
            CarFilterDto filter,
            Authentication authentication
    ) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        return carService.find(userCredentials.getClientId(), filter, pageable);
    }

    @GetMapping("/{id}")
    public CarInfoDto carById(@PathVariable long id, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        Long clientId = carService.getClientId(id);

        if (!userCredentials.getClientId().equals(clientId)) {
            throw new AccessException("Authenticated user haven't privileges to see cars of company with id %s", clientId);
        }

        return carService.findById(id);
    }
}
