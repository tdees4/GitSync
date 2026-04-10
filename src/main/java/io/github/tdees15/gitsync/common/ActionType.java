package io.github.tdees15.gitsync.common;

public enum ActionType {
    OPENED("opened"),
    CLOSED("closed"),
    DELETED("deleted"),
    ASSIGNED("assigned");

    private final String name;

    ActionType(String name) {
        this.name = name;
    }

    public static ActionType fromString(String name) {
        for (ActionType action : ActionType.values()) {
            if (name.equalsIgnoreCase(action.name))
                return action;
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
