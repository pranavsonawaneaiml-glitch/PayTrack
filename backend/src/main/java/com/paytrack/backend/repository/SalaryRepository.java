package com.paytrack.backend.repository;

import com.paytrack.backend.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {

    List<Salary> findByEmployeeId(Long employeeId);

    List<Salary> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

    List<Salary> findByEmployeeIdAndPaymentDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT s FROM Salary s WHERE s.employee.id = :employeeId ORDER BY s.paymentDate DESC")
    List<Salary> findByEmployeeIdOrderByPaymentDateDesc(@Param("employeeId") Long employeeId);

    @Query("SELECT SUM(s.netPay) FROM Salary s WHERE s.paymentDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalPayrollForPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT AVG(s.netPay) FROM Salary s WHERE s.employee.id = :employeeId")
    java.math.BigDecimal getAverageSalaryForEmployee(@Param("employeeId") Long employeeId);

    @Query("SELECT s FROM Salary s WHERE s.paymentStatus = 'PENDING' ORDER BY s.paymentDate ASC")
    List<Salary> findPendingPayments();

    boolean existsByEmployeeIdAndPayrollYearAndPayrollMonth(Long employeeId, int year, int month);

    boolean existsByPayrollYearAndPayrollMonth(int year, int month);

    List<Salary> findByPayrollYearAndPayrollMonth(int year, int month);
}