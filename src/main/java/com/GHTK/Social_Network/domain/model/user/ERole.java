package com.GHTK.Social_Network.domain.model.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Permission;
import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
public enum ERole {
  USER(Collections.emptySet()),
  ADMIN(Collections.emptySet());

  @Getter
  private final Set<Permission> permissions;

  public Set<Permission> getPermissions() {
    return permissions;
  }
}
