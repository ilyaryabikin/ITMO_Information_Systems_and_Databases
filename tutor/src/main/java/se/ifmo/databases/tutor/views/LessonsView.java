package se.ifmo.databases.tutor.views;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR;
import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;
import static com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition.TOP;
import static com.vaadin.flow.component.icon.VaadinIcon.ARROW_LEFT;
import static com.vaadin.flow.component.icon.VaadinIcon.ARROW_RIGHT;
import static com.vaadin.flow.component.icon.VaadinIcon.CALENDAR;
import static com.vaadin.flow.component.icon.VaadinIcon.CLOSE;
import static com.vaadin.flow.component.icon.VaadinIcon.HOME;
import static java.lang.String.format;
import static java.time.DayOfWeek.MONDAY;
import static org.vaadin.stefan.fullcalendar.CalendarViewImpl.DAY_GRID_MONTH;
import static org.vaadin.stefan.fullcalendar.CalendarViewImpl.LIST_MONTH;
import static org.vaadin.stefan.fullcalendar.CalendarViewImpl.TIME_GRID_DAY;

import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import elemental.json.Json;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.gatanaso.MultiselectComboBox;
import org.vaadin.stefan.fullcalendar.CalendarView;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import se.ifmo.databases.tutor.exceptions.EntityNotFoundException;
import se.ifmo.databases.tutor.models.Lesson;
import se.ifmo.databases.tutor.models.Material;
import se.ifmo.databases.tutor.models.Person;
import se.ifmo.databases.tutor.models.Student;
import se.ifmo.databases.tutor.models.Subject;
import se.ifmo.databases.tutor.models.Submission;
import se.ifmo.databases.tutor.models.Teacher;
import se.ifmo.databases.tutor.services.AvatarService;
import se.ifmo.databases.tutor.services.LessonService;
import se.ifmo.databases.tutor.services.MaterialService;
import se.ifmo.databases.tutor.services.StudentService;
import se.ifmo.databases.tutor.services.SubjectService;
import se.ifmo.databases.tutor.services.SubmissionService;
import se.ifmo.databases.tutor.services.TeacherService;

@Route(value = "lessons", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Lessons")
@CssImport("./styles/lessons-view/lessons-view.css")
public class LessonsView extends VerticalLayout {

  private final LessonService lessonService;
  private final StudentService studentService;
  private final TeacherService teacherService;
  private final AvatarService avatarService;
  private final MaterialService materialService;
  private final SubmissionService submissionService;
  private final SubjectService subjectService;

  private Person currentPerson;

  private FullCalendar calendar;
  private FormLayout toolbar;
  private Button datePickerButton;
  private ComboBox<CalendarView> viewComboBox;

  @Autowired
  public LessonsView(
      final LessonService lessonService,
      final StudentService studentService,
      final TeacherService teacherService,
      final AvatarService avatarService,
      final MaterialService materialService,
      final SubmissionService submissionService,
      final SubjectService subjectService) {
    this.currentPerson = VaadinSession.getCurrent().getAttribute(Person.class);
    this.lessonService = lessonService;
    this.studentService = studentService;
    this.teacherService = teacherService;
    this.avatarService = avatarService;
    this.materialService = materialService;
    this.submissionService = submissionService;
    this.subjectService = subjectService;

    setId("lessons");

    initToolbar();
    initCalendar();
    initEntries();

    add(toolbar, calendar);
  }

  private void initToolbar() {
    toolbar = new FormLayout();
    toolbar.setId("toolbar");
    toolbar.setResponsiveSteps(new ResponsiveStep("0px", 1), new ResponsiveStep("650px", 3));

    final Button todayButton = new Button("Go to today");
    todayButton.setIcon(HOME.create());
    todayButton.addClickListener(e -> calendar.today());

    final Button toPreviousButton = new Button();
    toPreviousButton.setIcon(ARROW_LEFT.create());
    toPreviousButton.addClickListener(e -> calendar.previous());
    toPreviousButton.setIconAfterText(true);

    final Button toNextButton = new Button();
    toNextButton.setIcon(ARROW_RIGHT.create());
    toNextButton.addClickListener(e -> calendar.next());
    toNextButton.setIconAfterText(true);

    final DatePicker datePicker = new DatePicker();
    datePicker.addValueChangeListener(e -> calendar.gotoDate(e.getValue()));

    datePickerButton = new Button();
    datePickerButton.setIcon(CALENDAR.create());
    datePickerButton.getElement().appendChild(datePicker.getElement());
    datePickerButton.addClickListener(e -> datePicker.open());
    datePickerButton.setWidthFull();

    final HorizontalLayout temporalLayout = new HorizontalLayout();
    temporalLayout.add(toPreviousButton, datePickerButton, toNextButton, datePicker);
    temporalLayout.setWidthFull();

    final List<CalendarView> calendarViews = List.of(DAY_GRID_MONTH, LIST_MONTH, TIME_GRID_DAY);

    viewComboBox = new ComboBox<>("", calendarViews);
    viewComboBox.setItemLabelGenerator(
        val -> {
          switch ((CalendarViewImpl) val) {
            case DAY_GRID_MONTH:
              return "Month overview";
            case LIST_MONTH:
              return "Month list overview";
            case TIME_GRID_DAY:
              return "Day overview";
            default:
              return "Undefined";
          }
        });
    viewComboBox.setValue(DAY_GRID_MONTH);
    viewComboBox.addValueChangeListener(
        e -> {
          final var val = e.getValue();
          calendar.changeView(val == null ? DAY_GRID_MONTH : val);
        });

    toolbar.add(todayButton, temporalLayout, viewComboBox);
  }

  private void initCalendar() {
    calendar = FullCalendarBuilder.create().withAutoBrowserTimezone().withEntryLimit(4).build();

    calendar.setFirstDay(MONDAY);
    calendar.setHeader("Your lessons");
    calendar.setNowIndicatorShown(true);
    calendar.setNumberClickable(true);
    calendar.setWidthFull();

    calendar.addDatesRenderedListener(
        e -> updateIntervalLabel(datePickerButton, viewComboBox.getValue(), e.getIntervalStart()));
    calendar.addTimeslotsSelectedListener(
        e -> {
          final Entry entry = new Entry();
          entry.setStart(e.getStartDateTimeUTC());
          entry.setEnd(e.getEndDateTimeUTC());
          entry.setAllDay(false);
          try {
            new LessonDialog(currentPerson, entry, true).open();
          } catch (EntityNotFoundException entityNotFoundException) {
            entityNotFoundException.printStackTrace();
          }
        });
    calendar.addEntryClickedListener(
        e -> {
          try {
            new LessonDialog(currentPerson, e.getEntry(), false).open();
          } catch (EntityNotFoundException entityNotFoundException) {
            entityNotFoundException.printStackTrace();
          }
        });
  }

  private void initEntries() {
    if (currentPerson instanceof Teacher) {
      for (final var lesson : lessonService.findAllByTeacherId(currentPerson.getId())) {
        createEntry(lesson);
      }
    } else {
      for (final var lesson : lessonService.findAllByStudentId(currentPerson.getId())) {
        createEntry(lesson);
      }
    }
  }

  private Entry createEntry(final Lesson lesson) {
    final Entry newEntry = new Entry(String.valueOf(lesson.getId()));
    newEntry.setTitle(lesson.getName());
    newEntry.setStart(lesson.getStartDate());
    newEntry.setEnd(lesson.getEndDate());
    calendar.addEntry(newEntry);
    return newEntry;
  }

  private Entry updateEntry(final Lesson lesson, final Entry entry) {
    entry.setTitle(lesson.getName());
    entry.setStart(lesson.getStartDate());
    entry.setEnd(lesson.getEndDate());
    calendar.updateEntry(entry);
    return entry;
  }

  private void updateIntervalLabel(
      final HasText intervalLabel, final CalendarView calendarView, final LocalDate intervalStart) {
    String text = "Undefined";
    if (calendarView == null) {
      text = intervalStart.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
    } else if (calendarView instanceof CalendarViewImpl) {
      switch ((CalendarViewImpl) calendarView) {
        case DAY_GRID_MONTH:
        case LIST_MONTH:
          text = intervalStart.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
          break;
        case TIME_GRID_DAY:
          text = intervalStart.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
          break;
        default:
          text = "Undefined";
      }
    }
    intervalLabel.setText(text);
  }

  private class LessonDialog extends Dialog {

    private FormLayout lessonLayout;
    private BeanValidationBinder<Lesson> lessonBinder = new BeanValidationBinder<>(Lesson.class);

    private Lesson currentLesson;
    private Entry currentEntry;
    private boolean isNew;

    private TextField nameField;
    private DateTimePicker startDatePicker;
    private DateTimePicker endDatePicker;
    private MultiselectComboBox<Student> participantsMultiselectComboBox;
    private ComboBox<Subject> subjectComboBox;

    public LessonDialog(final Person person, final Entry currentEntry, boolean isNew)
        throws EntityNotFoundException {
      this.currentEntry = currentEntry;
      this.isNew = isNew;
      setCloseOnEsc(true);

      initLessonLayout(person);

      add(lessonLayout);
    }

    private void initLessonLayout(final Person currentPerson)
        throws EntityNotFoundException {
      currentLesson =
          isNew ? new Lesson() : lessonService.findEntityById(Long.valueOf(currentEntry.getId()));

      Teacher creator;
      final var isCurrentTeacher = new AtomicBoolean(false);
      if (currentPerson instanceof Teacher) {
        creator = (Teacher) currentPerson;
        isCurrentTeacher.set(true);
      } else {
        creator = currentLesson.getTeacher();
      }

      nameField = new TextField("Lesson name");
      nameField.setRequired(true);
      nameField.setWidthFull();

      final Span creatorName = new Span(creator.getFullName());

      final Avatar creatorAvatar = new Avatar();
      creatorAvatar.setName(creator.getFullName());
      if (creator.getUser().getAvatarUuid() != null) {
        creatorAvatar.setImage(avatarService.getSource(creator.getUser()));
      }

      final HorizontalLayout creatorInfoLayout = new HorizontalLayout();
      creatorInfoLayout.add(creatorName, creatorAvatar);
      creatorInfoLayout.setWidthFull();

      startDatePicker = new DateTimePicker();
      startDatePicker.setLabel("Start time");
      startDatePicker.setStep(Duration.ofMinutes(30));
      startDatePicker.setWidthFull();
      startDatePicker.setValue(
          calendar.getTimezone().convertToLocalDateTime(currentEntry.getStartUTC()));

      endDatePicker = new DateTimePicker();
      endDatePicker.setLabel("End time");
      endDatePicker.setStep(Duration.ofMinutes(30));
      endDatePicker.setWidthFull();
      endDatePicker.setValue(
          calendar.getTimezone().convertToLocalDateTime(currentEntry.getEndUTC()));

      subjectComboBox = new ComboBox<>();
      subjectComboBox.setLabel("Subject");
      subjectComboBox.setItems(subjectService.findAllByTeacherId(creator.getId()));
      subjectComboBox.setItemLabelGenerator(Subject::getName);
      subjectComboBox.setWidthFull();

      participantsMultiselectComboBox = new MultiselectComboBox<>();
      participantsMultiselectComboBox.setLabel("Participants");
      participantsMultiselectComboBox.setItems(studentService.findAllByTeacherId(creator.getId()));
      participantsMultiselectComboBox.setRenderer(
          new ComponentRenderer<>(
              VerticalLayout::new,
              (container, student) -> {
                final Avatar avatar = new Avatar();
                avatar.setName(student.getFullName());
                if (student.getUser().getAvatarUuid() != null) {
                  avatar.setImage(avatarService.getSource(student.getUser()));
                }
                final Span name = new Span();
                name.setText(student.getFullName());
                container.add(new HorizontalLayout(avatar, name));
              }));
      participantsMultiselectComboBox.setItemLabelGenerator(Student::getFullName);
      participantsMultiselectComboBox.setWidthFull();

      final Grid<Material> materialGrid = new Grid<>();
      materialGrid
          .addComponentColumn(
              material -> {
                final Anchor anchor = new Anchor();
                anchor.setText(material.getName());
                anchor.setHref(materialService.getSource(material));
                return anchor;
              })
          .setHeader("Materials");
      materialGrid.addComponentColumn(
          material -> {
            final Button removeButton = new Button();
            removeButton.setIcon(CLOSE.create());
            removeButton.addThemeVariants(LUMO_TERTIARY);
            removeButton.setIconAfterText(true);
            removeButton.addClickListener(
                e -> {
                  final var dataProvider =
                      (ListDataProvider<Material>) materialGrid.getDataProvider();
                  dataProvider.getItems().remove(material);
                  materialService.deleteEntity(material);
                  dataProvider.refreshAll();
                });
            return removeButton;
          });
      materialGrid.getColumns().forEach(column -> column.setAutoWidth(true));
      materialGrid.setWidthFull();

      final MultiFileMemoryBuffer materialMemoryBuffer = new MultiFileMemoryBuffer();
      final Upload materialUpload = new Upload(materialMemoryBuffer);
      materialUpload.setDropLabel(new Span("Upload materials"));
      final var materialsUploaded = new AtomicBoolean(false);

      final Grid<Submission> submissionGrid = new Grid<>();
      submissionGrid
          .addComponentColumn(
              submission -> {
                final Anchor anchor = new Anchor();
                anchor.setText(submission.getName());
                anchor.setHref(submissionService.getSource(submission));
                return anchor;
              })
          .setHeader("Submissions");
      submissionGrid.addComponentColumn(
          submission -> {
            final ComboBox<Integer> gradeCombobox = new ComboBox<>();
            gradeCombobox.setItems(0, 1, 2, 3, 4, 5);
            gradeCombobox.addValueChangeListener(
                e -> {
                  submission.setGrade(gradeCombobox.getValue());
                });
            if (!isCurrentTeacher.get()) {
              gradeCombobox.setReadOnly(true);
            }
            if (submission.getGrade() != null) {
              gradeCombobox.setValue(submission.getGrade());
            }
            return gradeCombobox;
          });
      submissionGrid.addComponentColumn(
          submission -> {
            final Button removeButton = new Button();
            removeButton.setIcon(CLOSE.create());
            removeButton.addThemeVariants(LUMO_TERTIARY);
            removeButton.setIconAfterText(true);
            removeButton.addClickListener(
                e -> {
                  final var dataProvider =
                      (ListDataProvider<Submission>) submissionGrid.getDataProvider();
                  dataProvider.getItems().remove(submission);
                  submissionService.deleteEntity(submission);
                  dataProvider.refreshAll();
                });
            if (isCurrentTeacher.get()) {
              removeButton.setVisible(false);
            }
            return removeButton;
          });
      submissionGrid.getColumns().forEach(column -> column.setAutoWidth(true));
      submissionGrid.setWidthFull();

      final MultiFileMemoryBuffer submissionMemoryBuffer = new MultiFileMemoryBuffer();
      final Upload submissionUpload = new Upload(materialMemoryBuffer);
      submissionUpload.setDropLabel(new Span("Upload submissions"));
      final AtomicBoolean submissionsUploaded = new AtomicBoolean(false);

      final Button saveButton = new Button("Save");
      saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
      final Button cancelButton = new Button("Cancel");
      cancelButton.addClickListener(
          e -> {
            close();
          });
      final Button removeButton = new Button("Remove");
      removeButton.addThemeVariants(LUMO_ERROR);
      removeButton.setVisible(!isNew);
      removeButton.addClickListener(
          e -> {
            lessonService.deleteEntity(currentLesson);
            calendar.removeEntry(currentEntry);
            close();
          });

      materialUpload.addStartedListener(e -> saveButton.setEnabled(false));
      materialUpload.addFailedListener(e -> saveButton.setEnabled(true));
      materialUpload.addFileRejectedListener(e -> saveButton.setEnabled(true));
      materialUpload.addAllFinishedListener(
          e -> {
            saveButton.setEnabled(true);
            materialsUploaded.set(true);
          });
      submissionUpload.addAllFinishedListener(
          e -> {
            saveButton.setEnabled(true);
            submissionsUploaded.set(true);
          });

      final HorizontalLayout buttonsLayout = new HorizontalLayout();
      buttonsLayout.add(saveButton, cancelButton, removeButton);
      buttonsLayout.setWidthFull();

      saveButton.addClickListener(
          event -> {
            try {
              Lesson lesson;
              if (isNew) {
                lesson = new Lesson();
                lesson.setTeacher(creator);
              } else {
                lesson = lessonService.findEntityById(Long.valueOf(currentEntry.getId()));
              }
              final var submissionGridDataProvider =
                  (ListDataProvider<Submission>) submissionGrid.getDataProvider();
              lessonBinder.writeBean(lesson);
              if (isLessonDateInvalid(lesson, lesson.getStudents())) {
                Notification.show("Current lesson overlaps another!");
                return;
              }
              lesson.setSubmissions(new ArrayList<>(submissionGridDataProvider.getItems()));
              final Lesson savedLesson = lessonService.saveEntity(lesson);
              if (materialsUploaded.get()) {
                for (final var file : materialMemoryBuffer.getFiles()) {
                  final String filename = materialMemoryBuffer.getFileData(file).getFileName();
                  final Material material = new Material();
                  lesson.getMaterials().add(material);
                  material.setLesson(lesson);
                  material.setTeacher(creator);
                  material.setName(filename);
                  materialService.saveEntity(
                      material,
                      filename.substring(filename.lastIndexOf(".") + 1),
                      materialMemoryBuffer.getInputStream(filename));
                }
                materialUpload.getElement().setPropertyJson("files", Json.createArray());
                materialGrid.setItems(lesson.getMaterials());
                removeButton.setVisible(true);
              }
              if (submissionsUploaded.get()) {
                for (final var file : submissionMemoryBuffer.getFiles()) {
                  final String filename = submissionMemoryBuffer.getFileData(file).getFileName();
                  final Submission submission = new Submission();
                  lesson.getSubmissions().add(submission);
                  submission.setLesson(lesson);
                  submission.setStudent((Student) currentPerson);
                  submission.setName(filename);
                  submissionService.saveEntity(
                      submission,
                      filename.substring(filename.lastIndexOf(".") + 1),
                      submissionMemoryBuffer.getInputStream(filename));
                }
                submissionUpload.getElement().setPropertyJson("files", Json.createArray());
                submissionGrid.setItems(lesson.getSubmissions());
              }
              if (isNew) {
                createEntry(savedLesson);
              } else {
                updateEntry(savedLesson, currentEntry);
              }
              if (isNew) {
                Notification.show(format("Lesson %s created successfully!", lesson.getName()));
              } else {
                Notification.show(format("Lesson %s updated successfully!", lesson.getName()));
              }
            } catch (final ValidationException | EntityNotFoundException e) {
              Notification.show("Something went wrong!");
            }
          });
      if (!isCurrentTeacher.get()) {
        nameField.setReadOnly(true);
        subjectComboBox.setReadOnly(true);
        participantsMultiselectComboBox.setReadOnly(true);
        materialUpload.setVisible(false);
        startDatePicker.setReadOnly(true);
        endDatePicker.setReadOnly(true);
        removeButton.setVisible(false);
      } else {
        submissionUpload.setVisible(false);
      }

      initBinder();

      if (!isNew) {
        final Lesson lesson = lessonService.findEntityById(Long.valueOf(currentEntry.getId()));
        materialGrid.setItems(lesson.getMaterials());
        submissionGrid.setItems(lesson.getSubmissions());
        participantsMultiselectComboBox.setItems(lesson.getStudents());
        lessonBinder.readBean(lesson);
      } else {
        submissionGrid.setVisible(false);
        submissionUpload.setVisible(false);
      }

      lessonLayout = new FormLayout();
      lessonLayout.setResponsiveSteps(new ResponsiveStep("0", 2, TOP));

      lessonLayout.add(
          creatorInfoLayout,
          nameField,
          startDatePicker,
          endDatePicker,
          subjectComboBox,
          participantsMultiselectComboBox,
          materialGrid,
          materialUpload,
          submissionGrid,
          submissionUpload,
          buttonsLayout);

      lessonLayout.setColspan(creatorInfoLayout, 2);
      lessonLayout.setColspan(nameField, 2);
      lessonLayout.setColspan(subjectComboBox, 2);
      lessonLayout.setColspan(participantsMultiselectComboBox, 2);
      lessonLayout.setColspan(materialGrid, 2);
      lessonLayout.setColspan(materialUpload, 2);
      lessonLayout.setColspan(submissionGrid, 2);
      lessonLayout.setColspan(submissionUpload, 2);
      lessonLayout.setColspan(buttonsLayout, 2);
    }

    private boolean isLessonDateInvalid(
        final Lesson lesson, final Iterable<? extends Person> participants) {
      final Instant now = Instant.now();
      final Collection<Lesson> teacherLessons =
          lessonService.findAllByTeacherIdAfterDate(currentPerson.getId(), now);
      final Collection<Lesson> participantLessons = new ArrayList<>();
      for (final var participant : participants) {
        participantLessons.addAll(
            lessonService.findAllByStudentIdAfterDate(participant.getId(), now));
      }

      for (final var teacherLesson : teacherLessons) {
        if (isLessonNotUnique(lesson, teacherLesson)) {
          return true;
        }
      }
      for (final var participantLesson : participantLessons) {
        if (isLessonNotUnique(lesson, participantLesson)) {
          return true;
        }
      }
      return false;
    }

    private boolean isLessonNotUnique(final Lesson newLesson, final Lesson currentLesson) {
      final boolean isNewActivityAfterCurrent =
          newLesson.getStartDate().isAfter(currentLesson.getStartDate())
              && newLesson.getStartDate().compareTo(currentLesson.getEndDate()) >= 0;
      final boolean isNewActivityBeforeCurrent =
          newLesson.getEndDate().compareTo(currentLesson.getStartDate()) <= 0
              && newLesson.getEndDate().isBefore(currentLesson.getEndDate());
      return !(isNewActivityAfterCurrent || isNewActivityBeforeCurrent);
    }

    private void initBinder() {
      lessonBinder
          .forField(nameField)
          .withValidator(
              val -> !val.isBlank() && val.length() <= 255,
              "Name should not be empty and more than 255")
          .bind(Lesson::getName, Lesson::setName);

      lessonBinder
          .forField(startDatePicker)
          .withValidator(
              val -> !isNew || val.isAfter(calendar.getTimezone().convertToLocalDateTime(Instant.now())),
              "Start time should be after now")
          .withValidator(
              val -> val != null && val.isBefore(endDatePicker.getValue()),
              "Start time should be before end time")
          .withConverter((new LocalDateTimeToInstantConverter()))
          .bind(Lesson::getStartDate, Lesson::setStartDate);

      lessonBinder
          .forField(endDatePicker)
          .withValidator(
              val -> val != null && val.isAfter(startDatePicker.getValue()),
              "End time should be after start time")
          .withConverter((new LocalDateTimeToInstantConverter()))
          .bind(Lesson::getEndDate, Lesson::setEndDate);

      lessonBinder
          .forField(subjectComboBox)
          .withValidator(val -> val != null, "You should choose the subject")
          .bind(Lesson::getSubject, Lesson::setSubject);

      lessonBinder
          .forField(participantsMultiselectComboBox)
          .withValidator(val -> !val.isEmpty(), "Lesson should contain at least 1 participant")
          .bind(Lesson::getStudents, Lesson::setStudents);
    }

    private class LocalDateTimeToInstantConverter implements Converter<LocalDateTime, Instant> {

      @Override
      public Result<Instant> convertToModel(final LocalDateTime value, ValueContext context) {
        return Result.ok(calendar.getTimezone().convertToUTC(value));
      }

      @Override
      public LocalDateTime convertToPresentation(final Instant value, ValueContext context) {
        return calendar.getTimezone().convertToLocalDateTime(value);
      }
    }
  }
}
