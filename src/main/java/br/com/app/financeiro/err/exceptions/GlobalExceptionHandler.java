package br.com.app.financeiro.err.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.app.financeiro.err.ErrResponse;



@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FinanceiroException.class)
    public ResponseEntity<ErrResponse> handleFinanceiroException(FinanceiroException ex) {
        ErrResponse erro = new ErrResponse(ex.getMessage(), ex.getStatus().value());
        return new ResponseEntity<>(erro, HttpStatus.valueOf(ex.getStatus().value()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrResponse> handleUnauthorizedException(UnauthorizedException ex) {
        ErrResponse erro = new ErrResponse(ex.getMessage(), 401);
        return new ResponseEntity<>(erro, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrResponse> handleGenericException(Exception ex) {
        ErrResponse erro = new ErrResponse("Erro no servidor", 500);
        return new ResponseEntity<>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
