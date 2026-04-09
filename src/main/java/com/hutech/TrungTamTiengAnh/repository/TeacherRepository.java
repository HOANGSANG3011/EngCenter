package com.hutech.TrungTamTiengAnh.repository;

import com.hutech.TrungTamTiengAnh.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    @Query("SELECT t.mucDo, COUNT(t) FROM Teacher t GROUP BY t.mucDo")
    List<Object[]> thongKeTheoMucDo();
    
    Long countByTen(String ten);
}