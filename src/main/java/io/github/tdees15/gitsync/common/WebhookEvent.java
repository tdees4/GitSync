package io.github.tdees15.gitsync.common;

public enum WebhookEvent {
    ALL("*"),
    PING("ping"),
    PUSH("push");

    private final String name;

    WebhookEvent(String name) {
        this.name = name;
    }

    public static WebhookEvent fromString(String name) {
        for (WebhookEvent event : WebhookEvent.values()) {
            if (name.equalsIgnoreCase(event.name))
                return event;
        }
        throw new IllegalArgumentException("No valid Webhook Event called " + name);
    }

    @Override
    public String toString() {
        return name;
    }
}
