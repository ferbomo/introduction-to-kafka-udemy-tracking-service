package dev.lydtech.dispatch.handler;

import dev.lydtech.dispatch.message.DispatchCompleted;
import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.dispatch.service.TrackingService;
import dev.lydtech.dispatch.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.*;

class DispatchTrackingHandlerTest {

    private DispatchTrackingHandler handler;
    private TrackingService trackingServiceMock;

    @BeforeEach
    void setUp() {
        trackingServiceMock = mock(TrackingService.class);
        handler = new DispatchTrackingHandler(trackingServiceMock);
    }

    @Test
    void listen_preparing_Success() throws Exception {
        DispatchPreparing testEvent = TestEventData.buildDispatchPreparingEvent(randomUUID());
        handler.listenPreparing(testEvent);
        verify(trackingServiceMock, times(1)).processPreparing(testEvent);
    }

    @Test
    void listen_preparing_ServiceThrowsException() throws Exception {
        DispatchPreparing testEvent = TestEventData.buildDispatchPreparingEvent(randomUUID());
        doThrow(new RuntimeException("Service failure")).when(trackingServiceMock).processPreparing(testEvent);

        handler.listenPreparing(testEvent);

        verify(trackingServiceMock, times(1)).processPreparing(testEvent);
    }

    @Test
    void listen_completed_Success() throws Exception {
        DispatchCompleted testEvent = TestEventData.buildDispatchCompletedEvent(randomUUID(), LocalDateTime.now());
        handler.listenCompleted(testEvent);
        verify(trackingServiceMock, times(1)).processCompleted(testEvent);
    }

    @Test
    void listen_completed_ServiceThrowsException() throws Exception {
        DispatchCompleted testEvent = TestEventData.buildDispatchCompletedEvent(randomUUID(), LocalDateTime.now());
        doThrow(new RuntimeException("Service failure")).when(trackingServiceMock).processCompleted(testEvent);

        handler.listenCompleted(testEvent);

        verify(trackingServiceMock, times(1)).processCompleted(testEvent);
    }

}
