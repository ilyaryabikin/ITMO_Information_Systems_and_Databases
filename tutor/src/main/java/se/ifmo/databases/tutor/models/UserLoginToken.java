package se.ifmo.databases.tutor.models;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.domain.Persistable;

@Entity(name = "login_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class UserLoginToken implements Persistable<Long>, Serializable {

  private static final long serialVersionUID = 5629734019589347L;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @NotBlank
  @NaturalId(mutable = true)
  @Column(length = 64, unique = true, nullable = false)
  @EqualsAndHashCode.Include
  private String series;

  @NotBlank
  @Column(length = 64, nullable = false)
  private String value;

  @Column(name = "last_used", nullable = false)
  private Instant lastUsed;

  @NotNull
  @ManyToOne(cascade = DETACH, fetch = LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  @ToString.Exclude
  private User user;

  @Override
  public Long getId() {
    return id;
  }

  @Transient
  @Override
  public boolean isNew() {
    return id == null;
  }
}
