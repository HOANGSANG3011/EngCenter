package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.LopHoc;
import com.hutech.TrungTamTiengAnh.entity.Teacher;
import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import com.hutech.TrungTamTiengAnh.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Controller
@RequestMapping("/admin/lophoc")
public class LopHocController {

    @Autowired
    private LopHocRepository lopHocRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private com.hutech.TrungTamTiengAnh.repository.DangKyRepository dangKyRepository;

    // 📌 Hiển thị danh sách
    @GetMapping
    public String list(Model model) {
        List<LopHoc> list = lopHocRepository.findAll();
        model.addAttribute("list", list);
        return "admin/lophoc/list";
    }

    // 📌 Form thêm
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("lophoc", new LopHoc());
        model.addAttribute("teachers", teacherRepository.findAll());
        return "admin/lophoc/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("lophoc") LopHoc lopHoc,
                       @RequestParam(name="teacherId", required=false) Long teacherId,
                       @RequestParam(name="siSo", required=false, defaultValue="0") int siSoParam,
                       @RequestParam("fileImage") MultipartFile file,
                       org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        // Đảm bảo siSo luôn được set đúng từ request param
        if (siSoParam > 0) {
            lopHoc.setSiSo(siSoParam);
        }
        if (lopHoc.getSiSo() > 25) {
            redirectAttributes.addFlashAttribute("error", "Sĩ số không được vượt quá 25 học sinh");
            if (lopHoc.getId() != null) {
                return "redirect:/admin/lophoc/edit/" + lopHoc.getId();
            }
            return "redirect:/admin/lophoc/add";
        }

        try {
            LopHoc existing = null;
            if (lopHoc.getId() != null) {
                existing = lopHocRepository.findById(lopHoc.getId()).orElse(null);
            }

            if (existing != null) {
                if (lopHoc.getSiSo() < existing.getDaDangKy()) {
                    redirectAttributes.addFlashAttribute("error", "Sĩ số (" + lopHoc.getSiSo() + ") không được nhỏ hơn số học sinh đã tham gia (" + existing.getDaDangKy() + ")");
                    return "redirect:/admin/lophoc/edit/" + lopHoc.getId();
                }
                lopHoc.setDaDangKy(existing.getDaDangKy());
                if (file.isEmpty() && lopHoc.getHinhAnh() == null) {
                    lopHoc.setHinhAnh(existing.getHinhAnh());
                }
            }

            if (teacherId != null) {
                Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
                lopHoc.setGiaoVien(teacher);
            } else if (lopHoc.getGiaoVien() != null && lopHoc.getGiaoVien().getId() != null) {
                Teacher teacher = teacherRepository.findById(lopHoc.getGiaoVien().getId()).orElse(null);
                lopHoc.setGiaoVien(teacher);
            } else {
                lopHoc.setGiaoVien(null);
            }

            // Upload ảnh
            if (!file.isEmpty()) {

                String uploadDir = "src/main/resources/static/images/";
                String fileName = file.getOriginalFilename();

                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                File saveFile = new File(dir.getAbsolutePath() + File.separator + fileName);
                file.transferTo(saveFile);

                lopHoc.setHinhAnh(fileName);
                
                // Sync to Teacher
                if (lopHoc.getGiaoVien() != null) {
                    Teacher t = teacherRepository.findById(lopHoc.getGiaoVien().getId()).orElse(null);
                    if (t != null) {
                        t.setImagePath(fileName);
                        teacherRepository.save(t);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        lopHocRepository.save(lopHoc);

        return "redirect:/admin/lophoc";
    }

    // 📌 Form sửa
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        LopHoc lopHoc = lopHocRepository.findById(id).orElse(null);
        model.addAttribute("lophoc", lopHoc);
        model.addAttribute("teachers", teacherRepository.findAll());
        return "admin/lophoc/form";
    }

    // 📌 Xoá
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        // Delete all registrations first due to foreign key constraint
        dangKyRepository.deleteByLopHocId(id);
        lopHocRepository.deleteById(id);
        return "redirect:/admin/lophoc";
    }
}