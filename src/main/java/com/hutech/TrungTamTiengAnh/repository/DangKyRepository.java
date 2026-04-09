package com.hutech.TrungTamTiengAnh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import jakarta.transaction.Transactional;
import com.hutech.TrungTamTiengAnh.entity.DangKy;

import java.util.List;

public interface DangKyRepository extends JpaRepository<DangKy, Long> {

    List<DangKy> findByStudentId(Long studentId);
    List<DangKy> findByLopHocId(Long lopHocId);
    DangKy findFirstByStudentIdAndLopHocId(Long studentId, Long lopHocId);
    DangKy findByStudentIdAndLopHocId(Long studentId, Long lopHocId);
    
    long countByStudentId(Long studentId);
    long countByStudentIdAndLopHocNgayHocBefore(Long studentId, java.time.LocalDate date);
    
    @Query("SELECT SUM(dk.hocPhi) FROM DangKy dk WHERE dk.student.id = :studentId AND dk.thanhToan = 'DA_DONG'")
    Double sumHocPhiByStudentId(Long studentId);

    long count();

    @Query("""
        SELECT MONTH(d.ngayDangKy), COUNT(d)
        FROM DangKy d
        GROUP BY MONTH(d.ngayDangKy)
        ORDER BY MONTH(d.ngayDangKy)
    """)
    List<Object[]> thongKeTheoThang();

    @Query("""
        SELECT d.trangThai, COUNT(d)
        FROM DangKy d
        GROUP BY d.trangThai
    """)
    List<Object[]> thongKeTheoTrangThai();

    @Modifying
    @Transactional
    @Query("DELETE FROM DangKy d WHERE d.lopHoc.id = :lopHocId")
    void deleteByLopHocId(Long lopHocId);
}