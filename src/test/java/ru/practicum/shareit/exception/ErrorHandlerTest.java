package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

//    @Test
//    public void handleValidationExceptionTest() {
//        MethodArgumentNotValidException e = new MethodArgumentNotValidException("Internal server error");
//        ErrorResponse errorResponse = handler.handleThrowable(e);
//        assertNotNull(errorResponse);
//        assertEquals(errorResponse.getError(), "Internal server error");
//    }
}
