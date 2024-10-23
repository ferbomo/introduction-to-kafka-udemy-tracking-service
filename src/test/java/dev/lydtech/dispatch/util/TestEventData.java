package dev.lydtech.dispatch.util;

import dev.lydtech.dispatch.message.DispatchCompleted;
import dev.lydtech.dispatch.message.DispatchPreparing;

import java.time.LocalDateTime;
import java.util.UUID;

public class TestEventData {

    public static DispatchPreparing buildDispatchPreparingEvent(UUID uuid) {
        return DispatchPreparing.builder()
                .orderId(uuid)
                .build();
    }

    public static DispatchCompleted buildDispatchCompletedEvent(UUID uuid, LocalDateTime localDateTime) {
        return DispatchCompleted.builder()
                .orderId(uuid)
                .date(localDateTime.toString())
                .build();
    }

}
