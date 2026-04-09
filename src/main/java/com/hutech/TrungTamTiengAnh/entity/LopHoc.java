package com.hutech.TrungTamTiengAnh.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "lop_hoc")
public class LopHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenLop;

    @Column(name = "lich_hoc")
    private String lichHoc = "Chưa có";

    @ManyToOne
    @JoinColumn(name = "giao_vien")
    private Teacher giaoVien;

    // ngày học
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_hoc")
    private LocalDate ngayHoc;

    // sĩ số tối đa
    @Column(name = "si_so")
    private int siSo;

    // số student đã đăng ký
    @Column(name = "da_dang_ky")
    private int daDangKy = 0;

    private double hocPhi;

    private String hinhAnh;

    // rating trung bình (không lưu DB)
    @Transient
    private Double avgRating = 0.0;

    public LopHoc() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public String getLichHoc() {
        return lichHoc;
    }

    public void setLichHoc(String lichHoc) {
        this.lichHoc = lichHoc;
    }

    public Teacher getGiaoVien() {
        return giaoVien;
    }

    public void setGiaoVien(Teacher giaoVien) {
        this.giaoVien = giaoVien;
    }

    public LocalDate getNgayHoc() {
        return ngayHoc;
    }

    public void setNgayHoc(LocalDate ngayHoc) {
        this.ngayHoc = ngayHoc;
    }

    public int getSiSo() {
        return siSo;
    }

    public void setSiSo(int siSo) {
        this.siSo = siSo;
    }

    public int getDaDangKy() {
        return daDangKy;
    }

    public void setDaDangKy(int daDangKy) {
        this.daDangKy = daDangKy;
    }

    public double getHocPhi() {
        return hocPhi;
    }

    public void setHocPhi(double hocPhi) {
        this.hocPhi = hocPhi;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }
}