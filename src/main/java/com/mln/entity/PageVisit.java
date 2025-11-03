package com.mln.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "page_visits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String page;
    
    @Column(nullable = false)
    private Integer visits = 0;
}

