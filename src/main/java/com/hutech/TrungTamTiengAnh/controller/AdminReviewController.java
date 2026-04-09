package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.*;
import com.hutech.TrungTamTiengAnh.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin/review")
public class AdminReviewController {

    @Autowired
    private DanhGiaRepository danhGiaRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LopHocRepository lopHocRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    // ✅ Hiển thị danh sách các lớp học
    @GetMapping
    public String list(Model model) {
        List<LopHoc> list = lopHocRepository.findAll();
        model.addAttribute("classes", list);
        return "admin/review/list";
    }

    // ✅ Trang chi tiết review của lớp học
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        LopHoc lopHoc = lopHocRepository.findById(id).orElse(null);
        if (lopHoc == null) return "redirect:/admin/review";

        Teacher teacher = lopHoc.getGiaoVien();
        
        // Lấy danh sách đánh giá cho giáo viên này
        List<DanhGia> reviews = new ArrayList<>();
        if (teacher != null && teacher.getTen() != null) {
            reviews = danhGiaRepository.findByTenGiaoVien(teacher.getTen());
        }
        
        if (reviews == null) reviews = new ArrayList<>();
        reviews = reviews.stream().filter(r -> r != null).toList();

        // Tính rating trung bình
        double avgRating = 0;
        if (!reviews.isEmpty()) {
            avgRating = reviews.stream()
                .filter(r -> r != null)
                .mapToInt(DanhGia::getRating)
                .average()
                .orElse(0);
        }

        // Lấy thông tin các lớp khác của cùng giáo viên
        List<LopHoc> otherClasses = new ArrayList<>();
        if (teacher != null) {
            otherClasses = lopHocRepository.findAll().stream()
                .filter(l -> l != null && l.getGiaoVien() != null && l.getGiaoVien().getId().equals(teacher.getId()))
                .filter(l -> !l.getId().equals(lopHoc.getId()))
                .toList();
        }
        
        // Lấy lịch dạy của giáo viên
        List<Schedule> schedules = new ArrayList<>();
        if (teacher != null) {
            schedules = scheduleRepository.findAll().stream()
                .filter(s -> s != null && s.getGiaoVien() != null && s.getGiaoVien().getId().equals(teacher.getId()))
                .toList();
        }

        List<Quiz> quizzes = new ArrayList<>();
        if (teacher != null) {
            quizzes = quizRepository.findByTeacherId(teacher.getId());
        }
        if (quizzes == null) quizzes = new ArrayList<>();

        model.addAttribute("lophoc", lopHoc);
        model.addAttribute("teacher", teacher);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", Math.round(avgRating * 10.0) / 10.0);
        model.addAttribute("otherClasses", otherClasses);
        model.addAttribute("schedules", schedules);
        model.addAttribute("quizzes", quizzes);

        return "admin/review/detail";
    }

    // ✅ Gửi phản hồi cho sinh viên
    @PostMapping("/respond")
    public String respond(
            @RequestParam Long reviewId,
            @RequestParam String responseMsg,
            @RequestParam(required = false) Long classId) {
        
        DanhGia dg = danhGiaRepository.findById(reviewId).orElse(null);
        if (dg != null) {
            // Tìm sinh viên để gửi thông báo
            User student = userRepository.findByUsername(dg.getTenSinhVien());
            if (student != null) {
                Notification n = new Notification();
                n.setStudent(student);
                n.setTitle("Phản hồi từ Admin về đánh giá");
                n.setMessage("Admin đã phản hồi: " + responseMsg);
                n.setType("REVIEW_RESPONSE");
                n.setStatus("SUCCESS");
                n.setCreatedAt(java.time.LocalDateTime.now());
                notificationRepository.save(n);
            }
            
            // Tự động duyệt review khi đã phản hồi
            dg.setApproved(true);
            danhGiaRepository.save(dg);
        }

        if (classId != null) return "redirect:/admin/review/" + classId;

        // Fallback: Tìm lớp của giáo viên
        LopHoc fallbackClass = lopHocRepository.findAll().stream()
                .filter(l -> dg != null && l.getGiaoVien() != null && l.getGiaoVien().getTen().equalsIgnoreCase(dg.getTenGiaoVien()))
                .findFirst().orElse(null);
        
        return "redirect:/admin/review/" + (fallbackClass != null ? fallbackClass.getId() : "");
    }

    @GetMapping("/approve/{id}")
    public String approve(@PathVariable Long id, @RequestParam(required = false) Long classId) {
        DanhGia dg = danhGiaRepository.findById(id).orElse(null);
        if (dg != null) {
            dg.setApproved(true);
            danhGiaRepository.save(dg);
        }
        
        if (classId != null) return "redirect:/admin/review/" + classId;

        // Fallback: Tìm lớp của giáo viên
        LopHoc fallbackClass = lopHocRepository.findAll().stream()
                .filter(l -> dg != null && l.getGiaoVien() != null && l.getGiaoVien().getTen().equalsIgnoreCase(dg.getTenGiaoVien()))
                .findFirst().orElse(null);

        return "redirect:/admin/review/" + (fallbackClass != null ? fallbackClass.getId() : "");
    }
}