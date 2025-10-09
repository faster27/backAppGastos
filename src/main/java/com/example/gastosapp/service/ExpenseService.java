package com.example.gastosapp.service;
import com.example.gastosapp.dto.CreateExpenseDto;
import com.example.gastosapp.dto.UpdateExpenseDto;
import com.example.gastosapp.model.Expense;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface ExpenseService {

    List<Expense> getByYearMonthDay(String userId, String ymd, String date);

    Expense createExpense(String userId, CreateExpenseDto dto);

    Expense getById(String userId, String expenseId);

    Expense updateExpense(String userId, String expenseId, UpdateExpenseDto dto);

    void deleteExpense(String userId, String expenseId);

    void initMonth(String userId, int year, int month);
    
    List<Expense> searchExpenses(String userId, String category, String paymentMethod);
}
