package se.ifmo.databases.tutor.views;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY;
import static com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition.TOP;
import static java.lang.String.format;
import static se.ifmo.databases.tutor.models.AuthorityRoles.STUDENT;
import static se.ifmo.databases.tutor.models.AuthorityRoles.TEACHER;
import static se.ifmo.databases.tutor.utils.ViewsConstants.HASHID_MIN_LENGTH;
import static se.ifmo.databases.tutor.utils.ViewsConstants.HASHID_SALT;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.time.LocalDate;
import java.util.Objects;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.gatanaso.MultiselectComboBox;
import se.ifmo.databases.tutor.models.Person;
import se.ifmo.databases.tutor.models.Student;
import se.ifmo.databases.tutor.models.Subject;
import se.ifmo.databases.tutor.models.Teacher;
import se.ifmo.databases.tutor.services.AvatarService;
import se.ifmo.databases.tutor.services.StudentService;
import se.ifmo.databases.tutor.services.SubjectService;
import se.ifmo.databases.tutor.services.TeacherService;

@Route(value = "profile", layout = MainView.class)
@PageTitle("Profile")
@CssImport(value = "./styles/profile-form-view/profile-form.css")
public class ProfileView extends VerticalLayout implements BeforeLeaveObserver {

  private final StudentService studentService;
  private final TeacherService teacherService;
  private final SubjectService subjectService;
  private final AvatarService avatarService;

  private final BeanValidationBinder<Person> personBinder =
      new BeanValidationBinder<>(Person.class);
  private final BeanValidationBinder<Teacher> teacherBinder =
      new BeanValidationBinder<>(Teacher.class);

  private Person currentPerson;

  private FormLayout personForm;
  private FormLayout teacherForm;
  private Span inviteCodeParagraph;
  private TextField nameField;
  private TextField surnameField;
  private TextField middleNameField;
  private MultiselectComboBox<Subject> subjectMultiselectComboBox;
  private Upload upload;
  private Button removeAvatarButton;
  private TextField phoneNumberField;
  private TextField cityField;
  private DatePicker birthDatePicker;
  private Button applyChangesButton;

  private boolean avatarChanged = false;

  @Autowired
  public ProfileView(
      final StudentService studentService,
      final TeacherService teacherService,
      final SubjectService subjectService,
      final AvatarService avatarService) {
    this.currentPerson = VaadinSession.getCurrent().getAttribute(Person.class);
    this.studentService = studentService;
    this.teacherService = teacherService;
    this.subjectService = subjectService;
    this.avatarService = avatarService;

    this.personForm = new FormLayout();
    this.teacherForm = new FormLayout();

    setId("profile-form-layout");

    initFormFields();
    initFormLayouts();
    initBinders();
    populateData();

    setSizeFull();
    setAlignItems(Alignment.CENTER);
    setJustifyContentMode(JustifyContentMode.CENTER);
    add(personForm);
  }

  private void initFormFields() {
    inviteCodeParagraph = new Span();
    inviteCodeParagraph.setId("invitation-code");
    final Hashids hashids = new Hashids(HASHID_SALT, HASHID_MIN_LENGTH);
    inviteCodeParagraph.setText(
        format("Your invitation code is %s", hashids.encode(currentPerson.getId())));

    nameField = new TextField("Name");

    surnameField = new TextField("Surname");

    middleNameField = new TextField("Middle Name");

    subjectMultiselectComboBox = new MultiselectComboBox<>();
    if (Objects.equals(currentPerson.getUser().getRole(), STUDENT.getRole())) {
      teacherForm.setVisible(false);
    } else {
      subjectMultiselectComboBox.setLabel("Your subjects");
      subjectMultiselectComboBox.setItemLabelGenerator(Subject::getName);
      subjectMultiselectComboBox.setItems(subjectService.findAllEntities());
    }

    final MemoryBuffer memoryBuffer = new MemoryBuffer();
    upload = new Upload(memoryBuffer);
    upload.setAcceptedFileTypes("image/*");
    upload.setDropLabel(new Span("Upload avatar"));
    upload.addSucceededListener(e -> avatarChanged = true);

    removeAvatarButton = new Button("Remove avatar");
    removeAvatarButton.setId("remove-avatar");
    if (currentPerson.getUser().getAvatarUuid() == null) {
      removeAvatarButton.setEnabled(false);
    }
    removeAvatarButton.addClickListener(
        e -> {
          if (removeAvatarButton.isEnabled()) {
            currentPerson.getUser().setAvatarUuid(null);
            removeAvatarButton.setEnabled(false);
            avatarChanged = true;
          }
        });

    phoneNumberField = new TextField("Phone Number");

    cityField = new TextField("City");

    birthDatePicker = new DatePicker("Birth Date");
    birthDatePicker.setMin(LocalDate.of(1930, 1, 1));
    birthDatePicker.setMax(LocalDate.now().minusYears(2));

    applyChangesButton = new Button("Apply changes");
    applyChangesButton.addThemeVariants(LUMO_PRIMARY);
    applyChangesButton.addClickListener(
        e -> {
          try {
            personBinder.writeBean(currentPerson);
            if (memoryBuffer.getFileData() != null) {
              final String filename = memoryBuffer.getFileData().getFileName();
              avatarService.save(
                  currentPerson,
                  filename.substring(filename.lastIndexOf(".") + 1),
                  memoryBuffer.getInputStream());
              removeAvatarButton.setEnabled(true);
            }
            if (Objects.equals(currentPerson.getUser().getRole(), TEACHER.getRole())) {
              final Teacher currentTeacher = (Teacher) currentPerson;
              teacherBinder.writeBean(currentTeacher);
              teacherService.saveEntity(currentTeacher);
            } else {
              studentService.saveEntity((Student) currentPerson);
            }
            avatarChanged = false;
            getUI().ifPresent(ui -> ui.getPage().reload());
          } catch (final ValidationException validationException) {
            return;
          }
        });

    teacherForm.add(subjectMultiselectComboBox);

    personForm.add(
        inviteCodeParagraph,
        nameField,
        surnameField,
        middleNameField,
        teacherForm,
        upload,
        removeAvatarButton,
        phoneNumberField,
        cityField,
        birthDatePicker,
        applyChangesButton);
  }

  private void initFormLayouts() {
    personForm.setResponsiveSteps(
        new ResponsiveStep("0px", 1, TOP), new ResponsiveStep("750px", 3, TOP));
    personForm.setId("profile-form");
    personForm.setColspan(inviteCodeParagraph, 3);
    personForm.setColspan(teacherForm, 3);
    personForm.setColspan(upload, 2);
    personForm.setColspan(removeAvatarButton, 1);
    personForm.setColspan(applyChangesButton, 3);
    personForm.setMaxWidth("950px");

    teacherForm.setResponsiveSteps(
        new ResponsiveStep("0px", 1, TOP), new ResponsiveStep("750px", 3, TOP));
    teacherForm.setColspan(subjectMultiselectComboBox, 3);
  }

  private void initBinders() {
    personBinder
        .forField(nameField)
        .withValidator(
            val -> !val.isBlank() && val.length() <= 63,
            "Name must not be empty and should contain less than 64 character")
        .bind(Person::getName, Person::setName);

    personBinder
        .forField(surnameField)
        .withValidator(
            val -> !val.isBlank() && val.length() <= 63,
            "Surname must not be empty and should contain less than 64 character")
        .bind(Person::getSurname, Person::setSurname);

    personBinder
        .forField(middleNameField)
        .withValidator(
            val -> val == null || val.length() <= 63,
            "Middle name should contain less than 64 characters")
        .bind(Person::getMiddleName, Person::setMiddleName);

    teacherBinder
        .forField(subjectMultiselectComboBox)
        .withValidator(val -> !val.isEmpty(), "You should select at least one subject")
        .bind(Teacher::getSubjects, Teacher::setSubjects);

    personBinder
        .forField(phoneNumberField)
        .withValidator(
            val -> {
              if (val == null) {
                return true;
              }
              final var phoneNumberUtil = PhoneNumberUtil.getInstance();
              try {
                final var phoneNumber = phoneNumberUtil.parse(val, "RU");
                return phoneNumberUtil.isValidNumber(phoneNumber);
              } catch (final NumberParseException e) {
                return false;
              }
            },
            "Phone number should be in correct form")
        .bind(Person::getPhoneNumber, Person::setPhoneNumber);

    personBinder
        .forField(birthDatePicker)
        .withValidator(
            val -> val == null || val.isBefore(LocalDate.now()), "Birth date should be before now")
        .bind(Person::getBirthDate, Person::setBirthDate);

    personBinder
        .forField(cityField)
        .withValidator(
            val -> val == null || val.length() <= 63, "City should contain less than 64 characters")
        .bind(Person::getCity, Person::setCity);
  }

  private void populateData() {
    personBinder.readBean(currentPerson);
    if (Objects.equals(currentPerson.getUser().getRole(), TEACHER.getRole())) {
      teacherBinder.readBean((Teacher) currentPerson);
    }
  }

  @Override
  public void beforeLeave(final BeforeLeaveEvent event) {
    if (personBinder.hasChanges() || teacherBinder.hasChanges() || avatarChanged) {
      final BeforeLeaveEvent.ContinueNavigationAction action = event.postpone();
      final ConfirmDialog confirmDialog = new ConfirmDialog();
      confirmDialog.setText("You have unsaved changes! Are you sure you want to leave?");
      confirmDialog.setConfirmButton("Stay", e -> confirmDialog.close());
      confirmDialog.setCancelButton("Leave", e -> action.proceed());
      confirmDialog.setCancelable(true);
      confirmDialog.open();
    }
  }
}
