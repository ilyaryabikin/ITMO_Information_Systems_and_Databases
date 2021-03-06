package se.ifmo.databases.tutor.utils;

import static com.vaadin.flow.shared.ApplicationConstants.REQUEST_TYPE_PARAMETER;
import static java.util.Arrays.asList;
import static lombok.AccessLevel.PRIVATE;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.HandlerHelper;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import se.ifmo.databases.tutor.views.LoginFormView;
import se.ifmo.databases.tutor.views.RegisterFormView;

@NoArgsConstructor(access = PRIVATE)
public final class SecurityUtil {

  public static Collection<Class<? extends Component>> getPublicViews() {
    return List.of(LoginFormView.class, RegisterFormView.class);
  }

  public static String[] getPublicAntMatchers() {
    return getPublicViews().stream()
        .map(component -> RouteUtil.getRoutePath(component, component.getAnnotation(Route.class)))
        .map(route -> "/" + route)
        .toArray(String[]::new);
  }

  @Nullable
  public static UserDetails getCurrentUserDetails() {
    if (isUserLoggedIn()) {
      final SecurityContext securityContext = SecurityContextHolder.getContext();
      final Object principal = securityContext.getAuthentication().getPrincipal();
      if (principal instanceof UserDetails) {
        return (UserDetails) securityContext.getAuthentication().getPrincipal();
      }
    }
    return null;
  }

  public static boolean isAccessGranted(final Class<?> view) {
    if (getPublicViews().contains(view)) {
      return true;
    }

    if (!isUserLoggedIn()) {
      return false;
    }

    final Secured securedAnnotation = AnnotationUtils.findAnnotation(view, Secured.class);
    if (securedAnnotation == null) {
      return true;
    }

    final UserDetails currentUser = getCurrentUserDetails();
    if (currentUser == null) {
      return false;
    }

    final List<String> allowedRoles = asList(securedAnnotation.value());
    return currentUser.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(allowedRoles::contains);
  }

  public static boolean isVaadinInternalRequest(final HttpServletRequest request) {
    final String vaadinParameterValue = request.getParameter(REQUEST_TYPE_PARAMETER);
    return (vaadinParameterValue != null
        && Stream.of(HandlerHelper.RequestType.values())
            .anyMatch(requestType -> requestType.getIdentifier().equals(vaadinParameterValue)));
  }

  public static boolean isUserLoggedIn() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (authentication != null
        && !(authentication instanceof AnonymousAuthenticationToken)
        && authentication.isAuthenticated());
  }
}
