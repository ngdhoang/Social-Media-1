package com.GHTK.Social_Network.common.customAnnotation.logic;

public enum ValidationPattern {
    EMAIL("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", "Invalid email address format."),
    PHONE("^(84|0[3|5|7|8|9])([0-9]{8})$", "Invalid phone number format."),
    FILTER_FRIEND_STATUS("(?i)^(REQUESTED|PENDING|CLOSE_FRIEND|SIBLING|PARENT|BLOCK|OTHER)$", "Invalid friendship status."),
    UPDATE_FRIEND_STATUS("(?i)^(CLOSE_FRIEND|SIBLING|PARENT|OTHER)$", "Invalid friendship status."),
    BINARY("^(0|1)$","Invalid boolean format."),
    REACTION_TYPE("(?i)^(LIKE|LOVE|SMILE|ANGRY)$", "Invalid reaction format.");
    private final String pattern;
    private final String message;

    ValidationPattern(String pattern, String message) {
        this.pattern = pattern;
        this.message = message;
    }

    public String getPattern() {
        return pattern;
    }

    public String getMessage() {
        return message;
    }
}
