package com.hutech.TrungTamTiengAnh.controller;

import com.hutech.TrungTamTiengAnh.entity.User;
import com.hutech.TrungTamTiengAnh.service.EmailService;
import com.hutech.TrungTamTiengAnh.service.OtpService;
import com.hutech.TrungTamTiengAnh.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;
    private final OtpService otpService;

    // Constructor Injection
    public AuthController(UserService userService, EmailService emailService, OtpService otpService) {
        this.userService = userService;
        this.emailService = emailService;
        this.otpService = otpService;
    }

    // ===================== LOGIN =====================

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        User user = userService.login(username, password);

        if (user != null) {
            session.setAttribute("user", user);

            // Phân quyền
            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin/home";
            } else {
                return "redirect:/student/home";
            }
        }

        model.addAttribute("error", "Sai tài khoản hoặc mật khẩu!");
        return "login";
    }

    // ===================== REGISTER =====================

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                           Model model,
                           org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        // Mặc định tài khoản đăng ký là STUDENT
        user.setRole("STUDENT");

        String result = userService.register(user);

        if ("SUCCESS".equals(result)) {
            redirectAttributes.addFlashAttribute("showFireworks", true);
            return "redirect:/login";
        }

        model.addAttribute("error", result);
        return "register";
    }

    // ===================== LOGOUT =====================

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ===================== FORGOT PASSWORD =====================

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, HttpSession session, Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "Email không tồn tại trong hệ thống!");
            return "login";
        }

        String otp = otpService.generateOtp(email);
        // emailService.sendOtpEmail(email, otp); // Vô hiệu hóa gửi email thực tế nếu muốn giữ mock
        session.setAttribute("resetEmail", email);

        if (!"admin@gmail.com".equals(email)) {
            session.setAttribute("otpType", "RESET");
            session.setAttribute("otpVerified", false);
            return "redirect:/student/gmail/" + user.getUsername();
        }

        model.addAttribute("message", "Mã OTP đã được gửi đến email của bạn.");
        model.addAttribute("showOtpForm", true);
        model.addAttribute("email", email);
        return "login";
    }
    
    // ================= STUDENT GMAIL RECOVERY (MOCK) =================
    
    @GetMapping("/student/gmail/{name}")
    public String studentGmailPage(@PathVariable String name, HttpSession session, Model model) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null) return "redirect:/login";

        model.addAttribute("userName", name);
        model.addAttribute("otp", otpService.getOtp(email));
        
        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
        model.addAttribute("otpVerified", otpVerified != null && otpVerified);
        model.addAttribute("error", session.getAttribute("otpError"));
        session.removeAttribute("otpError");
        
        return "student_email";
    }
    
    @PostMapping("/student/gmail/verify-otp")
    public String verifyStudentOtp(@RequestParam String otp, HttpSession session) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null) return "redirect:/login";
        
        User user = userService.findByEmail(email);
        String name = user != null ? user.getUsername() : "student";

        if (otpService.validateOtp(email, otp)) {
            session.setAttribute("otpVerified", true);
        } else {
            session.setAttribute("otpError", "Mã OTP không chính xác hoặc đã hết hạn!");
        }
        return "redirect:/student/gmail/" + name;
    }
    
    @PostMapping("/student/gmail/update-password")
    public String updateStudentPassword(@RequestParam String newPassword, 
                                        @RequestParam String confirmPassword, 
                                        HttpSession session, 
                                        org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("resetEmail");
        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");

        if (email == null || otpVerified == null || !otpVerified) {
            return "redirect:/login";
        }
        
        User user = userService.findByEmail(email);
        String name = user != null ? user.getUsername() : "student";

        if (!newPassword.equals(confirmPassword)) {
            session.setAttribute("otpError", "Mật khẩu không khớp!");
            return "redirect:/student/gmail/" + name;
        }

        if (user != null) {
            user.setPassword(newPassword);
            userService.save(user); // Lưu database để đồng bộ DB
        }

        session.removeAttribute("resetEmail");
        session.removeAttribute("otpVerified");
        session.removeAttribute("otpType");

        redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
        return "redirect:/login";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String otp, HttpSession session, Model model) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null) return "redirect:/login";

        if (otpService.validateOtp(email, otp)) {
            session.setAttribute("otpVerified", true);
            model.addAttribute("showResetForm", true);
            return "login";
        }

        model.addAttribute("error", "Mã OTP không chính xác hoặc đã hết hạn!");
        model.addAttribute("showOtpForm", true);
        model.addAttribute("email", email);
        return "login";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String newPassword, 
                                @RequestParam String confirmPassword, 
                                HttpSession session, 
                                Model model) {
        String email = (String) session.getAttribute("resetEmail");
        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");

        if (email == null || otpVerified == null || !otpVerified) {
            return "redirect:/login";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu không khớp!");
            model.addAttribute("showResetForm", true);
            return "login";
        }

        User user = userService.findByEmail(email);
        user.setPassword(newPassword);
        userService.save(user); // Lưu lại

        session.removeAttribute("resetEmail");
        session.removeAttribute("otpVerified");

        model.addAttribute("message", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
        return "login";
    }
}