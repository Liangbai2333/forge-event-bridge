package site.liangbai.forgeeventbridge.event;

import java.util.Objects;

public final class EventBridge {
    public static Builder builder() {
        return new Builder();
    }

    private final String source;

    private final EventPriority priority;

    private final Bus bus;

    private final boolean receiveCanceled;

    public EventBridge(
            String source,
            EventPriority priority,
            Bus bus,
            boolean receiveCanceled
    ) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(priority);
        Objects.requireNonNull(bus);

        this.source = source;
        this.priority = priority;
        this.bus = bus;
        this.receiveCanceled = receiveCanceled;
    }

    public String getSource() {
        return source;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public boolean isReceiveCanceled() {
        return receiveCanceled;
    }

    public Bus getBus() {
        return bus;
    }

    public String getSourceASMClassName() {
        return "L" + getSource().replace(".", "/") + ";";
    }

    public static final class Builder {
        private String source;

        private EventPriority priority = EventPriority.NORMAL;

        private Bus bus = Bus.FORGE;

        private boolean receiveCanceled;

        public Builder source(String source) {
            this.source = source;

            return this;
        }

        public Builder target(Class<?> target) {
            return source(target.getName());
        }

        public Builder bus(Bus bus) {
            this.bus = bus;

            return this;
        }

        public Builder priority(EventPriority priority) {
            this.priority = priority;

            return this;
        }

        public Builder receiveCanceled(boolean receiveCanceled) {
            this.receiveCanceled = receiveCanceled;

            return  this;
        }
    }

    public enum Bus {
        FORGE,
        MOD
    }
}
