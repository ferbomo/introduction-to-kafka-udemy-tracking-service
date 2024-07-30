package dev.lydtech.dispatch.integration;


import dev.lydtech.dispatch.TrackingConfiguration;
import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.dispatch.message.TrackingStatusUpdated;
import dev.lydtech.dispatch.util.TestEventData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.UUID.randomUUID;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@SpringBootTest(classes = {TrackingConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(controlledShutdown = true)
public class DispatchTrackingIntegrationTest {

    private final static String DISPATCH_TRACKING_TOPIC = "dispatch.tracking";
    private final static String TRACKING_STATUS_TOPIC = "tracking.status";

    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    private KafkaListenerEndpointRegistry registry;
    @Autowired
    private KafkaTestListener testListener;

    @Configuration
    static class TestConfig {
        @Bean
        public KafkaTestListener testListener() {
            return new KafkaTestListener();
        }
    }

    /**
     * Use this receiver to consume messages from the outbound topics.
     */
    public static class KafkaTestListener {
        AtomicInteger trackingStatusUpdatedCounter = new AtomicInteger(0);

        @KafkaListener(groupId = "KafkaIntegrationTest", topics = TRACKING_STATUS_TOPIC)
        void receiveDispatchPreparing(@Payload TrackingStatusUpdated payload) {
            log.info("Received TrackingStatusUpdated: {}", payload);
            trackingStatusUpdatedCounter.incrementAndGet();
        }

    }

    @BeforeEach
    public void setUp() {
        testListener.trackingStatusUpdatedCounter.set(0);
        // Wait until the partitions are assigned.
        registry.getListenerContainers()
                .stream()
                .forEach(container -> ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic()));
    }

    /**
     * Send in an dispatchPreparing event and ensure the expected outbound event is emitted.
     */
    @Test
    public void testDispatchTrackingFlow() throws Exception {
        DispatchPreparing dispatchPreparing = TestEventData.buildDispatchPreparingEvent(randomUUID());
        sendMessage(DISPATCH_TRACKING_TOPIC, dispatchPreparing);

        await().atMost(1, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(testListener.trackingStatusUpdatedCounter::get, equalTo(1));
    }

    private void sendMessage(String topic, Object data) throws Exception {
        kafkaTemplate.send(MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build()).get();
    }

}
