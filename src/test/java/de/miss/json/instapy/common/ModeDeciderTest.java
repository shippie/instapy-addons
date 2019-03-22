package de.miss.json.instapy.common;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestApplicationConfiguration.class)
@ActiveProfiles("staging")
public class ModeDeciderTest {

  @MockBean JSplit mockJSplit;
  @MockBean JDistinct jDistinct;
  @MockBean JDiff jDiff;

  @BeforeEach
  void checkMock() {
    assertThat(this.mockJSplit).isNotNull();
  }

  @Test
  public void decideModeSplit() {

    final ModeDecider modeDecider =
        new ModeDecider(
            "split",
            PropertyHolder.builder().splitsize(25).inputfile("test.json").build(),
            this.mockJSplit,
            this.jDistinct, this.jDiff);
    modeDecider.decideMode();

    verify(this.mockJSplit, times(1)).split(any(PropertyHolder.class));
  }

  @Test
  public void decideModeHelp() {

    final ch.qos.logback.classic.Logger root =
        (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
    final Appender mockAppender = mock(Appender.class);
    when(mockAppender.getName()).thenReturn("MOCK");
    root.addAppender(mockAppender);

    // ... do whatever you need to trigger the log
    final ModeDecider modeDecider =
        new ModeDecider(
            null,
            PropertyHolder.builder().splitsize(25).inputfile("test.json").build(),
            this.mockJSplit,
            this.jDistinct, this.jDiff);
    modeDecider.decideMode();

    verify(mockAppender)
        .doAppend(
            argThat(
                new ArgumentMatcher() {
                  @Override
                  public boolean matches(final Object argument) {
                    return ((LoggingEvent) argument).getMessage().contains("Options");
                  }
                }));
  }
}
