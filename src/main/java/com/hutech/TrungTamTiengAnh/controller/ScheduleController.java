package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.Schedule;
import com.hutech.TrungTamTiengAnh.entity.Teacher;
import com.hutech.TrungTamTiengAnh.repository.ScheduleRepository;
import com.hutech.TrungTamTiengAnh.repository.TeacherRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    // =========================
    // 📌 HIỂN THỊ THEO TUẦN
    // =========================
    @GetMapping
    public String schedule(
            @RequestParam(defaultValue = "0") int weekOffset,
            Model model) {

        LocalDate today = LocalDate.now().plusWeeks(weekOffset);

        LocalDate startWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endWeek = startWeek.plusDays(6);

        List<Schedule> schedules =
                scheduleRepository.findByNgayBetween(startWeek, endWeek);

        model.addAttribute("schedules", schedules);
        model.addAttribute("days", List.of("T2","T3","T4","T5","T6","T7","CN"));

        model.addAttribute("startWeek", startWeek);
        model.addAttribute("endWeek", endWeek);
        model.addAttribute("weekOffset", weekOffset);

        return "admin/schedule";
    }

    // =========================
    // 📌 FORM PHÂN CÔNG
    // =========================
    @GetMapping("/assign")
    public String assign(@RequestParam(required = false) String thu, @RequestParam(required = false) String ngay, Model model) {

        model.addAttribute("thu", thu);
        model.addAttribute("ngay", ngay);
        model.addAttribute("teachers", teacherRepository.findAll());

        return "admin/assign";
    }

    // =========================
    // 📌 LƯU LỊCH
    // =========================
    @PostMapping("/save")
    public String save(
            @RequestParam Long teacherId,
            @RequestParam String buoi,
            @RequestParam int tietBatDau,
            @RequestParam int tietKetThuc,
            @RequestParam String ngay
    ) {

        LocalDate date = LocalDate.parse(ngay);

        String thu = switch (date.getDayOfWeek()) {
            case MONDAY -> "T2";
            case TUESDAY -> "T3";
            case WEDNESDAY -> "T4";
            case THURSDAY -> "T5";
            case FRIDAY -> "T6";
            case SATURDAY -> "T7";
            case SUNDAY -> "CN";
        };

        // CHECK BUỔI
        if (buoi.equals("SANG") && (tietBatDau < 1 || tietKetThuc > 6)) {
            return "redirect:/admin/schedule?error=sang";
        }

        if (buoi.equals("CHIEU") && (tietBatDau < 7 || tietKetThuc > 12)) {
            return "redirect:/admin/schedule?error=chieu";
        }

        if (buoi.equals("TOI") && (tietBatDau < 13 || tietKetThuc > 15)) {
            return "redirect:/admin/schedule?error=toi";
        }

        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher == null) return "redirect:/admin/schedule?error=noteacher";

        // CHECK CONFLICT
        List<Schedule> exSchedules = scheduleRepository.findByGiaoVienIdAndNgay(teacherId, date);
        for (Schedule ex : exSchedules) {
            // Trùng tiết nếu tiết bắt đầu mới <= tiết kết thúc cũ VÀ tiết kết thúc mới >= tiết bắt đầu cũ
            if (tietBatDau <= ex.getTietKetThuc() && tietKetThuc >= ex.getTietBatDau()) {
                return "redirect:/admin/schedule?error=conflict";
            }
        }

        Schedule s = new Schedule();
        s.setThu(thu);
        s.setNgay(date);
        s.setNam(date.getYear());

        // ✅ Auto-fill mức độ từ giáo viên
        s.setMonHoc(teacher != null ? teacher.getMucDo() : "");
        s.setBuoi(buoi);
        s.setTietBatDau(tietBatDau);
        s.setTietKetThuc(tietKetThuc);
        s.setGiaoVien(teacher);

        scheduleRepository.save(s);

        return "redirect:/admin/schedule";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        scheduleRepository.deleteById(id);
        return "redirect:/admin/schedule";
    }

    public static java.time.LocalTime getStartTime(int tiet) {
        if (tiet >= 1 && tiet <= 6) return java.time.LocalTime.of(6, 30).plusMinutes((tiet - 1) * 45);
        if (tiet >= 7 && tiet <= 12) return java.time.LocalTime.of(12, 30).plusMinutes((tiet - 7) * 45);
        if (tiet >= 13 && tiet <= 15) return java.time.LocalTime.of(19, 30).plusMinutes((tiet - 13) * 30);
        return null;
    }

    public static java.time.LocalTime getEndTime(int tiet) {
        if (tiet >= 1 && tiet <= 6) return java.time.LocalTime.of(6, 30).plusMinutes(tiet * 45);
        if (tiet >= 7 && tiet <= 12) return java.time.LocalTime.of(12, 30).plusMinutes((tiet - 6) * 45);
        if (tiet >= 13 && tiet <= 15) return java.time.LocalTime.of(19, 30).plusMinutes((tiet - 12) * 30);
        return null;
    }
}