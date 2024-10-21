package com.tomtom.orbis.odp.layers.impl;

import lombok.extern.slf4j.Slf4j;
import java.util.Collection;
import java.util.concurrent.Callable;

@Slf4j
public final class RetryHelper {

    public static final int MAX_RETRIES = 5;
    public static final int DELAY = 1000;
    public static final double BACKOFF = 1.5;

    public static <T> T doWithRetry(final Callable<T> fun,
                                    final int maxRetries,
                                    final int delay,
                                    final double backoff,
                                    final Collection<Class<? extends Exception>> exceptions)
        throws RetryFailedException, InterruptedException {
        Exception lastException = null;
        for (int count = 0; count <= maxRetries; count++) {
            try {
                return fun.call();
            } catch (Exception e) {
                if (count == maxRetries) break;
                if (exceptions.stream().noneMatch(eClass -> e.getClass().isAssignableFrom(eClass))) {
                    log.error("Un-retryable exception caught: {}", e.getClass());
                    throw new RuntimeException(e);
                }
                lastException = e;
                long timeout = (long)(delay * (Math.pow(backoff, count)));
                log.warn("Caught {}, retrying after {} ms", lastException.getClass(), timeout);
                Thread.sleep(timeout);
            }
        }
        assert lastException != null;
        log.error("Retries exhausted, {} failed", fun.toString());
        throw new RetryFailedException(lastException);
    }

    public static <T> T doWithRetry(final Callable<T> fun,
                                    final Collection<Class<? extends Exception>> exceptions)
        throws InterruptedException {
        return doWithRetry(fun, MAX_RETRIES, DELAY, BACKOFF, exceptions);
    }

    public static class RetryFailedException extends RuntimeException {

        public RetryFailedException(Throwable cause) {
            super(cause);
        }
    }

}
