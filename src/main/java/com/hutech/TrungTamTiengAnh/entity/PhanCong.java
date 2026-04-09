package com.hutech.TrungTamTiengAnh.entity;

import jakarta.persistence.*;

@Entity
public class PhanCong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String thu;

    private int tiet;

    private String giaoVien;

    private String monHoc;

    public Long getId() {
        return id;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public int getTiet() {
        return tiet;
    }

    public void setTiet(int tiet) {
        this.tiet = tiet;
    }

    public String getGiaoVien() {
        return giaoVien;
    }

    public void setGiaoVien(String giaoVien) {
        this.giaoVien = giaoVien;
    }

    public String getMonHoc() {
        return monHoc;
    }

    public void setMonHoc(String monHoc) {
        this.monHoc = monHoc;
    }
}