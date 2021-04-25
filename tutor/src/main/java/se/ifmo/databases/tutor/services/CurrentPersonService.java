package se.ifmo.databases.tutor.services;

import static se.ifmo.databases.tutor.models.AuthorityRoles.STUDENT;
import static se.ifmo.databases.tutor.models.AuthorityRoles.TEACHER;

import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.databases.tutor.models.Person;
import se.ifmo.databases.tutor.models.Student;
import se.ifmo.databases.tutor.models.Teacher;
import se.ifmo.databases.tutor.repositories.StudentRepository;
import se.ifmo.databases.tutor.repositories.TeacherRepository;
import se.ifmo.databases.tutor.utils.SecurityUtil;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CurrentPersonService {

  private final StudentRepository studentRepository;
  private final TeacherRepository teacherRepository;

  @Transactional(readOnly = true)
  public Person getCurrentPerson() {
    final UserDetails currentUser = getCurrentUserDetails();
    if (currentUser.getAuthorities().contains(STUDENT.getAuthority())) {
      return studentRepository
          .findByUserUsername(currentUser.getUsername())
          .orElseThrow(EntityNotFoundException::new);
    } else if (currentUser.getAuthorities().contains(TEACHER.getAuthority())) {
      return teacherRepository
          .findByUserUsername(currentUser.getUsername())
          .orElseThrow(EntityNotFoundException::new);
    }
    throw new UnsupportedOperationException();
  }

  @Transactional(readOnly = true)
  public Teacher getCurrentTeacher() {
    return (Teacher) getCurrentPerson();
  }

  @Transactional(readOnly = true)
  public Student getCurrentStudent() {
    return (Student) getCurrentPerson();
  }

  private UserDetails getCurrentUserDetails() {
    return Optional.ofNullable(SecurityUtil.getCurrentUserDetails())
        .orElseThrow(UnsupportedOperationException::new);
  }
}
