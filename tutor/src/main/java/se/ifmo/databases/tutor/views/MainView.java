package se.ifmo.databases.tutor.views;

import static com.vaadin.flow.component.applayout.AppLayout.Section.DRAWER;
import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;
import static com.vaadin.flow.component.icon.VaadinIcon.SIGN_OUT;
import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import static com.vaadin.flow.component.tabs.Tabs.Orientation.VERTICAL;
import static com.vaadin.flow.component.tabs.TabsVariant.LUMO_MINIMAL;
import static se.ifmo.databases.tutor.utils.ViewsConstants.LOGOUT_ACTION;
import static se.ifmo.databases.tutor.utils.ViewsConstants.LOGO_SRC;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import se.ifmo.databases.tutor.models.Person;
import se.ifmo.databases.tutor.services.AvatarService;
import se.ifmo.databases.tutor.services.CurrentPersonService;
import se.ifmo.databases.tutor.utils.SecurityUtil;

@CssImport(value = "./styles/global.css")
@CssImport(value = "./styles/main-view/main-view.css")
public class MainView extends AppLayout {

  private final AvatarService avatarService;

  private final Person currentPerson;

  private final Tabs menu;
  private H1 viewHeaderTitle;

  @Autowired
  public MainView(
      final CurrentPersonService currentPersonService, final AvatarService avatarService) {
    this.avatarService = avatarService;
    this.currentPerson = currentPersonService.getCurrentPerson();
    VaadinSession.getCurrent().setAttribute(Person.class, currentPerson);

    setPrimarySection(DRAWER);
    addToNavbar(true, initHeader());
    menu = initMenu();
    addToDrawer(initDrawerContent(menu));
  }

  @Override
  protected void afterNavigation() {
    super.afterNavigation();
    getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
    viewHeaderTitle.setText(getCurrentPageTitle());
  }

  private Component initHeader() {
    final HorizontalLayout header = new HorizontalLayout();
    header.setId("header");
    header.setWidthFull();
    header.setSpacing(false);
    header.setAlignItems(Alignment.CENTER);

    viewHeaderTitle = new H1();

    final Avatar personAvatar = new Avatar();
    personAvatar.setName(currentPerson.getFullName());
    if (currentPerson.getUser().getAvatarUuid() != null) {
      personAvatar.setImage(avatarService.getSource(currentPerson.getUser()));
    }

    final Button logoutButton = new Button("Log Out", new Icon(SIGN_OUT));
    logoutButton.addThemeVariants(LUMO_TERTIARY);
    logoutButton.setIconAfterText(true);

    final Anchor logoutAnchor = new Anchor(LOGOUT_ACTION);
    logoutAnchor.getElement().appendChild(logoutButton.getElement());

    header.add(new DrawerToggle());
    header.add(viewHeaderTitle);
    header.add(personAvatar);
    header.add(logoutAnchor);
    return header;
  }

  private Component initDrawerContent(final Tabs menu) {
    final VerticalLayout drawerLayout = new VerticalLayout();
    drawerLayout.setSizeFull();
    drawerLayout.setPadding(false);
    drawerLayout.setSpacing(false);
    drawerLayout.getThemeList().set("spacing-s", true);
    drawerLayout.setAlignItems(Alignment.STRETCH);

    final HorizontalLayout logoLayout = new HorizontalLayout();
    logoLayout.setId("logo");
    logoLayout.setAlignItems(Alignment.CENTER);
    logoLayout.add(new Image(LOGO_SRC, "Tutor logo"));
    logoLayout.add(new H1("Tutor"));

    drawerLayout.add(logoLayout, menu);
    return drawerLayout;
  }

  private Tabs initMenu() {
    final Tabs tabs = new Tabs();
    tabs.setOrientation(VERTICAL);
    tabs.addThemeVariants(LUMO_MINIMAL);
    tabs.add(initMenuItems());
    return tabs;
  }

  private Component[] initMenuItems() {
    final List<Tab> tabs = new LinkedList<>();
    tabs.add(initMenuItem("Lessons", LessonsView.class));
    tabs.add(initMenuItem("Profile", ProfileView.class));
    if (SecurityUtil.isAccessGranted(StudentsView.class)) {
      tabs.add(initMenuItem("Students", StudentsView.class));
    }
    if (SecurityUtil.isAccessGranted(TeachersView.class)) {
      tabs.add(initMenuItem("Teachers", TeachersView.class));
    }
    return tabs.toArray(new Tab[0]);
  }

  private Tab initMenuItem(final String text, final Class<? extends Component> navigationTarget) {
    final Tab tab = new Tab();
    tab.add(new RouterLink(text, navigationTarget));
    ComponentUtil.setData(tab, Class.class, navigationTarget);
    return tab;
  }

  private Optional<Tab> getTabForComponent(Component component) {
    return menu.getChildren()
        .filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
        .findFirst()
        .map(Tab.class::cast);
  }

  private String getCurrentPageTitle() {
    return getContent().getClass().getAnnotation(PageTitle.class).value();
  }
}
