/*
 * Forge-Event-Bridge
 * Copyright (C) 2021  Liangbai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package site.liangbai.forgeeventbridge.event;

import java.util.Objects;

public final class EventBridge {
    public static Builder builder() {
        return new Builder();
    }

    private final String source;

    private final EventPriority priority;

    private final Bus bus;

    private final IEventBusProxy busProxy;

    private final boolean receiveCanceled;

    public EventBridge(
            String source,
            EventPriority priority,
            Bus bus,
            IEventBusProxy busProxy,
            boolean receiveCanceled
    ) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(priority);
        Objects.requireNonNull(bus);

        this.source = source;
        this.priority = priority;
        this.bus = bus;
        this.busProxy = busProxy;
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

    public IEventBusProxy getBusProxy() {
        return busProxy;
    }

    public String getASMSourceName() {
        return "L" + getSource().replace(".", "/") + ";";
    }

    public static final class Builder {
        private String source;

        private EventPriority priority = EventPriority.NORMAL;

        private Bus bus = Bus.FORGE;

        private IEventBusProxy busProxy = null;

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

        public Builder bus(IEventBusProxy busProxy) {
            this.bus = Bus.CUSTOM;
            this.busProxy = busProxy;

            return this;
        }

        public Builder priority(EventPriority priority) {
            this.priority = priority;

            return this;
        }

        public Builder receiveCanceled(boolean receiveCanceled) {
            this.receiveCanceled = receiveCanceled;

            return this;
        }

        public EventBridge build() {
            return new EventBridge(source, priority, bus, busProxy, receiveCanceled);
        }
    }

    public enum Bus {
        FORGE,
        MOD,
        CUSTOM
    }
}
