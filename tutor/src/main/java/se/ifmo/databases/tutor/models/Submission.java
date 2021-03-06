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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Persistable;

@Entity(name = "lesson_submissions")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true, onlyExplicitlyIncluded = true)
public class Submission implements Persistable<Long> {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @NotNull
  @Column(nullable = false)
  private String name;

  @NotNull
  @Column(unique = true, nullable = false)
  private String uuid;

  @Min(0)
  @Max(5)
  private Integer grade;

  @NotNull
  @ManyToOne(cascade = DETACH, fetch = LAZY, optional = false)
  @JoinColumn(name = "student_id", nullable = false)
  @ToString.Exclude
  private Student student;

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
