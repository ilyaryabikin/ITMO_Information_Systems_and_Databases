package se.ifmo.databases.tutor.views;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY;
import static java.lang.String.format;
import static se.ifmo.databases.tutor.utils.ViewsConstants.HASHID_MIN_LENGTH;
import static se.ifmo.databases.tutor.utils.ViewsConstants.HASHID_SALT;
import static se.ifmo.databases.tutor.utils.ViewsConstants.ROLE_STUDENT;

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
import java.util.stream.Collectors;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import se.ifmo.databases.tutor.exceptions.EntityNotFoundException;
import se.ifmo.databases.tutor.models.Person;
import se.ifmo.databases.tutor.models.Student;
import se.ifmo.databases.tutor.models.Subject;
import se.ifmo.databases.tutor.models.Teacher;
import se.ifmo.databases.tutor.services.StudentService;
import se.ifmo.databases.tutor.services.TeacherService;

@Route(value = "teachers", layout = MainView.class)
@PageTitle("Teachers")
@CssImport("./styles/students-teachers-view/students-teachers.css")
@Secured(ROLE_STUDENT)
public class TeachersView extends VerticalLayout {

  private final Student currentStudent;
  private final StudentService studentService;
  private final TeacherService teacherService;

  private Grid<Teacher> teachersGrid;

  @Autowired
  public TeachersView(final StudentService studentService, final TeacherService teacherService) {
    this.currentStudent = (Student) VaadinSession.getCurrent().getAttribute(Person.class);
    this.studentService = studentService;
    this.teacherService = teacherService;

    updateCurrentTeachers();

    setSizeFull();
    add(initInvitationLayout(), initTeachersLayout());
  }

  private VerticalLayout initTeachersLayout() {
    final VerticalLayout teachersLayout = new VerticalLayout();
    final H2 currentTeachersHeader = new H2("Current teachers");
    teachersGrid = initCurrentTeachersGrid();
    teachersLayout.setSizeFull();
    teachersLayout.add(currentTeachersHeader, teachersGrid);
    return teachersLayout;
  }

  private Grid<Teacher> initCurrentTeachersGrid() {
    final Grid<Teacher> grid = new Grid<>();
    grid.setSelectionMode(Grid.SelectionMode.NONE);
    grid.addColumn(Teacher::getSurname).setHeader("Surname");
    grid.addColumn(Teacher::getName).setHeader("Name");
    grid.addColumn(
            teacher ->
                teacher.getSubjects().stream()
                    .map(Subject::getName)
                    .collect(Collectors.joining(", ")))
        .setHeader("Subjects");
    grid.addComponentColumn(
            teacher -> {
              final Avatar teacherAvatar = new Avatar();
              teacherAvatar.setName(teacher.getFullName());
              return teacherAvatar;
            })
        .setHeader("Avatar");
    grid.getColumns().forEach(column -> column.setAutoWidth(true));
    grid.setItems(currentStudent.getTeachers());
    return grid;
  }

  @Transactional
  protected HorizontalLayout initInvitationLayout() {
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
          System.out.println("Your hashid is " + hashids.encode(currentStudent.getId()));
          final long inviterId = hashids.decode(invitationCodeField.getValue())[0];
          try {
            final Teacher teacher = teacherService.findEntityById(inviterId);
            currentStudent.addTeacher(teacher);
            studentService.saveEntity(currentStudent);
            updateCurrentTeachers();
            Notification.show(format("Teacher %s was successfully added!", teacher.getFullName()));
          } catch (final EntityNotFoundException ex) {
            Notification.show("Teacher with such invitation code was not found!");
          }
        });

    invitationLayout.add(invitationCodeField, invitationButton);
    return invitationLayout;
  }

  private void updateCurrentTeachers() {
    final Set<Teacher> currentTeachers =
        new LinkedHashSet<>(teacherService.findAllByStudentId(currentStudent.getId()));
    currentStudent.setTeachers(currentTeachers);
    if (teachersGrid != null) {
      teachersGrid.setItems(currentTeachers);
    }
  }
}
