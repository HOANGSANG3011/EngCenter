package com.hutech.TrungTamTiengAnh.repository;

import com.hutech.TrungTamTiengAnh.entity.PhanCong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhanCongRepository extends JpaRepository<PhanCong, Long> {

    PhanCong findByThuAndTiet(String thu, int tiet);

}