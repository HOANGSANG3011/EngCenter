package com.hutech.TrungTamTiengAnh.repository;

import com.hutech.TrungTamTiengAnh.entity.DanhGia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DanhGiaRepository extends JpaRepository<DanhGia, Long> {

    // lấy review theo giáo viên
    List<DanhGia> findByTenGiaoVien(String tenGiaoVien);

    // lấy review đã được admin duyệt
    List<DanhGia> findByTenGiaoVienAndApprovedTrue(String tenGiaoVien);

    // admin xem review chưa duyệt
    List<DanhGia> findByApprovedFalse();

    long countByTenSinhVien(String tenSinhVien);
}