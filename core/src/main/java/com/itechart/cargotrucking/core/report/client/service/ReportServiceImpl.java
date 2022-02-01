package com.itechart.cargotrucking.core.report.client.service;

import com.itechart.cargotrucking.core.product.Product;
import com.itechart.cargotrucking.core.product.ProductStatus;
import com.itechart.cargotrucking.core.product.repository.ProductRepository;
import com.itechart.cargotrucking.core.report.client.ClientReport;
import com.itechart.cargotrucking.core.report.client.dto.ClientReportCreateDto;
import com.itechart.cargotrucking.core.report.client.dto.DriverStatistic;
import com.itechart.cargotrucking.core.report.client.dto.WeekCompanyStatistic;
import com.itechart.cargotrucking.core.report.exception.InvalidDateIntervalException;
import com.itechart.cargotrucking.core.user.User;
import com.itechart.cargotrucking.core.way.Waybill;
import com.itechart.cargotrucking.core.way.repository.WaybillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Validated
public class ReportServiceImpl implements ReportService {
    private static final int WEEK_DAYS_COUNT = 7;

    @Value("${report.fuel-cost}")
    private double fuelCost;

    @Value("${report.product-cost}")
    private double productCost;

    @Value("${report.product-lost-fine}")
    private double productLostFine;

    @Value("${report.kilometer-cost}")
    private double kilometerCost;

    @Value("${report.day-fine}")
    private double dayFine;

    private WaybillRepository waybillRepository;
    private ProductRepository productRepository;

    @Autowired
    public ReportServiceImpl(WaybillRepository waybillRepository, ProductRepository productRepository) {
        this.waybillRepository = waybillRepository;
        this.productRepository = productRepository;
    }

    @Override
    public ClientReport createClientReport(long clientId, ClientReportCreateDto createDto) {
        validateCreateClientReport(createDto);

        List<WeekCompanyStatistic> statistics = new ArrayList<>();

        List<DriverStatistic> drivers = new ArrayList<>();
        List<Long> driverIds = new ArrayList<>();

        while (createDto.getInitialDate().isBefore(createDto.getFinalDate())
                || createDto.getInitialDate().isEqual(createDto.getFinalDate())
        ) {
            double income = 0;
            double consumption = 0;

            int daysInterval = WEEK_DAYS_COUNT - createDto.getInitialDate().getDayOfWeek().getValue();
            daysInterval = daysInterval == 6 ? 7 : daysInterval + 1;

            if (createDto.getInitialDate().plusDays(daysInterval).isAfter(createDto.getFinalDate())) {
                daysInterval = createDto.getFinalDate().getDayOfWeek().getValue()
                        - createDto.getInitialDate().getDayOfWeek().getValue() + 1;
            }

            List<Waybill> waybills = waybillRepository.findByClientIdAndDate(
                    clientId, createDto.getInitialDate().atStartOfDay(),
                    createDto.getInitialDate().plusDays(daysInterval - 1).atTime(LocalTime.MAX)
            );

            for (Waybill waybill : waybills) {
                User driver = waybill.getInvoice().getDriver();
                if (!driverIds.contains(driver.getId())) {
                    driverIds.add(driver.getId());
                    String fullName = driver.getSurname();
                    fullName += StringUtils.isEmpty(driver.getName()) ? "" : ' ' + driver.getName();
                    fullName += StringUtils.isEmpty(driver.getPatronymic()) ? "" : ' ' + driver.getPatronymic();
                    drivers.add(new DriverStatistic(fullName, 0));
                }

                List<Product> products = productRepository.findByInvoiceId(waybill.getInvoice().getId());

                if (waybill.getFactualEndDate().isAfter(waybill.getEndDate())) {
                    long fineDays = waybill.getEndDate().until(waybill.getFactualEndDate(), ChronoUnit.DAYS);
                    consumption += fineDays * dayFine;
                }

                income += kilometerCost * waybill.getDistance() / 1000;
                consumption += fuelCost * waybill.getDistance() / 1000 * waybill.getCar().getFuelConsumption().doubleValue();
                for (Product product : products) {
                    if (product.getStatus() == ProductStatus.LOST) {
                        consumption += productLostFine * product.getAmount();
                    } else {
                        income += productCost * product.getAmount();
                    }
                }

                for (int i = 0; i < driverIds.size(); i++) {
                    if (driverIds.get(i).equals(driver.getId())) {
                        DriverStatistic driverStatistic = drivers.get(i);
                        driverStatistic.setProfit(driverStatistic.getProfit() + income - consumption);
                        break;
                    }
                }
            }

            WeekCompanyStatistic statistic = new WeekCompanyStatistic(
                    createDto.getInitialDate(),
                    createDto.getInitialDate().plusDays(daysInterval - 1),
                    consumption, income
            );
            statistics.add(statistic);

            createDto.setInitialDateFromDate(createDto.getInitialDate().plusDays(daysInterval));
        }

        drivers.sort(Comparator.comparingDouble(DriverStatistic::getProfit).reversed());
        while (drivers.size() > 5) {
            drivers.remove(drivers.size() - 1);
        }

        return new ClientReport(statistics, drivers);
    }

    private void validateCreateClientReport(ClientReportCreateDto createDto) {
        if (createDto.getInitialDate().isAfter(createDto.getFinalDate())) {
            throw new InvalidDateIntervalException();
        }
    }
}
