package com.hutech.TrungTamTiengAnh.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String thu;

    private int tietBatDau;
    private int tietKetThuc;

    private String buoi;

    private String monHoc;

    // ✅ THÊM FIELD NGÀY
    private LocalDate ngay;

    private Integer nam;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher giaoVien;

    // ===== Getter Setter =====

    public Long getId() { return id; }

    public String getThu() { return thu; }
    public void setThu(String thu) { this.thu = thu; }

    public int getTietBatDau() { return tietBatDau; }
    public void setTietBatDau(int tietBatDau) { this.tietBatDau = tietBatDau; }

    public int getTietKetThuc() { return tietKetThuc; }
    public void setTietKetThuc(int tietKetThuc) { this.tietKetThuc = tietKetThuc; }

    public String getBuoi() { return buoi; }
    public void setBuoi(String buoi) { this.buoi = buoi; }

    public String getMonHoc() { return monHoc; }
    public void setMonHoc(String monHoc) { this.monHoc = monHoc; }

    public LocalDate getNgay() { return ngay; }
    public void setNgay(LocalDate ngay) { this.ngay = ngay; }

    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }

    public Teacher getGiaoVien() { return giaoVien; }
    public void setGiaoVien(Teacher giaoVien) { this.giaoVien = giaoVien; }
}