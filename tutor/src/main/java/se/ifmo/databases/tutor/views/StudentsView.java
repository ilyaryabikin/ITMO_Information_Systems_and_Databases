package se.ifmo.databases.tutor.views;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY;
import static java.lang.String.format;
import static se.ifmo.databases.tutor.utils.ViewsConstants.HASHID_MIN_LENGTH;
import static se.ifmo.databases.tutor.utils.ViewsConstants.HASHID_SALT;
import static se.ifmo.databases.tutor.utils.ViewsConstants.ROLE_TEACHER;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.util.LinkedHashSet;
import java.util.Set;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import se.ifmo.databases.tutor.exceptions.EntityNotFoundException;
import se.ifmo.databases.tutor.models.Person;
import se.ifmo.databases.tutor.models.Student;
import se.ifmo.databases.tutor.models.Teacher;
import se.ifmo.databases.tutor.services.StudentService;
import se.ifmo.databases.tutor.services.TeacherService;

@Route(value = "students", layout = MainView.class)
@PageTitle("Students")
@CssImport("./styles/students-teachers-view/students-teachers.css")
@Secured(ROLE_TEACHER)
public class StudentsView extends VerticalLayout {

  private final Teacher currentTeacher;
  private final TeacherService teacherService;
  private final StudentService studentService;

  private Grid<Student> studentsGrid;

  @Autowired
  public StudentsView(final TeacherService teacherService, final StudentService studentService) {
    this.currentTeacher = (Teacher) VaadinSession.getCurrent().getAttribute(Person.class);
    this.teacherService = teacherService;
    this.studentService = studentService;

    updateCurrentStudents();

    setSizeFull();
    add(initInvitationLayout(), initTeachersLayout());
  }

  private VerticalLayout initTeachersLayout() {
    final VerticalLayout studentsLayout = new VerticalLayout();
    final H2 currentTeachersHeader = new H2("Current students");
    studentsGrid = initCurrentTeachersGrid();
    studentsLayout.setSizeFull();
    studentsLayout.add(currentTeachersHeader, studentsGrid);
    return studentsLayout;
  }

  private Grid<Student> initCurrentTeachersGrid() {
    final Grid<Student> grid = new Grid<>();
    grid.setSelectionMode(Grid.SelectionMode.NONE);
    grid.addColumn(Student::getSurname).setHeader("Surname");
    grid.addColumn(Student::getName).setHeader("Name");
    grid.addComponentColumn(
            teacher -> {
              final Avatar teacherAvatar = new Avatar();
              teacherAvatar.setName(teacher.getFullName());
              return teacherAvatar;
            })
        .setHeader("Avatar");
    grid.getColumns().forEach(column -> column.setAutoWidth(true));
    grid.setItems(currentTeacher.getStudents());
    return grid;
  }

  private HorizontalLayout initInvitationLayout() {
    final HorizontalLayout invitationLayout = new HorizontalLayout();
    invitationLayout.setId("invitation-code-layout");
    final TextField invitationCodeField = new TextField();
    invitationCodeField.setId("invitation-code-field");
    invitationCodeField.setPlaceholder("Invitation code");
    invitationCodeField.setClearButtonVisible(true);

    final Button invitationButton = new Button("Proceed");
    invitationButton.setId("proceed-button");
    invitationButton.addThemeVariants(LUMO_PRIMARY);
    invitationButton.addClickListener(
        e -> {
          if (invitationCodeField.getValue().isBlank()) {
            return;
          }
          final Hashids hashids = new Hashids(HASHID_SALT, HASHID_MIN_LENGTH);
          final long[] inviterId = hashids.decode(invitationCodeField.getValue());
          if (inviterId.length == 0) {
            Notification.show("User with such invitation code was not found!");
          }
          try {
            final Student student = studentService.findEntityById(inviterId[0]);
            currentTeacher.addStudent(student);
            teacherService.saveEntity(currentTeacher);
            updateCurrentStudents();
            Notification.show(format("Student %s was successfully added!", student.getFullName()));
          } catch (final EntityNotFoundException ex) {
            Notification.show("User with such invitation code was not found!");
          }
        });

    invitationLayout.add(invitationCodeField, invitationButton);
    return invitationLayout;
  }

  private void updateCurrentStudents() {
    final Set<Student> currentStudents =
        new LinkedHashSet<>(studentService.findAllByTeacherId(currentTeacher.getId()));
    currentTeacher.setStudents(currentStudents);
    if (studentsGrid != null) {
      studentsGrid.setItems(currentStudents);
    }
  }
}
