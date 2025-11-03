package com.mln.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "additional_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String category; // "devices", "users", "sources"
    
    @Column(nullable = false)
    private String keyName; // e.g., "mobile", "newUsers", "search"
    
    @Column(nullable = false)
    private String value; // Can be percentage, number as string, etc.
    
    // Composite unique constraint
    @Column(nullable = false, unique = true, name = "composite_key")
    private String compositeKey; // category_keyName for unique constraint
}

