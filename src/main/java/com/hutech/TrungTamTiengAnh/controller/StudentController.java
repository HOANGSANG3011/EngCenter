package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.*;
import com.hutech.TrungTamTiengAnh.repository.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private LopHocRepository lopHocRepository;

    @Autowired
    private DangKyRepository dangKyRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private DanhGiaRepository danhGiaRepository;

    @Autowired
    private UserRepository userRepository;

    // ===== THÊM QUIZ =====
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    // ================= ROOM STATE (Realtime Presence via Polling) =================
    // Map<scheduleId, Map<username, {micOn, camOn, lastPing(ms)}>>
    private static final java.util.concurrent.ConcurrentHashMap<Long, java.util.concurrent.ConcurrentHashMap<String, long[]>> roomStates
            = new java.util.concurrent.ConcurrentHashMap<>();
    // long[] = [lastPingMs, micOn(1/0), camOn(1/0)]

    @PostMapping("/api/room/{scheduleId}/ping")
    @ResponseBody
    public String pingRoom(@PathVariable Long scheduleId,
                           @RequestParam(defaultValue = "1") int micOn,
                           @RequestParam(defaultValue = "1") int camOn,
                           HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "UNAUTHORIZED";
        roomStates.computeIfAbsent(scheduleId, k -> new java.util.concurrent.ConcurrentHashMap<>())
                .put(user.getUsername(), new long[]{System.currentTimeMillis(), micOn, camOn});
        return "OK";
    }

    @GetMapping("/api/room/{scheduleId}/active")
    @ResponseBody
    public java.util.List<java.util.Map<String, Object>> getActiveStudents(
            @PathVariable Long scheduleId, HttpSession session) {
        long threshold = 5000; // 5 seconds
        java.util.Map<String, long[]> map = roomStates.get(scheduleId);
        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        if (map != null) {
            long now = System.currentTimeMillis();
            map.forEach((username, data) -> {
                if (now - data[0] < threshold) {
                    java.util.Map<String, Object> entry = new java.util.HashMap<>();
                    entry.put("username", username);
                    entry.put("micOn", data[1] == 1);
                    entry.put("camOn", data[2] == 1);
                    result.add(entry);
                } else {
                    map.remove(username);
                }
            });
        }
        return result;
    }

    // ================= HOME =================
    @GetMapping("/home")
    public String home(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        List<Notification> notifications =
                notificationRepository.findByStudentIdOrderByCreatedAtDesc(user.getId());

        long soThongBaoChuaDoc =
                notificationRepository.countByStudentIdAndIsReadFalse(user.getId());

        model.addAttribute("notifications", notifications);
        model.addAttribute("soThongBaoChuaDoc", soThongBaoChuaDoc);

        List<Object[]> thongKeThang = dangKyRepository.thongKeTheoThang();

        List<Integer> thangList = new ArrayList<>();
        List<Long> soLuongList = new ArrayList<>();

        for (Object[] obj : thongKeThang) {
            if (obj[0] != null) {
                thangList.add(((Number) obj[0]).intValue());
                soLuongList.add(((Number) obj[1]).longValue());
            }
        }

        model.addAttribute("thangList", thangList);
        model.addAttribute("soLuongList", soLuongList);

        List<Object[]> thongKeTrangThai = dangKyRepository.thongKeTheoTrangThai();

        List<String> trangThaiList = new ArrayList<>();
        List<Long> soLuongTrangThai = new ArrayList<>();

        for (Object[] obj : thongKeTrangThai) {
            String status = (String) obj[0];
            if (status != null) {
                trangThaiList.add(status);
                soLuongTrangThai.add(((Number) obj[1]).longValue());
            }
        }

        model.addAttribute("trangThaiList", trangThaiList);
        model.addAttribute("soLuongTrangThai", soLuongTrangThai);

        List<Object[]> thongKeGiaoVien = teacherRepository.thongKeTheoMucDo();

        List<String> monList = new ArrayList<>();
        List<Long> soLuongGiaoVienList = new ArrayList<>();

        for (Object[] obj : thongKeGiaoVien) {
            String mon = (String) obj[0];
            if (mon != null) {
                monList.add(mon);
                soLuongGiaoVienList.add(((Number) obj[1]).longValue());
            }
        }

        model.addAttribute("monList", monList);
        model.addAttribute("soLuongGiaoVienList", soLuongGiaoVienList);

        return "student/home";
    }

    // ================= QUIZ =================
    @GetMapping("/quizzes")
    public String myQuizzes(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<DangKy> dangKys = dangKyRepository.findByStudentId(user.getId());
        List<Quiz> quizzes = new ArrayList<>();
        List<Long> addedQuizIds = new ArrayList<>();

        for (DangKy dk : dangKys) {
            if ("DA_DONG".equals(dk.getThanhToan()) && dk.getLopHoc() != null && dk.getLopHoc().getGiaoVien() != null) {
                List<Quiz> teacherQuizzes = quizRepository.findByTeacherId(dk.getLopHoc().getGiaoVien().getId());
                for (Quiz q : teacherQuizzes) {
                    if (q.isConfirmed() && !addedQuizIds.contains(q.getId())) {
                        quizzes.add(q);
                        addedQuizIds.add(q.getId());
                    }
                }
            }
        }

        // Tạo map để biết trạng thái làm bài (chưa bài map -> điểm / null nếu chưa làm)
        java.util.Map<Long, Double> quizScores = new java.util.HashMap<>();
        for (Quiz q : quizzes) {
            QuizResult result = quizResultRepository.findByStudentIdAndQuizId(user.getId(), q.getId());
            quizScores.put(q.getId(), result != null ? result.getScore() : null);
        }

        model.addAttribute("quizzes", quizzes);
        model.addAttribute("quizScores", quizScores);

        return "student/quiz-list";
    }

    @GetMapping("/quiz/{id}")
    public String doQuiz(@PathVariable Long id,
                         HttpSession session,
                         Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        Quiz quiz = quizRepository.findById(id).orElse(null);

        if (quiz == null) {
            return "redirect:/student/home";
        }

        List<Question> questions = questionRepository.findByQuizId(id);

        for (Question q : questions) {
            q.setAnswers(answerRepository.findByQuestionId(q.getId()));
        }

        // Check if already completed
        QuizResult result = quizResultRepository.findByStudentIdAndQuizId(user.getId(), id);
        if (result != null) {
            model.addAttribute("resultScore", result.getScore());
            model.addAttribute("isDone", true);
        } else {
            model.addAttribute("isDone", false);
        }

        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);

        return "student/quiz";
    }

    @PostMapping("/quiz/submit")
    @ResponseBody
    public String submitQuiz(@RequestParam Long quizId,
                             @RequestParam double score,
                             HttpSession session) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz != null) {
            QuizResult exists = quizResultRepository.findByStudentIdAndQuizId(user.getId(), quizId);
            if (exists == null) {
                QuizResult qr = new QuizResult();
                qr.setStudent(user);
                qr.setQuiz(quiz);
                qr.setScore(score);
                quizResultRepository.save(qr);
            }
        }

        return "OK";
    }

    @GetMapping("/quiz/downloadText_word")
    public void downloadWord(@RequestParam Long quizId, jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) return;
        
        response.setContentType("application/msword; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Quiz_" + quizId + ".doc\"");
        
        java.io.PrintWriter writer = response.getWriter();
        writer.println(quiz.getTitle());
        writer.println("===============================");
        
        List<Question> questions = questionRepository.findByQuizId(quizId);
        int qNum = 1;
        for (Question q : questions) {
            writer.println("Câu " + qNum + ": " + q.getContent());
            List<Answer> answers = answerRepository.findByQuestionId(q.getId());
            char aChar = 'A';
            for (Answer a : answers) {
                writer.println(aChar + ". " + a.getContent());
                aChar++;
            }
            writer.println();
            qNum++;
        }
        writer.flush();
        writer.close();
    }

    // ================= SCHEDULE =================
    @GetMapping("/schedule")
    public String schedule(
            @RequestParam(defaultValue = "0") int weekOffset,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        List<DangKy> dkList = dangKyRepository.findByStudentId(user.getId());

        if (dkList.isEmpty()) {
            model.addAttribute("error", "Vui lòng kiểm tra lại sau");
            return "student/error";
        }

        // Get IDs of classes the student is enrolled in
        List<Long> registeredClassIds = dkList.stream()
                .filter(dk -> dk.getLopHoc() != null)
                .map(dk -> dk.getLopHoc().getId())
                .toList();

        LocalDate today = LocalDate.now().plusWeeks(weekOffset);

        LocalDate startWeek = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate endWeek = startWeek.plusDays(6);

        // Fetch schedules and filter by registered classes
        List<Schedule> allSchedules = scheduleRepository.findByNgayBetween(startWeek, endWeek);
        
        // Filter: Only show if the teacher of the schedule is teaching any of the student's registered classes
        // OR better: Only show if the student is registered for a class that this schedule belongs to.
        // Since Schedule doesn't directly link to LopHoc (it links to Teacher), 
        // we'll filter by teachers the student is learning from.
        List<Long> teacherIds = dkList.stream()
                .filter(dk -> dk.getLopHoc() != null && dk.getLopHoc().getGiaoVien() != null)
                .map(dk -> dk.getLopHoc().getGiaoVien().getId())
                .distinct()
                .toList();

        List<Schedule> schedules = allSchedules.stream()
                .filter(s -> s.getGiaoVien() != null && teacherIds.contains(s.getGiaoVien().getId()))
                .toList();

        // Check if classes for these schedules are full
        java.util.Map<Long, Boolean> isFullMap = new java.util.HashMap<>();
        for (Schedule s : schedules) {
            LopHoc lh = lopHocRepository.findAll().stream()
                    .filter(l -> l.getGiaoVien() != null && l.getGiaoVien().getId().equals(s.getGiaoVien().getId()))
                    .findFirst().orElse(null);
            if (lh != null) {
                isFullMap.put(s.getId(), lh.getDaDangKy() >= lh.getSiSo());
            } else {
                isFullMap.put(s.getId(), false);
            }
        }

        model.addAttribute("schedules", schedules);
        model.addAttribute("isFullMap", isFullMap);
        model.addAttribute("startWeek", startWeek);
        model.addAttribute("endWeek", endWeek);
        model.addAttribute("weekOffset", weekOffset);
        model.addAttribute("days", List.of("T2","T3","T4","T5","T6","T7","CN"));

        return "student/schedule";
    }

    // ================= JOIN ROOM =================
    @GetMapping("/join")
    public String joinRoom(@RequestParam Long roomId,
                           HttpSession session,
                           Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("roomId", roomId);
        model.addAttribute("username", user.getUsername());

        return "student/room";
    }

    @GetMapping("/join/{scheduleId}")
    public String joinClass(@PathVariable Long scheduleId,
                            HttpSession session,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null) return "redirect:/student/schedule";

        // Check if student is registered for this teacher's classes
        List<DangKy> list = dangKyRepository.findByStudentId(user.getId());
        boolean isMine = list.stream().anyMatch(dk -> 
            dk.getLopHoc() != null && dk.getLopHoc().getGiaoVien() != null && 
            dk.getLopHoc().getGiaoVien().getId().equals(schedule.getGiaoVien().getId()) &&
            "DA_DONG".equals(dk.getThanhToan())
        );

        if (!isMine) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa đăng ký hoặc chưa hoàn tất học phí cho lớp này.");
            return "redirect:/student/schedule";
        }

        // --- DYNAMIC NAVIGATION LOGIC ---
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalTime now = java.time.LocalTime.now();

        java.time.LocalTime startTime = com.hutech.TrungTamTiengAnh.controller.ScheduleController.getStartTime(schedule.getTietBatDau());
        java.time.LocalTime endTime = com.hutech.TrungTamTiengAnh.controller.ScheduleController.getEndTime(schedule.getTietKetThuc());

        if (schedule.getNgay().isBefore(today)) {
            // Đã kết thúc
            redirectAttributes.addFlashAttribute("info", "Lớp học này đã kết thúc.");
            return "redirect:/student/home";
        } else if (schedule.getNgay().isAfter(today)) {
            // Sắp tới
            model.addAttribute("schedule", schedule);
            model.addAttribute("startTime", startTime != null ? startTime.toString() : "00:00");
            model.addAttribute("isToday", false);
            return "student/room/wait";
        } else {
            // Cùng ngày
            if (now.isBefore(startTime)) {
                // Đang chờ (trong ngày)
                model.addAttribute("schedule", schedule);
                model.addAttribute("startTime", startTime != null ? startTime.toString() : "00:00");
                model.addAttribute("isToday", true);
                return "student/room/wait";
            } else if (now.isAfter(endTime)) {
                // Đã xong (trong ngày)
                redirectAttributes.addFlashAttribute("info", "Lớp học này đã kết thúc vào lúc " + endTime);
                return "redirect:/student/home";
            } else {
                // Đang diễn ra
                model.addAttribute("schedule", schedule);
                model.addAttribute("username", user.getUsername());
                
                long secondsRemaining = java.time.Duration.between(now, endTime).getSeconds();
                model.addAttribute("secondsRemaining", secondsRemaining);
                
                // Fetch classroom image from LopHoc
                LopHoc lh = lopHocRepository.findAll().stream()
                        .filter(l -> l.getGiaoVien() != null && l.getGiaoVien().getId().equals(schedule.getGiaoVien().getId()))
                        .findFirst().orElse(null);
                model.addAttribute("classImage", lh != null ? lh.getHinhAnh() : null);
                
                // Sĩ số - số ô hiển thị
                int siSo = lh != null ? lh.getSiSo() : 10;
                model.addAttribute("siSo", siSo);
                model.addAttribute("scheduleId", scheduleId);

                // Tất cả học sinh đã đăng ký và đóng học phí (cho lưới ô)
                List<String> allStudents = new ArrayList<>();
                if (lh != null) {
                    List<DangKy> attendants = dangKyRepository.findByLopHocId(lh.getId());
                    for (DangKy att : attendants) {
                        if ("DA_DONG".equals(att.getThanhToan()) && att.getStudent() != null) {
                            allStudents.add(att.getStudent().getUsername());
                        }
                    }
                }
                model.addAttribute("allStudents", allStudents);
                
                return "student/room";
            }
        }
    }

    // ================= CLASS =================
    @GetMapping("/classes")
    public String viewClasses(Model model) {
        model.addAttribute("list", lopHocRepository.findAll());
        return "student/classes";
    }

    @GetMapping("/register/{id}")
    public String register(@PathVariable Long id,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        LopHoc lopHoc = lopHocRepository.findById(id).orElse(null);

        if (lopHoc == null) {
            return "redirect:/student/classes";
        }

        LocalDate ngayHoc = lopHoc.getNgayHoc();

        if (ngayHoc == null) {
            return "redirect:/student/classes";
        }

        LocalDate today = LocalDate.now();
        LocalDate startRegister = ngayHoc.minusDays(20);

        if (today.isBefore(startRegister)) {
            redirectAttributes.addFlashAttribute("warning",
                    "Vui lòng chờ đến " + startRegister + " để đăng ký");
            return "redirect:/student/classes";
        }

        if (today.isAfter(ngayHoc)) {
            redirectAttributes.addFlashAttribute("warning",
                    "Lịch đăng ký đã hết hạn");
            return "redirect:/student/classes";
        }

        if (lopHoc.getDaDangKy() >= lopHoc.getSiSo()) {
            redirectAttributes.addFlashAttribute("warning",
                    "Lớp đã đủ số lượng");
            return "redirect:/student/classes";
        }

        DangKy existed =
                dangKyRepository.findByStudentIdAndLopHocId(user.getId(), id);

        if (existed != null) {
            redirectAttributes.addFlashAttribute("warning", "Đã đăng ký rồi");
            return "redirect:/student/my-classes";
        }

        DangKy dk = new DangKy();
        dk.setStudent(user);
        dk.setLopHoc(lopHoc);
        dk.setTrangThai("CHO_DUYET");
        dk.setNgayDangKy(java.time.LocalDate.now());

        dangKyRepository.save(dk);

        return "redirect:/student/my-classes";
    }

    @GetMapping("/my-classes")
    public String myClasses(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        List<DangKy> list = dangKyRepository.findByStudentId(user.getId());
        
        // --- FILTER DUPLICATES ---
        // if user has multiple registrations for the same class, only show one
        if (list != null) {
            list = list.stream()
                .collect(java.util.stream.Collectors.toMap(
                    dk -> dk.getLopHoc() != null ? dk.getLopHoc().getId() : -1,
                    dk -> dk,
                    (existing, replacement) -> existing, // keep the first one
                    java.util.LinkedHashMap::new
                ))
                .values().stream()
                .toList();
        }

        model.addAttribute("list", list);

        return "student/my-classes";
    }

    // ================= REVIEW =================
    @GetMapping("/review/{id}")
    public String review(@PathVariable Long id, Model model) {
        LopHoc lopHoc = lopHocRepository.findById(id).orElse(null);
        model.addAttribute("lopHoc", lopHoc);
        return "student/review";
    }

    @PostMapping("/review/save")
    public String saveReview(@RequestParam String tenGiaoVien,
                             @RequestParam int rating,
                             @RequestParam String comment,
                             HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        DanhGia dg = new DanhGia();
        dg.setTenGiaoVien(tenGiaoVien);
        dg.setTenSinhVien(user.getUsername());
        dg.setRating(rating);
        dg.setComment(comment);
        dg.setApproved(false);

        danhGiaRepository.save(dg);

        return "redirect:/student/my-classes";
    }

    // ================= PAYMENT =================
    @GetMapping("/pay/{id}")
    public String showPaymentForm(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        DangKy dk = dangKyRepository.findById(id).orElse(null);
        if (dk == null || !dk.getStudent().getId().equals(user.getId())) {
            return "redirect:/student/my-classes";
        }
        
        model.addAttribute("dk", dk);
        model.addAttribute("maHocPhi", "HP" + dk.getId() + "SV" + user.getId());
        return "student/payment";
    }

    @PostMapping("/pay")
    public String manualPayment(@RequestParam Long id,
                                @RequestParam String hoTen,
                                @RequestParam String phuongThuc,
                                @RequestParam(required = false) String maGiaoDich,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        DangKy dk = dangKyRepository.findById(id).orElse(null);
        if (dk != null && dk.getStudent().getId().equals(user.getId())) {
            dk.setHoTenThanhToan(hoTen);
            dk.setPhuongThuc(phuongThuc);
            dk.setMaGiaoDich(maGiaoDich);
            dk.setThanhToan("DA_DONG");
            dangKyRepository.save(dk);
            
            // Effect payload
            redirectAttributes.addFlashAttribute("successPaidId", dk.getId());
            return "redirect:/student/my-classes?success=paid";
        }

        return "redirect:/student/my-classes";
    }

    @GetMapping("/api/payment/status/{id}")
    @ResponseBody
    public String checkPaymentStatus(@PathVariable Long id) {
        DangKy dk = dangKyRepository.findById(id).orElse(null);
        if (dk != null) {
            return dk.getThanhToan() == null ? "" : dk.getThanhToan();
        }
        return "NOT_FOUND";
    }

    @PostMapping("/api/payment/simulate-phone-approve/{id}")
    @ResponseBody
    public String simulatePhoneApprove(@PathVariable Long id) {
        DangKy dk = dangKyRepository.findById(id).orElse(null);
        if (dk != null) {
            dk.setThanhToan("DA_DONG");
            dk.setPhuongThuc("QR");
            dangKyRepository.save(dk);
            return "SUCCESS";
        }
        return "FAIL";
    }

    // 📱 MOBILE SIMULATION ENDPOINT
    @GetMapping("/mobile-checkout/{id}")
    public String mobileCheckout(@PathVariable Long id, Model model) {
        DangKy dk = dangKyRepository.findById(id).orElse(null);
        if (dk == null) return "student/error";
        model.addAttribute("dk", dk);
        return "student/mobile_checkout";
    }

    // ================= PROFILE & SCORES =================
    @GetMapping("/profile")
    public String getProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        long totalRegistered = dangKyRepository.countByStudentId(user.getId());
        long finishedClasses = dangKyRepository.countByStudentIdAndLopHocNgayHocBefore(user.getId(), LocalDate.now());
        Double avgScore = quizResultRepository.getAverageScoreByStudentId(user.getId());
        long reviewsLeft = danhGiaRepository.countByTenSinhVien(user.getUsername());
        Double totalPaid = dangKyRepository.sumHocPhiByStudentId(user.getId());

        model.addAttribute("totalRegistered", totalRegistered);
        model.addAttribute("finishedClasses", finishedClasses);
        model.addAttribute("avgScore", avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0.0);
        model.addAttribute("reviewsLeft", reviewsLeft);
        model.addAttribute("totalPaid", totalPaid != null ? totalPaid : 0.0);
        model.addAttribute("user", user);

        return "student/profile";
    }

    @GetMapping("/diem")
    public String viewScores(@RequestParam(required = false) Long teacherId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<QuizResult> results;
        if (teacherId != null && teacherId > 0) {
            // Hiển thị điểm của TẤT CẢ học sinh theo giáo viên đó (theo yêu cầu)
            results = quizResultRepository.findByQuizTeacherId(teacherId);
            Teacher t = teacherRepository.findById(teacherId).orElse(null);
            model.addAttribute("selectedTeacherName", t != null ? t.getTen() : "Không xác định");
        } else {
            // Mặc định: Chỉ xem điểm của CHÍNH MÌNH
            results = quizResultRepository.findByStudentId(user.getId());
        }

        List<Teacher> teachers = teacherRepository.findAll();
        model.addAttribute("teachers", teachers);
        model.addAttribute("selectedTeacherId", teacherId);
        model.addAttribute("results", results);
        model.addAttribute("studentName", user.getUsername());

        return "student/diem";
    }
}