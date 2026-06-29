package net.weyne1.easegui.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static net.weyne1.easegui.client.EaseGUIClient.LOGGER;

public class EaseGUIDebug {
    public static final boolean IS_DEV = Boolean.getBoolean("easegui.debug");

    private static final Map<String, Long> LAST_LOG_TIMES = new ConcurrentHashMap<>();
    private static final long LOG_COOLDOWN_MS = 5000;


    public static void reportError(String logKey, Supplier<String> messageSupplier) {
        if (IS_DEV) {
            throw new IllegalStateException("[EaseGUI Critical] " + messageSupplier.get());
        } else {
            long now = System.currentTimeMillis();
            long lastLog = LAST_LOG_TIMES.getOrDefault(logKey, 0L);

            if (now - lastLog > LOG_COOLDOWN_MS) {
                LOGGER.error("[EaseGUI] {}", messageSupplier.get());
                LAST_LOG_TIMES.put(logKey, now);
            }
        }
    }

    public static void validate(boolean condition, String logKey, Supplier<String> messageSupplier) {
        if (!condition) {
            reportError(logKey, messageSupplier);
        }
    }

    public static void devAssert(boolean condition, Supplier<String> devMessageSupplier) {
        if (IS_DEV && !condition) {
            throw new AssertionError("[EaseGUI DevAssert] " + devMessageSupplier.get());
        }
    }
}