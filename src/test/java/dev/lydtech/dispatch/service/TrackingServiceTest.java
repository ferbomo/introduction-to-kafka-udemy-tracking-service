package dev.lydtech.dispatch.service;

import dev.lydtech.dispatch.message.DispatchCompleted;
import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.dispatch.message.TrackingStatusUpdated;
import dev.lydtech.dispatch.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TrackingServiceTest {
    private static final String TRACKING_STATUS_TOPIC = "tracking.status";

    private KafkaTemplate<String, Object> kafkaProducerMock;
    private TrackingService service;

    @BeforeEach
    void setUp() {
        kafkaProducerMock = mock(KafkaTemplate.class);
        service = new TrackingService(kafkaProducerMock);
    }

    @Test
    void process_preparing_Success() throws Exception {
        when(kafkaProducerMock.send(anyString(), any(TrackingStatusUpdated.class))).thenReturn(mock(CompletableFuture.class));

        DispatchPreparing testEvent = TestEventData.buildDispatchPreparingEvent(randomUUID());
        service.processPreparing(testEvent);

        verify(kafkaProducerMock, times(1)).send(eq(TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));
    }

    @Test
    void testProcess_preparing_DispatchTrackingProducerThrowsException() {
        DispatchPreparing testEvent = TestEventData.buildDispatchPreparingEvent(randomUUID());
        doThrow(new RuntimeException("dispatch tracking producer failure")).when(kafkaProducerMock).send(eq(TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));

        Exception exception = assertThrows(RuntimeException.class, () -> service.processPreparing(testEvent));

        verify(kafkaProducerMock, times(1)).send(eq(TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));
        verifyNoMoreInteractions(kafkaProducerMock);
        assertThat(exception.getMessage(), equalTo("dispatch tracking producer failure"));
    }

    @Test
    void testProcess_preparing_OrderDispatchedProducerThrowsException() {
        DispatchPreparing testEvent = TestEventData.buildDispatchPreparingEvent(randomUUID());
        when(kafkaProducerMock.send(anyString(), any(TrackingStatusUpdated.class))).thenReturn(mock(CompletableFuture.class));
        doThrow(new RuntimeException("order dispatched producer failure")).when(kafkaProducerMock).send(eq(TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));

        Exception exception = assertThrows(RuntimeException.class, () -> service.processPreparing(testEvent));

        verify(kafkaProducerMock, times(1)).send(eq(TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));
        assertThat(exception.getMessage(), equalTo("order dispatched producer failure"));
    }

    @Test
    void process_completed_Success() throws Exception {
        when(kafkaProducerMock.send(anyString(), any(TrackingStatusUpdated.class))).thenReturn(mock(CompletableFuture.class));

        DispatchCompleted testEvent = TestEventData.buildDispatchCompletedEvent(randomUUID(), LocalDateTime.now());
        service.processCompleted(testEvent);

        verify(kafkaProducerMock, times(1)).send(eq(TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));
    }

    @Test
    void testProcess_completed_DispatchTrackingProducerThrowsException() {
        DispatchCompleted testEvent = TestEventData.buildDispatchCompletedEvent(randomUUID(), LocalDateTime.now());
        doThrow(new RuntimeException("dispatch tracking producer failure")).when(kafkaProducerMock).send(eq(TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));

        Exception exception = assertThrows(RuntimeException.class, () -> service.processCompleted(testEvent));

        verify(kafkaProducerMock, times(1)).send(eq(TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));
        verifyNoMoreInteractions(kafkaProducerMock);
        assertThat(exception.getMessage(), equalTo("dispatch tracking producer failure"));
    }

    @Test
    void testProcess_completed_OrderDispatchedProducerThrowsException() {
        DispatchCompleted testEvent = TestEventData.buildDispatchCompletedEvent(randomUUID(), LocalDateTime.now());
        when(kafkaProducerMock.send(anyString(), any(TrackingStatusUpdated.class))).thenReturn(mock(CompletableFuture.class));
        doThrow(new RuntimeException("order dispatched producer failure")).when(kafkaProducerMock).send(eq(TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));

        Exception exception = assertThrows(RuntimeException.class, () -> service.processCompleted(testEvent));

        verify(kafkaProducerMock, times(1)).send(eq(TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));
        assertThat(exception.getMessage(), equalTo("order dispatched producer failure"));
    }

}
