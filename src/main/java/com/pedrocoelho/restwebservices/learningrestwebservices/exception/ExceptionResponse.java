package com.pedrocoelho.restwebservices.learningrestwebservices.exception;


import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

public class ExceptionResponse {
    private Date timestamp;
    private String message;
    private String details;

    ExceptionResponse(Date timestamp, String message, String details) {
        super();
        this.message = message;
        this.details = details;
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}
