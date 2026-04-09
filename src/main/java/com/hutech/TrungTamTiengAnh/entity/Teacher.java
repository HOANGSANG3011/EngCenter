package com.hutech.TrungTamTiengAnh.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ten;

    @Column(name = "mon")
    private String mucDo;

    // 🎨 MÀU HIỂN THỊ
    private String color;

    @OneToMany(mappedBy = "giaoVien", cascade = CascadeType.ALL)
    private List<LopHoc> lopHocs;

    private String imagePath; // 📸 ĐƯỜNG DẪN ẢNH

    // ===== Getter Setter =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getMucDo() {
        return mucDo;
    }

    public void setMucDo(String mucDo) {
        this.mucDo = mucDo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<LopHoc> getLopHocs() {
        return lopHocs;
    }

    public void setLopHocs(List<LopHoc> lopHocs) {
        this.lopHocs = lopHocs;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}