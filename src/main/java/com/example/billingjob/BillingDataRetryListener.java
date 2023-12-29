package com.example.billingjob;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

public class BillingDataRetryListener implements RetryListener {
    
    @Override
    public void close(RetryContext context, RetryCallback callback, Throwable throwable) {
        Integer retryCount = context.getRetryCount();
        if (retryCount > 0) {
            System.out.println("Retry count: " + context.getRetryCount());
        }
    }

}
