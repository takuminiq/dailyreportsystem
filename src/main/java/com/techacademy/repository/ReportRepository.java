package com.techacademy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    List<Report> findByEmployeeCode(String employeeCode);

    // 従業員に紐づく日報を取得するクエリメソッド
    List<Report> findByEmployee(Employee employee);
}
