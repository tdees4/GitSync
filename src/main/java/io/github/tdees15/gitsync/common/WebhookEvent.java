package io.github.tdees15.gitsync.common;

public enum WebhookEvent {
    PING("ping"),
    PUSH("push"),
    ISSUES("issues");

    private final String name;

    WebhookEvent(String name) {
        this.name = name;
    }

    public static WebhookEvent fromString(String name) {
        for (WebhookEvent event : WebhookEvent.values()) {
            if (name.equalsIgnoreCase(event.name))
                return event;
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
