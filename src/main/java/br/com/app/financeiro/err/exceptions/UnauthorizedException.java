package br.com.app.financeiro.err.exceptions;

public class UnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private final int errorCode;

    public UnauthorizedException(String message) {
        super(message);
        this.errorCode = 0;
    }

    public UnauthorizedException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = 0;
    }

    public UnauthorizedException(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
