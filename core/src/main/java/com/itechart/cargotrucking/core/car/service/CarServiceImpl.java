package com.itechart.cargotrucking.core.car.service;

import com.itechart.cargotrucking.core.car.Car;
import com.itechart.cargotrucking.core.car.QCar;
import com.itechart.cargotrucking.core.car.dto.CarAddDto;
import com.itechart.cargotrucking.core.car.dto.CarFilterDto;
import com.itechart.cargotrucking.core.car.dto.CarInfoDto;
import com.itechart.cargotrucking.core.car.dto.CarUpdateDto;
import com.itechart.cargotrucking.core.car.exception.CarNotFoundException;
import com.itechart.cargotrucking.core.car.exception.DuplicatedCarNumberException;
import com.itechart.cargotrucking.core.car.repository.CarRepository;
import com.itechart.cargotrucking.core.client.exception.ClientNotFoundException;
import com.itechart.cargotrucking.core.client.service.ClientService;
import com.querydsl.core.BooleanBuilder;
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

@Service
@Validated
public class CarServiceImpl implements CarService {
    @Value("${pagination.page-size.min}")
    private int minPageSize;

    @Value("${pagination.page-size.max}")
    private int maxPageSize;

    @Value("${pagination.page-size.default}")
    private int defaultPageSize;

    private CarRepository carRepository;
    private ClientService clientService;
    private ModelMapper modelMapper;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, ClientService clientService, ModelMapper modelMapper) {
        this.carRepository = carRepository;
        this.clientService = clientService;
        this.modelMapper = modelMapper;

        this.modelMapper.addMappings(new PropertyMap<CarAddDto, Car>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });
    }

    @Override
    public long add(CarAddDto carAddDto) {
        validateAdd(carAddDto);

        Car car = modelMapper.map(carAddDto, Car.class);

        carRepository.save(car);
        return car.getId();
    }

    @Override
    public void update(long id, CarUpdateDto carUpdateDto) {
        validateUpdate(id, carUpdateDto);

        Car car = carRepository.getOne(id);

        modelMapper.map(carUpdateDto, car);
    }

    @Override
    public void delete(long... ids) {
        for (long id : ids) {
            validateDelete(id);
            carRepository.delete(id);
        }
    }

    @Override
    public Page<CarInfoDto> find(long clientId, CarFilterDto filter, Pageable pageable) {
        if (pageable.getPageSize() < minPageSize || pageable.getPageSize() > maxPageSize) {
            pageable = PageRequest.of(pageable.getPageNumber(), defaultPageSize, Sort.Direction.ASC, "id");
        }

        Page<Car> page = carRepository.findAll(buildWhere(clientId, filter), pageable);
        return page.map(car -> modelMapper.map(car, CarInfoDto.class));
    }

    @Override
    public CarInfoDto findById(long id) {
        validateExist(id);

        Car car = carRepository.getOne(id);
        return modelMapper.map(car, CarInfoDto.class);
    }

    @Override
    public Long getClientId(long id) {
        validateExist(id);

        return carRepository.getClientId(id);
    }

    @Override
    public boolean existsById(long id) {
        return carRepository.existsByIdAndDeleteDateIsNull(id);
    }

    private void validateAdd(CarAddDto carAddDto) {
        if (carRepository.existsByNumber(carAddDto.getNumber())) {
            throw new DuplicatedCarNumberException("Car with number %s already exists", carAddDto.getNumber());
        }

        if (!clientService.existsById(carAddDto.getClientId())) {
            throw new ClientNotFoundException("Client with id %s doesn't exists", carAddDto.getClientId());
        }
    }

    private void validateUpdate(long id, CarUpdateDto carUpdateDto) {
        if (!carRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new CarNotFoundException("Car with id %s doesn't exists", id);
        }

        String carNumber = carRepository.getOne(id).getNumber();
        if (!carNumber.equals(carUpdateDto.getNumber()) && carRepository.existsByNumber(carUpdateDto.getNumber())) {
            throw new DuplicatedCarNumberException("Car with number %s already exists", carUpdateDto.getNumber());
        }
    }

    private void validateDelete(long id) {
        if (!carRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new CarNotFoundException("Car with id %s doesn't exists", id);
        }
    }

    private void validateExist(long id) {
        if (!carRepository.existsByIdAndDeleteDateIsNull(id)) {
            throw new CarNotFoundException("Car with id %s doesn't exists", id);
        }
    }

    private BooleanBuilder buildWhere(long clientId, CarFilterDto filter) {
        BooleanBuilder whereBuilder = new BooleanBuilder(QCar.car.deleteDate.isNull());
        whereBuilder.and(QCar.car.clientId.eq(clientId));

        if (!StringUtils.isEmpty(filter.getNumber())) {
            whereBuilder.and(QCar.car.number.startsWithIgnoreCase(filter.getNumber()));
        }

        if (filter.getFuelConsumptionLess() != null) {
            whereBuilder.and(QCar.car.fuelConsumption.loe(filter.getFuelConsumptionLess()));
        }
        if (filter.getFuelConsumptionMore() != null) {
            whereBuilder.and(QCar.car.fuelConsumption.goe(filter.getFuelConsumptionMore()));
        }

        if (filter.getLoadCapacityLess() != null) {
            whereBuilder.and(QCar.car.loadCapacity.loe(filter.getLoadCapacityLess()));
        }
        if (filter.getLoadCapacityMore() != null) {
            whereBuilder.and(QCar.car.loadCapacity.goe(filter.getLoadCapacityMore()));
        }

        if (filter.getCarTypes() != null && filter.getCarTypes().size() != 0) {
            whereBuilder.and(QCar.car.carType.in(filter.getCarTypes()));
        }

        return whereBuilder;
    }
}
