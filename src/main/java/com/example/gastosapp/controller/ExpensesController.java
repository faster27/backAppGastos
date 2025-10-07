package com.example.gastosapp.controller;

import com.example.gastosapp.dto.CreateExpenseDto;
import com.example.gastosapp.dto.UpdateExpenseDto;
import com.example.gastosapp.model.Expense;
import com.example.gastosapp.service.ExpenseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/expenses")
public class ExpensesController {

    @Autowired
    private ExpenseService expenseService;

    private String getUserIdOrThrow(HttpServletRequest req) {
        String userId = (String) req.getAttribute("userId");
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid token");
        }
        return userId;
    }

    @GetMapping("/summary/{year}/{month}")
    public ResponseEntity<?> listByMonth(@PathVariable int year, @PathVariable int month, HttpServletRequest req) {
        String userId = getUserIdOrThrow(req);
        List<Expense> expenses = expenseService.getByMonth(userId, year, month);
        return ResponseEntity.ok(Map.of("year", year, "month", month, "expenses", expenses));
    }

    @PostMapping("/{year}/{month}")
    public ResponseEntity<?> create(@PathVariable int year, @PathVariable int month,
            @Valid @RequestBody CreateExpenseDto dto,
            HttpServletRequest req) {
        String userId = getUserIdOrThrow(req);
        Expense e = expenseService.createExpense(userId, year, month, dto);
        log.info("Expense created by {} for {}/{}", userId, month, year);
        return ResponseEntity.status(HttpStatus.CREATED).body(e);
    }

    @GetMapping("/{year}/{month}/{expenseId}")
    public ResponseEntity<?> getOne(@PathVariable int year, @PathVariable int month,
            @PathVariable String expenseId, HttpServletRequest req) {
        String userId = getUserIdOrThrow(req);
        Expense e = expenseService.getById(userId, expenseId);
        if (e == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found");
        }
        return ResponseEntity.ok(e);
    }

    @PutMapping("/{year}/{month}/{expenseId}")
    public ResponseEntity<?> update(@PathVariable int year, @PathVariable int month,
            @PathVariable String expenseId,
            @Valid @RequestBody UpdateExpenseDto dto,
            HttpServletRequest req) {
        String userId = getUserIdOrThrow(req);
        Expense updated = expenseService.updateExpense(userId, expenseId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{year}/{month}/{expenseId}")
    public ResponseEntity<?> delete(@PathVariable int year, @PathVariable int month,
            @PathVariable String expenseId, HttpServletRequest req) {
        String userId = getUserIdOrThrow(req);
        expenseService.deleteExpense(userId, expenseId);
        log.info("Expense {} deleted by {}", expenseId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{year}/{month}/init")
    public ResponseEntity<?> initMonth(@PathVariable int year, @PathVariable int month, HttpServletRequest req) {
        String userId = getUserIdOrThrow(req);
        expenseService.initMonth(userId, year, month);
        return ResponseEntity.ok(Map.of("message", "initialized"));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchExpenses(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String paymentMethod,
            HttpServletRequest req) {

        String userId = getUserIdOrThrow(req);
        List<Expense> expenses = expenseService.searchExpenses(userId, q, category, paymentMethod);
        return ResponseEntity.ok(expenses);
    }
}
