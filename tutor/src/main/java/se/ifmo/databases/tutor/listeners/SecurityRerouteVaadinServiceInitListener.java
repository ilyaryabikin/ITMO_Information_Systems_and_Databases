package se.ifmo.databases.tutor.listeners;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;
import se.ifmo.databases.tutor.utils.SecurityUtil;
import se.ifmo.databases.tutor.views.LessonsView;
import se.ifmo.databases.tutor.views.LoginFormView;

@Component
public class SecurityRerouteVaadinServiceInitListener implements VaadinServiceInitListener {

  @Override
  public void serviceInit(final ServiceInitEvent event) {
    event
        .getSource()
        .addUIInitListener(
            uiEvent -> {
              final UI ui = uiEvent.getUI();
              ui.addBeforeEnterListener(this::forwardNavigation);
            });
  }

  private void forwardNavigation(final BeforeEvent beforeEvent) {
    if (!SecurityUtil.getPublicViews().contains(beforeEvent.getNavigationTarget())
        && !SecurityUtil.isUserLoggedIn()) {
      beforeEvent.forwardTo(LoginFormView.class);
    }
    if (!SecurityUtil.isAccessGranted(beforeEvent.getNavigationTarget())) {
      beforeEvent.forwardTo(LessonsView.class);
    }
  }
}
