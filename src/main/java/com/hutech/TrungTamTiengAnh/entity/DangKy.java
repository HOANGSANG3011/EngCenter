package com.hutech.TrungTamTiengAnh.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class DangKy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User student;

    @ManyToOne
    private LopHoc lopHoc;

    private String trangThai = "CHO_DUYET";

    private LocalDate ngayDangKy = LocalDate.now();

    // 🔥 học phí
    private double hocPhi;

    // 🔥 trạng thái thanh toán
    private String thanhToan = "CHUA_DONG";

    // 🔥 thông tin thanh toán
    private String hoTenThanhToan;
    private String phuongThuc; // TIEN_MAT | CHUYEN_KHOAN | QR
    private String maGiaoDich;

    // ===== Getter Setter =====

    public Long getId() { return id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public LopHoc getLopHoc() { return lopHoc; }
    public void setLopHoc(LopHoc lopHoc) { this.lopHoc = lopHoc; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public LocalDate getNgayDangKy() { return ngayDangKy; }
    public void setNgayDangKy(LocalDate ngayDangKy) { this.ngayDangKy = ngayDangKy; }

    public double getHocPhi() { return hocPhi; }
    public void setHocPhi(double hocPhi) { this.hocPhi = hocPhi; }

    public String getThanhToan() { return thanhToan; }
    public void setThanhToan(String thanhToan) { this.thanhToan = thanhToan; }

    public String getHoTenThanhToan() { return hoTenThanhToan; }
    public void setHoTenThanhToan(String hoTenThanhToan) { this.hoTenThanhToan = hoTenThanhToan; }

    public String getPhuongThuc() { return phuongThuc; }
    public void setPhuongThuc(String phuongThuc) { this.phuongThuc = phuongThuc; }

    public String getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }
}