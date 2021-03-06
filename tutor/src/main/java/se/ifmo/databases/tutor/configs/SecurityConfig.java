package se.ifmo.databases.tutor.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.ifmo.databases.tutor.services.UserLoginTokenService;
import se.ifmo.databases.tutor.services.UserService;
import se.ifmo.databases.tutor.utils.SecurityUtil;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String LOGIN_PROCESSING_URL = "/login";
  private static final String LOGIN_FAILURE_URL = "/login?error=true";
  private static final String LOGIN_URL = "/login";
  private static final String LOGOUT_URL = "/logout";
  private static final String LOGOUT_SUCCESS_URL = "/login";
  private static final String JSESSION_COOKIE = "JSESSIONID";
  private static final String REMEMBER_ME_COOKIE = "remember-me";
  private static final String SECRET_KEY = "Lup4@6^v$$wQ!t9zSKSToLan@zASTRqU";

  private final UserService userService;
  private final UserLoginTokenService userLoginTokenService;
  private final PasswordEncoder bcryptPasswordEncoder;

  @Bean
  @Override
  protected UserDetailsService userDetailsService() {
    return userService;
  }

  @Override
  public void configure(final WebSecurity web) {
    web.ignoring()
        .antMatchers(
            "/VAADIN/**",
            "/icons/favicon.ico",
            "/robots.txt",
            "/manifest.webmanifest",
            "/sw.js",
            "/offline.html",
            "/icons/**",
            "/images/**",
            "/styles/**",
            "/h2-console/**");
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .requestCache()
        .requestCache(new RequestCacheConfig())
        .and()
        .authorizeRequests()
        .requestMatchers(SecurityUtil::isVaadinInternalRequest)
        .permitAll()
        .antMatchers(SecurityUtil.getPublicAntMatchers())
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .formLogin()
        .loginPage(LOGIN_URL)
        .permitAll()
        .loginProcessingUrl(LOGIN_PROCESSING_URL)
        .failureUrl(LOGIN_FAILURE_URL)
        .and()
        .logout()
        .permitAll()
        .logoutUrl(LOGOUT_URL)
        .deleteCookies(JSESSION_COOKIE, REMEMBER_ME_COOKIE)
        .logoutSuccessUrl(LOGOUT_SUCCESS_URL)
        .and()
        .rememberMe()
        .userDetailsService(userService)
        .tokenRepository(userLoginTokenService)
        .key(SECRET_KEY)
        .rememberMeCookieName(REMEMBER_ME_COOKIE)
        .alwaysRemember(true);
  }

  @Override
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService).passwordEncoder(bcryptPasswordEncoder);
  }
}
