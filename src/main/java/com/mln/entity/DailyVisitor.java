package com.mln.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_visitors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyVisitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String date; // Format: "dd/MM"
    
    @Column(nullable = false)
    private Integer visitors = 0;
    
    @Column(nullable = false)
    private Integer pageViews = 0;
    
    @Column(nullable = false, unique = true, name = "date_key")
    private String dateKey; // For unique constraint on date
}

