package se.ifmo.databases.tutor.models;

import java.io.Serializable;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@RequiredArgsConstructor
@Getter
@ToString(doNotUseGetters = true)
public enum AuthorityRoles implements Serializable {
  STUDENT(new Role(1L, "ROLE_STUDENT")),
  TEACHER(new Role(2L, "ROLE_TEACHER"));

  private final Role role;

  public Set<SimpleGrantedAuthority> getAuthorities() {
    return Set.of(getAuthority());
  }

  public String getName() {
    return role.getName();
  }

  public SimpleGrantedAuthority getAuthority() {
    return new SimpleGrantedAuthority(getName());
  }
}
