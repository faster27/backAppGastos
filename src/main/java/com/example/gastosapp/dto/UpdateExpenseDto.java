package com.example.gastosapp.dto;

public record UpdateExpenseDto(String date, String description, Integer amount, String category, String paymentMethod) { }
