package se.ifmo.databases.tutor.models;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static se.ifmo.databases.tutor.models.AuthorityRoles.STUDENT;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity(name = "students")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString(callSuper = true, doNotUseGetters = true)
@EqualsAndHashCode(callSuper = true, doNotUseGetters = true, onlyExplicitlyIncluded = true)
public class Student extends Person {

  private static final long serialVersionUID = 6813685318957186L;

  @ManyToMany(
      cascade = {DETACH, MERGE, REFRESH},
      mappedBy = "students")
  @ToString.Exclude
  private Set<Teacher> teachers = new LinkedHashSet<>();

  @ManyToMany(
      cascade = {DETACH, MERGE, REFRESH},
      mappedBy = "students")
  @ToString.Exclude
  private Set<Lesson> lessons = new LinkedHashSet<>();

  @Override
  public User getUser() {
    if (user == null) {
      user = new User();
    }
    user.setRole(STUDENT.getRole());
    return user;
  }

  public void addTeacher(final Teacher teacher) {
    if (teachers.contains(teacher)) {
      return;
    }
    teachers.add(teacher);
    teacher.addStudent(this);
  }
}
