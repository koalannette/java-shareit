package ru.practicum.shareit.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    public void handleNotAvailableExceptionTest() {
        NotAvailableException e = new NotAvailableException("Не доступен запрос");
        ErrorResponse errorResponse = handler.handleNotAvailableException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), "Не доступен запрос");
    }

    @Test
    public void handleValidation_ReturnsBadRequest() {
        ErrorHandler errorHandler = new ErrorHandler();
        Exception exception = new ConstraintViolationException("Validation error", null, null);
        ErrorResponse errorResponse = errorHandler.handleValidation(exception);
        ResponseEntity<ErrorResponse> response = new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation error", response.getBody().getError());
    }

    @Test
    public void handleNotFoundExceptionTest() {
        NotFoundException e = new NotFoundException("Вещь не найдена");
        ErrorResponse errorResponse = handler.handleNotFoundException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), "Вещь не найдена");
    }

    @Test
    public void handleThrowableTest() {
        Throwable e = new Throwable("Internal server error");
        ErrorResponse errorResponse = handler.handleThrowable(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), "Internal server error");
    }

}

