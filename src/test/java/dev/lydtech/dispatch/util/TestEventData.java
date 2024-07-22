package dev.lydtech.dispatch.util;

import dev.lydtech.dispatch.message.DispatchPreparing;

import java.util.UUID;

public class TestEventData {

    public static DispatchPreparing buildDispatchPreparingEvent(UUID uuid, String string) {
        return DispatchPreparing.builder()
                .orderId(uuid)
                .build();
    }

}
