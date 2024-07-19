package com.GHTK.Social_Network.domain.entity;

public enum EFriendshipStatus {
    PENDING,
    CLOSE_FRIEND,
    SIBLING,
    PARENT,
    BLOCK,
    OTHER;

    public static EFriendshipStatus fromString(String text) {
        for (EFriendshipStatus b : EFriendshipStatus.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    public static String toString(EFriendshipStatus status) {
        return status.toString();
    }

    public static boolean contains(String test) {
        for (EFriendshipStatus c : EFriendshipStatus.values()) {
            if (c.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
    }
