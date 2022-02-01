package com.itechart.cargotrucking.core.way.service;

import com.itechart.cargotrucking.core.car.Car;
import com.itechart.cargotrucking.core.car.exception.CarNotFoundException;
import com.itechart.cargotrucking.core.car.repository.CarRepository;
import com.itechart.cargotrucking.core.invoice.Invoice;
import com.itechart.cargotrucking.core.invoice.InvoiceStatus;
import com.itechart.cargotrucking.core.invoice.exception.InvoiceNotFoundException;
import com.itechart.cargotrucking.core.invoice.repository.InvoiceRepository;
import com.itechart.cargotrucking.core.product.Product;
import com.itechart.cargotrucking.core.product.ProductStatus;
import com.itechart.cargotrucking.core.productowner.ProductOwner;
import com.itechart.cargotrucking.core.productowner.repository.ProductOwnerRepository;
import com.itechart.cargotrucking.core.storage.Storage;
import com.itechart.cargotrucking.core.storage.repository.StorageRepository;
import com.itechart.cargotrucking.core.user.User;
import com.itechart.cargotrucking.core.user.exception.UserNotFoundException;
import com.itechart.cargotrucking.core.user.service.UserService;
import com.itechart.cargotrucking.core.way.CarriageStatus;
import com.itechart.cargotrucking.core.way.Checkpoint;
import com.itechart.cargotrucking.core.way.QWaybill;
import com.itechart.cargotrucking.core.way.Waybill;
import com.itechart.cargotrucking.core.way.dto.CheckpointAddDto;
import com.itechart.cargotrucking.core.way.dto.CheckpointInfoDto;
import com.itechart.cargotrucking.core.way.dto.WaybillAddDto;
import com.itechart.cargotrucking.core.way.dto.WaybillFilterDto;
import com.itechart.cargotrucking.core.way.dto.WaybillInfoDto;
import com.itechart.cargotrucking.core.way.exception.CheckpointsNotFoundException;
import com.itechart.cargotrucking.core.way.exception.DuplicatedWaybillByInvoiceIdException;
import com.itechart.cargotrucking.core.way.exception.ReachCheckpointAccessException;
import com.itechart.cargotrucking.core.way.exception.WaybillNotFoundException;
import com.itechart.cargotrucking.core.way.repository.CheckpointRepository;
import com.itechart.cargotrucking.core.way.repository.WaybillRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WaybillServiceImpl implements WaybillService {
    @Value("${pagination.page-size.min}")
    private int minPageSize;

    @Value("${pagination.page-size.max}")
    private int maxPageSize;

    @Value("${pagination.page-size.default}")
    private int defaultPageSize;

    private WaybillRepository waybillRepository;
    private CheckpointRepository checkpointRepository;
    private CarRepository carRepository;
    private InvoiceRepository invoiceRepository;
    private ProductOwnerRepository productOwnerRepository;
    private StorageRepository storageRepository;

    private UserService userService;
    private ModelMapper modelMapper;

    @Autowired
    public WaybillServiceImpl(
            WaybillRepository waybillRepository,
            CheckpointRepository checkpointRepository,
            CarRepository carRepository,
            InvoiceRepository invoiceRepository,
            ProductOwnerRepository productOwnerRepository,
            StorageRepository storageRepository,
            UserService userService,
            ModelMapper modelMapper
    ) {
        this.waybillRepository = waybillRepository;
        this.checkpointRepository = checkpointRepository;
        this.carRepository = carRepository;
        this.invoiceRepository = invoiceRepository;
        this.productOwnerRepository = productOwnerRepository;
        this.storageRepository = storageRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;

        this.modelMapper.addMappings(new PropertyMap<WaybillAddDto, Waybill>() {
            @Override
            protected void configure() {
                skip(destination.getCheckpoints());
            }
        });
        this.modelMapper.addMappings(new PropertyMap<CheckpointAddDto, Checkpoint>() {
            @Override
            protected void configure() {
                skip(destination.getCheckpointDate());
            }
        });
    }

    @Override
    public List<CheckpointInfoDto> findCheckpoints(long waybillId) {
        validateExists(waybillId);

        List<Checkpoint> checkpointsFromRepo = checkpointRepository.findByWaybillId(waybillId);
        Waybill waybill = waybillRepository.getOne(waybillId);

        List<CheckpointInfoDto> checkpoints = new ArrayList<>();

        CheckpointInfoDto startCheckpoint = new CheckpointInfoDto();
        startCheckpoint.setAddress(waybill.getStartPoint());
        startCheckpoint.setCheckpointDate(waybill.getFactualStartDate());
        startCheckpoint.setRequiredArrivalDate(waybill.getStartDate());
        checkpoints.add(startCheckpoint);

        checkpoints.addAll(checkpointsFromRepo.stream()
                .map(checkpoint -> modelMapper.map(checkpoint, CheckpointInfoDto.class))
                .collect(Collectors.toList())
        );
        checkpoints.sort(Comparator.comparing(CheckpointInfoDto::getRequiredArrivalDate));

        CheckpointInfoDto endCheckpoint = new CheckpointInfoDto();
        endCheckpoint.setAddress(waybill.getEndPoint());
        endCheckpoint.setCheckpointDate(waybill.getFactualEndDate());
        endCheckpoint.setRequiredArrivalDate(waybill.getEndDate());
        checkpoints.add(endCheckpoint);

        return checkpoints;
    }

    @Override
    public Page<WaybillInfoDto> find(Long userId, Long clientId, WaybillFilterDto filter, Pageable pageable) {
        if (pageable.getPageSize() < minPageSize || pageable.getPageSize() > maxPageSize) {
            pageable = PageRequest.of(pageable.getPageNumber(), defaultPageSize, Sort.Direction.ASC, "id");
        }
        Page<Waybill> page = waybillRepository.findAll(buildWhere(userId, clientId, filter), pageable);

        return mapWaybillPageToWaybillPageDto(page);
    }

    @Override
    public List<Waybill> findByClientId(long clientId) {
        return waybillRepository.findByClientId(clientId);
    }

    @Override
    public void reachCheckpoint(long waybillId, long userId) {
        validateExists(waybillId);

        Waybill waybill = waybillRepository.getOne(waybillId);

        validateReachCheckpoint(waybill, userId);

        if (waybill.getFactualStartDate() == null) {
            waybill.setFactualStartDate(LocalDateTime.now());
            return;
        }

        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.ASC, "requiredArrivalDate");
        List<Checkpoint> checkpoints = checkpointRepository.getNextWaybillCheckpoint(waybill.getId(), pageable);
        if (checkpoints.size() != 0) {
            checkpointRepository.reachCheckpoint(checkpoints.get(0).getId());
            return;
        }

        if (waybill.getFactualEndDate() == null) {
            waybill.setFactualEndDate(LocalDateTime.now());
            waybill.setStatus(CarriageStatus.FINISHED_CARRIAGE);
            waybill.getInvoice().setStatus(InvoiceStatus.DELIVERED);

            for (Product product : waybill.getInvoice().getProducts()) {
                product.setStatus(ProductStatus.DELIVERED);
            }

            return;
        }

        throw new CheckpointsNotFoundException("Waybill with id %s hasn't checkpoints", waybillId);
    }

    @Override
    public long createWaybill(WaybillAddDto waybillAddDto) {
        validateAdd(waybillAddDto);

        Waybill waybill = modelMapper.map(waybillAddDto, Waybill.class);

        Invoice invoice = invoiceRepository.getOne(waybillAddDto.getInvoiceId());
        invoice.setVerifiedDate(LocalDate.now());
        User verifier = new User();
        verifier.setId(waybillAddDto.getVerifierId());
        invoice.setVerifier(verifier);
        invoice.setStatus(InvoiceStatus.VERIFICATION_COMPLETE);

        ProductOwner productOwner = productOwnerRepository.getOne(invoice.getProductOwner().getId());
        String startPoint = productOwner.getAddress();

        Storage storage = storageRepository.getOne(invoice.getStorage().getId());
        String endPoint = storage.getAddress();

        waybill.setStartPoint(startPoint);
        waybill.setEndPoint(endPoint);
        waybill.setStartDate(LocalDateTime.now());
        waybill.setStatus(CarriageStatus.STARTED_CARRIAGE);

        waybillRepository.save(waybill);

        if (waybillAddDto.getCheckpoints() != null) {
            List<Checkpoint> checkpoints = waybillAddDto.getCheckpoints().stream()
                    .map(checkpointAddDto -> modelMapper.map(checkpointAddDto, Checkpoint.class))
                    .collect(Collectors.toList());

            for (Checkpoint checkpoint : checkpoints) {
                checkpoint.setWaybillId(waybill.getId());
                checkpointRepository.save(checkpoint);
            }
        }

        return waybill.getId();
    }

    @Override
    public void deleteWaybill(long[] ids) {
        for (long id : ids) {
            validateDelete(id);
            waybillRepository.delete(id);
        }
    }

    private BooleanBuilder buildWhere(Long userId, Long clientId, WaybillFilterDto filter) {
        BooleanBuilder whereBuilder = new BooleanBuilder(QWaybill.waybill.deleteDate.isNull());
        if (userId != null) {
            whereBuilder.and(QWaybill.waybill.invoice.driver.id.eq(userId));
        }

        if (clientId != null) {
            whereBuilder.and(QWaybill.waybill.invoice.creator.clientId.eq(clientId));
        }

        if (filter.getCarriageStatuses() != null && filter.getCarriageStatuses().size() != 0) {
            whereBuilder.and(QWaybill.waybill.status.in(filter.getCarriageStatuses()));
        }

        return whereBuilder;
    }

    private void validateDelete(long id) {
        if (!waybillRepository.existsById(id)) {
            throw new WaybillNotFoundException("Waybill with id %s doesn't exists", id);
        }
    }

    private Page<WaybillInfoDto> mapWaybillPageToWaybillPageDto(Page<Waybill> page) {
        Page<WaybillInfoDto> map = page.map(waybill -> modelMapper.map(waybill, WaybillInfoDto.class));

        for (WaybillInfoDto waybillInfoDto : map) {
            long carId = waybillInfoDto.getCarId();
            Car car = carRepository.getOne(carId);
            waybillInfoDto.setCarNumber(car.getNumber());

            long invoiceId = waybillInfoDto.getInvoiceId();
            Invoice invoice = invoiceRepository.getOne(invoiceId);

            long productOwnerId = invoice.getProductOwner().getId();
            ProductOwner productOwner = productOwnerRepository.getOne(productOwnerId);
            waybillInfoDto.setSender(productOwner.getName());

            long storageId = invoice.getStorage().getId();
            Storage storage = storageRepository.getOne(storageId);
            waybillInfoDto.setReceiver(storage.getName());
        }

        return map;
    }

    private void validateAdd(WaybillAddDto waybill) {
        if (!invoiceRepository.existsById(waybill.getInvoiceId())) {
            throw new InvoiceNotFoundException("Invoice with id %s doesn't exists", waybill.getInvoiceId());
        }

        if (waybillRepository.existsByInvoiceId(waybill.getInvoiceId())) {
            throw new DuplicatedWaybillByInvoiceIdException(
                    "Waybill with invoice id %s is already exists", waybill.getInvoiceId()
            );
        }

        Invoice invoice = invoiceRepository.getOne(waybill.getInvoiceId());
        int load = 0;
        for (Product product : invoice.getProducts()) {
            load += product.getAmount();
        }

        if (!carRepository.existsByIdAndLoadCapacity(waybill.getCarId(), load)) {
            throw new CarNotFoundException(
                    "Car with id %s and load capacity %s doesn't exists",
                    waybill.getCarId(), load
            );
        }

        if (!userService.existsByIdAndManagerRole(waybill.getVerifierId())) {
            throw new UserNotFoundException("Manager with id %s doesn't exists", waybill.getVerifierId());
        }
    }

    private void validateExists(long id) {
        if (!waybillRepository.existsById(id)) {
            throw new WaybillNotFoundException("Waybill with id %s doesn't exists", id);
        }
    }

    private void validateReachCheckpoint(Waybill waybill, long userId) {
        if (waybill.getInvoice().getDriver().getId() != userId) {
            throw new ReachCheckpointAccessException(
                    "User with id %s cannot reach checkpoint of waybill with id %s",
                    userId, waybill.getId()
            );
        }
    }
}
