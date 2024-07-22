package dev.lydtech.track.handler;

import dev.lydtech.track.message.DispatchPreparing;
import dev.lydtech.track.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DispatchTrackingHandler {

    private final TrackingService trackingService;

    @KafkaListener(
            id = "dispatchPreparingConsumerClient",
            topics = "dispatch.tracking",
            groupId = "dev.lydtech.dispatch.message",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(DispatchPreparing payload) {
        log.info("Received message: payload: " + payload);
        try {
            trackingService.process(payload);
        } catch (Exception e) {
            log.error("Processing failure", e);
        }
    }

}
