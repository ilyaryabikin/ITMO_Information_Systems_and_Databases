package se.ifmo.databases.tutor.models;

import static lombok.AccessLevel.PROTECTED;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.domain.Persistable;

@Entity(name = "roles")
@NoArgsConstructor
@AllArgsConstructor(access = PROTECTED)
@Getter
@Setter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true, onlyExplicitlyIncluded = true)
public class Role implements Persistable<Long>, Serializable {

  @Id @EqualsAndHashCode.Include private Long id;

  @NotBlank
  @NaturalId
  @Column(length = 64, unique = true, nullable = false)
  @EqualsAndHashCode.Include
  private String name;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public boolean isNew() {
    return id == null;
  }
}
