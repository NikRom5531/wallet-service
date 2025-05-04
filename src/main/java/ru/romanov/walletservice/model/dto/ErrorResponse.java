package ru.romanov.walletservice.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private String timestamp;
    private String statusName;
    private int statusCode;
    private String message;

    public ErrorResponse(int statusCode, String message, String statusName) {
        this.statusCode = statusCode;
        this.message = message;
        this.statusName = statusName;
        this.timestamp = LocalDateTime.now().toString();
    }
}
