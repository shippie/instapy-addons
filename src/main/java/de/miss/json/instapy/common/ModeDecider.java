package de.miss.json.instapy.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ModeDecider {

  private final String mode;
  private final JSplit jSplit;
  private final JDistinct jDistinct;
  private final JDiff jDiff;
  private final PropertyHolder propertyHolder;

  public ModeDecider(
      @Value("${mode:#{null}}") final String mode,
      final PropertyHolder propertyHolder,
      final JSplit jSplit,
      final JDistinct jDistinct,
      final JDiff jDiff) {
    this.mode = mode;
    this.propertyHolder = propertyHolder;
    this.jSplit = jSplit;
    this.jDistinct = jDistinct;
    this.jDiff = jDiff;
  }

  protected void decideMode() {

    if (StringUtils.equalsIgnoreCase(this.mode, "split")) {
      log.info("split mode enabled");
      this.jSplit.split(this.propertyHolder);

    } else if (StringUtils.equalsIgnoreCase(this.mode, "distinct")) {
      log.info("distinct mode enabled");
      this.jDistinct.distinct(this.propertyHolder);
    } else if (StringUtils.equalsIgnoreCase(this.mode, "diff")) {
      log.info("diff mode enabled");
      this.jDiff.diff(this.propertyHolder);
    } else showUsage();
  }

  private void showUsage() {
    final StringBuffer usage = new StringBuffer();

    usage.append(StringUtils.LF).append(StringUtils.LF);
    usage.append("Show usage:");
    usage.append(StringUtils.LF).append(StringUtils.LF);
    usage.append("Options").append(StringUtils.LF);
    usage.append("-------").append(StringUtils.LF).append(StringUtils.LF);
    usage
        .append("  --mode=...              mode can be split or distict or diff")
        .append(StringUtils.LF);
    usage
        .append("  --json.inputfile=...    inputfile as json array from instapy")
        .append(StringUtils.LF);
    usage
        .append("  --json.splitsize=...    split size, split after xxx users")
        .append(StringUtils.LF);
    usage
        .append(
            "  --json.inputfile2=...   second inputfile as json array from instapy only for distinct and diff")
        .append(StringUtils.LF);

    usage.append(StringUtils.LF).append(StringUtils.LF);
    usage.append("Examples").append(StringUtils.LF);
    usage.append("-------").append(StringUtils.LF);

    usage
        .append(StringUtils.LF)
        .append(StringUtils.LF)
        .append("1. Splitting, create chunk files of 1000 users")
        .append(StringUtils.LF);
    usage.append(
        "  --mode=split  --json.inputfile=06-03-2019~full~3083.json --json.splitsize=1000");
    usage
        .append(StringUtils.LF)
        .append(StringUtils.LF)
        .append("2. Distinct compare inputfile and inputfile2 and removes duplicates")
        .append(StringUtils.LF);
    usage.append(
        "  --mode=distinct  --json.inputfile=06-03-2019~full~3083.json --json.inputfile2=16-03-2019~full~3283.json");

    usage
        .append(StringUtils.LF)
        .append(StringUtils.LF)
        .append("3. Diff compare inputfile and inputfile2 and find all new users in inputfile2")
        .append(StringUtils.LF);
    usage.append(
        "  --mode=diff  --json.inputfile=06-03-2019~full~3083.json --json.inputfile2=16-03-2019~full~3283.json");

    log.info(usage.toString());
  }
}
