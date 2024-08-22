package com.GHTK.Social_Network.domain.collection.chat;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EGroupType {
  GROUP_PUBLIC,
  GROUP_PRIVATE,
  PERSONAL;

  public static  boolean isGroupPublic(EGroupType role) {
    return isGroupType(role) && role.toString().contains("PUBLIC");
  }

  public static boolean isGroupType(EGroupType type){
    return type.toString().contains("GROUP");
  }
}