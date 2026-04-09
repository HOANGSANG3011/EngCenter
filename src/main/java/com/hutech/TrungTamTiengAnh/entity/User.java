package com.hutech.TrungTamTiengAnh.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String fullName;

    @Column(unique = true)
    private String email;

    private String phoneNumber;

    private String password;

    private String role; // ADMIN hoặc STUDENT
}