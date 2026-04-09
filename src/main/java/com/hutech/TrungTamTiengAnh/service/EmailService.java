package com.hutech.TrungTamTiengAnh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nghoangsang@gmail.com");
        message.setTo(to);
        message.setSubject("Mã xác minh (OTP) thay đổi mật khẩu - EngCenter");
        message.setText("Chào bạn,\n\nMã xác minh (OTP) của bạn là: " + otp + 
                        "\n\nMã này có hiệu lực trong vòng 5 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.\n\nTrân trọng,\nEngCenter Team");
        mailSender.send(message);
    }
}
