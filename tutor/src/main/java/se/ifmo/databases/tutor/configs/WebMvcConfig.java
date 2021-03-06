package se.ifmo.databases.tutor.configs;

import static java.lang.String.format;

import java.nio.file.Paths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

  /*@Bean
  public SimpleUrlHandlerMapping faviconHandlerMapping() {
    final var mapping = new SimpleUrlHandlerMapping();
    mapping.setUrlMap(Collections.singletonMap("/icon.png", faviconRequestHandler()));
    return mapping;
  }

  @Bean
  public ResourceHttpRequestHandler faviconRequestHandler() {
    final var requestHandler = new ResourceHttpRequestHandler();
    final var classPathResource = new ClassPathResource("icons");
    requestHandler.setLocations(List.of(classPathResource));
    return requestHandler;
  }*/

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/avatars/**")
        .addResourceLocations(getFilesystemLocation("avatars"));

    registry.addResourceHandler("/icons/**").addResourceLocations("classpath:/icons/");

    registry
        .addResourceHandler("/materials/**")
        .addResourceLocations(getFilesystemLocation("materials"));

    registry
        .addResourceHandler("/submissions/**")
        .addResourceLocations(getFilesystemLocation("submissions"));
  }

  private String getFilesystemLocation(final String relativePathDirectory) {
    return format("file:%s/", Paths.get(relativePathDirectory).toAbsolutePath().toString());
  }
}
