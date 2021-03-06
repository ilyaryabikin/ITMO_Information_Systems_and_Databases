package se.ifmo.databases.tutor.configs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import se.ifmo.databases.tutor.utils.SecurityUtil;

class RequestCacheConfig extends HttpSessionRequestCache {

  @Override
  public void saveRequest(final HttpServletRequest request, final HttpServletResponse response) {
    if (!SecurityUtil.isVaadinInternalRequest(request)) {
      super.saveRequest(request, response);
    }
  }
}
