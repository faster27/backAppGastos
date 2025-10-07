package com.example.gastosapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    private String email;

    private String name;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
    
    

    @PrePersist
    public void prePersist() {
        createdAt = OffsetDateTime.now();
    }
}


