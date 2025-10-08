package com.example.gastosapp.service;

import com.example.gastosapp.dto.CreateExpenseDto;
import com.example.gastosapp.dto.UpdateExpenseDto;
import com.example.gastosapp.model.Expense;
import com.example.gastosapp.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    private final DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE;

    public List<Expense> getByMonth(String userId, int year, int month) {
        String ym = String.format("%04d-%02d", year, month);
        return expenseRepository.findByUserIdAndYearMonth(userId, ym);
    }

    public Expense createExpense(String userId, int year, int month, CreateExpenseDto dto) {
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Expense e = new Expense();
        e.setId(UUID.randomUUID().toString());
        e.setUserId(userId);
        LocalDate date = LocalDate.parse(dto.getDate(), DF);
        e.setDate(date);
        e.setDescription(dto.getDescription());
        e.setAmount(dto.getAmount());
        e.setCategory(dto.getCategory());
        e.setPaymentMethod(dto.getPaymentMethod());
        e.setYearMonth(String.format("%04d-%02d", year, month));
        return expenseRepository.save(e);
    }

    public Expense getById(String userId, String expenseId) {
        return expenseRepository.findByIdAndUserId(expenseId, userId).orElse(null);
    }

    public Expense updateExpense(String userId, String expenseId, UpdateExpenseDto dto) {
        Expense e = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (dto.date() != null) e.setDate(LocalDate.parse(dto.date(), DF));
        if (dto.description() != null) e.setDescription(dto.description());
        if (dto.amount() != null) e.setAmount(dto.amount());
        if (dto.category() != null) e.setCategory(dto.category());
        if (dto.paymentMethod() != null) e.setPaymentMethod(dto.paymentMethod());
        return expenseRepository.save(e);
    }

    public void deleteExpense(String userId, String expenseId) {
        Expense e = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        expenseRepository.delete(e);
    }

    public void initMonth(String userId, int year, int month) {
        // ejemplo sencillo: no-op o crear filas base si necesitas
    }
    
    public List<Expense> searchExpenses(String userId, String q, String category, String paymentMethod) {
        return expenseRepository.searchExpenses(userId,
                q == null ? "" : "%" + q.toLowerCase() + "%",
                category,
                paymentMethod);
    }
}
