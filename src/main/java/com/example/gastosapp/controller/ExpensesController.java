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

    // Obtener usuario o lanzar excepción
    private String getUserIdOrThrow(HttpServletRequest req) {
        String userId = (String) req.getAttribute("userId");
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid token");
        }
        return userId;
    }

    // Listar gastos por mes Y ear M onth D ay = year | month | day
    @GetMapping("/summary/{ymd}/{date}")
    public ResponseEntity<?> listByMonth(@PathVariable String ymd, @PathVariable String date, HttpServletRequest req) {
        String userId = getUserIdOrThrow(req);
        List<Expense> expenses = expenseService.getByYearMonthDay(userId, ymd, date);
        return ResponseEntity.ok(expenses);
    }

    // Crear gasto
    @PostMapping("/create")
    public ResponseEntity<?> create(
            @Valid @RequestBody CreateExpenseDto dto,
            HttpServletRequest req) {
        String userId = getUserIdOrThrow(req);
        Expense e = expenseService.createExpense(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(e);
    }

    // Obtener gasto por id
    @GetMapping("/getOne/{expenseId}")
    public ResponseEntity<?> getOne(@PathVariable String expenseId, HttpServletRequest req) {
        String userId = getUserIdOrThrow(req);
        Expense expense = expenseService.getById(userId, expenseId);
        if (expense == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found");
        }
        return ResponseEntity.ok(expense);
    }

    // Actualizar gasto
    @PutMapping("/update/{expenseId}")
    public ResponseEntity<?> update(
            @PathVariable String expenseId,
            @Valid @RequestBody UpdateExpenseDto dto,
            HttpServletRequest req) {
        String userId = getUserIdOrThrow(req);
        Expense updated = expenseService.updateExpense(userId, expenseId, dto);
        return ResponseEntity.ok(updated);
    }

    // Eliminar gasto
    @DeleteMapping("/{year}/{month}/{expenseId}")
    public ResponseEntity<?> delete(@PathVariable int year, @PathVariable int month,
            @PathVariable String expenseId, HttpServletRequest req) {
        String userId = getUserIdOrThrow(req);
        expenseService.deleteExpense(userId, expenseId);
        log.info("Expense {} deleted by {}", expenseId, userId);
        return ResponseEntity.noContent().build();
    }

    // Inicializar mes
    @PostMapping("/{year}/{month}/init")
    public ResponseEntity<?> initMonth(@PathVariable int year, @PathVariable int month, HttpServletRequest req) {
        String userId = getUserIdOrThrow(req);
        expenseService.initMonth(userId, year, month);
        return ResponseEntity.ok(Map.of("message", "initialized"));
    }

    // Buscar gastos
    @GetMapping("/search")
    public ResponseEntity<?> searchExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String paymentMethod,
            HttpServletRequest req) {

        String userId = getUserIdOrThrow(req);
        List<Expense> expenses = expenseService.searchExpenses(userId, category, paymentMethod);
        return ResponseEntity.ok(expenses);
    }
}
