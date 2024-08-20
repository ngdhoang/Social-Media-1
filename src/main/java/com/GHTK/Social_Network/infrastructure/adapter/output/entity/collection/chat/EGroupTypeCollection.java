package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum EGroupTypeCollection {
  GROUP(Arrays.asList(EGroupRoleCollection.PUBLIC, EGroupRoleCollection.PRIVATE)),
  PERSONAL(Collections.emptyList());

  private final List<EGroupRoleCollection> allowedRoles;

  EGroupTypeCollection(List<EGroupRoleCollection> allowedRoles) {
    this.allowedRoles = allowedRoles;
  }

  public List<EGroupRoleCollection> getAllowedRoles() {
    return allowedRoles;
  }

  public enum EGroupRoleCollection {
    PUBLIC,
    PRIVATE
  }

  public boolean isRoleAllowed(EGroupRoleCollection role) {
    return allowedRoles.contains(role);
  }
}
