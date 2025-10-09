package com.example.gastosapp.repository;

import com.example.gastosapp.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, String> {
    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND EXTRACT(YEAR FROM e.date) = :year")
    List<Expense> findByUserIdAndYear(@Param("userId") String userId, @Param("year") int year);
    
    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND EXTRACT(MONTH FROM e.date) = :month")
    List<Expense> findByUserIdAndMonth(@Param("userId") String userId, @Param("month") int month);
    
    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND EXTRACT(DAY FROM e.date) = :day")
    List<Expense> findByUserIdAndDay(@Param("userId") String userId, @Param("day") int day);

    Optional<Expense> findByIdAndUserId(String id, String userId);

    @Query("""
        SELECT e FROM Expense e
        WHERE e.userId = :userId
          AND (:category IS NULL OR e.category = :category)
          AND (:paymentMethod IS NULL OR e.paymentMethod = :paymentMethod)
    """)
    List<Expense> searchExpenses(String userId, String category, String paymentMethod);
}
