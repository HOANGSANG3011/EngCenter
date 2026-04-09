package com.hutech.TrungTamTiengAnh.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        HttpSession session = req.getSession(false);

        // ===== 1. CHO PHÉP TRUY CẬP TỰ DO =====
        if (uri.equals("/") ||
                uri.equals("/login") ||
                uri.equals("/register") ||
                uri.equals("/forgot-password") ||
                uri.equals("/verify-otp") ||
                uri.equals("/reset-password") ||
                uri.startsWith("/admin/email") ||
                uri.startsWith("/admin/gmail") ||
                uri.startsWith("/admin/verify-otp") ||
                uri.startsWith("/admin/update-password") ||
                uri.startsWith("/student/gmail/") ||
                uri.startsWith("/css/") ||
                uri.startsWith("/js/") ||
                uri.startsWith("/images/") ||
                uri.startsWith("/fonts/")) {

            chain.doFilter(request, response);
            return;
        }

        // ===== 2. KIỂM TRA CHƯA LOGIN =====
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect("/login");
            return;
        }

        // ===== 3. NẾU ĐÃ LOGIN → CHO ĐI TIẾP =====
        chain.doFilter(request, response);
    }
}