package com.example.vendorservice.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VendorNotFoundException extends RuntimeException {

    public VendorNotFoundException(String message){super(message);}
    public VendorNotFoundException(Throwable cause){super(cause);}
    public VendorNotFoundException(String message, Throwable cause) { super(message, cause);}
}
