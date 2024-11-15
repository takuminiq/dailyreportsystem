package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    List<Report> findByEmployeeCode(String employeeCode);

    // 従業員に紐づく日報を取得するクエリメソッド
    List<Report> findByEmployee(Employee employee);


    // 従業員と日付に基づいて日報が存在するか確認するメソッド
    boolean existsByEmployeeAndReportDate(Employee employee, LocalDate reportDate);

    boolean existsByEmployeeAndReportDateAndIdNot(Employee employee, LocalDate reportDate, Integer id);

}