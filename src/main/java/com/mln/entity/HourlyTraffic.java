package com.mln.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hourly_traffic")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HourlyTraffic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String hour; // Format: "HH:00"
    
    @Column(nullable = false)
    private Integer users = 0;
}

