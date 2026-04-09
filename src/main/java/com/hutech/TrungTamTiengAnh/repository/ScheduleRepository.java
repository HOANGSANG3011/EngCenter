package com.hutech.TrungTamTiengAnh.repository;

import com.hutech.TrungTamTiengAnh.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // Lấy lịch theo tuần (ngày)
    List<Schedule> findByNgayBetween(LocalDate start, LocalDate end);

    List<Schedule> findByNgayAndBuoi(LocalDate ngay, String buoi);
    
    List<Schedule> findByGiaoVienIdAndNgay(Long giaoVienId, LocalDate ngay);

}