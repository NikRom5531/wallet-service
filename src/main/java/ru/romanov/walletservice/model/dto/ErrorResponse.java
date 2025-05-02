package ru.romanov.walletservice.model.dto;

import lombok.Data;

@Data
public class ErrorResponse {

    private boolean success;
    private String message;
    private Object data;

    public ErrorResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
