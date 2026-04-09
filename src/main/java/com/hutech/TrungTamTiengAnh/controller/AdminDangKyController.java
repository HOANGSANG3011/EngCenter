package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.DangKy;
import com.hutech.TrungTamTiengAnh.entity.LopHoc;
import com.hutech.TrungTamTiengAnh.entity.User;
import com.hutech.TrungTamTiengAnh.repository.DangKyRepository;
import com.hutech.TrungTamTiengAnh.repository.LopHocRepository;
import com.hutech.TrungTamTiengAnh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/dangky")
public class AdminDangKyController {

    @Autowired
    private DangKyRepository dangKyRepository;

    @Autowired
    private LopHocRepository lopHocRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.hutech.TrungTamTiengAnh.repository.NotificationRepository notificationRepository;

    // Hiển thị danh sách đăng ký
    @GetMapping
    public String list(Model model) {

        List<DangKy> list = dangKyRepository.findAll();
        model.addAttribute("list", list);

        return "admin/dangky/list";
    }

    // Form thêm đăng ký
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("dangky", new DangKy());
        model.addAttribute("students", userRepository.findAll().stream()
                .filter(u -> "STUDENT".equals(u.getRole())).toList());
        model.addAttribute("lophocList", lopHocRepository.findAll());
        return "admin/dangky/form";
    }

    // Lưu đăng ký
    @PostMapping("/save")
    public String save(@ModelAttribute DangKy dangky) {

        if (dangky.getLopHoc() != null) {
            LopHoc lh = lopHocRepository
                    .findById(dangky.getLopHoc().getId())
                    .orElse(null);

            if (lh != null) {
                dangky.setHocPhi(lh.getHocPhi()); // 🔥 lấy học phí từ lớp
            }
        }

        dangky.setThanhToan("CHUA_DONG"); // mặc định

        dangKyRepository.save(dangky);

        return "redirect:/admin/dangky";
    }

    // Duyệt đăng ký
    @GetMapping("/duyet/{id}")
    public String duyet(@PathVariable Long id) {

        DangKy dk = dangKyRepository.findById(id).orElse(null);

        if (dk != null && !"DA_DUYET".equals(dk.getTrangThai())) {

            LopHoc lh = dk.getLopHoc();

            // 🔥 check sĩ số trước khi duyệt
            if (lh.getDaDangKy() >= lh.getSiSo()) {
                return "redirect:/admin/dangky?error=full";
            }

            dk.setTrangThai("DA_DUYET");
            dk.setHocPhi(lh.getHocPhi());
            dk.setThanhToan("CHUA_DONG");

            // 🔥 tăng số lượng đã đăng ký
            lh.setDaDangKy(lh.getDaDangKy() + 1);

            lopHocRepository.save(lh);
            dangKyRepository.save(dk);

            // 🔥 Tạo thông báo cho học sinh
            com.hutech.TrungTamTiengAnh.entity.Notification notification = new com.hutech.TrungTamTiengAnh.entity.Notification();
            notification.setTitle("Đăng ký thành công lớp " + lh.getTenLop());
            notification.setMessage("Bạn đã được duyệt vào lớp " + lh.getTenLop() + ". Vui lòng thanh toán học phí sớm.");
            notification.setType("INFO");
            notification.setStatus("UNREAD");
            notification.setCreatedAt(java.time.LocalDateTime.now());
            notification.setStudent(dk.getStudent());
            notificationRepository.save(notification);
        }

        return "redirect:/admin/dangky";
    }

    // ✅ Duyệt tất cả đơn chờ duyệt
    @GetMapping("/duyet-tat-ca")
    public String duyetTatCa() {
        List<DangKy> pending = dangKyRepository.findAll().stream()
                .filter(dk -> "CHO_DUYET".equals(dk.getTrangThai()))
                .toList();

        int duyetCount = 0;
        for (DangKy dk : pending) {
            LopHoc lh = dk.getLopHoc();
            if (lh == null) continue;

            // Giới hạn theo sĩ số
            if (lh.getDaDangKy() >= lh.getSiSo()) continue;

            dk.setTrangThai("DA_DUYET");
            dk.setHocPhi(lh.getHocPhi());
            dk.setThanhToan("CHUA_DONG");
            lh.setDaDangKy(lh.getDaDangKy() + 1);

            lopHocRepository.save(lh);
            dangKyRepository.save(dk);

            // Gửi thông báo cho từng học sinh
            com.hutech.TrungTamTiengAnh.entity.Notification n = new com.hutech.TrungTamTiengAnh.entity.Notification();
            n.setTitle("Đăng ký thành công lớp " + lh.getTenLop());
            n.setMessage("Bạn đã được duyệt vào lớp " + lh.getTenLop() + ". Vui lòng thanh toán học phí sớm.");
            n.setType("INFO");
            n.setStatus("UNREAD");
            n.setCreatedAt(java.time.LocalDateTime.now());
            n.setStudent(dk.getStudent());
            notificationRepository.save(n);

            duyetCount++;
        }

        return "redirect:/admin/dangky?duyetAll=" + duyetCount;
    }
}