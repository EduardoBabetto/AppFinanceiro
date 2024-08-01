package br.com.app.financeiro.exceptions;

public class FinanceiroException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private final int errorCode;

    public FinanceiroException(String message) {
        super(message);
        this.errorCode = 0;
    }

    public FinanceiroException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public FinanceiroException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = 0;
    }

    public FinanceiroException(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}


