package com.hutech.TrungTamTiengAnh.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseCleanupService {

    @Autowired
    private EntityManager entityManager;

    @PostConstruct
    @Transactional
    public void executeCleanup() {
        try {
            // Keep the earliest dang_ky for a given student and class, delete the rest
            String deleteDuplicateDangKy = 
                "DELETE t1 FROM dang_ky t1 " +
                "INNER JOIN dang_ky t2 " +
                "WHERE t1.id > t2.id " +
                "AND t1.student_id = t2.student_id " +
                "AND t1.lop_hoc_id = t2.lop_hoc_id";
            
            int dangKyDeleted = entityManager.createNativeQuery(deleteDuplicateDangKy).executeUpdate();
            System.out.println("----- Database Cleanup: Removed " + dangKyDeleted + " duplicate dang_ky records.");

            // Standardize class logic if needed
            // Fix any other 500 error DB problems found
            
            // Clean up missing/null teacher logic if teacher duplicate name exists
            String deleteDuplicateTeacher = 
                "DELETE t1 FROM teacher t1 " +
                "INNER JOIN teacher t2 " +
                "WHERE t1.id > t2.id " +
                "AND t1.ten = t2.ten";
            int teacherDeleted = entityManager.createNativeQuery(deleteDuplicateTeacher).executeUpdate();
            System.out.println("----- Database Cleanup: Removed " + teacherDeleted + " duplicate teachers.");

            // Remove duplicated hoc_phi based on student_id and lop_hoc_id
            String deleteDuplicateHocPhi = 
                "DELETE t1 FROM hoc_phi t1 " +
                "INNER JOIN hoc_phi t2 " +
                "WHERE t1.id > t2.id " +
                "AND t1.student_id = t2.student_id " +
                "AND t1.lop_hoc_id = t2.lop_hoc_id";
            int hocPhiDeleted = entityManager.createNativeQuery(deleteDuplicateHocPhi).executeUpdate();
            System.out.println("----- Database Cleanup: Removed " + hocPhiDeleted + " duplicate hoc_phi records.");

        } catch (Exception e) {
            System.out.println("----- Database Cleanup Failed (maybe tables don't exist yet or syntax error): " + e.getMessage());
        }
    }
}
