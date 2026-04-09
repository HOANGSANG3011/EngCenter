package com.hutech.TrungTamTiengAnh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbCleanupController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/admin/db-cleanup")
    public String cleanup() {
        StringBuilder result = new StringBuilder();
        try {
            // 1. Delete duplicate teachers
            int teachersDeleted = jdbcTemplate.update(
                    "DELETE t1 FROM teacher t1 INNER JOIN teacher t2 WHERE t1.id > t2.id AND t1.ten = t2.ten");
            
            // 2. Delete duplicate/invalid dangky
            int dangKyDeleted = jdbcTemplate.update(
                    "DELETE d1 FROM dang_ky d1 INNER JOIN dang_ky d2 WHERE d1.id > d2.id AND d1.student_id = d2.student_id AND d1.lop_hoc_id = d2.lop_hoc_id");
            
            // 3. Delete invalid hoc_phi
            int hocPhiDeleted = jdbcTemplate.update(
                    "DELETE h1 FROM hoc_phi h1 INNER JOIN hoc_phi h2 WHERE h1.id > h2.id AND h1.student_id = h2.student_id AND h1.lop_hoc_id = h2.lop_hoc_id");

            result.append(String.format("Cleanup successful: %d duplicate teachers, %d duplicate registrations, %d duplicate tuition records deleted.", 
                    teachersDeleted, dangKyDeleted, hocPhiDeleted));
        } catch (Exception e) {
            result.append("Cleanup part 1-3 failed: ").append(e.getMessage());
        }

        // 4. Drop old 'mo_dang_ky' column if exists
        try {
            jdbcTemplate.execute("ALTER TABLE lop_hoc DROP COLUMN mo_dang_ky");
            result.append(" | Column 'mo_dang_ky' dropped successfully.");
        } catch (Exception e) {
            // Probably already dropped or doesn't exist
            result.append(" | Note: Column 'mo_dang_ky' not found or already dropped.");
            System.out.println("Drop mo_dang_ky failed (likely already gone): " + e.getMessage());
        }

        return result.toString();
    }
}
