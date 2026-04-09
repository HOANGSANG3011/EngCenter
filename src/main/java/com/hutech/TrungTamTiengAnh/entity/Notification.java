package com.hutech.TrungTamTiengAnh.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;      // tiêu đề
    private String message;    // nội dung

    private String type;       // DANG_KY | HOC_PHI | KIEM_TRA
    private String status;     // SUCCESS | WARNING | DANGER

    private boolean isRead = false;

    private LocalDateTime createdAt;

    // ===== STUDENT =====
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    // ===== THÊM QUIZ =====
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    // ===== getter setter =====

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    // ===== getter setter QUIZ =====
    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
}