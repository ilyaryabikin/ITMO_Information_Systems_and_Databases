package se.ifmo.databases.tutor.views;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY;
import static com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition.TOP;
import static com.vaadin.flow.data.binder.ValidationResult.error;
import static com.vaadin.flow.data.binder.ValidationResult.ok;
import static se.ifmo.databases.tutor.utils.ViewsConstants.LOGIN_QUERY_REGISTERED;
import static se.ifmo.databases.tutor.utils.ViewsConstants.ROLE_STUDENT_NAME;
import static se.ifmo.databases.tutor.utils.ViewsConstants.ROLE_TEACHER_NAME;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.gatanaso.MultiselectComboBox;
import se.ifmo.databases.tutor.models.Person;
import se.ifmo.databases.tutor.models.Student;
import se.ifmo.databases.tutor.models.Subject;
import se.ifmo.databases.tutor.models.Teacher;
import se.ifmo.databases.tutor.services.StudentService;
import se.ifmo.databases.tutor.services.SubjectService;
import se.ifmo.databases.tutor.services.TeacherService;

@Route("register")
@PageTitle("Register to Tutor")
@CssImport(value = "./styles/global.css")
@CssImport(value = "./styles/register-form-view/register-form.css")
public class RegisterFormView extends VerticalLayout {

  private static final long serialVersionUID = 7047980916197767004L;

  private final StudentService studentService;
  private final TeacherService teacherService;
  private final SubjectService subjectService;

  private final BeanValidationBinder<Person> personBinder =
      new BeanValidationBinder<>(Person.class);
  private final BeanValidationBinder<Teacher> teacherBinder =
      new BeanValidationBinder<>(Teacher.class);

  private final FormLayout personForm;
  private final FormLayout teacherForm;

  private RadioButtonGroup<String> personRoleRadioGroup;
  private TextField nameField;
  private TextField surnameField;
  private MultiselectComboBox<Subject> subjectMultiselectComboBox;
  private TextField emailField;
  private PasswordField passwordField;
  private PasswordField confirmPasswordField;
  private Button registrationButton;

  @Autowired
  public RegisterFormView(
      final StudentService studentService,
      final TeacherService teacherService,
      final SubjectService subjectService) {
    this.studentService = studentService;
    this.teacherService = teacherService;
    this.subjectService = subjectService;

    final H2 registrationHeader = new H2("Tutor Registration");
    this.personForm = new FormLayout();
    this.teacherForm = new FormLayout();

    initFormFields();
    initFormLayoutView();
    initBinder();

    setSizeFull();
    setAlignItems(Alignment.CENTER);
    setJustifyContentMode(JustifyContentMode.CENTER);
    add(registrationHeader, personForm);
  }

  private void initFormFields() {
    personRoleRadioGroup = new RadioButtonGroup<>();
    personRoleRadioGroup.setLabel("What is your role?");
    personRoleRadioGroup.setItems(ROLE_STUDENT_NAME, ROLE_TEACHER_NAME);
    personRoleRadioGroup.setRequired(true);
    personRoleRadioGroup.addValueChangeListener(
        e -> {
          if (e.getValue().equals(ROLE_TEACHER_NAME)) {
            teacherForm.setVisible(true);
            subjectMultiselectComboBox.setRequired(true);
          } else if (e.getValue().equals(ROLE_STUDENT_NAME)) {
            teacherForm.setVisible(false);
            subjectMultiselectComboBox.setRequired(false);
          }
        });

    nameField = new TextField("Name");
    nameField.setRequired(true);

    surnameField = new TextField("Surname");
    surnameField.setRequired(true);

    subjectMultiselectComboBox = new MultiselectComboBox<>();
    subjectMultiselectComboBox.setLabel("Your subjects");
    subjectMultiselectComboBox.setItemLabelGenerator(Subject::getName);
    subjectMultiselectComboBox.setItems(subjectService.findAllEntities());

    emailField = new TextField("Email");
    emailField.setRequired(true);

    passwordField = new PasswordField("Password");
    passwordField.setRequired(true);

    confirmPasswordField = new PasswordField("Confirm Password");
    confirmPasswordField.setRequired(true);
    confirmPasswordField.addValueChangeListener(
        e -> {
          confirmPasswordField.setInvalid(false);
          passwordField.setInvalid(false);
          personBinder.validate();
        });

    registrationButton = new Button("Join the community");
    registrationButton.addThemeVariants(LUMO_PRIMARY);
    registrationButton.addClickListener(
        e -> {
          try {
            if (Objects.equals(personRoleRadioGroup.getValue(), ROLE_STUDENT_NAME)) {
              final Student student = new Student();
              personBinder.writeBean(student);
              studentService.saveEntity(student);
            } else if (Objects.equals(personRoleRadioGroup.getValue(), ROLE_TEACHER_NAME)) {
              final Teacher teacher = new Teacher();
              personBinder.writeBean(teacher);
              teacherBinder.writeBean(teacher);
              teacherService.saveEntity(teacher);
            } else {
              personBinder.validate();
              return;
            }
            final var queryParameters =
                QueryParameters.simple(Map.of(LOGIN_QUERY_REGISTERED, "true"));
            registrationButton
                .getUI()
                .ifPresent(
                    ui ->
                        ui.navigate(
                            RouteUtil.getRoutePath(
                                LoginFormView.class,
                                LoginFormView.class.getAnnotation(Route.class)),
                            queryParameters));
          } catch (final ValidationException validationException) {
            return;
          }
        });

    teacherForm.add(subjectMultiselectComboBox);

    personForm.add(
        personRoleRadioGroup,
        nameField,
        surnameField,
        teacherForm,
        emailField,
        passwordField,
        confirmPasswordField,
        registrationButton);
  }

  private void initFormLayoutView() {
    personForm.setResponsiveSteps(
        new ResponsiveStep("0px", 1, TOP), new ResponsiveStep("500px", 2, TOP));
    personForm.setId("register-form");
    personForm.setColspan(personRoleRadioGroup, 2);
    personForm.setColspan(teacherForm, 2);
    personForm.setColspan(emailField, 2);
    personForm.setColspan(registrationButton, 2);
    personForm.setMaxWidth("650px");
    personForm.getStyle().set("margin", "0 auto");

    teacherForm.setResponsiveSteps(
        new ResponsiveStep("0px", 1, TOP), new ResponsiveStep("500px", 2, TOP));
    teacherForm.setColspan(subjectMultiselectComboBox, 2);
  }

  private void initBinder() {
    teacherBinder
        .forField(subjectMultiselectComboBox)
        .withValidator(val -> !val.isEmpty(), "You should select at least one subject")
        .bind(Teacher::getSubjects, Teacher::setSubjects);

    personBinder
        .forField(nameField)
        .withValidator(
            val -> !val.isBlank() && val.length() <= 63,
            "Name must not be empty and should contain less than 64 characters")
        .bind(Person::getName, Person::setName);

    personBinder
        .forField(surnameField)
        .withValidator(
            val -> !val.isBlank() && val.length() <= 63,
            "Surname must not be empty and should contain less than 64 characters")
        .bind(Person::getSurname, Person::setSurname);

    personBinder
        .forField(emailField)
        .withValidator(new EmailValidator("You should enter valid email address"))
        .bind(
            person -> person.getUser().getEmail(),
            (person, email) -> person.getUser().setEmail(email));

    personBinder
        .forField(passwordField)
        .withValidator(this::passwordValidator)
        .bind(
            person -> person.getUser().getPassword(),
            (person, password) -> person.getUser().setPassword(password));

    personBinder
        .forField(confirmPasswordField)
        .withValidator(
            val -> val.equals(passwordField.getValue()),
            "Confirmed password doesn't match initial password");
  }

  private ValidationResult passwordValidator(
      final String initialPassword, final ValueContext context) {
    if (initialPassword == null || initialPassword.length() < 8) {
      confirmPasswordField.setInvalid(true);
      confirmPasswordField.setErrorMessage("Password should be at least 8 characters long");
      return error("Password should be at least 8 characters long");
    }

    if (initialPassword.equals(confirmPasswordField.getValue())) {
      confirmPasswordField.setInvalid(false);
      return ok();
    } else {
      final String errorMessage = "Confirmed password doesn't match initial password";
      confirmPasswordField.setInvalid(true);
      confirmPasswordField.setErrorMessage(errorMessage);
      return error(errorMessage);
    }
  }
}
