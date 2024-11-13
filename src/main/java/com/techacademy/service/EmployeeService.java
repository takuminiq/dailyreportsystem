package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.EmployeeRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ReportService reportService; // ReportServiceをインジェクト

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 従業員保存
    @Transactional
    public ErrorKinds save(Employee employee) {
        // パスワードチェック
        ErrorKinds result = employeePasswordCheck(employee);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }

        // 従業員番号重複チェック
        if (findByCode(employee.getCode()) != null) {
            return ErrorKinds.DUPLICATE_ERROR;
        }

        employee.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);

        employeeRepository.save(employee);
        return ErrorKinds.SUCCESS;
    }

    // 従業員更新
    @Transactional
    public ErrorKinds update(Employee employee) {
        // 既存のデータを取得
        Employee existingEmployee = findByCode(employee.getCode());
        if (existingEmployee == null) {
            return ErrorKinds.DUPLICATE_ERROR;
        }

        // パスワードが入力されている場合のみチェックを行う
        if (!"".equals(employee.getPassword())) {
            ErrorKinds result = employeePasswordCheck(employee);
            if (ErrorKinds.CHECK_OK != result) {
                return result;
            }
        } else {
            // パスワードが空の場合は既存のパスワードを使用
            employee.setPassword(existingEmployee.getPassword());
        }

        // 既存の値を保持
        employee.setDeleteFlg(existingEmployee.getDeleteFlg());
        employee.setCreatedAt(existingEmployee.getCreatedAt());
        employee.setUpdatedAt(LocalDateTime.now());

        // 更新を実行
        employeeRepository.save(employee);
        return ErrorKinds.SUCCESS;
    }

    // 従業員削除
    @Transactional
    public ErrorKinds delete(String code, UserDetail userDetail) {
        // 自分を削除しようとした場合はエラーメッセージを表示
        if (code.equals(userDetail.getEmployee().getCode())) {
            return ErrorKinds.LOGINCHECK_ERROR;
        }
        Employee employee = findByCode(code);
        if (employee == null) {
            return ErrorKinds.DUPLICATE_ERROR; // 従業員が見つからない場合のエラーハンドリング
        }

        // 削除対象の従業員に紐づいている日報情報の削除
        List<Report> reportList = reportService.findByEmployee(employee);
        for (Report report : reportList) {
            reportService.delete(report.getId());
        }

        LocalDateTime now = LocalDateTime.now();
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(true);

        employeeRepository.save(employee);
        return ErrorKinds.SUCCESS;
    }

    // 従業員一覧表示処理
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    // 1件を検索
    public Employee findByCode(String code) {
        // findByIdで検索
        Optional<Employee> option = employeeRepository.findById(code);
        // 取得できなかった場合はnullを返す
        Employee employee = option.orElse(null);
        return employee;
    }

    // パスワードチェック
    private ErrorKinds employeePasswordCheck(Employee employee) {
        // 半角英数字チェック
        if (!employee.getPassword().matches("^[A-Za-z0-9]+$")) {
            return ErrorKinds.HALFSIZE_ERROR;
        }

        // 8文字～16文字チェック
        if (employee.getPassword().length() < 8 || employee.getPassword().length() > 16) {
            return ErrorKinds.RANGECHECK_ERROR;
        }

        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        return ErrorKinds.CHECK_OK;
    }
}

