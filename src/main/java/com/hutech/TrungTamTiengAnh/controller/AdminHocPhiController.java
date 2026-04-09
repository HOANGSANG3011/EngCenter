package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.DangKy;
import com.hutech.TrungTamTiengAnh.entity.LopHoc;
import com.hutech.TrungTamTiengAnh.repository.DangKyRepository;
import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/hocphi")
public class AdminHocPhiController {

    @Autowired
    private DangKyRepository dangKyRepository;

    @Autowired
    private LopHocRepository lopHocRepository;

    // 📌 DANH SÁCH + FILTER + CHART
    @GetMapping
    public String list(
            @RequestParam(required = false) Long lopId,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<DangKy> list = dangKyRepository.findAll();

        // 🔍 LỌC THEO LỚP
        if (lopId != null) {
            list = list.stream()
                    .filter(d -> d.getLopHoc().getId().equals(lopId))
                    .toList();
        }

        // 🔍 LỌC THEO TÊN
        if (keyword != null && !keyword.isEmpty()) {
            list = list.stream()
                    .filter(d -> d.getStudent().getUsername().toLowerCase()
                            .contains(keyword.toLowerCase()))
                    .toList();
        }

        // 🔥 ĐÃ ĐÓNG
        List<DangKy> daDong = list.stream()
                .filter(d -> "DA_DONG".equals(d.getThanhToan()))
                .toList();

        // 💰 TỔNG TIỀN
        double tongTien = daDong.stream()
                .mapToDouble(DangKy::getHocPhi)
                .sum();

        // 📊 DOANH THU THEO THÁNG
        double[] thangData = new double[12];

        for (DangKy d : daDong) {
            int month = d.getNgayDangKy().getMonthValue();
            thangData[month - 1] += d.getHocPhi();
        }

        model.addAttribute("list", list);
        model.addAttribute("tongTien", tongTien);
        model.addAttribute("thangData", thangData);
        model.addAttribute("lopHocList", lopHocRepository.findAll());

        return "admin/hocphi/list";
    }

    // 📌 FORM EDIT
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {

        DangKy dk = dangKyRepository.findById(id).orElse(null);

        model.addAttribute("dk", dk);

        return "admin/hocphi/form";
    }

    // 📌 SAVE
    @PostMapping("/save")
    public String save(@ModelAttribute DangKy dk) {

        DangKy old = dangKyRepository.findById(dk.getId()).orElse(null);

        if (old != null) {
            old.setHoTenThanhToan(dk.getHoTenThanhToan());
            old.setPhuongThuc(dk.getPhuongThuc());
            old.setMaGiaoDich(dk.getMaGiaoDich());
            old.setThanhToan(dk.getThanhToan());

            dangKyRepository.save(old);
        }

        return "redirect:/admin/hocphi";
    }

    // 📌 EXPORT EXCEL
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {

        List<DangKy> list = dangKyRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("HocPhi");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Học viên");
        header.createCell(1).setCellValue("Lớp");
        header.createCell(2).setCellValue("Số tiền");
        header.createCell(3).setCellValue("Trạng thái");

        int rowNum = 1;

        for (DangKy dk : list) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(dk.getStudent().getUsername());
            row.createCell(1).setCellValue(dk.getLopHoc().getTenLop());
            row.createCell(2).setCellValue(dk.getHocPhi());
            row.createCell(3).setCellValue(dk.getThanhToan());
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=hocphi.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}