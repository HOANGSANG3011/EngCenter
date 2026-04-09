package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import com.hutech.TrungTamTiengAnh.repository.DangKyRepository;
import com.hutech.TrungTamTiengAnh.repository.UserRepository;
import com.hutech.TrungTamTiengAnh.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private LopHocRepository lopHocRepository;

    @Autowired
    private DangKyRepository dangKyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    // Trang chủ
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalClasses", lopHocRepository.count());
        model.addAttribute("totalRegistrations", dangKyRepository.count());
        model.addAttribute("totalStudents", userRepository.countByRole("STUDENT"));
        model.addAttribute("totalTeachers", teacherRepository.count());
        return "index";
    }
}