package br.com.app.financeiro.err.exceptions;

import org.springframework.http.HttpStatus;

public class FinanceiroException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private final int errorCode;

    private final HttpStatus status;

    public FinanceiroException(String message, HttpStatus status) {
        super(message);
        this.errorCode = 0;
        this.status = status;
    }

    public FinanceiroException(String message, int errorCode , HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    
    public FinanceiroException(String message, Throwable cause, HttpStatus status ) {
        super(message, cause);
        this.errorCode = 0;
        this.status = status;
    }

    public FinanceiroException(String message, int errorCode, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus(){
        return status;
    }
}


