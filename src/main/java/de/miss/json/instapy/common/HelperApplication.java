package de.miss.json.instapy.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({PropertyHolder.class})
public class HelperApplication {

  public static void main(final String[] args) {
    SpringApplication.run(HelperApplication.class, args);
  }
}
