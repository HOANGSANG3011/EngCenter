package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.LopHoc;
import com.hutech.TrungTamTiengAnh.entity.DanhGia;
import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import com.hutech.TrungTamTiengAnh.repository.DanhGiaRepository;

import com.hutech.TrungTamTiengAnh.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class TeacherController {

    @Autowired
    private LopHocRepository lopHocRepository;

    @Autowired
    private DanhGiaRepository danhGiaRepository;

    @GetMapping("/admin/teacher")
    public String teacherList(Model model) {

        List<LopHoc> list = lopHocRepository.findAll();

        Map<String, Map<String, Object>> teachers = new HashMap<>();

        for (LopHoc lop : list) {

            String tenGV = (lop.getGiaoVien() != null) ? lop.getGiaoVien().getTen() : "Unknown";

            if (!teachers.containsKey(tenGV)) {

                Map<String, Object> data = new HashMap<>();

                data.put("ten", tenGV);
                data.put("avatar", lop.getHinhAnh());
                data.put("lichHoc", lop.getNgayHoc());
                data.put("classes", 1);
                data.put("rating", 4 + new Random().nextInt(2));

                teachers.put(tenGV, data);

            } else {

                int count = (int) teachers.get(tenGV).get("classes");
                teachers.get(tenGV).put("classes", count + 1);
            }
        }

        model.addAttribute("teachers", teachers.values());

        return "admin/teacher/list";
    }

    @GetMapping("/admin/teacher/{ten}")
    public String teacherDetail(@PathVariable String ten, Model model) {

        List<LopHoc> classes = lopHocRepository.findAll();

        List<LopHoc> teacherClasses = classes.stream()
                .filter(c -> c.getGiaoVien() != null && c.getGiaoVien().getTen().equals(ten))
                .toList();

        List<DanhGia> reviews = danhGiaRepository.findByTenGiaoVien(ten);

        double avgRating = 0;

        if (!reviews.isEmpty()) {
            avgRating = reviews.stream()
                    .mapToInt(DanhGia::getRating)
                    .average()
                    .orElse(0);
        }

        model.addAttribute("teacherName", ten);
        model.addAttribute("classes", teacherClasses);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", avgRating);

        return "admin/teacher/detail";
    }

}