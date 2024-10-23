package dev.lydtech.dispatch.handler;

import dev.lydtech.dispatch.message.DispatchCompleted;
import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.dispatch.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@KafkaListener(
        id = "dispatchPreparingConsumerClient",
        topics = "dispatch.tracking",
        groupId = "dev.lydtech.dispatch.tracking",
        containerFactory = "kafkaListenerContainerFactory"
)
public class DispatchTrackingHandler {

    @Autowired
    private final TrackingService trackingService;

    @KafkaHandler
    public void listenPreparing(DispatchPreparing payload) {
        log.info("Received message: payload: " + payload);
        try {
            trackingService.processPreparing(payload);
        } catch (Exception e) {
            log.error("Processing failure", e);
        }
    }

    @KafkaHandler
    public void listenCompleted(DispatchCompleted payload) {
        log.info("Received message: payload: " + payload);
        try {
            trackingService.processCompleted(payload);
        } catch (Exception e) {
            log.error("Processing failure", e);
        }
    }

}
