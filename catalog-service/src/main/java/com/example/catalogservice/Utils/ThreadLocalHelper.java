package com.example.catalogservice.Utils;

public class ThreadLocalHelper {
    private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();

    public static String getCorrelationId() {
        return CORRELATION_ID.get();
    }

    public static void setCorrelationId(String correlationId){
        CORRELATION_ID.set(correlationId);
    }
}
