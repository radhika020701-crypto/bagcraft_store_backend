package com.bagcraft.store.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "APP_USER")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // BCrypt hashed

    @Column(nullable = false)
    private String fullName;

    private String phone;
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.CUSTOMER;
}
