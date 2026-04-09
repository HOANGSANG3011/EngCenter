package com.hutech.TrungTamTiengAnh.repository;

import com.hutech.TrungTamTiengAnh.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByStudentId(Long studentId);
    QuizResult findByStudentIdAndQuizId(Long studentId, Long quizId);
    
    @Query("SELECT qr FROM QuizResult qr WHERE qr.quiz.teacher.id = :teacherId")
    List<QuizResult> findByQuizTeacherId(Long teacherId);
    
    @Query("SELECT AVG(qr.score) FROM QuizResult qr WHERE qr.student.id = :studentId")
    Double getAverageScoreByStudentId(Long studentId);

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM QuizResult qr WHERE qr.quiz.id = :quizId")
    void deleteByQuizId(Long quizId);
}
