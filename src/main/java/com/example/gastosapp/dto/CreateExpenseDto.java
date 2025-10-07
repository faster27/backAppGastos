package com.example.gastosapp.dto;

public record CreateExpenseDto(String date, String description, Integer amount, String category, String paymentMethod) { }
