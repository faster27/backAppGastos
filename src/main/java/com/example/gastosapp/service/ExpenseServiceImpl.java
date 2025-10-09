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
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    private final DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE;

    public Expense createExpense(String userId, CreateExpenseDto dto) {
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Expense e = new Expense();
        e.setId(UUID.randomUUID().toString());
        e.setUserId(userId);
        LocalDate date = LocalDate.parse(dto.date(), DF);
        e.setDate(date);
        e.setDescription(dto.description());
        e.setAmount(dto.amount());
        e.setCategory(dto.category());
        e.setPaymentMethod(dto.paymentMethod());
        e.setYearMonth(String.format("%04d-%02d", date.getYear(), date.getMonthValue()));
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

    public List<Expense> getByYearMonthDay(String userId, String ymd, String date) {
        if (date == null || date.isBlank() || ymd == null || ymd.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date and ymd are required");
        }
        
        List<Expense> expenses = null;
        
        try {
            if (ymd.equals("year")) {
                // Para año, esperamos solo el año (ej: "2025")
                int year = Integer.parseInt(date);
                expenses = expenseRepository.findByUserIdAndYear(userId, year);
            } else if (ymd.equals("month")) {
                // Para mes, esperamos formato YYYY-MM (ej: "2025-10")
                String[] parts = date.split("-");
                if (parts.length != 2) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid month format. Expected format: YYYY-MM");
                }
                int month = Integer.parseInt(parts[1]);
                if (month < 1 || month > 12) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid month. Must be between 1 and 12");
                }
                expenses = expenseRepository.findByUserIdAndMonth(userId, month);
            } else if (ymd.equals("day")) {
                // Para día, esperamos una fecha completa (ej: "2025-10-09")
                LocalDate parsedDate = LocalDate.parse(date, DF);
                expenses = expenseRepository.findByUserIdAndDay(userId, parsedDate.getDayOfMonth());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ymd must be 'year', 'month', or 'day'");
            }
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid number format in date");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Expected: year=YYYY, month=YYYY-MM, day=YYYY-MM-DD");
        }
        
        return expenses;
    }
    
    public List<Expense> searchExpenses(String userId, String category, String paymentMethod) {
        return expenseRepository.searchExpenses(userId,
                category,
                paymentMethod);
    }
}
