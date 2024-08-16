package br.com.app.financeiro.err;

import java.time.LocalDateTime;

public class ErrResponse {
    
    private String message;
    private int status;
    private String path;
    private String timestamp;

    public ErrResponse() {}

    public ErrResponse(String message, int status){
        this.message = message;
        this.status = status;
        this.path = "";
        this.timestamp = LocalDateTime.now().toString();
    }

    public ErrResponse(String message, int status, String path){
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = LocalDateTime.now().toString();
    }

    public ErrResponse(String message, int status, String path, String timestamp){
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
}