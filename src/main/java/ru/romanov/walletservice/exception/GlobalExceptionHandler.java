package ru.romanov.walletservice.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.romanov.walletservice.model.dto.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWalletNotFound(WalletNotFoundException e) {
        var status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), status.getReasonPhrase(), e.getMessage()));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException e) {
        var status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), status.getReasonPhrase(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
        var status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), status.getReasonPhrase(), e.getMessage()));
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException e) {
        var status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), status.getReasonPhrase(), e.getMessage()));
    }
}
