package com.example.gastosapp.repository;

import com.example.gastosapp.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, String> {

    List<Expense> findByUserIdAndYearMonth(String userId, String yearMonth);

    Optional<Expense> findByIdAndUserId(String id, String userId);

    @Query("""
        SELECT e FROM Expense e
        WHERE e.userId = :userId
          AND (:category IS NULL OR e.category = :category)
          AND (:paymentMethod IS NULL OR e.paymentMethod = :paymentMethod)
          AND (:q = '' OR LOWER(e.description) LIKE :q)
    """)
    List<Expense> searchExpenses(String userId, String q, String category, String paymentMethod);
}
