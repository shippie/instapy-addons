package de.miss.json.instapy.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("production")
public class CommonRunner implements CommandLineRunner {

  private final ModeDecider modeDecider;

  @Autowired
  public CommonRunner(final ModeDecider modeDecider) {
    this.modeDecider = modeDecider;
  }

  @Override
  public void run(final String... args) throws Exception {
    log.info("Startup with args {}", StringUtils.join(args, ' '));
    this.modeDecider.decideMode();
  }
}
