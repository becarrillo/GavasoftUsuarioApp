package com.microservices.usuarioapp.exceptions.controllers;


import com.microservices.usuarioapp.exceptions.ResourceNotFoundException;
import com.microservices.usuarioapp.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handResourceNotFoundException(ResourceNotFoundException resourceNotFoundException) {
        String mensaje = resourceNotFoundException.getMessage();

        new ApiResponse();
        ApiResponse response = ApiResponse.builder()
                .message(mensaje)
                .success(false)
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
