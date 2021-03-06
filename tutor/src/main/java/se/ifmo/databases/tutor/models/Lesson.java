package se.ifmo.databases.tutor.models;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Persistable;

@Entity(name = "lessons")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true, onlyExplicitlyIncluded = true)
public class Lesson implements Persistable<Long>, Serializable {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @NotNull
  @Column(name = "start_date", nullable = false)
  private Instant startDate;

  @NotNull
  @Column(name = "end_date", nullable = false)
  private Instant endDate;

  private String name;

  @NotNull
  @ManyToOne(cascade = DETACH, fetch = LAZY, optional = false)
  @JoinColumn(name = "teacher_id", nullable = false)
  @ToString.Exclude
  private Teacher teacher;

  @NotNull
  @ManyToOne(cascade = DETACH, fetch = LAZY, optional = false)
  @JoinColumn(name = "subject_id", nullable = false)
  @ToString.Exclude
  private Subject subject;

  @OneToMany(mappedBy = "lesson", cascade = ALL)
  @ToString.Exclude
  private List<Material> materials = new LinkedList<>();

  @OneToMany(mappedBy = "lesson", cascade = ALL)
  @ToString.Exclude
  private List<Submission> submissions = new LinkedList<>();

  @ManyToMany(cascade = {DETACH, MERGE, REFRESH})
  @JoinTable(
      name = "lessons_students",
      joinColumns = @JoinColumn(name = "lesson_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "student_id", nullable = false))
  @ToString.Exclude
  private Set<Student> students = new LinkedHashSet<>();

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public boolean isNew() {
    return id == null;
  }
}
