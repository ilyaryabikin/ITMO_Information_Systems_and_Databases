package se.ifmo.databases.tutor.models;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.domain.Persistable;

@Entity(name = "materials")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true, onlyExplicitlyIncluded = true)
public class Material implements Persistable<Long> {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @NotNull
  @Column(nullable = false)
  private String name;

  @NaturalId
  @NotNull
  @Column(unique = true, nullable = false)
  @EqualsAndHashCode.Include
  private String uuid;

  @NotNull
  @ManyToOne(cascade = DETACH, fetch = LAZY, optional = false)
  @JoinColumn(name = "teacher_id", nullable = false)
  @ToString.Exclude
  private Teacher teacher;

  @NotNull
  @ManyToOne(cascade = DETACH, fetch = LAZY, optional = false)
  @JoinColumn(name = "lesson_id", nullable = false)
  @ToString.Exclude
  private Lesson lesson;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public boolean isNew() {
    return id == null;
  }
}
