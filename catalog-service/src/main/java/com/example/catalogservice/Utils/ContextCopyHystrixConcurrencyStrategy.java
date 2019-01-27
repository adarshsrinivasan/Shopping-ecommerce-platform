package com.example.catalogservice.Utils;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
@Slf4j
public class ContextCopyHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

    public ContextCopyHystrixConcurrencyStrategy() {
        HystrixPlugins.getInstance().registerConcurrencyStrategy(this);
    }

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return new MyCallable(callable, ThreadLocalHelper.getCorrelationId());
    }

    public static class MyCallable<T> implements Callable<T> {

        private final Callable<T> actual;
        private final String correlationId;

        public MyCallable(Callable<T> callable, String correlationId) {
            this.actual = callable;
            this.correlationId = correlationId;
        }

        @Override
        public T call() throws Exception {
            ThreadLocalHelper.setCorrelationId(correlationId);
            try {
                return actual.call();
            } finally {
                ThreadLocalHelper.setCorrelationId(null);
            }
        }
    }
}
