package com.hutech.TrungTamTiengAnh.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, OtpInfo> otpCache = new ConcurrentHashMap<>();

    @Getter
    @AllArgsConstructor
    private static class OtpInfo {
        private String otp;
        private LocalDateTime expiryTime;
    }

    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpCache.put(email, new OtpInfo(otp, LocalDateTime.now().plusMinutes(5)));
        return otp;
    }

    public String getOtp(String email) {
        OtpInfo info = otpCache.get(email);
        return info != null ? info.getOtp() : null;
    }

    public boolean validateOtp(String email, String otp) {
        OtpInfo info = otpCache.get(email);
        if (info == null) return false;
        
        if (info.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpCache.remove(email);
            return false;
        }

        boolean isValid = info.getOtp().equals(otp);
        if (isValid) {
            otpCache.remove(email); // Xóa sau khi dùng thành công
        }
        return isValid;
    }
}
