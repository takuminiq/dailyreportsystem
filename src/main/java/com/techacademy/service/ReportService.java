package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 自分が登録した日報を取得
    public List<Report> findByEmployeeCode(String employeeCode) {
        return reportRepository.findByEmployeeCode(employeeCode);
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {
        // タイトルと内容の必須チェックはバリデーションで実施

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報更新
    @Transactional
    public ErrorKinds update(Report report) {
        // 既存のデータを取得
        Report existingReport = findById(report.getId());
        if (existingReport == null) {
            return ErrorKinds.DUPLICATE_ERROR;
        }

        // 既存の値を保持
        report.setDeleteFlg(existingReport.getDeleteFlg());
        report.setCreatedAt(existingReport.getCreatedAt());
        report.setUpdatedAt(LocalDateTime.now());
        report.setEmployee(existingReport.getEmployee()); // 作成者は変更不可

        // 更新を実行
        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報削除
    @Transactional
    public ErrorKinds delete(Integer id) {
        Report report = findById(id);
        if (report == null) {
            return ErrorKinds.DUPLICATE_ERROR;
        }

        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    public List<Report> findByEmployeeCode1(String employeeCode) {
        return reportRepository.findByEmployeeCode(employeeCode);
    }
}
