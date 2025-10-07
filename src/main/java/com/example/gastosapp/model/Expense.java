package com.example.gastosapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "expenses", indexes = {
        @Index(name = "idx_user_year_month", columnList = "user_id, year_month")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    private LocalDate date;

    private String description;

    private Integer amount; // en centavos

    private String category;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "year_month", nullable = false)
    private String yearMonth; // YYYY-MM

    @PrePersist
    public void prePersist() {
        createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
        if (yearMonth == null && date != null) {
            yearMonth = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
