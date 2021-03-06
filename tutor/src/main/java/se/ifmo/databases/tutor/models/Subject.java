package se.ifmo.databases.tutor.models;

import static lombok.AccessLevel.PROTECTED;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.domain.Persistable;

@Entity(name = "subjects")
@NoArgsConstructor
@AllArgsConstructor(access = PROTECTED)
@Setter
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true, onlyExplicitlyIncluded = true)
public class Subject implements Persistable<Long>, Serializable {

  private static final long serialVersionUID = 6987192088874552814L;

  @Id @EqualsAndHashCode.Include private Long id;

  @NotBlank
  @NaturalId
  @Column(length = 64, unique = true, nullable = false)
  @EqualsAndHashCode.Include
  private String name;

  @ManyToMany(mappedBy = "subjects")
  @ToString.Exclude
  private Set<Teacher> teachers = new LinkedHashSet<>();

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public boolean isNew() {
    return id == null;
  }
}
