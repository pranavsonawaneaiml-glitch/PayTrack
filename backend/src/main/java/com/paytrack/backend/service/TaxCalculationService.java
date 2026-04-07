package com.paytrack.backend.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TaxCalculationService {

    public BigDecimal calculateIncomeTax(BigDecimal grossSalary) {
        BigDecimal annualSalary = grossSalary.multiply(BigDecimal.valueOf(12));
        BigDecimal tax = BigDecimal.ZERO;

        if (annualSalary.compareTo(BigDecimal.valueOf(10000)) <= 0) {
            tax = BigDecimal.ZERO;
        } else if (annualSalary.compareTo(BigDecimal.valueOf(40000)) <= 0) {
            tax = annualSalary.subtract(BigDecimal.valueOf(10000)).multiply(BigDecimal.valueOf(0.10));
        } else if (annualSalary.compareTo(BigDecimal.valueOf(100000)) <= 0) {
            tax = BigDecimal.valueOf(3000).add(
                    annualSalary.subtract(BigDecimal.valueOf(40000)).multiply(BigDecimal.valueOf(0.20))
            );
        } else {
            tax = BigDecimal.valueOf(15000).add(
                    annualSalary.subtract(BigDecimal.valueOf(100000)).multiply(BigDecimal.valueOf(0.30))
            );
        }

        return tax.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
    }
}
