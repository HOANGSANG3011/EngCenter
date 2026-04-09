package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.Teacher;
import com.hutech.TrungTamTiengAnh.entity.LopHoc;
import com.hutech.TrungTamTiengAnh.entity.Schedule;
import com.hutech.TrungTamTiengAnh.repository.TeacherRepository;
import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import com.hutech.TrungTamTiengAnh.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/teacher-manage")
public class AdminTeacherController {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private LopHocRepository lopHocRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    // Danh sách
    @GetMapping
    public String list(Model model) {
        List<Teacher> list = teacherRepository.findAll();
        model.addAttribute("teachers", list);
        return "admin/teacher/manage";
    }

    // Form thêm
    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("teacher", new Teacher());
        return "admin/teacher/form";
    }

    // Lưu
    @PostMapping("/save")
    public String save(@ModelAttribute Teacher teacher, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        
        if (teacher.getId() == null) {
            // Check for duplicates
            Long count = teacherRepository.countByTen(teacher.getTen());
            if (count > 0) {
                redirectAttributes.addFlashAttribute("error", "Tên giáo viên đã tồn tại: " + teacher.getTen() + ". Vui lòng nhập tên khác.");
                return "redirect:/admin/teacher-manage/add";
            }
        }
        
        teacherRepository.save(teacher);
        redirectAttributes.addFlashAttribute("successAddedId", teacher.getId()); // Trigger glowing effect in view based on ID
        return "redirect:/admin/teacher-manage";
    }

    // Sửa
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Teacher t = teacherRepository.findById(id).orElse(null);
        model.addAttribute("teacher", t);
        return "admin/teacher/form";
    }

    // Xóa (safe: null-out all FK references first)
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Teacher teacher = teacherRepository.findById(id).orElse(null);
        if (teacher == null) {
            return "redirect:/admin/teacher-manage";
        }

        // Detach from LopHoc
        List<LopHoc> lopHocs = lopHocRepository.findAll();
        for (LopHoc lop : lopHocs) {
            if (lop.getGiaoVien() != null && lop.getGiaoVien().getId().equals(id)) {
                lop.setGiaoVien(null);
                lopHocRepository.save(lop);
            }
        }

        // Detach from Schedule
        List<Schedule> schedules = scheduleRepository.findAll();
        for (Schedule s : schedules) {
            if (s.getGiaoVien() != null && s.getGiaoVien().getId().equals(id)) {
                s.setGiaoVien(null);
                scheduleRepository.save(s);
            }
        }

        teacherRepository.deleteById(id);
        return "redirect:/admin/teacher-manage";
    }
}