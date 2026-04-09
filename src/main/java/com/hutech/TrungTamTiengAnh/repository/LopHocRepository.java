package com.hutech.TrungTamTiengAnh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hutech.TrungTamTiengAnh.entity.LopHoc;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface LopHocRepository extends JpaRepository<LopHoc, Long> {

    @Query("SELECT l.giaoVien.ten, COUNT(l) FROM LopHoc l GROUP BY l.giaoVien.ten")
    List<Object[]> thongKeLopHocTheoGiaoVien();
}