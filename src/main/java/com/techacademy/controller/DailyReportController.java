package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Report;
import com.techacademy.entity.Employee;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class DailyReportController {

    private final ReportService reportService;

    @Autowired
    public DailyReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {
        if (userDetail == null) {
            return "redirect:/login"; // 未ログインの場合はログイン画面にリダイレクト
        }

        List<Report> reportList;

        // ユーザーの役割に応じて日報を取得
        if (userDetail.getEmployee().getRole() == Employee.Role.ADMIN) {
            // 管理者の場合、全ての日報を表示
            reportList = reportService.findAll();
        } else {
            // 一般ユーザーの場合、自分が登録した日報のみ表示
            reportList = reportService.findByEmployeeCode(userDetail.getEmployee().getCode());
        }

        // reportListがnullの場合は空のリストを設定
        if (reportList == null) {
            reportList = List.of(); // 空のリストを設定
        }

        // 削除フラグが立っていない日報のみをフィルタリング
        reportList = reportList.stream()
                .filter(report -> !report.getDeleteFlg())
                .toList();

        model.addAttribute("listSize", reportList.size());
        model.addAttribute("reportList", reportList);
        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping("/{id}/")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping("/add")
    public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        report.setEmployee(userDetail.getEmployee());
        model.addAttribute("employeeName", userDetail.getEmployee().getName());
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping("/add")
    public String add(@Validated @ModelAttribute Report report, BindingResult res, Model model, @AuthenticationPrincipal UserDetail userDetail) {
        // タイトルの桁数チェック
        if (report.getTitle() != null && report.getTitle().length() > 100) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.TITLE_LENGTH_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.TITLE_LENGTH_ERROR));
            return create(report, userDetail, model);
        }

        // 内容の桁数チェック
        if (report.getContent() != null && report.getContent().length() > 600) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.CONTENT_LENGTH_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.CONTENT_LENGTH_ERROR));
            return create(report, userDetail, model);
        }
        if (res.hasErrors()) {
            return create(report, userDetail, model);
        }

        report.setEmployee(userDetail.getEmployee()); // ログインユーザーを設定
        ErrorKinds result = reportService.save(report);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(report, userDetail, model);
        }

        return "redirect:/reports";
    }

    // 日報更新画面
    @GetMapping("/{id}/update")
    public String showUpdate(@PathVariable Integer id, Model model) {
        model.addAttribute("report", reportService.findById(id));
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id, @Validated @ModelAttribute Report report, BindingResult res, Model model) {
        // タイトルの桁数チェック
        if (report.getTitle() != null && report.getTitle().length() > 100) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.TITLE_LENGTH_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.TITLE_LENGTH_ERROR));
            return showUpdate(id, model);
        }

        // 内容の桁数チェック
        if (report.getContent() != null && report.getContent().length() > 600) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.CONTENT_LENGTH_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.CONTENT_LENGTH_ERROR));
            return showUpdate(id, model);
        }
        if (res.hasErrors()) {
            return showUpdate(id, model);
        }

        if (res.hasErrors()) {
            model.addAttribute("report", reportService.findById(id));
            return showUpdate(id, model);
        }

        report.setId(id); // IDを設定
        ErrorKinds result = reportService.update(report);

        if (ErrorKinds.SUCCESS != result) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findById(id));
            return showUpdate(id, model);
        }

        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, Model model) {
        ErrorKinds result = reportService.delete(id);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findById(id));
            return detail(id, model);
        }

        return "redirect:/reports";
    }
}
