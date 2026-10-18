package com.bagcraft.store.model;

import jakarta.persistence.*;
import lombok.*;

// e.g. BAGS, JEWELRY, SAREES
@Entity
@Table(name = "CATEGORY")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String slug;
    private String description;
}
