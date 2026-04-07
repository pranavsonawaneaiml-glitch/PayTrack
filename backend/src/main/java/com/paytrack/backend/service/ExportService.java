package com.paytrack.backend.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.paytrack.backend.model.Salary;
import com.paytrack.backend.repository.SalaryRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    private final SalaryRepository salaryRepository;

    public ExportService(SalaryRepository salaryRepository) {
        this.salaryRepository = salaryRepository;
    }

    public byte[] generatePayslipPdf(Long salaryId) throws IOException {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("Salary record not found"));

        String htmlContent = generatePayslipHtml(salary);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, "/");
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        }
    }

    private String generatePayslipHtml(Salary salary) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; padding: 40px; }
                        .header { text-align: center; margin-bottom: 30px; border-bottom: 2px solid #333; padding-bottom: 10px; }
                        .title { font-size: 24px; font-weight: bold; }
                        .info { margin: 20px 0; }
                        .row { display: flex; margin: 8px 0; }
                        .label { width: 200px; font-weight: bold; }
                        .amount { text-align: right; }
                        .total { border-top: 2px solid #333; padding-top: 10px; margin-top: 10px; font-size: 18px; font-weight: bold; }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <div class="title">PayTrack Payslip</div>
                        <div>%s %d</div>
                    </div>
                    <div class="info">
                        <div class="row"><div class="label">Employee:</div><div>%s %s</div></div>
                        <div class="row"><div class="label">Department:</div><div>%s</div></div>
                        <div class="row"><div class="label">Position:</div><div>%s</div></div>
                        <div class="row"><div class="label">Payment Date:</div><div>%s</div></div>
                    </div>
                    <div style="margin-top: 40px;">
                        <div class="row"><div class="label">Gross Salary:</div><div class="amount">$ %.2f</div></div>
                        <div class="row"><div class="label">Income Tax:</div><div class="amount">$ %.2f</div></div>
                        <div class="row total"><div class="label">Net Pay:</div><div class="amount">$ %.2f</div></div>
                    </div>
                </body>
                </html>
                """.formatted(
                salary.getPayrollMonth(), salary.getPayrollYear(),
                salary.getEmployee().getFirstName(), salary.getEmployee().getLastName(),
                salary.getEmployee().getDepartment(),
                salary.getEmployee().getPosition(),
                salary.getPaymentDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                salary.getGrossAmount(), salary.getTaxDeduction(), salary.getNetAmount()
        );
    }

    public byte[] exportPayrollCsv(int year, int month) throws IOException {
        List<Salary> salaries = salaryRepository.findByPayrollYearAndPayrollMonth(year, month);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(os);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                     "Employee ID", "First Name", "Last Name", "Department", "Gross Amount",
                     "Tax Deduction", "Net Amount", "Status", "Payment Date"
             ))) {

            for (Salary salary : salaries) {
                csvPrinter.printRecord(
                        salary.getEmployee().getId(),
                        salary.getEmployee().getFirstName(),
                        salary.getEmployee().getLastName(),
                        salary.getEmployee().getDepartment(),
                        salary.getGrossAmount(),
                        salary.getTaxDeduction(),
                        salary.getNetAmount(),
                        salary.getStatus(),
                        salary.getPaymentDate()
                );
            }

            csvPrinter.flush();
            return os.toByteArray();
        }
    }
}
