package com.tomtom.orbis.odp.layers.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.tomtom.orbis.odp.layers.impl.RetryHelper.BACKOFF;
import static com.tomtom.orbis.odp.layers.impl.RetryHelper.DELAY;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;


class RetryHelperTest {
    AutoCloseable openMocks;
    @Mock
    private MockClient client;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }
    @Test
    void testAlwaysFailing() {
        // given
        int retries = 2;
        Mockito.doThrow(IllegalArgumentException.class).when(client).doStuff();
        // when-then
        assertThrows(RetryHelper.RetryFailedException.class,
                () -> RetryHelper.doWithRetry(() -> client.doStuff(), retries, DELAY, BACKOFF, List.of(IllegalArgumentException.class)));
        Mockito.verify(client, times(retries + 1)).doStuff();
    }

    @Test
    void testUnsupportedExceptions() {
        Mockito.doThrow(IllegalArgumentException.class).when(client).doStuff();
        assertThrows(RuntimeException.class,
                () -> RetryHelper.doWithRetry(() -> client.doStuff(), List.of()));
        Mockito.verify(client, times(1)).doStuff();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    private interface MockClient {
        Void doStuff();
    }

}