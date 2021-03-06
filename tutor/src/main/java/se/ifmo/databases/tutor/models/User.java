package se.ifmo.databases.tutor.models;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.Collection;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.domain.Persistable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import se.ifmo.databases.tutor.listeners.UserListener;

@Entity(name = "users")
@EntityListeners(UserListener.class)
@NoArgsConstructor
@Getter
@Setter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true, onlyExplicitlyIncluded = true)
public class User implements Persistable<Long>, UserDetails {

  private static final long serialVersionUID = 145242134264657L;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Email
  @NaturalId(mutable = true)
  @Column(length = 63, unique = true, nullable = false)
  @EqualsAndHashCode.Include
  private String email;

  @NotBlank
  @Column(length = 63, nullable = false)
  private String password;

  @Column(length = 63, unique = true)
  protected String avatarUuid;

  @NotNull
  @ManyToOne(cascade = DETACH, fetch = EAGER, optional = false)
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

  @Transient
  @Override
  public boolean isNew() {
    return id == null;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Transient
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Set.of(new SimpleGrantedAuthority(role.getName()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Transient
  @Override
  public String getUsername() {
    return email;
  }

  @Transient
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Transient
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Transient
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Transient
  @Override
  public boolean isEnabled() {
    return true;
  }
}
