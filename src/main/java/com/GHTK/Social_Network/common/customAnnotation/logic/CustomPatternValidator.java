package com.GHTK.Social_Network.common.customAnnotation.logic;

public enum CustomPatternValidator {
    EMAIL("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", "Invalid email address format."),

    PHONE_NUMBER("^(84|0[3|5|7|8|9])([0-9]{8})$", "Invalid phone number format."),

    FILTER_FRIEND_STATUS("(?i)^(REQUESTED|PENDING|CLOSE_FRIEND|SIBLING|PARENT|OTHER)$", "Invalid friendship status."),

    UPDATE_FRIEND_STATUS("(?i)^(CLOSE_FRIEND|SIBLING|PARENT|OTHER)$", "Invalid friendship status."),

    BINARY("^(0|1)$","Invalid boolean format."),

    REACTION_TYPE("(?i)^(LIKE|LOVE|SMILE|ANGRY)$", "Invalid reaction format."),

    STRONG_PASSWORD( "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()]).{8,}$", "Password: Must be 8 characters long and combination of uppercase letters, lowercase letters, numbers, special characters."),
    ORDER_BY("(?i)^(ASC|DESC)$", "Order by: Must be a valid order by or not."),
    SORT_BY("(?i)^(createAt|updateAt)$", "Sort by: Must be a valid sort by or not."),
    GENDER("(?i)^(MALE|FEMALE|OTHER)$", "Gender: Must be a valid genre or not."),
    GROUP_TYPE("(?i)^(GROUP_PUBLIC|GROUP_PRIVATE)$","Group type: Must be GROUP");
    private final String pattern;
    private final String message;

    CustomPatternValidator(String pattern, String message) {
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
