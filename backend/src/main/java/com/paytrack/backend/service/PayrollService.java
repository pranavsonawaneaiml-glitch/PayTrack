package com.paytrack.backend.service;

import com.paytrack.backend.model.Employee;
import com.paytrack.backend.model.Salary;
import com.paytrack.backend.repository.EmployeeRepository;
import com.paytrack.backend.repository.SalaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayrollService {

    private final EmployeeRepository employeeRepository;
    private final SalaryRepository salaryRepository;
    private final TaxCalculationService taxCalculationService;

    public PayrollService(EmployeeRepository employeeRepository, SalaryRepository salaryRepository,
                          TaxCalculationService taxCalculationService) {
        this.employeeRepository = employeeRepository;
        this.salaryRepository = salaryRepository;
        this.taxCalculationService = taxCalculationService;
    }

    @Transactional
    public List<Salary> generatePayrollBatch(int year, int month) {
        YearMonth payrollPeriod = YearMonth.of(year, month);
        List<Employee> activeEmployees = employeeRepository.findByIsActiveTrue();
        List<Salary> generatedSalaries = new ArrayList<>();

        for (Employee employee : activeEmployees) {
            if (salaryRepository.existsByEmployeeIdAndPayrollYearAndPayrollMonth(employee.getId(), year, month)) {
                continue;
            }

            BigDecimal grossSalary = employee.getMonthlySalary();
            BigDecimal taxDeduction = taxCalculationService.calculateIncomeTax(grossSalary);
            BigDecimal netSalary = grossSalary.subtract(taxDeduction);

            Salary salary = new Salary();
            salary.setEmployee(employee);
            salary.setPayrollYear(year);
            salary.setPayrollMonth(month);
            salary.setGrossAmount(grossSalary);
            salary.setTaxDeduction(taxDeduction);
            salary.setNetAmount(netSalary);
            salary.setPaymentDate(payrollPeriod.atEndOfMonth());
            salary.setStatus(Salary.PaymentStatus.PENDING);
            salary.setPayPeriodStart(payrollPeriod.atDay(1));
            salary.setPayPeriodEnd(payrollPeriod.atEndOfMonth());
            salary.setBaseSalary(grossSalary);

            generatedSalaries.add(salaryRepository.save(salary));
        }

        return generatedSalaries;
    }

    public boolean isPayrollGenerated(int year, int month) {
        return salaryRepository.existsByPayrollYearAndPayrollMonth(year, month);
    }
}
