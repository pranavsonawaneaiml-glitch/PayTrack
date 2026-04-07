package com.paytrack.backend.controller;

import com.paytrack.backend.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/export")
@Tag(name = "Export", description = "APIs for exporting payslips and reports")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/payslip/{salaryId}/pdf")
    @Operation(summary = "Download payslip PDF", description = "Generate and download individual payslip as PDF")
    public ResponseEntity<byte[]> downloadPayslipPdf(@PathVariable Long salaryId) throws IOException {
        byte[] pdfContent = exportService.generatePayslipPdf(salaryId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payslip-" + salaryId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    @GetMapping("/payroll/{year}/{month}/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Export payroll CSV", description = "Export complete payroll batch as CSV file")
    public ResponseEntity<byte[]> exportPayrollCsv(@PathVariable int year, @PathVariable int month) throws IOException {
        byte[] csvContent = exportService.exportPayrollCsv(year, month);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payroll-" + year + "-" + month + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvContent);
    }
}
