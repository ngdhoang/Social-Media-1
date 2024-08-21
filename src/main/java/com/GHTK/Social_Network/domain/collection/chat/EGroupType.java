package com.GHTK.Social_Network.domain.collection.chat;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

public enum EGroupType {
  GROUP(Arrays.asList(EGroupRole.PUBLIC, EGroupRole.PRIVATE)),
  PERSONAL(Collections.emptyList());

  private final List<EGroupRole> allowedRoles;

  EGroupType(List<EGroupRole> allowedRoles) {
    this.allowedRoles = allowedRoles;
  }

  public List<EGroupRole> getAllowedRoles() {
    return allowedRoles;
  }

  public enum EGroupRole {
    PUBLIC,
    PRIVATE
  }

  public static EGroupType createGroupWithRole(EGroupRole role) {
    if (role == EGroupRole.PUBLIC || role == EGroupRole.PRIVATE) {
      return GROUP;
    } else {
      throw new IllegalArgumentException("Invalid group role");
    }
  }

  public boolean isRolePublic(EGroupRole role) {
    return allowedRoles.contains(role) && allowedRoles.contains(EGroupRole.PUBLIC);
  }
}