package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    GROUP_PUBLIC("group:public"),
    GROUP_PRIVATE("group:private")

    ;

    @Getter
    private final String permission;
}
