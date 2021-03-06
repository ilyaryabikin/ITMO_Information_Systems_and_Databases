package se.ifmo.databases.tutor.views;

import static se.ifmo.databases.tutor.utils.ViewsConstants.LOGIN_ACTION;
import static se.ifmo.databases.tutor.utils.ViewsConstants.LOGIN_QUERY_ERROR;
import static se.ifmo.databases.tutor.utils.ViewsConstants.LOGIN_QUERY_REGISTERED;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLink;

@Route("login")
@RouteAlias("login-alias")
@PageTitle("Login into Tutor")
@CssImport(value = "./styles/global.css")
public class LoginFormView extends VerticalLayout implements HasUrlParameter<String> {

  private final LoginForm loginForm;

  public LoginFormView() {
    loginForm = new LoginForm();
    loginForm.setAction(LOGIN_ACTION);
    loginForm.setForgotPasswordButtonVisible(false);

    Paragraph registerParagraph = new Paragraph("Don't have an account yet?");
    RouterLink registerLink = new RouterLink("Register", RegisterFormView.class);
    initPageView();
    add(new H1("Welcome to Tutor"), loginForm, registerParagraph, registerLink);
  }

  @Override
  public void setParameter(final BeforeEvent event, final @OptionalParameter String parameter) {
    final Location location = event.getLocation();
    final var queryParams = location.getQueryParameters().getParameters();
    if (queryParams.containsKey(LOGIN_QUERY_ERROR)) {
      loginForm.setError(true);
    } else if (queryParams.containsKey(LOGIN_QUERY_REGISTERED)) {
      Notification.show("You have registered successfully!");
    }
  }

  private void initPageView() {
    addClassName("login-view");
    setSizeFull();
    setAlignItems(Alignment.CENTER);
    setJustifyContentMode(JustifyContentMode.CENTER);
  }
}
