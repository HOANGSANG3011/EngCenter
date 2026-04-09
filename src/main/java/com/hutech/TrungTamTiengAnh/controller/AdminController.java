package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.repository.*;
import com.hutech.TrungTamTiengAnh.entity.*;
import com.hutech.TrungTamTiengAnh.service.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private LopHocRepository lopHocRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DangKyRepository dangKyRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // ===== THÊM MỚI =====
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserService userService;

    // ================= ADMIN EMAIL RECOVERY =================

    @GetMapping({"/email", "/gmail"})
    public String adminEmailPage(HttpSession session, Model model) {
        // Removed restriction for direct access via Special Admin button
        session.setAttribute("adminResetFlow", true); 
        
        // Generate a real OTP for the admin session to display in "Gmail"
        String adminOtp = otpService.generateOtp("admin@gmail.com");
        model.addAttribute("adminOtp", adminOtp);
        Boolean otpVerified = (Boolean) session.getAttribute("adminOtpVerified");
        model.addAttribute("otpVerified", otpVerified != null ? otpVerified : false);
        model.addAttribute("error", session.getAttribute("otpError"));
        session.removeAttribute("otpError");
        
        return "admin_email";
    }

    @PostMapping("/verify-otp")
    public String verifyAdminOtp(@RequestParam String otp, HttpSession session) {
        String email = "admin@gmail.com";
        if (otpService.validateOtp(email, otp)) {
            session.setAttribute("adminOtpVerified", true);
        } else {
            session.setAttribute("otpError", "Mã OTP không đúng hoặc đã hết hạn!");
        }
        return "redirect:/admin/email";
    }

    @PostMapping("/update-password")
    public String updateAdminPassword(@RequestParam String newPassword, 
                                     @RequestParam String confirmPassword,
                                     HttpSession session) {
        if (!newPassword.equals(confirmPassword)) {
            session.setAttribute("otpError", "Mật khẩu không khớp!");
            return "redirect:/admin/email";
        }

        // 1. Update Database
        User admin = userRepository.findByUsername("admin");
        if (admin != null) {
            admin.setPassword(newPassword);
            userRepository.save(admin);
        }

        // 2. Update TrungTamTiengAnhApplication.java
        updateSourceCode(newPassword);

        // 3. Update application.properties (nếu có lưu)
        updateConfigProperties(newPassword);

        session.invalidate(); // Clear session
        return "redirect:http://localhost:8080";
    }

    private void updateSourceCode(String newPassword) {
        try {
            Path path = Paths.get("src/main/java/com/hutech/TrungTamTiengAnh/TrungTamTiengAnhApplication.java");
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            
            // Regex để tìm .password("...")
            content = content.replaceAll("\\.password\\(\"[^\"]*\"\\)", ".password(\"" + newPassword + "\")");
            
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateConfigProperties(String newPassword) {
        try {
            Path path = Paths.get("src/main/resources/application.properties");
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            boolean found = false;
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith("admin.password=")) {
                    lines.set(i, "admin.password=" + newPassword);
                    found = true;
                    break;
                }
            }
            if (!found) {
                lines.add("admin.password=" + newPassword);
            }
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= HOME =================
    @GetMapping("/home")
    public String home(Model model) {

        model.addAttribute("tongLop", lopHocRepository.count());
        model.addAttribute("tongHocVien", userRepository.countByRole("STUDENT"));
        model.addAttribute("tongDangKy", dangKyRepository.count());
        model.addAttribute("tongGiaoVien", teacherRepository.count());

        Long studentId = 1L;

        List<Notification> notifications =
                notificationRepository.findByStudentIdOrderByCreatedAtDesc(studentId);

        long soThongBaoChuaDoc =
                notificationRepository.countByStudentIdAndIsReadFalse(studentId);

        model.addAttribute("notifications", notifications);
        model.addAttribute("soThongBaoChuaDoc", soThongBaoChuaDoc);

        // ===== Biểu đồ tháng =====
        List<Object[]> thongKeThang = dangKyRepository.thongKeTheoThang();

        List<Integer> thangList = new ArrayList<>();
        List<Long> soLuongList = new ArrayList<>();

        for (Object[] obj : thongKeThang) {
            thangList.add((Integer) obj[0]);
            soLuongList.add((Long) obj[1]);
        }

        model.addAttribute("thangList", thangList);
        model.addAttribute("soLuongList", soLuongList);

        // ===== Biểu đồ trạng thái =====
        List<Object[]> thongKeTrangThai = dangKyRepository.thongKeTheoTrangThai();

        List<String> trangThaiList = new ArrayList<>();
        List<Long> soLuongTrangThai = new ArrayList<>();

        for (Object[] obj : thongKeTrangThai) {
            trangThaiList.add((String) obj[0]);
            soLuongTrangThai.add((Long) obj[1]);
        }

        model.addAttribute("trangThaiList", trangThaiList);
        model.addAttribute("soLuongTrangThai", soLuongTrangThai);

        // ===== Biểu đồ giáo viên =====
        List<Object[]> thongKeGiaoVien = teacherRepository.thongKeTheoMucDo();

        List<String> monList = new ArrayList<>();
        List<Long> soLuongGiaoVienList = new ArrayList<>();

        for (Object[] obj : thongKeGiaoVien) {
            monList.add((String) obj[0]);
            soLuongGiaoVienList.add((Long) obj[1]);
        }

        model.addAttribute("monList", monList);
        model.addAttribute("soLuongGiaoVienList", soLuongGiaoVienList);

        // ===== Biểu đồ Lớp học theo giáo viên =====
        List<Object[]> thongKeLopHoc = lopHocRepository.thongKeLopHocTheoGiaoVien();
        List<String> tenGvs = new ArrayList<>();
        List<Long> slLops = new ArrayList<>();
        for (Object[] obj : thongKeLopHoc) {
            String tenGv = (String) obj[0];
            if (tenGv == null) tenGv = "Chưa xếp";
            tenGvs.add(tenGv);
            slLops.add((Long) obj[1]);
        }
        model.addAttribute("tenGvs", tenGvs);
        model.addAttribute("slLops", slLops);

        return "admin/home";
    }

    // ================= ADMIN PROFILE & CHAT LOG =================
    @GetMapping("/profile")
    public String adminProfile(Model model) {
        List<java.util.Map<String, String>> logs = new ArrayList<>();
        
        // Lọc log đăng ký sinh viên
        List<User> students = userRepository.findAll().stream().filter(u -> "STUDENT".equals(u.getRole())).limit(5).toList();
        for (User s : students) {
            java.util.Map<String, String> log = new java.util.HashMap<>();
            log.put("role", "STUDENT");
            log.put("action", "Học viên [" + s.getUsername() + "] đã đăng ký tài khoản thành công qua hệ thống Gmail ảo. (SĐT liên kết: " + s.getPhoneNumber() + ")");
            log.put("time", "Hệ thống");
            logs.add(log);
        }
        
        // Lọc log Đăng ký lớp học
        List<DangKy> dks = dangKyRepository.findAll();
        // Lấy top 5 lớp gần nhất cho bớt tải
        int count = 0;
        for (DangKy dk : dks) {
            if (count > 10) break;
            count++;
            if (dk.getStudent() != null) {
                java.util.Map<String, String> log = new java.util.HashMap<>();
                log.put("role", "STUDENT");
                log.put("action", "Học viên [" + dk.getStudent().getUsername() + "] đã nộp đơn đăng ký học lớp: " + (dk.getLopHoc() != null ? dk.getLopHoc().getTenLop() : ""));
                log.put("time", dk.getNgayDangKy() != null ? dk.getNgayDangKy().toString() : "Hệ thống");
                logs.add(log);
                
                if ("DA_DONG".equals(dk.getThanhToan())) {
                    java.util.Map<String, String> adminLog = new java.util.HashMap<>();
                    adminLog.put("role", "ADMIN");
                    adminLog.put("action", "Hệ thống & Admin đã duyệt đơn đăng ký của [" + dk.getStudent().getUsername() + "] cho lớp " + (dk.getLopHoc() != null ? dk.getLopHoc().getTenLop() : ""));
                    adminLog.put("time", dk.getNgayDangKy() != null ? dk.getNgayDangKy().toString() : "Hệ thống");
                    logs.add(adminLog);
                }
            }
        }
        
        // Lọc log Bài kiểm tra
        List<Quiz> quizzes = quizRepository.findAll();
        for (Quiz q : quizzes) {
            java.util.Map<String, String> adminLog = new java.util.HashMap<>();
            adminLog.put("role", "ADMIN");
            adminLog.put("action", "Admin tạo thành công bài kiểm tra: '" + q.getTitle() + "' giao cho giảng viên " + (q.getTeacher() != null ? q.getTeacher().getTen() : ""));
            adminLog.put("time", q.getTestDate() != null ? q.getTestDate().toLocalDate().toString() : "Hệ thống");
            logs.add(adminLog);
        }
        
        model.addAttribute("logs", logs);
        model.addAttribute("adminUser", userRepository.findByUsername("admin"));
        
        return "admin/profile";
    }

    // ================= QUIZ =================

    // ✅ Trang tạo quiz
    @GetMapping("/quiz/create")
    public String createQuizPage(Model model) {

        model.addAttribute("teachers",
                teacherRepository.findAll());

        return "admin/quiz-create";
    }

    // ✅ Lưu quiz
    @PostMapping("/quiz/save")
    public String saveQuiz(
            @RequestParam String title,
            @RequestParam Long teacherId,
            @RequestParam(required = false, defaultValue = "15") Integer timeLimit,
            @RequestParam List<String> questions,
            @RequestParam List<String> answers,
            @RequestParam List<Integer> correctIndex) {

        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);

        // ===== 1. TẠO QUIZ =====
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setTeacher(teacher);
        quiz.setTimeLimit(timeLimit);
        quiz.setConfirmed(false); // Chưa xác nhận
        quiz.setTestDate(java.time.LocalDateTime.now());
        quizRepository.save(quiz);

        // ===== 2. TẠO CÂU HỎI =====
        int answerPointer = 0;

        for (int i = 0; i < questions.size(); i++) {

            Question q = new Question();
            q.setContent(questions.get(i));
            q.setQuiz(quiz);
            questionRepository.save(q);

            for (int j = 0; j < 4; j++) {

                Answer a = new Answer();
                a.setContent(answers.get(answerPointer));
                a.setQuestion(q);

                a.setCorrect(j == correctIndex.get(i));

                answerRepository.save(a);

                answerPointer++;
            }
        }

        return "redirect:/admin/quiz";
    }

    // ✅ Danh sách quiz
    @GetMapping("/quiz")
    public String listQuiz(Model model) {
        model.addAttribute("quizzes", quizRepository.findAll());
        return "admin/quiz/list";
    }

    // ✅ Xác nhận quiz -> Gửi thông báo
    @PostMapping("/quiz/confirm/{id}")
    public String confirmQuiz(@PathVariable Long id) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz != null && !quiz.isConfirmed()) {
            quiz.setConfirmed(true);
            quizRepository.save(quiz);

            Teacher teacher = quiz.getTeacher();
            // Gửi NOTIFICATION cho học sinh thuộc lớp của giáo viên này
            if (teacher != null && teacher.getLopHocs() != null) {
                List<DangKy> allDks = dangKyRepository.findAll();
                for (LopHoc lopHoc : teacher.getLopHocs()) {
                    for (DangKy dk : allDks) {
                        if (dk.getLopHoc() != null && dk.getLopHoc().getId().equals(lopHoc.getId())) {
                            Notification n = new Notification();
                            n.setStudent(dk.getStudent());
                            n.setTitle("Bài kiểm tra mới");
                            n.setMessage("Bạn có bài kiểm tra mới: " + quiz.getTitle() + " (Giáo viên: " + teacher.getTen() + ")");
                            n.setType("KIEM_TRA");
                            n.setStatus("WARNING");
                            n.setCreatedAt(java.time.LocalDateTime.now());
                            n.setQuiz(quiz);

                            notificationRepository.save(n);
                        }
                    }
                }
            }
        }
        return "redirect:/admin/quiz";
    }

    // ✅ Xóa quiz
    @GetMapping("/quiz/delete/{id}")
    public String deleteQuiz(@PathVariable Long id) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz != null) {
            notificationRepository.deleteByQuizId(id);
            quizResultRepository.deleteByQuizId(id);
            quizRepository.delete(quiz);
        }
        return "redirect:/admin/quiz";
    }

    // ================= THÔNG BÁO THỦ CÔNG =================
    @GetMapping("/notification/send")
    public String sendNotificationPage(Model model) {
        model.addAttribute("students", userRepository.findAll().stream()
                .filter(u -> "STUDENT".equals(u.getRole())).toList());
        return "admin/notification/send";
    }

    @PostMapping("/notification/send")
    public String processSendNotification(
            @RequestParam Long studentId,
            @RequestParam String type,
            @RequestParam String message) {

        User student = userRepository.findById(studentId).orElse(null);
        if (student == null) return "redirect:/admin/home";

        Notification n = new Notification();
        n.setStudent(student);
        n.setType(type);
        n.setStatus("WARNING");
        n.setCreatedAt(java.time.LocalDateTime.now());
        n.setMessage(message);

        if ("NHAC_NO".equals(type)) {
            n.setTitle("Nhắc nhở nợ học phí");
        } else if ("VANG_MAT".equals(type)) {
            n.setTitle("Cảnh báo vắng mặt");
        } else {
            n.setTitle("Thông báo từ Trung tâm");
        }

        notificationRepository.save(n);
        return "redirect:/admin/notification/send?success=true";
    }
}