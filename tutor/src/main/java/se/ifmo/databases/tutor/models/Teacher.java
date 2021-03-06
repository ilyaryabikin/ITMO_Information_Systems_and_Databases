package se.ifmo.databases.tutor.models;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static se.ifmo.databases.tutor.models.AuthorityRoles.TEACHER;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity(name = "teachers")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString(callSuper = true, doNotUseGetters = true)
@EqualsAndHashCode(callSuper = true, doNotUseGetters = true, onlyExplicitlyIncluded = true)
public class Teacher extends Person {

  private static final long serialVersionUID = 9128477513564L;

  @OneToMany(
      cascade = {DETACH, MERGE, REFRESH},
      mappedBy = "teacher")
  @ToString.Exclude
  private List<Lesson> lessons = new LinkedList<>();

  @ManyToMany(cascade = {DETACH, MERGE, REFRESH})
  @JoinTable(
      name = "teachers_subjects",
      joinColumns = @JoinColumn(name = "teacher_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "subject_id", nullable = false))
  @ToString.Exclude
  private Set<Subject> subjects = new LinkedHashSet<>();

  @ManyToMany(cascade = {DETACH, MERGE, REFRESH})
  @JoinTable(
      name = "students_teachers",
      joinColumns = @JoinColumn(name = "teacher_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "student_id", nullable = false))
  @ToString.Exclude
  private Set<Student> students = new LinkedHashSet<>();

  @Override
  public User getUser() {
    if (user == null) {
      user = new User();
    }
    user.setRole(TEACHER.getRole());
    return user;
  }

  public void addStudent(final Student student) {
    if (students.contains(student)) {
      return;
    }
    students.add(student);
    student.addTeacher(this);
  }
}
