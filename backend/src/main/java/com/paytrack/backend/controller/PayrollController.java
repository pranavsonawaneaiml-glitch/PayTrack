package com.paytrack.backend.controller;

import com.paytrack.backend.model.Salary;
import com.paytrack.backend.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@Tag(name = "Payroll Processing", description = "APIs for payroll batch generation")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/generate/{year}/{month}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Generate payroll batch", description = "Generate salary records for all active employees for given month")
    public ResponseEntity<List<Salary>> generatePayroll(@PathVariable int year, @PathVariable int month) {
        List<Salary> salaries = payrollService.generatePayrollBatch(year, month);
        return ResponseEntity.ok(salaries);
    }

    @GetMapping("/status/{year}/{month}")
    @Operation(summary = "Check payroll status", description = "Check if payroll has been generated for given month")
    public ResponseEntity<Boolean> checkPayrollStatus(@PathVariable int year, @PathVariable int month) {
        return ResponseEntity.ok(payrollService.isPayrollGenerated(year, month));
    }
}
